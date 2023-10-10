package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.di

import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.FoodAPI
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo.FoodRepositoryImpl
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.domain.repo.FoodRepository
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFoodAPI() : FoodAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FoodAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideMovieRepository(foodAPI: FoodAPI) : FoodRepository {
        return FoodRepositoryImpl(foodAPI = foodAPI)
    }

}