package com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo

import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteType
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    suspend fun getFoodList(): List<Food>

    fun getFavoriteFood(): Flow<List<FavoriteEntity>>

    suspend fun insertFavoriteFood(favoriteFood: FavoriteEntity, favoriteType: Set<FavoriteType>)

    suspend fun deleteFavoriteFood(favoriteFood: FavoriteEntity)

    suspend fun searchFavoriteFood(search: String): List<FavoriteEntity>

    suspend fun getSearchFood(search: String): List<Food> {
        val foodList = getFoodList()
        val query = search.trim()
        val results = foodList.filter { food ->
            food.foodName.lowercase().contains(query.lowercase(), ignoreCase = true)
        }
        return results
    }
}
