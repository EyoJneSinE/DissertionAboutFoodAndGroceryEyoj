package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentOrderSuccessBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderSuccessFragment : Fragment() {

    private var _binding: FragmentOrderSuccessBinding? = null
    private val binding get() = _binding!!
    private val args: OrderSuccessFragmentArgs by navArgs()
    private lateinit var viewModel: OrderViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel : OrderViewModel by viewModels()
        viewModel = tempViewModel
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderSuccessBinding.inflate(layoutInflater, container, false)
        val text = args.userFullName.plus(" ").plus(getString(R.string.info_order_success_affirmation_template))
        binding.txtAffirmation.text = text
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnTrackOrder.setOnClickListener {
            val total = args.totalCost
            val order = args.order
            val action = OrderSuccessFragmentDirections.actionFragmentOrderSuccessToNavigationOrders()
            Navigation.findNavController(it).navigate(action)
        }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
