package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity

import com.google.gson.annotations.SerializedName

data class FoodInCartResponse(
    val success: Int,
    @SerializedName("sepet_yemekler")
    val foodInCart: List<FoodInCart>
)
