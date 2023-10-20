package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentOrdersBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.FadingSnackbar
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.UiState
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.launchAndRepeatWithViewLifecycle
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.setLoginToPerformActionPrompt
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrdersFragment : Fragment(), OrderHistoryItemViewHolder.OnDetailsClickedListener {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private lateinit var auth: FirebaseAuth
    private val orderHistoryAdapter = OrderHistoryAdapter()
    private var snackbar: FadingSnackbar? = null

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
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        binding.rvOrdersHistory.adapter = orderHistoryAdapter
        binding.txtPromptLogin.setLoginToPerformActionPrompt(
            promptStringResId = R.string.info_to_view_order_history,
            loginTextModifier = { textSize = 48f },
            onLogin = {  }
        )
        snackbar = binding.snackbar
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeOrderHistoryList(orderHistoryAdapter)
        launchAndRepeatWithViewLifecycle {
            launch { updateVisibilityForStateChanges() }
        }
        launchAndRepeatWithViewLifecycle {
            launch {
                binding.swipeRefreshOrdersHistory.setOnRefreshListener {
                    viewModel.refreshOrderList()
                }
            }
        }
    }

    private suspend fun updateVisibilityForStateChanges() {
        var isLoggedIn = viewModel.isLoggedIn
        val user = auth.currentUser?.email
        user?.let {
            isLoggedIn = MutableStateFlow(true)
        }
        val order = viewModel.newOrderList
        orderHistoryAdapter.submitList(order)
        combine(isLoggedIn, viewModel.userOrdersState, ::Pair)
            .collect { (loggedIn, ordersState) ->
                binding.swipeRefreshOrdersHistory.isRefreshing =
                    loggedIn && ordersState is UiState.Loading
                val ordersListVisibleState = ordersState is UiState.Success
                if (ordersState is UiState.Success) {
                    orderHistoryAdapter.submitList(ordersState.value)
                    binding.txtOrderHistoryEmpty.isVisible = ordersState.value.isEmpty()
                    binding.rvOrdersHistory.isVisible = ordersListVisibleState
                            && loggedIn
                            && ordersState.value.isNotEmpty()
                }
                binding.txtPromptLogin.isVisible = !loggedIn
                binding.swipeRefreshOrdersHistory.isVisible = loggedIn
            }
    }

    private fun observeOrderHistoryList(orderHistoryAdapter: OrderHistoryAdapter) {
        launchAndRepeatWithViewLifecycle {
            launch { viewModel.orderList.collect { orders ->
                orderHistoryAdapter.submitList(orders)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        orderHistoryAdapter.onDetailsClickedListener = this
    }

    override fun onPause() {
        orderHistoryAdapter.onDetailsClickedListener = null
        super.onPause()
    }

    override fun onDestroyView() {
        _binding = null
        snackbar = null
        super.onDestroyView()
    }

    override fun onDetailsClicked(orderId: String) {
        lifecycleScope.launch {
            viewModel.updateSelectedOrderById(orderId) { orderDetailsFound->
                if (orderDetailsFound) showBottomSheetWithOrderDetails(orderId)
                else snackbar?.show(R.string.error_order_details_not_found)
            }
        }
    }

    private fun showBottomSheetWithOrderDetails(ordersId: String) {
        val action = OrdersFragmentDirections.actionNavigationOrdersToBottomSheetOrderDetails(ordersId = ordersId)
        findNavController().navigate(action)
    }

    companion object {
        const val ORDER_DATE_FORMAT = "EEE, dd LLL, yyyy"
    }
}
