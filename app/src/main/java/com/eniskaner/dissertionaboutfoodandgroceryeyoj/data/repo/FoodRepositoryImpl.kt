package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo

import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.FoodAPI
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.CrudResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCartResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo.FoodRepository
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val foodAPI: FoodAPI
): FoodRepository {
    override suspend fun getFoodList(): FoodResponse {
        return foodAPI.getFoodList()
    }

    override suspend fun addFoodToCart(
        foodName: String,
        foodPosterName: String,
        foodPrice: Int,
        foodAmount: Int,
        userName: String
    ): CrudResponse {
        return foodAPI.addFoodToCart(foodName, foodPosterName, foodPrice, foodAmount, userName)
    }

    override suspend fun getFoodListFromCart(userName: String): FoodInCartResponse {
        return foodAPI.getFoodListFromCart(userName)
    }

    override suspend fun deleteFoodFromCart(cartFoodId: Int, userName: String): CrudResponse {
        return foodAPI.deleteFoodFromCart(cartFoodId, userName)
    }
}