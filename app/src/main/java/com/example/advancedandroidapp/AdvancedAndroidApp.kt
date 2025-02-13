package com.example.advancedandroidapp

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.advancedandroidapp.utils.NotificationManager
import com.example.advancedandroidapp.utils.WorkManagerUtils
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AdvancedAndroidApp : Application(), Configuration.Provider {
    
    @Inject
    lateinit var notificationManager: NotificationManager
    
    @Inject
    lateinit var workManagerUtils: WorkManagerUtils

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Initialize WorkManager
        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
                .build()
        )

        // Create notification channels
        notificationManager.createNotificationChannels()

        // Initialize background work if needed
        initializeBackgroundWork()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .build()
    }

    private fun initializeBackgroundWork() {
        try {
            // Schedule periodic sync work
            workManagerUtils.schedulePeriodicSync()
            
            // Initialize other background tasks as needed
            Timber.d("Background work initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize background work")
        }
    }

    companion object {
        init {
            // Enable StrictMode in debug builds
            if (BuildConfig.DEBUG) {
                android.os.StrictMode.setThreadPolicy(
                    android.os.StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build()
                )
                
                android.os.StrictMode.setVmPolicy(
                    android.os.StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build()
                )
            }
        }
    }
}
