package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food

enum class FavoriteType {
    WEEKLY, MONTHLY
}

@Entity(primaryKeys = ["type", "favoriteFood"], tableName = "favorite")
data class FavoriteEntity(
    var type: FavoriteType,
    @ColumnInfo(name = "favoriteFood")
    var favoriteFood: Food,
    var userName: String
)

@DatabaseView(
    "SELECT favoriteFood FROM favorite WHERE type = 'WEEKLY'",
    viewName = "weekly_favorite"
)
data class WeeklyFavorite(
    @ColumnInfo(name = "favoriteFood")
    val favoriteFood: Food
)

@DatabaseView(
    "SELECT favoriteFood FROM favorite WHERE type = 'MONTHLY'",
    viewName = "monthly_favorite"
)
data class MonthlyFavorite(
    @ColumnInfo(name = "favoriteFood")
    val favoriteFood: Food
)
