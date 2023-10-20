package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local

import androidx.room.TypeConverter
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import com.google.gson.Gson

class FavoriteFoodTypeConverters {

    private var gson = Gson()

    @TypeConverter
    fun fromFood(food: Food): String {
        return gson.toJson(food)
    }

    @TypeConverter
    fun toFood(foodJson: String): Food {
        return gson.fromJson(foodJson, Food::class.java)
    }
}
