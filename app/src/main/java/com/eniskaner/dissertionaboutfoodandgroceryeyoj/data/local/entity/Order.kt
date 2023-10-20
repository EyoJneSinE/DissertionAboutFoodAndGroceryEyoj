package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime

data class OrderLine(
    val inventoryId: Int,
    val quantity: Int
): Serializable

enum class OrderStatus {
    ACCEPTED, PENDING, DISPATCHED, DELIVERED, CANCELLED
}

@Entity(tableName = "orders")
data class Order(
    val createdAt: LocalDateTime,
    val orderId: String,
    val orderLines: List<OrderLine>,
    var status: OrderStatus,
    val total: Float = 0f,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
): Serializable
