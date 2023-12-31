package com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils

import androidx.annotation.StringRes

sealed class UiState<out S> {
    object Idle : UiState<Nothing>()
    data class Success<T>(val value: T) : UiState<T>()
    object Error : UiState<Nothing>()
    data class Loading(@StringRes val msgResId: Int? = null) : UiState<Nothing>()
}

val UiState<*>.enableActionButton get() = this is UiState.Error || this is UiState.Idle
