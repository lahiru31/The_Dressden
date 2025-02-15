package com.example.advancedandroidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val userId: String,
    
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val language: String = "en",
    val locationTrackingEnabled: Boolean = true,
    val dataBackupEnabled: Boolean = true,
    val lastSyncTimestamp: Long? = null
)
