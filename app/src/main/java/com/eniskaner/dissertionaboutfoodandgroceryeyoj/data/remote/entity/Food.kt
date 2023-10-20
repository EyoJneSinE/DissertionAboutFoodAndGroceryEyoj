package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Food(
    @SerializedName("yemek_adi")
    var foodName: String,
    @SerializedName("yemek_fiyat")
    var foodPrice: String,
    @SerializedName("yemek_id")
    var foodId: String,
    @SerializedName("yemek_resim_adi")
    var foodPoster: String,

): Serializable {}
