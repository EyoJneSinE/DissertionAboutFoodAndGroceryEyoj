package com.eniskaner.dissertionaboutfoodandgroceryeyoj.presentation.home

import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.local.entity.FavoriteEntity
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.data.remote.entity.Food

interface OnItemActionCallback {

    fun onFavoriteClicked(product: FavoriteEntity)

    fun onCartClicked(product: Food, quantity: Int)
}