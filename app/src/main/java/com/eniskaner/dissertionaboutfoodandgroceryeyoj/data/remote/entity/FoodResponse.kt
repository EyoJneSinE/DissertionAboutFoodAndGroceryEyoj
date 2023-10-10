package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FoodResponse(
    @SerializedName("yemek_adi")
    val foodName: String,
    @SerializedName("yemek_fiyat")
    val foodPrice: String,
    @SerializedName("yemek_id")
    val foodId: String,
    @SerializedName("yemek_resim_adi")
    val foodPoster: String
): Serializable