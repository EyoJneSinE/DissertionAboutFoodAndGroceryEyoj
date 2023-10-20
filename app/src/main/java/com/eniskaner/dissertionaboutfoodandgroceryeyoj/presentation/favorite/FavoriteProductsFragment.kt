package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteType
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentFavoriteProductsBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.cart.CartFragment.Companion.CART_CHANGED_RESULT
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.HomeFragment
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.HomeFragment.Companion.PRODUCTS_UPDATED_RESULT_KEY
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.OnItemActionCallback
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.FadingSnackbar
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.launchAndRepeatWithViewLifecycle
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class FavoriteProductsFragment : Fragment(), OnItemActionCallback {

    private var _binding: FragmentFavoriteProductsBinding? = null
    private val binding get() = _binding!!
    private val args: FavoriteProductsFragmentArgs by navArgs()
    private val productListAdapter = FavoriteProductsListAdapter()
    private lateinit var viewModel : FavoriteProductsViewModel
    private var snackbar: FadingSnackbar? = null
    private var isAddAllClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteProductsBinding.inflate(inflater, container, false)
        binding.tvListTitle.text = requireContext().getString(args.listTitle)
        snackbar = binding.snackbar
        productListAdapter.onItemActionCallback = this
        binding.rvProducts.adapter = productListAdapter
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: FavoriteProductsViewModel by viewModels()
        viewModel = tempViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadFavoriteProducts()
        setupAddAllButton()
        observeProductList(productListAdapter)
    }

    private fun setupAddAllButton() {
        binding.btnAddAll.setOnClickListener {
            if (!isAddAllClicked) {
                isAddAllClicked = true
                viewModel.readFavoriteFood.observe(viewLifecycleOwner, Observer { favoriteList ->
                    lifecycleScope.launch {
                        val foodInCart = viewModel.cartFoodList.value
                        val productsInCartNames = foodInCart.map { it.cartFoodName.lowercase() }.toSet()
                        for (favoriteFood in favoriteList) {
                            if (favoriteFood.favoriteFood.foodName.lowercase() !in productsInCartNames) {
                                viewModel.addToCart(favoriteFood.favoriteFood, 1)
                            }
                        }
                        snackbar?.show(messageText = requireContext().getString(R.string.all_favorite_foods_added_to_cart))
                    }
                })
            } else {
                snackbar?.show(messageText = requireContext().getString(R.string.all_favorite_foods_already_in_to_cart))
            }
        }
    }

    override fun onCartClicked(product: Food, quantity: Int) {
        lifecycleScope.launch {
            viewModel.addToCart(product, quantity)
        }
    }

    override fun onDestroyView() {
        _binding = null
        snackbar = null
        productListAdapter.onItemActionCallback = null
        super.onDestroyView()
    }

    private fun observeProductList(favoriteFoodAdapter: FavoriteProductsListAdapter) {
        launchAndRepeatWithViewLifecycle {
            launch { observeSearchQuery() }
            launch { viewModel.productList.collect(favoriteFoodAdapter::submitList) }
        }
    }

    override fun onFavoriteClicked(product: FavoriteEntity) {
        if (product.favoriteFood.foodName.isNotEmpty()) {
            viewModel.removeFavorites(product)
            notifyProductsUpdated()
        } else {
            val favoriteType = titleIdToFavoriteType(args.listTitle)
            Timber.e("Product: '$product' inappropriately appeared in $favoriteType list")
        }
    }

    private fun titleIdToFavoriteType(@StringRes titleId: Int): FavoriteType {
        return when (titleId) {
            R.string.title_my_monthly_list -> FavoriteType.MONTHLY
            else -> FavoriteType.WEEKLY // Using weekly list by default
        }
    }

    private fun notifyProductsUpdated() {
        setFragmentResult(PRODUCTS_UPDATED_RESULT_KEY, bundleOf())
    }

    private fun notifyCartChanged() {
        setFragmentResult(CART_CHANGED_RESULT, bundleOf())
    }

    private suspend fun observeSearchQuery() {
        binding.textFieldSearch.editText?.let { searchField ->
            searchField.textChanges()
                .debounce(HomeFragment.SEARCH_DEBOUNCE_MILLIS)
                .collect { viewModel.getSearchFoodList(query = it.toString()) }
        }
    }
}
