package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity

import com.google.gson.annotations.SerializedName

data class FoodInCart(
    val success: Int,
    @SerializedName("sepet_yemekler")
    val foodInCart: List<FoodInCartResponse>
)