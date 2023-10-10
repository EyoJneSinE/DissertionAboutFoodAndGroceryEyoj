package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.BottomsheetOrderDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.format.DateTimeFormatter

class OrderDetailsBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: BottomsheetOrderDetailsBinding
    private val orderDateTimeFormatter = DateTimeFormatter.ofPattern(OrdersFragment.ORDER_DATE_FORMAT)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
}