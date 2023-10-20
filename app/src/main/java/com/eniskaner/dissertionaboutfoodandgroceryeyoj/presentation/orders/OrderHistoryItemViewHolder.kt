package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.Order
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.OrderHistoryItemViewBinding
import java.time.format.DateTimeFormatter

class OrderHistoryItemViewHolder(
    private val binding: OrderHistoryItemViewBinding,
    var onDetailsClickedListener: OnDetailsClickedListener? = null
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val customDateTimeFormatter =
        DateTimeFormatter.ofPattern(OrdersFragment.ORDER_DATE_FORMAT)

    fun bind(order: Order) {
        val context = binding.root.context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.txtDate.text = customDateTimeFormatter.format(order.createdAt)
        }
        val strippedOrderId = stripOrderIdToFirstSegment(order.orderId)
        binding.txtOrderId.text =
            context.getString(R.string.info_order_id_template, strippedOrderId)
        binding.txtTotalCost.text =
            context.getString(R.string.info_price_tl_template, order.total)
        binding.btnDetails.setOnClickListener { onDetailsClickedListener?.onDetailsClicked(order.orderId) }
    }

    private fun stripOrderIdToFirstSegment(orderId: String): String {
        return orderId.takeWhile { it != '-' }
    }

    fun interface OnDetailsClickedListener {
        fun onDetailsClicked(orderId: String)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onDetailsClickedListener: OnDetailsClickedListener? = null
        ): OrderHistoryItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = OrderHistoryItemViewBinding.inflate(inflater, parent, false)
            return OrderHistoryItemViewHolder(binding, onDetailsClickedListener)
        }
    }
}
