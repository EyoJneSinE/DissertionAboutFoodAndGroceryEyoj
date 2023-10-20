package com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo

import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.CrudResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCart
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Resource

interface CartRepository {

    suspend fun getFoodInCart(): Resource<List<Food>>

    suspend fun addFoodToCart(
        foodInCart: FoodInCart,
        foodAmount: Int,
        userName: String
    ): Resource<CrudResponse>

    suspend fun getUserCart(username:String): Resource<List<FoodInCart>>

    suspend fun deleteFoodInCart(foodId: Int, username: String): Resource<CrudResponse>

}
