package com.example.advancedandroidapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    private val context: Context
) {
    companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val MEDIA_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        val NOTIFICATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyArray()
        }
    }

    fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun registerForPermissionResult(
        fragment: Fragment,
        onResult: (Boolean) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            onResult(allGranted)
        }
    }

    fun shouldShowRationale(fragment: Fragment, permissions: Array<String>): Boolean {
        return permissions.any { permission ->
            fragment.shouldShowRequestPermissionRationale(permission)
        }
    }

    // Extension function to check specific permissions
    fun hasLocationPermissions() = hasPermissions(LOCATION_PERMISSIONS)
    fun hasMediaPermissions() = hasPermissions(MEDIA_PERMISSIONS)
    fun hasNotificationPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        hasPermissions(NOTIFICATION_PERMISSION)
    } else {
        true
    }
}

// Extension functions for Fragment
fun Fragment.requestLocationPermissions(
    permissionManager: PermissionManager,
    onResult: (Boolean) -> Unit
) {
    val launcher = permissionManager.registerForPermissionResult(this, onResult)
    when {
        permissionManager.hasLocationPermissions() -> onResult(true)
        permissionManager.shouldShowRationale(this, PermissionManager.LOCATION_PERMISSIONS) -> {
            // Show rationale dialog
            showPermissionRationaleDialog(
                "Location Permission",
                "Location permission is required to show your current location on the map.",
                onPositive = { launcher.launch(PermissionManager.LOCATION_PERMISSIONS) },
                onNegative = { onResult(false) }
            )
        }
        else -> launcher.launch(PermissionManager.LOCATION_PERMISSIONS)
    }
}

fun Fragment.requestMediaPermissions(
    permissionManager: PermissionManager,
    onResult: (Boolean) -> Unit
) {
    val launcher = permissionManager.registerForPermissionResult(this, onResult)
    when {
        permissionManager.hasMediaPermissions() -> onResult(true)
        permissionManager.shouldShowRationale(this, PermissionManager.MEDIA_PERMISSIONS) -> {
            showPermissionRationaleDialog(
                "Media Permission",
                "Media permission is required to access photos and videos.",
                onPositive = { launcher.launch(PermissionManager.MEDIA_PERMISSIONS) },
                onNegative = { onResult(false) }
            )
        }
        else -> launcher.launch(PermissionManager.MEDIA_PERMISSIONS)
    }
}

fun Fragment.requestNotificationPermission(
    permissionManager: PermissionManager,
    onResult: (Boolean) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = permissionManager.registerForPermissionResult(this, onResult)
        when {
            permissionManager.hasNotificationPermission() -> onResult(true)
            permissionManager.shouldShowRationale(this, PermissionManager.NOTIFICATION_PERMISSION) -> {
                showPermissionRationaleDialog(
                    "Notification Permission",
                    "Notification permission is required to receive important updates.",
                    onPositive = { launcher.launch(PermissionManager.NOTIFICATION_PERMISSION) },
                    onNegative = { onResult(false) }
                )
            }
            else -> launcher.launch(PermissionManager.NOTIFICATION_PERMISSION)
        }
    } else {
        onResult(true)
    }
}

private fun Fragment.showPermissionRationaleDialog(
    title: String,
    message: String,
    onPositive: () -> Unit,
    onNegative: () -> Unit
) {
    androidx.appcompat.app.AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Grant") { _, _ -> onPositive() }
        .setNegativeButton("Deny") { _, _ -> onNegative() }
        .show()
}
