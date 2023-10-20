package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo

import androidx.lifecycle.MutableLiveData
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.FoodAPI
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.CrudResponse
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCart
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo.CartRepository
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val foodAPI: FoodAPI
): CartRepository {

    var username : String
    var auth: FirebaseAuth
    val foodListInCart : MutableLiveData<List<FoodInCart>>

    init {
        foodListInCart = MutableLiveData()
        auth = Firebase.auth
        username = auth.currentUser?.email.toString() ?: "eyoj"
    }

    override suspend fun getFoodInCart(): Resource<List<Food>> {
        return try {
            val result = foodAPI.getFoodList()
            if(result.success == 1){
                Resource.Success(data = result.food)
            }else{
                Resource.Failure(error = "Error !")
            }
        }catch (e:Exception){
            Resource.Failure(e.message)
        }
    }

    fun getFoodListInCartInRepository(): MutableLiveData<List<FoodInCart>> {
        return foodListInCart
    }

    override suspend fun addFoodToCart(
        foodInCart: FoodInCart,
        foodAmount: Int,
        userName: String
    ): Resource<CrudResponse> {
        return try {
            val result = foodAPI.addFoodToCart(
                foodName = foodInCart.cartFoodName,
                foodPosterName = foodInCart.cartFoodPoster,
                foodPrice = foodInCart.cartFoodPrice,
                foodAmount = foodAmount,
                userName = userName
            )
            if(result.success == 1){
                Resource.Success(data = result)
            }else{
                Resource.Failure(error = result.message ?:"Unknown Error!")
            }
        }catch (e:Exception){
            Resource.Failure(e.message ?:"Error!")
        }
    }

    override suspend fun getUserCart(username: String): Resource<List<FoodInCart>> {
        return try {
            val result = foodAPI.getFoodListFromCart(username)
            Resource.Success(data =result.foodInCart)
        } catch (e:Exception){
            Resource.Failure(error = e.localizedMessage)
        }
    }

    override suspend fun deleteFoodInCart(foodId: Int, username: String): Resource<CrudResponse> {
        return try {
            val result = foodAPI.deleteFoodFromCart(foodId,username)
            if(result.success == 1){
                Resource.Success(data = result)
            } else {
                Resource.Failure(error = result.message ?:"UnKnown Error!")
            }
        } catch (e:Exception){
            Resource.Failure(error = e.message?:"Error!")
        }
    }
}
