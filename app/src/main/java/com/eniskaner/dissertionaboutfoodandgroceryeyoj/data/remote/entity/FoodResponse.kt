package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity

import com.google.gson.annotations.SerializedName

data class FoodResponse(
    val success: Int,
    @SerializedName("yemekler")
    val food: List<Food>
)
