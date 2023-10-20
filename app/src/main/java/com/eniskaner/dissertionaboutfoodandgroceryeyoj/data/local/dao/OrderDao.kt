package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.dao

import androidx.room.*
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.Order
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.OrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: String): Order?

    @Delete
    suspend fun deleteOrder(order: Order)

    @Update
    suspend fun updateOrderStatus(order: Order)
}
