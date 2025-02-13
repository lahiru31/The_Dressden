package com.example.advancedandroidapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Context Extensions
fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.getBitmapDescriptor(@DrawableRes vectorResId: Int): BitmapDescriptor? {
    val vectorDrawable = ContextCompat.getDrawable(this, vectorResId) ?: return null
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth,
        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

// Fragment Extensions
fun Fragment.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: Pair<String, View.OnClickListener>? = null
) {
    view?.let { view ->
        Snackbar.make(view, message, duration).apply {
            action?.let { (text, listener) ->
                setAction(text, listener)
            }
        }.show()
    }
}

fun Fragment.collectLifecycleFlow(flow: Flow<*>, collect: suspend (Any) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { collect(it) }
        }
    }
}

// ImageView Extensions
fun ImageView.loadImage(
    url: String?,
    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null,
    crossFade: Boolean = true
) {
    Glide.with(this)
        .load(url)
        .apply {
            placeholder?.let { placeholder(it) }
            error?.let { error(it) }
            if (crossFade) transition(DrawableTransitionOptions.withCrossFade())
        }
        .into(this)
}

fun ImageView.loadCircleImage(
    url: String?,
    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null
) {
    Glide.with(this)
        .load(url)
        .apply {
            placeholder?.let { placeholder(it) }
            error?.let { error(it) }
            circleCrop()
        }
        .into(this)
}

// View Extensions
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.enable() {
    isEnabled = true
    alpha = 1f
}

fun View.disable() {
    isEnabled = false
    alpha = 0.5f
}

// Date Extensions
fun Date.formatToString(pattern: String = "dd MMM yyyy, HH:mm"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

fun String.toDate(pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): Date? {
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

// String Extensions
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { it.capitalize(Locale.getDefault()) }
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return length >= 6 // Add more password validation rules as needed
}

// Number Extensions
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (kotlin.math.round(this * multiplier) / multiplier).toFloat()
}

// Coroutine Extensions
fun CoroutineScope.launchCatching(
    onError: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
) = launch {
    try {
        block()
    } catch (e: Exception) {
        onError(e)
    }
}

// Result Extensions
suspend fun <T> kotlin.Result<T>.onSuccessWithData(
    block: suspend (T) -> Unit
): kotlin.Result<T> {
    if (isSuccess) {
        getOrNull()?.let { block(it) }
    }
    return this
}

fun <T> kotlin.Result<T>.getOrDefault(defaultValue: T): T {
    return getOrNull() ?: defaultValue
}
