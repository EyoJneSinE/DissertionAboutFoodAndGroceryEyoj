package com.eniskaner.dissertionaboutfoodandgroceryeyoj.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eniskaner.dissertionaboutfoodandgroceryeyoj.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

internal fun TextView.setLoginToPerformActionPrompt(
    promptStringResId: Int,
    loginTextModifier: TextPaint.() -> Unit = {},
    onLogin: () -> Unit
) {
    val loginTextSpan = object : ClickableSpan() {
        override fun onClick(view: View) {
            onLogin()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.apply(loginTextModifier)
        }
    }
    val loginText = context.getString(R.string.label_login)
    val promptText = context.getString(promptStringResId)
    val loginStart = 0
    val targetSpannableString = SpannableString("$loginText $promptText").apply {
        setSpan(loginTextSpan, loginStart, loginText.length, Spanned.SPAN_POINT_MARK)
    }
    text = targetSpannableString
    movementMethod = LinkMovementMethod.getInstance()
}

inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

class SwipeRefreshHandler(
    private val scope: CoroutineScope,
    swipeRefreshLayout: SwipeRefreshLayout,
    private val action: suspend () -> Unit
) : SwipeRefreshLayout.OnRefreshListener, DefaultLifecycleObserver {
    private val layoutRef = WeakReference(swipeRefreshLayout)
    private var refreshJob: Job? = null
    override fun onRefresh() {
        refreshJob = scope.launch { action() }
        refreshJob?.invokeOnCompletion {
            layoutRef.get()?.isRefreshing = false
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        refreshJob?.cancel()
        layoutRef.get()?.isRefreshing = false
    }
}