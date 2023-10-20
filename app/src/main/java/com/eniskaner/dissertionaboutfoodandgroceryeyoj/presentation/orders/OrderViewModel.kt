package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.dao.OrderDao
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.Order
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.repo.CartRepositoryImpl
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor (
    private val cartRepository: CartRepositoryImpl,
    private val orderDao: OrderDao
): ViewModel() {

    private val _userOrdersState = MutableStateFlow<UiState<List<Order>>>(UiState.Idle)
    val userOrdersState: StateFlow<UiState<List<Order>>>
        get() = _userOrdersState.asStateFlow()

    private val _orderList = MutableStateFlow<List<Order>>(emptyList())
    val orderList: StateFlow<List<Order>>
        get() = _orderList

    var newOrderList = mutableListOf<Order>()
    private val userName = cartRepository.username

    var _selectedOrder: Order? = null

    /** this field should only be accessed when updateSelectedOrder was succeeded */
    val selectedOrder: Order get() = _selectedOrder!!

    var isLoggedIn = MutableStateFlow(false)

    val orderListStateFlow: StateFlow<List<Order>> = _orderList.asStateFlow()

    init {
        loggedIn()
        getOrderList()
    }

    suspend fun updateSelectedOrderById(orderId: String, callback: (Boolean) -> Unit) {
        val order = orderDao.getOrderById(orderId)
        if (order != null) {
            _selectedOrder = order
            callback(true)
        } else {
            callback(false)
        }
    }

    suspend fun updateSelectedOrderById(orderId: String): Boolean {
        val order = orderDao.getOrderById(orderId)
        if (order != null) {
            _selectedOrder = order
            return true
        }
        return false
        /*val currentOrderList = _orderList.value
        _selectedOrder = currentOrderList.find { it.orderId == orderId }
        return  _selectedOrder != null*/
    }

    fun addOrder(order: Order) {
        viewModelScope.launch {
            orderDao.insertOrder(order)
        }
    }

    suspend fun getOrderListForBottomSheet(orderId: String): Order? {
        return orderDao.getOrderById(orderId)
    }

    fun getOrderList() {
        viewModelScope.launch {
            val orderList = orderDao.getAllOrders().firstOrNull() ?: emptyList()
            val reverseOrderList = orderList.sortedWith(compareByDescending { it.createdAt })
            _orderList.value = reverseOrderList
            _userOrdersState.value = UiState.Success(reverseOrderList)
        }
    }

    fun refreshOrderList()  {
        viewModelScope.launch {
            try {
                val updatedOrderList = orderDao.getAllOrders().firstOrNull() ?: emptyList()
                val reverseOrderList = updatedOrderList.sortedWith(compareByDescending { it.createdAt })
                _orderList.value = reverseOrderList
                _userOrdersState.value = UiState.Success(reverseOrderList)
            } catch (e: Exception) {
                _userOrdersState.value = UiState.Error
            }
        }
    }

    fun loggedIn() {
        if (userName.isNotEmpty())
            isLoggedIn.value = true
    }

    fun updateOrdersStatus(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<UpdateOrderStatusWorker>().setInitialDelay(10, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
    }
}
