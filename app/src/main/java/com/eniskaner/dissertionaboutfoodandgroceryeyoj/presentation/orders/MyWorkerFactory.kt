package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.dao.OrderDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyWorkerFactory @Inject constructor(
    private val orderDao: OrderDao
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters): ListenableWorker? {
        return when (workerClassName) {
            UpdateOrderStatusWorker::class.java.name -> {
                UpdateOrderStatusWorker(appContext, workerParameters, orderDao)
            }
            else -> null
        }
    }
}
