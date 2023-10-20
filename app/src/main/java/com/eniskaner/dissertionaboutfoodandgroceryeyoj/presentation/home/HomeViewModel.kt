package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteType
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCart
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo.CartRepositoryImpl
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo.FoodRepositoryImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val foodRepository: FoodRepositoryImpl,
    private val cartRepository: CartRepositoryImpl
): ViewModel() {

    companion object {
        private const val KEY_QUERY = "query"
    }

    private val _foodList = MutableStateFlow<List<Food>>(emptyList())
    val foodList: StateFlow<List<Food>>
        get() = _foodList

    private val _favoriteFoodList = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    val favoriteFoodList: StateFlow<List<FavoriteEntity>>
        get() = _favoriteFoodList

    var cartFoodList = MutableStateFlow<List<FoodInCart>>(emptyList())
    var userName = cartRepository.username

    private val _searchFoodList = MutableStateFlow<List<Food>>(emptyList())
    val searchFoodList: StateFlow<List<Food>>
        get() = _searchFoodList

    private val _searchResults = MutableLiveData<List<FavoriteEntity>>()
    val searchResults: LiveData<List<FavoriteEntity>> get() = _searchResults

    private val _filter = MutableStateFlow(
        FilterParams(
            query = savedStateHandle[KEY_QUERY]
        )
    )

    val searchQuery: StateFlow<String?>
        get() = _filter
            .map { it.query }
            .stateIn(viewModelScope, WhileSubscribed(), _filter.value.query)

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent>
        get() = _uiEvents.shareIn(viewModelScope, WhileSubscribed(5000))

    init {
        getFoodList()
        addFavoriteFood()
        loadAllFoodCart()
        currentlyFoodInCart()
        getAlreadyListInCart()
        getSearchFoodList(query = savedStateHandle[KEY_QUERY])
    }

    private fun getAlreadyListInCart(): MutableLiveData<List<FoodInCart>> {
        val i = cartRepository.getFoodListInCartInRepository()
        return i
    }

    private fun updateCart(cartItems: List<FoodInCart>) {
        cartFoodList.value = cartItems
    }

    fun currentlyFoodInCart() {
        viewModelScope.launch {
            cartFoodList.emit(cartRepository.getFoodListInCartInRepository().value ?: emptyList())
        }
    }

    private fun getFoodList(forceRefresh: Boolean = false) {
        if (_searchFoodList.value.isEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                val productList = foodRepository.getFoodList()
                _foodList.emit(productList)
            }
        }
    }

    fun getSearchFoodList(query: String? = null) {
        viewModelScope.launch {
            query?.let {
                val productList = foodRepository.getSearchFood(query)
                _foodList.emit(productList)
            }
        }
    }

    fun reloadProductsData(forceRefresh: Boolean = false) {
        getFoodList(forceRefresh)
    }

    fun addFavoriteFood() = viewModelScope.launch {
        foodRepository.getFavoriteFood().collect {favoriteFoods ->
            _favoriteFoodList.emit(favoriteFoods)
        }
    }

    fun insertFavoriteFood(favoriteFood: FavoriteEntity, favoriteType: Set<FavoriteType>) = viewModelScope.launch {
        foodRepository.insertFavoriteFood(favoriteFood, favoriteType)
    }

    fun updateFavorites(food: Food, favoriteType: Set<FavoriteType>) {
        viewModelScope.launch {

            val selectedFood = FavoriteEntity(
                type = favoriteType.first(),
                favoriteFood = food,
                userName = Firebase.auth.currentUser?.email ?: "Eyoj",
                )
            insertFavoriteFood(selectedFood, favoriteType)
        }
    }

    private fun loadAllFoodCart() {
        viewModelScope.launch {
            cartRepository.getUserCart(userName)
        }
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

    data class FilterParams(
        val query: String? = null
    )

    sealed class UiEvent {
        object ClearFilters : UiEvent()
        object FetchingProducts : UiEvent()
        sealed class FetchCompleted : UiEvent() {
            object Failure : FetchCompleted()
            object Success : FetchCompleted()
        }
    }
}
