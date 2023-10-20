package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteType
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentHomeBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.cart.CartViewModel
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.FadingSnackbar
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.SwipeRefreshHandler
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.launchAndRepeatWithViewLifecycle
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.textChanges
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemActionCallback {
    companion object {

        const val FAVORITE_OPTIONS_RESULT_KEY =
            "com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.favorite-options-result"

        /** Use this key with bundle to return an array of [FavoriteType] ordinals */
        const val FAVORITE_OPTIONS_BUNDLE_KEY =
            "com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.favorite-options-bundle"

        /** Use this key with bundle to return product-sku */
        const val FAVORITE_SKU_BUNDLE_KEY =
            "com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.favorite-product-sku-bundle"

        /** Use this key to notify [HomeFragment] about product-list has been updated */
        const val PRODUCTS_UPDATED_RESULT_KEY =
            "com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.products-updated"

        const val SEARCH_DEBOUNCE_MILLIS = 1500L
    }

    private var _homeBinding: FragmentHomeBinding? = null
    private val homeBinding get() = _homeBinding!!
    private lateinit var viewModel : HomeViewModel
    private lateinit var cartViewModel: CartViewModel
    private val foodListAdapter = ProductListAdapter()
    private var snackbar: FadingSnackbar? = null
    private val favoriteTypeValues = enumValues<FavoriteType>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        snackbar = homeBinding.snackbar
        foodListAdapter.onItemActionCallback = this
        homeBinding.rvProducts.adapter = foodListAdapter
        foodListAdapter.viewModel = viewModel
        homeBinding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFoodList(foodListAdapter)
        setUpSwipeRefreshUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: HomeViewModel by viewModels()
        val tempViewModel2: CartViewModel by viewModels()
        viewModel = tempViewModel
        cartViewModel = tempViewModel2
        auth = Firebase.auth
        listenFavoriteOptionsResult()
        listenProductsUpdatedResult()
    }

    override fun onDestroyView() {
        _homeBinding = null
        snackbar = null
        super.onDestroyView()
    }

    override fun onStop() {
        super.onStop()
        // To prevent any memory leaks
        foodListAdapter.onItemActionCallback = null
    }

    private fun setUpSwipeRefreshUI() {
        val swipeRefreshHandler = SwipeRefreshHandler(
            scope = viewLifecycleOwner.lifecycle.coroutineScope,
            swipeRefreshLayout = homeBinding.swipeRefreshProducts,
            action = { viewModel.reloadProductsData(forceRefresh = true) }
        )
        lifecycle.addObserver(swipeRefreshHandler)
        homeBinding.swipeRefreshProducts.setOnRefreshListener(swipeRefreshHandler)
    }

    private fun observeFoodList(foodListAdapter: ProductListAdapter) {
        launchAndRepeatWithViewLifecycle {
            launch { observeSearchQuery() }
            launch { viewModel.foodList.collect(foodListAdapter::submitList) }
        }
    }

    private fun listenFavoriteOptionsResult() {
        setFragmentResultListener(FAVORITE_OPTIONS_RESULT_KEY) { _, bundle ->
            val favoriteOrdinals = bundle.getIntArray(FAVORITE_OPTIONS_BUNDLE_KEY)
            val favoriteFood = viewModel.favoriteFoodList.value
            val productSku = bundle.getString(FAVORITE_SKU_BUNDLE_KEY)
            val favoriteChoices = favoriteOrdinals?.map { favoriteTypeValues[it] }
            if (favoriteChoices != null && productSku != null) {
                favoriteFood
                    ?.let { viewModel.updateFavorites(it.first().favoriteFood, favoriteChoices.toSet()) }
            } else {
                Timber.e("Unable to update favorites with favoriteFood=$productSku and favoriteChoices=$favoriteChoices")
            }
        }
    }

    private fun listenProductsUpdatedResult() {
        setFragmentResultListener(PRODUCTS_UPDATED_RESULT_KEY) { _, _ ->
            lifecycleScope.launch { viewModel.reloadProductsData() }
        }
    }
    override fun onFavoriteClicked(product: FavoriteEntity) {
        if (product.favoriteFood.foodName.isNotEmpty()) {
            launchFavoriteOptionsBottomSheet(product)
        } else {
           viewModel.updateFavorites(product.favoriteFood, setOf(FavoriteType.WEEKLY))
            showFavoriteSnackbarFor(product)
        }
    }

    override fun onCartClicked(product: Food, quantity: Int) {
        lifecycleScope.launch {
            val foodInCart = viewModel.cartFoodList.value
            val isProductInCart = foodInCart.any { cart ->
                cart.cartFoodName == product.foodName
            } ?: false
            if (!isProductInCart) {
                viewModel.addToCart(product, quantity)
                snackbar?.show(messageText = requireContext().getString(R.string.info_inventory_added_to_cart, product.foodName))
            } else {
                snackbar?.show(messageText = requireContext().getString(R.string.info_inventory_already_in_cart, product.foodName))
            }
        }
    }

    private fun showFavoriteSnackbarFor(product: FavoriteEntity) {
        snackbar?.show(
            messageText = requireContext().getString(R.string.info_added_to_weekly_favorites, product.favoriteFood.foodName),
            actionId = R.string.action_change,
            actionClick = {
                launchFavoriteOptionsBottomSheet(product)
            }
        )
    }

    private fun launchFavoriteOptionsBottomSheet(product: FavoriteEntity) {
        findNavController().navigate(
            HomeFragmentDirections.actionNavigationHomeToBottomSheetFavoriteOptions(
                product.favoriteFood,
            )
        )
    }

    private suspend fun observeSearchQuery() {
        homeBinding.textFieldSearch.editText?.let { searchField ->
            searchField.textChanges()
                .debounce(SEARCH_DEBOUNCE_MILLIS)
                .collect { viewModel.getSearchFoodList(query = it.toString()) }
        }
    }
}
