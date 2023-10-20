package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.cart

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.FoodInCart
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.RowItemsCartBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Constants.POSTER_URL
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.load

class CartItemListAdapter(
    var mContext: Context,
    var cartList: List<FoodInCart>,
    var viewModel: CartViewModel
) : RecyclerView.Adapter<CartItemListAdapter.CartItemViewHolder>() {

    private var listData: MutableList<FoodInCart> = cartList as MutableList<FoodInCart>

    class CartItemViewHolder(var binding: RowItemsCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(foodInCart: FoodInCart)  {
            binding.foodInCart = foodInCart
            binding.imageFood.load(POSTER_URL + foodInCart.cartFoodPoster)
            binding.cardViewCart.setOnClickListener {
                val action = CartFragmentDirections.actionNavigationCartToFoodDetailFragment(foodInCart)
                Navigation.findNavController(it).navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view: RowItemsCartBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.row_items_cart,
            parent,
            false
        )
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) = with(holder) {
        val cartItem = listData[position]
        bind(cartItem)
    }

    override fun getItemCount(): Int = listData.size

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(index:Int){
        viewModel.deleteCartItem(foodId = cartList[index].cartFoodId,username = cartList[index].cartUserName ?: "eyoj")
        notifyDataSetChanged()
    }
}
