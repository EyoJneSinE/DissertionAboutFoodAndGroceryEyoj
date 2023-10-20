package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteType
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.databinding.ProductGridItemViewBinding
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Constants.POSTER_URL
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.load
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProductViewHolder(
    private val productBinding: ProductGridItemViewBinding,
    var onItemActionCallback: OnItemActionCallback? = null,
    val viewModel: HomeViewModel? = null
): RecyclerView.ViewHolder(productBinding.root) {

    fun bindProduct(item: Food) {
        with(productBinding) {
            foodName.text = item.foodName
            ivImage.load(POSTER_URL.plus(item.foodPoster))
            foodPrice.text = this.root.context.getString(R.string.favorite_food_price, item.foodPrice)
            val selectedFoodList = viewModel?.favoriteFoodList?.value
            val isFavorite = selectedFoodList?.any { it.favoriteFood == item }
            if (isFavorite != null) {
                updateFavoriteButtonColors(isFavorite)
            }

            btnFavorite.setOnClickListener {
                if (isFavorite != null)
                if (!isFavorite)
                    updateFavoriteButtonColors(true)
                val newFavorite = FavoriteEntity(FavoriteType.WEEKLY,item, userName = Firebase.auth.currentUser?.email ?: "Eyoj")
                viewModel?.insertFavoriteFood(newFavorite, setOf(FavoriteType.WEEKLY))
                onItemActionCallback?.onFavoriteClicked(newFavorite)
            }

            btnCart.setOnClickListener {
                onItemActionCallback?.onCartClicked(item,1)
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
