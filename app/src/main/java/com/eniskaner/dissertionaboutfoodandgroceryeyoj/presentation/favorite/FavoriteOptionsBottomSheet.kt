package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.BottomsheetFavoriteOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FavoriteOptionsBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: BottomsheetFavoriteOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetFavoriteOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }
}