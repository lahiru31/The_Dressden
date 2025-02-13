package com.example.advancedandroidapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AdvancedAndroidApp : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "default_channel"
        lateinit var instance: AdvancedAndroidApp
            private set
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        
        // Create notification channel for Android O and above
        createNotificationChannel()
        
        // Initialize other app-wide dependencies here
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getFirebaseAuth(): FirebaseAuth = auth
}
