package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.FavoriteFoodDatabase
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.dao.FavoriteFoodDao
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.dao.OrderDao
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.FoodAPI
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo.CartRepositoryImpl
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo.FoodRepositoryImpl
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo.CartRepository
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo.FoodRepository
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders.UpdateOrderStatusWorker
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Constants.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient() = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Singleton
    @Provides
    fun provideFoodAPI(retrofit: Retrofit) : FoodAPI {
        return retrofit.create(FoodAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideFoodRepository(foodAPI: FoodAPI, favoriteFoodDao: FavoriteFoodDao) : FoodRepository {
        return FoodRepositoryImpl(foodAPI = foodAPI, favoriteFoodDao = favoriteFoodDao)
    }

    @Singleton
    @Provides
    fun provideCartRepository(foodAPI: FoodAPI) : CartRepository {
        return CartRepositoryImpl(foodAPI = foodAPI)
    }

    @Singleton
    @Provides
    fun provideFavoriteFoodRoomDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, FavoriteFoodDatabase::class.java,"FavoriteFoodDb").build()

    @Singleton
    @Provides
    fun provideFavoriteFoodDao(
        database: FavoriteFoodDatabase
    ) = database.getFavoriteDao()

    @Singleton
    @Provides
    fun provideOrderDao(
        database: FavoriteFoodDatabase
    ) = database.orderDao()


}