package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.dao

import androidx.room.*
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteFoodDao {

    @Query("SELECT * FROM favorite")
    fun observeFavoriteFood(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteFood(favoriteFood: FavoriteEntity)

    @Delete
    suspend fun deleteFavoriteFood(favoriteFood: FavoriteEntity)

    @Query("SELECT * FROM favorite WHERE favoriteFood like '%' || :search || '%'")
    suspend fun searchFavoriteFood(search: String): List<FavoriteEntity>
}
