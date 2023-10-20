package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.OrderStatus
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.BottomsheetOrderDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class OrderDetailsBottomSheet: BottomSheetDialogFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: OrderViewModel
    private var _binding: BottomsheetOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: OrderDetailsBottomSheetArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.O)
    private val orderDateTimeFormatter = DateTimeFormatter.ofPattern(OrdersFragment.ORDER_DATE_FORMAT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel : OrderViewModel by viewModels()
        viewModel = tempViewModel
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            val orderId = args.ordersId
            val selectedOrder = viewModel.getOrderListForBottomSheet(orderId)
                selectedOrder?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        binding.txtOrderDateTime.text = orderDateTimeFormatter.format(it.createdAt)
                    }
                    binding.txtOrderId.text = it.orderId
                    binding.txtStatus.text = getString(it.status.displayTextResId)
                    binding.txtTotal.text = getString(R.string.total_cost, it.total)
                }
            }
    }
    private val OrderStatus.displayTextResId: Int
        get() {
            return when (this) {
                OrderStatus.ACCEPTED -> R.string.label_order_status_accepted
                OrderStatus.PENDING -> R.string.label_order_status_pending
                OrderStatus.DISPATCHED -> R.string.label_order_status_dispatched
                OrderStatus.DELIVERED -> R.string.label_order_status_delivered
                OrderStatus.CANCELLED -> R.string.label_order_status_canceled
            }
        }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
