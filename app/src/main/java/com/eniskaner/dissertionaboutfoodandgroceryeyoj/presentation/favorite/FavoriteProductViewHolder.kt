package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.favorite

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.ProductRowItemViewBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home.OnItemActionCallback
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Constants
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.load

class FavoriteProductViewHolder(
    private val productBinding: ProductRowItemViewBinding,
    var onItemActionCallback: OnItemActionCallback? = null
): RecyclerView.ViewHolder(productBinding.root) {

    fun bindProduct(item: FavoriteEntity) {
        with(productBinding) {
            foodName.text = item.favoriteFood.foodName
            ivImage.load(Constants.POSTER_URL.plus(item.favoriteFood.foodPoster))
            foodPrice.text = this.root.context.getString(R.string.favorite_food_price, item.favoriteFood.foodPrice)
            updateFavoriteButtonColors(item.favoriteFood.foodName.isNotEmpty())
            btnFavorite.setOnClickListener {
                if (item.favoriteFood.foodName.isEmpty())
                    updateFavoriteButtonColors(true)
                onItemActionCallback?.onFavoriteClicked(item)
            }

            btnCart.setOnClickListener {
                onItemActionCallback?.onCartClicked(item.favoriteFood, 1)
            }
        }
    }

    private fun updateFavoriteButtonColors(favorite: Boolean) {
        with(productBinding) {
            if (favorite) {
                btnFavorite.setIconTintResource(R.color.eyoj_green)
                btnFavorite.backgroundTintList =
                    ContextCompat.getColorStateList(btnFavorite.context, R.color.eyoj_green_light)
            } else {
                btnFavorite.setIconTintResource(R.color.eyoj_gray_200)
                btnFavorite.backgroundTintList =
                    ContextCompat.getColorStateList(btnFavorite.context, R.color.eyoj_gray_100)
            }
        }
    }
}
