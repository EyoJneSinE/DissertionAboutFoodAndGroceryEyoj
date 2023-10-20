package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.ProductRowItemViewBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.OnItemActionCallback

class FavoriteProductsListAdapter(
    var onItemActionCallback: OnItemActionCallback? = null
): ListAdapter<FavoriteEntity, FavoriteProductViewHolder>(ProductDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteProductViewHolder {
        val binding = ProductRowItemViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteProductViewHolder(binding, onItemActionCallback)
    }

    override fun onBindViewHolder(holder: FavoriteProductViewHolder, position: Int) {
        val food = getItem(position)
        holder.bindProduct(food)
    }

    class ProductDiffCallBack: DiffUtil.ItemCallback<FavoriteEntity>() {

        override fun areItemsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
            return oldItem.favoriteFood == newItem.favoriteFood
        }

        override fun areContentsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
            return oldItem == newItem
        }
    }
}
