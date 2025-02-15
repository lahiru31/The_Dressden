package com.example.advancedandroidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val userId: String,
    
    val theme: String = "system",
    val notificationsEnabled: Boolean = true,
    val locationTrackingEnabled: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
