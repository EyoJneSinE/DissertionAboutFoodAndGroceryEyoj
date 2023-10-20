package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.ProductGridItemViewBinding

class ProductListAdapter(
    var onItemActionCallback: OnItemActionCallback? = null,
    var viewModel: HomeViewModel? = null
): ListAdapter<Food, ProductViewHolder>(ProductDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductGridItemViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onItemActionCallback, viewModel)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val food = getItem(position)
        holder.bindProduct(food)
    }

    class ProductDiffCallBack: DiffUtil.ItemCallback<Food>() {

        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.foodId == newItem.foodId
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
}
