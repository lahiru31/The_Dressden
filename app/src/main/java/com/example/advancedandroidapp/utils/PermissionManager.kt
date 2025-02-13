package com.example.advancedandroidapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Permission groups
    val locationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    val cameraPermissions = arrayOf(
        Manifest.permission.CAMERA
    )

    val storagePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    val notificationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyArray()
    }

    val phonePermissions = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.SEND_SMS
    )

    fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(
        permissions: Array<String>,
        launcher: ActivityResultLauncher<Array<String>>,
        onResult: (Map<String, Boolean>) -> Unit
    ) {
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            launcher.launch(permissionsToRequest)
        } else {
            onResult(permissions.associateWith { true })
        }
    }

    fun shouldShowRationale(
        permissions: Array<String>,
        activity: androidx.fragment.app.FragmentActivity
    ): Boolean {
        return permissions.any {
            activity.shouldShowRequestPermissionRationale(it)
        }
    }

    // Location specific checks
    fun hasLocationPermissions(): Boolean = hasPermissions(locationPermissions)
    fun hasBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // Camera specific checks
    fun hasCameraPermission(): Boolean = hasPermissions(cameraPermissions)

    // Storage specific checks
    fun hasStoragePermissions(): Boolean = hasPermissions(storagePermissions)

    // Notification specific checks
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermissions(notificationPermissions)
        } else {
            true
        }
    }

    // Phone specific checks
    fun hasPhonePermissions(): Boolean = hasPermissions(phonePermissions)

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
    }
}

sealed class PermissionResult {
    object Granted : PermissionResult()
    object Denied : PermissionResult()
    object PermanentlyDenied : PermissionResult()
    object ShowRationale : PermissionResult()
}

data class PermissionState(
    val permission: String,
    val granted: Boolean,
    val shouldShowRationale: Boolean
)
