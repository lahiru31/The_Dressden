package com.example.advancedandroidapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.advancedandroidapp.utils.NotificationManager
import com.example.advancedandroidapp.utils.PreferencesManager
import com.example.advancedandroidapp.utils.WorkManagerUtils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var preferencesManager: PreferencesManager
    
    @Inject
    lateinit var notificationManager: NotificationManager
    
    @Inject
    lateinit var workManagerUtils: WorkManagerUtils

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.d("Boot completed, initializing app services")
            
            try {
                // Restore app state after device reboot
                if (preferencesManager.isUserLoggedIn()) {
                    // Schedule background work
                    workManagerUtils.schedulePeriodicSync()
                    
                    // Initialize location tracking if enabled
                    if (preferencesManager.isLocationTrackingEnabled()) {
                        workManagerUtils.startLocationTracking()
                    }
                    
                    // Restore notification settings
                    notificationManager.createNotificationChannels()
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize app services after boot")
            }
        }
    }
}
