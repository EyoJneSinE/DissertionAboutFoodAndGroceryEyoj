package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.cart

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.Order
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.OrderLine
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.OrderStatus
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.FragmentCartBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.BadgeBox
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.orders.OrderViewModel
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Resource
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.SwipeToDeleteCallback
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.launchAndRepeatWithViewLifecycle
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime

@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var badgeBoxInterface: BadgeBox
    private var selectedCategoryIndex = 0
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CartViewModel
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel : CartViewModel by viewModels()
        val tempViewModel2 : OrderViewModel by viewModels()
        viewModel = tempViewModel
        orderViewModel = tempViewModel2
        auth = Firebase.auth
        viewModel.getUserCart(auth.currentUser?.email.toString() ?: "eyoj")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categories = resources.getStringArray(R.array.category_names).toList()
        launchAndRepeatWithViewLifecycle {
            launch { cratedChip(categories) }
            launch { observeCart() }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeCart() {
        viewModel.cart.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resource.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                }

                is Resource.Success -> {

                    binding.progressCircular.visibility = View.GONE
                    resources.data?.let { foodInCarts ->
                        sendNumberToActivity(foodInCarts.size)
                        val adapter = CartItemListAdapter(requireContext(), foodInCarts,viewModel)
                        binding.recyclerViewHor.apply {
                            binding.cartAdapter = adapter
                            val swipeDel = object : SwipeToDeleteCallback(requireContext()){
                                override fun onSwiped(
                                    viewHolder: RecyclerView.ViewHolder,
                                    direction: Int
                                ) {
                                    selectChip(0)
                                    adapter.deleteItem(viewHolder.layoutPosition)

                                }
                            }
                            val touchHelper = ItemTouchHelper(swipeDel)
                            touchHelper.attachToRecyclerView(this)
                        }
                        var total = 0
                        for (foodInCart in foodInCarts) {
                            val q = foodInCart.cartFoodAmount
                            val p = foodInCart.cartFoodPrice
                            total += q * p
                        }
                        val randomOrderId = (1_000_000..9_000_000).random()
                        val orderLines = foodInCarts.map { foodInCart ->
                            OrderLine(
                                inventoryId = foodInCart.cartFoodId,
                                quantity = foodInCart.cartFoodAmount
                            )
                        }
                        val selectedOrder = Order(
                            createdAt = LocalDateTime.now(),
                            orderId = randomOrderId.toString(),
                            orderLines = orderLines,
                            status = OrderStatus.ACCEPTED,
                            total = total.toFloat()
                        )
                        binding.orderSendButton.setOnClickListener { view ->
                            viewLifecycleOwner.lifecycleScope.launch {
                                binding.recyclerViewHor.visibility = View.GONE
                                binding.lottieAnimationForTakeOrder.visibility = View.VISIBLE

                                delay(5000)

                                orderViewModel.addOrder(selectedOrder)
                                for (foodInCart in foodInCarts)
                                    auth.currentUser?.email.let{
                                        if (it != null) {
                                            viewModel.deleteCartItem(foodInCart.cartFoodId, username = it.toString())
                                        }
                                    }
                                binding.recyclerViewHor.visibility = View.VISIBLE
                                binding.lottieAnimationForTakeOrder.visibility = View.GONE

                                orderViewModel.updateOrdersStatus(requireContext())

                                val action = CartFragmentDirections.actionNavigationCartToFragmentOrderSuccess(auth.currentUser?.email.toString() ?: "eyoj", total, selectedOrder)
                                Navigation.findNavController(view).navigate(action)
                            }
                        }
                    }
                }
                is Resource.Failure -> {
                    binding.progressCircular.visibility = View.GONE
                    print(resources.error)
                }
                is Resource.Empty -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.recyclerViewHor.adapter = null
                    sendNumberToActivity(0)
                }
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BadgeBox) {
            badgeBoxInterface = context
        } else {
            Timber.tag("Context").e("$context must implement MyInterface")
        }
    }

    private fun cratedChip(categories: List<String>){
        val margin = resources.getDimensionPixelSize(R.dimen.margin_normal)
        val lastMargin = resources.getDimensionPixelSize(R.dimen.margin_normal)
        val marginEnd = resources.getDimensionPixelSize(R.dimen.margin_none)

        for(i in categories.indices){
            val chip = layoutInflater.inflate(R.layout.chip_item, null, false) as Chip
            chip.text = categories[i]
            chip.isClickable = true
            chip.isChecked = i == selectedCategoryIndex

            chip.setOnClickListener{
                selectChip(i)
            }

            binding.chipGroup.addView(chip)

            if(selectedCategoryIndex == 0){
                selectChip(0)
            }

            val params = chip.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = margin
            params.marginEnd = if (i == categories.size - 1) lastMargin else marginEnd
            chip.layoutParams = params
        }
    }
    private fun selectChip(index: Int) {
        val previousChip = binding.chipGroup.getChildAt(selectedCategoryIndex) as Chip
        previousChip.isChecked = false
        previousChip.setChipBackgroundColorResource(R.color.eyoj_orange_light)

        val selectedChip = binding.chipGroup.getChildAt(index) as Chip
        selectedChip.isChecked = true
        selectedChip.setChipBackgroundColorResource(R.color.eyoj_orange)

        viewModel.sortWithDifferentTypes(selectedChip.text.toString(),auth.currentUser?.email.toString() ?: "eyoj", resources)
        selectedCategoryIndex = index
    }

    private fun sendNumberToActivity(number: Int) {
        badgeBoxInterface.onNumberCart(number)
    }

    companion object {
        /** Result Key to notify cart items changed */
        const val CART_CHANGED_RESULT = "com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.cart.cart-changed-result"
    }
}
