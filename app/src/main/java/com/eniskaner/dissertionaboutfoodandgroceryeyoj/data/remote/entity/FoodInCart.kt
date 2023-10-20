package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FoodInCart(
    @SerializedName("sepet_yemek_id")
    val cartFoodId: Int,
    @SerializedName("yemek_adi")
    val cartFoodName: String,
    @SerializedName("yemek_resim_adi")
    val cartFoodPoster : String,
    @SerializedName("yemek_fiyat")
    val cartFoodPrice: Int,
    @SerializedName("yemek_siparis_adet")
    var cartFoodAmount: Int,
    @SerializedName("kullanici_adi")
    val cartUserName: String? = "eyoj"
): Serializable
