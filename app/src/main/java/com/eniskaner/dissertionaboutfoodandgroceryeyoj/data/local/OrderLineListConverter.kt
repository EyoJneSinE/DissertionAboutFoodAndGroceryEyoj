package com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local

import androidx.room.TypeConverter
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.OrderLine
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OrderLineListConverter {

    private var gson = Gson()
    @TypeConverter
    fun fromOrderLines(orderLines: List<OrderLine>): String {
        return gson.toJson(orderLines)
    }

    @TypeConverter
    fun toOrderLines(orderLinesJson: String): List<OrderLine> {
        val listType = object : TypeToken<List<OrderLine>>() {}.type
        return gson.fromJson(orderLinesJson, listType)
    }
}
