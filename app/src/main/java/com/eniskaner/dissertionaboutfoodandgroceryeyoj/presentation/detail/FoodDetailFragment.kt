package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCart
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentFoodDetailBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Constants.POSTER_URL
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Resource
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.load
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodDetailFragment : Fragment() {

    private var _binding: FragmentFoodDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FragmentFoodDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: FragmentFoodDetailViewModel by viewModels()
        viewModel = tempViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle: FoodDetailFragmentArgs by navArgs()
        val data = bundle.FoodInCart

        if (data.cartFoodId != 0){
            binding.buttonAdd.text = getString(R.string.change_items_quantity_in_cart, data.cartFoodName)
        }

        binding.toolbarDetail.setNavigationOnClickListener {
            nav(data.cartFoodId,it)
        }

        with(binding) {
            imageViewCart.load(POSTER_URL + data.cartFoodPoster)
            textViewPrice.text = getString(R.string.cart_food_price, data.cartFoodPrice)
            textViewName.text = data.cartFoodName
        }
        var count = data.cartFoodAmount

        binding.imageButtonSub.isEnabled = count > 1

        binding.imageButtonAdd.setOnClickListener {
            count++
            updateCountAndButtonStatus(count, data.cartFoodPrice)
        }

        binding.imageButtonSub.setOnClickListener {
            if (count > 1) {
                count--
                updateCountAndButtonStatus(count, data.cartFoodPrice)
            }
        }

        binding.buttonAdd.setOnClickListener {
            viewModel.getUserCart(username = data.cartUserName ?: "eyoj")
            viewModel.cart.observe(viewLifecycleOwner) { resources ->
                when (resources) {

                    is Resource.Loading -> {  }

                    is Resource.Success -> {
                        println("Success")
                        resources.data?.let { meals ->

                            if (searchInCartMeals(meals, data.cartFoodName)) {

                                val id = meals.filter { cartMeal ->
                                    cartMeal.cartFoodName.contains(data.cartFoodName, ignoreCase = true)
                                }[0]

                                viewModel.deleteCartItem(
                                    foodId = id.cartFoodId,
                                    username = data.cartUserName ?: "eyoj"
                                )
                            }
                            val sampleMeal = FoodInCart(
                                cartFoodId = data.cartFoodId,
                                cartFoodName = data.cartFoodName,
                                cartFoodPoster = data.cartFoodPoster,
                                cartFoodPrice = data.cartFoodPrice,
                                cartUserName = data.cartUserName,
                                cartFoodAmount = count
                            )

                            viewModel.addMealToCart(
                                foodInCart = sampleMeal,
                                username = data.cartUserName ?: "eyoj",
                                quantity = count
                            )
                        }
                        nav(data.cartFoodId,it)
                    }

                    is Resource.Failure -> {}

                    is Resource.Empty -> {
                        println("Empty")
                        val sampleMeal = FoodInCart(
                            cartFoodId = data.cartFoodId,
                            cartFoodName = data.cartFoodName,
                            cartFoodPoster = data.cartFoodPoster,
                            cartFoodPrice = data.cartFoodPrice,
                            cartUserName = data.cartUserName,
                            cartFoodAmount = count
                        )
                        viewModel.addMealToCart(
                            foodInCart = sampleMeal,
                            username = data.cartUserName ?: "",
                            quantity = count
                        )
                        nav(data.cartFoodId,it)
                    }
                }
            }
        }
    }

    private fun nav(id:Int, view: View){
        if (id == 0) {
            Navigation.findNavController(view).navigate(R.id.navigation_home)
        } else {
            Navigation.findNavController(view).navigate(R.id.navigation_cart)
        }
    }

    private fun updateCountAndButtonStatus(count: Int, price: Int) {
        binding.textViewCount.text = count.toString()
        val totalPrice = count * price
        binding.textViewPrice.text = getString(R.string.label_total, totalPrice)
        binding.imageButtonSub.isEnabled = count > 1
    }

    private fun searchInCartMeals(foodInCartList: List<FoodInCart>, searchName: String): Boolean {
        if (searchName.isBlank()) {
            return false
        }

        return foodInCartList.any { foodInCart ->
            foodInCart.cartFoodName.contains(searchName, ignoreCase = true)
        }
    }
}
