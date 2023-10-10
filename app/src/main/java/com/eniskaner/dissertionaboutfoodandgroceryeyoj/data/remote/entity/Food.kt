package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity

import com.google.gson.annotations.SerializedName

data class Food(
    val success: Int,
    @SerializedName("yemekler")
    val food: List<FoodResponse>
)