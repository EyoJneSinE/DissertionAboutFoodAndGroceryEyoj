package com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo

import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.CrudResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCartResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodResponse
import retrofit2.http.Field

interface FoodRepository {

    suspend fun getFoodList(): FoodResponse

    suspend fun addFoodToCart(
        foodName: String,
        foodPosterName: String,
        foodPrice: Int,
        foodAmount: Int,
        userName: String
    ): CrudResponse

    suspend fun getFoodListFromCart(userName: String): FoodInCartResponse

    suspend fun deleteFoodFromCart(cartFoodId: Int, userName: String): CrudResponse
}