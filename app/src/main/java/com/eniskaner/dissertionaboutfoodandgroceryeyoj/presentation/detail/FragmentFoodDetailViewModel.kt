package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.CrudResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCart
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo.CartRepositoryImpl
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentFoodDetailViewModel @Inject constructor(
    private val cartRepository: CartRepositoryImpl
): ViewModel() {

    private val _cart = MutableLiveData<Resource<List<FoodInCart>>>()
    val cart: LiveData<Resource<List<FoodInCart>>> = _cart

    private val _addMealToCart = MutableLiveData<Resource<CrudResponse>>()
    val addMealToCart: LiveData<Resource<CrudResponse>> = _addMealToCart

    private val _deleteCartMeal = MutableLiveData<Resource<CrudResponse>>()
    val deleteCartMeal: LiveData<Resource<CrudResponse>> = _deleteCartMeal


    fun addMealToCart(foodInCart: FoodInCart, username: String, quantity: Int) {
        viewModelScope.launch {
            _addMealToCart.value = Resource.Loading
            try {
                val result = cartRepository.addFoodToCart(foodInCart, quantity, username)
                _addMealToCart.value = result
            } catch (e: Exception) {
                _addMealToCart.value = Resource.Failure(e.message)
            }
        }
    }

    fun getUserCart(username: String) {
        viewModelScope.launch {
            _cart.value = Resource.Loading
            try {
                val result = cartRepository.getUserCart(username)
                val cartItems = (result as? Resource.Success)?.data
                if(cartItems != null){
                    val items = cartItems.sortedBy { it.cartUserName }
                    _cart.value = Resource.Success(items)
                }else{
                    _cart.value = Resource.Empty
                }
            } catch (e: Exception) {
                _cart.value = Resource.Failure(e.localizedMessage)
            }
        }
    }

    fun deleteCartItem(foodId: Int, username: String) {
        viewModelScope.launch {
            _deleteCartMeal.value = Resource.Loading
            try {
                val result =
                    cartRepository.deleteFoodInCart(foodId = foodId, username = username)
                _deleteCartMeal.value = result
                getUserCart(username)
            } catch (e: Exception) {
                _deleteCartMeal.value = Resource.Failure(e.message)
            }
        }
    }
}
