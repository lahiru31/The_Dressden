package com.example.advancedandroidapp.utils

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

object UiUtils {
    // Animation helpers
    fun fadeIn(view: View, duration: Long = 300) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    fun fadeOut(view: View, duration: Long = 300) {
        ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            withEndAction { view.visibility = View.GONE }
            start()
        }
    }

    fun slideIn(view: View, @AnimRes animResId: Int) {
        val animation = AnimationUtils.loadAnimation(view.context, animResId)
        view.visibility = View.VISIBLE
        view.startAnimation(animation)
    }

    fun slideOut(view: View, @AnimRes animResId: Int) {
        val animation = AnimationUtils.loadAnimation(view.context, animResId)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        view.startAnimation(animation)
    }

    // Image loading helpers
    fun loadImage(
        imageView: ImageView,
        url: String?,
        @DrawableRes placeholder: Int? = null,
        @DrawableRes error: Int? = null,
        onSuccess: (() -> Unit)? = null,
        onError: (() -> Unit)? = null
    ) {
        Glide.with(imageView.context)
            .load(url)
            .apply {
                placeholder?.let { placeholder(it) }
                error?.let { error(it) }
            }
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onError?.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onSuccess?.invoke()
                    return false
                }
            })
            .into(imageView)
    }

    // Window insets helpers
    fun View.applySystemWindowInsets(
        applyTop: Boolean = false,
        applyBottom: Boolean = false
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                if (applyTop) topMargin = insets.top
                if (applyBottom) bottomMargin = insets.bottom
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    // UI state helpers
    fun showLoading(loadingView: View, contentView: View? = null) {
        loadingView.visibility = View.VISIBLE
        contentView?.visibility = View.GONE
    }

    fun hideLoading(loadingView: View, contentView: View? = null) {
        loadingView.visibility = View.GONE
        contentView?.visibility = View.VISIBLE
    }

    fun showError(
        view: View,
        message: String,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
            if (actionText != null && action != null) {
                setAction(actionText) { action.invoke() }
            }
        }.show()
    }

    // Text formatting helpers
    fun formatDate(date: Date, pattern: String = "MMM dd, yyyy"): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }

    fun formatDistance(meters: Float): String {
        return when {
            meters < 1000 -> "${meters.toInt()}m"
            else -> String.format("%.1fkm", meters / 1000)
        }
    }

    fun formatDuration(minutes: Int): String {
        return when {
            minutes < 60 -> "${minutes}min"
            else -> "${minutes / 60}h ${minutes % 60}min"
        }
    }

    // Color helpers
    fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }

    fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
        setTextColor(context.getColorCompat(colorRes))
    }

    // Dimension helpers
    fun Context.dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    fun Context.pxToDp(px: Float): Float {
        return px / resources.displayMetrics.density
    }

    // View state persistence
    fun saveViewState(view: View, key: String) {
        view.tag = key
    }

    fun restoreViewState(view: View, key: String): Any? {
        return if (view.tag == key) view.tag else null
    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

interface UiStateHandler<T> {
    fun onLoading()
    fun onSuccess(data: T)
    fun onError(message: String)
}
