package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote

import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.CrudResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCartResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodAPI {

    @GET("yemekler/tumYemekleriGetir.php")
    suspend fun getFoodList(): FoodResponse

    @POST("yemekler/sepeteYemekEkle.php")
    @FormUrlEncoded
    suspend fun addFoodToCart(
        @Field("yemek_adi")
        foodName: String,
        @Field("yemek_resim_adi")
        foodPosterName: String,
        @Field("yemek_fiyat")
        foodPrice: Int,
        @Field("yemek_siparis_adet")
        foodAmount: Int,
        @Field("kullanici_adi")
        userName: String
    ): CrudResponse

    @POST("yemekler/sepettekiYemekleriGetir.php")
    @FormUrlEncoded
    suspend fun getFoodListFromCart(
        @Field("kullanici_adi")
        userName: String
    ): FoodInCartResponse

    @POST("yemekler/sepettenYemekSil.php")
    @FormUrlEncoded
    suspend fun deleteFoodFromCart(
        @Field("sepet_yemek_id")
        cartFoodId: Int,
        @Field("kullanici_adi")
        userName: String
    ): CrudResponse
}