package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo

import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.dao.FavoriteFoodDao
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteType
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.FoodAPI
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo.FoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val foodAPI: FoodAPI,
    private val favoriteFoodDao: FavoriteFoodDao
): FoodRepository {

    override suspend fun getFoodList(): List<Food> {
        return foodAPI.getFoodList().food
    }

    override fun getFavoriteFood(): Flow<List<FavoriteEntity>> {
        return favoriteFoodDao.observeFavoriteFood()
    }

    override suspend fun insertFavoriteFood(favoriteFood: FavoriteEntity, favoriteType: Set<FavoriteType>) {
        favoriteFoodDao.insertFavoriteFood(favoriteFood)
    }

    override suspend fun deleteFavoriteFood(favoriteFood: FavoriteEntity) {
        favoriteFoodDao.deleteFavoriteFood(favoriteFood)
    }

    override suspend fun searchFavoriteFood(search: String): List<FavoriteEntity> {
        return favoriteFoodDao.searchFavoriteFood(search)
    }
}
