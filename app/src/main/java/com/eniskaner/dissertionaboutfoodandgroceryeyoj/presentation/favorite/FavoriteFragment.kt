package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteType
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentFavoriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoriteProductsViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel : FavoriteProductsViewModel by viewModels()
        viewModel = tempViewModel
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        binding.cardviewWeekly.setOnClickListener { navigateToFavoriteProducts(FavoriteType.WEEKLY) }
        binding.cardviewMonthly.setOnClickListener { navigateToFavoriteProducts(FavoriteType.MONTHLY) }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    private fun favoriteTypeToTitleRes(favoriteType: FavoriteType): Int {
        return when (favoriteType) {
            FavoriteType.WEEKLY -> R.string.title_my_weekly_list
            FavoriteType.MONTHLY -> R.string.title_my_monthly_list
        }
    }

    private fun navigateToFavoriteProducts(favoriteType: FavoriteType) {
        val titleRes = favoriteTypeToTitleRes(favoriteType)
        val action =
            FavoriteFragmentDirections.actionNavigationFavoriteToFragmentFavoriteProducts(titleRes)
        findNavController().navigate(action)
    }
}
