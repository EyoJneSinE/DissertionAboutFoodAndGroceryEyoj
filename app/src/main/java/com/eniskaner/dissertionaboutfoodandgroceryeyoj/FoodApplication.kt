package com.eniskaner.dissertionaboutfoodandgroceryeyoj

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders.MyWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FoodApplication: Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: MyWorkerFactory
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
