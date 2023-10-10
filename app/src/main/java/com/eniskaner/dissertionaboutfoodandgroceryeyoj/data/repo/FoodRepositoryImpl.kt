package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo

import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.FoodAPI
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.CrudResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCartResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo.FoodRepository
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val api: FoodAPI
): FoodRepository {
    override suspend fun getFoodList(): FoodResponse {
        return api.getFoodList()
    }

    override suspend fun addFoodToCart(
        foodName: String,
        foodPosterName: String,
        foodPrice: Int,
        foodAmount: Int,
        userName: String
    ): CrudResponse {
        return api.addFoodToCart(foodName, foodPosterName, foodPrice, foodAmount, userName)
    }

    override suspend fun getFoodListFromCart(userName: String): FoodInCartResponse {
        return api.getFoodListFromCart(userName)
    }

    override suspend fun deleteFoodFromCart(cartFoodId: Int, userName: String): CrudResponse {
        return api.deleteFoodFromCart(cartFoodId, userName)
    }
}