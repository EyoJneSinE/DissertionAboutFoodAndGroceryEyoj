package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.dao.FavoriteFoodDao
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.dao.OrderDao
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.MonthlyFavorite
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.Order
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.WeeklyFavorite

@Database(
    entities = [
        FavoriteEntity::class,
        Order::class
    ],
    views = [
        WeeklyFavorite::class,
        MonthlyFavorite::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(FavoriteFoodTypeConverters::class, LocalDateTimeConverter::class, OrderLineListConverter::class)
abstract class FavoriteFoodDatabase: RoomDatabase() {

    abstract fun getFavoriteDao(): FavoriteFoodDao
    abstract fun orderDao(): OrderDao
}
