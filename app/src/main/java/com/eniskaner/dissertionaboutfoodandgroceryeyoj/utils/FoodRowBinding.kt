package com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils.Constants.POSTER_URL

class FoodRowBinding {

    companion object {

        @JvmStatic
        @BindingAdapter("app:load_image")
        fun loadImage(imageView: ImageView, foodImageName: String){
            imageView.load(POSTER_URL + foodImageName)
        }

        @JvmStatic
        @BindingAdapter("app:favorite_icon_color")
        fun favoriteIconColor(imageView: ImageView, isFavorite: Boolean){
            when(isFavorite){
                true -> imageView.setColorFilter(ContextCompat.getColor(imageView.context, R.color.eyoj_green))
                false -> imageView.setColorFilter(ContextCompat.getColor(imageView.context, R.color.eyoj_gray_100))
            }
        }
    }
}
