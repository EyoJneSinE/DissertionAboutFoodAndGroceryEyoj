package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.Dao
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.FavoriteFoodDatabase
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.dao.OrderDao
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.Order
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.OrderStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@HiltWorker
class UpdateOrderStatusWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val orderDao: OrderDao
) : Worker(context, params) {
    override fun doWork(): Result {

        try {
            CoroutineScope(Dispatchers.IO).launch {
                val orders = orderDao.getAllOrders().firstOrNull()
                orders?.let {
                    for (order in orders)
                        when (order.status) {
                            OrderStatus.ACCEPTED -> {
                                delay(15000)
                                updateOrderStatus(order, OrderStatus.PENDING)
                            }

                            OrderStatus.PENDING -> {
                                delay(3000)
                                updateOrderStatus(order, OrderStatus.DISPATCHED)
                            }

                            OrderStatus.DISPATCHED -> {
                                delay(900000)
                                updateOrderStatus(order, OrderStatus.DELIVERED)
                            }

                            OrderStatus.DELIVERED -> {}

                            OrderStatus.CANCELLED -> {}
                        }
                }
            }
        } catch (e: Exception) {
            return Result.failure()
        }
        return Result.success()
    }
    private suspend fun updateOrderStatus(order: Order, newStatus: OrderStatus) {
        order.status = newStatus
        orderDao.updateOrderStatus(order)
    }
}
