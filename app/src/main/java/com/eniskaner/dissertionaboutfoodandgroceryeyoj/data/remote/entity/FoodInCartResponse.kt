package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity

import com.google.gson.annotations.SerializedName

data class FoodInCartResponse(
    @SerializedName("sepet_yemek_id")
    val cartFoodId: Int,
    @SerializedName("yemek_adi")
    val cartFoodName: String,
    @SerializedName("yemek_resim_adi")
    val cartFoodPoster : String,
    @SerializedName("yemek_fiyat")
    val cartFoodPrice: Int,
    @SerializedName("yemek_siparis_adet")
    val cartFoodAmount: Int,
    @SerializedName("kullanici_adi")
    val cartUserName: String
)