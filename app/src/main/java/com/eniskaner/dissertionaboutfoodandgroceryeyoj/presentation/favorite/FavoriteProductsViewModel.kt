package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteType
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCart
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo.CartRepositoryImpl
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteProductsViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val cartRepository: CartRepositoryImpl
) : ViewModel() {

    var readFavoriteFood: LiveData<List<FavoriteEntity>> = foodRepository.getFavoriteFood().asLiveData()
    var userName = cartRepository.username
    var cartFoodList = MutableStateFlow<List<FoodInCart>>(emptyList())

    private val _productList = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    val productList: StateFlow<List<FavoriteEntity>>
        get() = _productList.asStateFlow()

    init {
        loadFavoriteProducts()
        loadAllFoodCart()
        currentlyFoodInCart()
        getAlreadyListInCart()
    }

    fun loadFavoriteProducts() {
        viewModelScope.launch {
            foodRepository.getFavoriteFood().collect{favoriteFoods ->
                _productList.emit(favoriteFoods)
            }
        }
    }

    fun getAlreadyListInCart(): MutableLiveData<List<FoodInCart>> {
        val i = cartRepository.getFoodListInCartInRepository()
        return i
    }

    fun currentlyFoodInCart() {
        viewModelScope.launch {
            cartFoodList.emit(cartRepository.getFoodListInCartInRepository().value ?: emptyList())
        }
    }

    fun loadAllFoodCart() {
        viewModelScope.launch {
            cartRepository.getUserCart(userName)
        }
    }

    fun removeFavorites(product: FavoriteEntity) {
        viewModelScope.launch {
            deleteFavoriteFood(product)
        }
        loadFavoriteProducts()
    }

    fun insertFavoriteFood(favorite: FavoriteEntity, favoriteType: Set<FavoriteType>) =
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.insertFavoriteFood(favorite, favoriteType)
        }

    fun getSearchFoodList(query: String? = null) {
        viewModelScope.launch {
            query?.let {
                val productList = foodRepository.searchFavoriteFood(query)
                _productList.emit(productList)
            }
        }
    }

    fun deleteFavoriteFood(favorite: FavoriteEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            foodRepository.deleteFavoriteFood(favorite)
        }

    suspend fun addToCart(product: Food, quantity: Int) {
        val selectedFoodInCart = FoodInCart(
            product.foodId.toInt(),
            product.foodName,
            product.foodPoster,
            product.foodPrice.toInt(),
            quantity,
            userName
        )
        loadAllFoodCart()
        val cartItems = cartFoodList.value.toMutableList()


        if (!cartItems.isNullOrEmpty()) {
            val existingCartItem = cartItems.find { it.cartFoodName == product.foodName }
            if (existingCartItem != null) {
                existingCartItem.cartFoodAmount += quantity
            } else {
                cartItems.add(selectedFoodInCart)
            }
        } else {
            cartItems.add(selectedFoodInCart)
        }

        viewModelScope.launch {
            updateCart(cartItems)
            cartRepository.addFoodToCart(
                foodInCart = selectedFoodInCart,
                quantity,
                userName
            )
        }
    }
    suspend fun updateCart(cartItems: List<FoodInCart>) {
        cartFoodList.value = cartItems
    }
}
