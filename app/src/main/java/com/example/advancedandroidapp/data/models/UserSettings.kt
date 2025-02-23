package com.example.advancedandroidapp.data.models

import androidx.room.*

@Entity(
    tableName = "user_settings",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserSettings(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "notifications_enabled")
    val notificationsEnabled: Boolean = true,
    
    @ColumnInfo(name = "dark_mode_enabled")
    val darkModeEnabled: Boolean = false,
    
    val language: String = "en",
    
    @ColumnInfo(name = "location_tracking_enabled")
    val locationTrackingEnabled: Boolean = true,
    
    @ColumnInfo(name = "data_backup_enabled")
    val dataBackupEnabled: Boolean = true,
    
    @ColumnInfo(name = "last_sync_timestamp")
    val lastSyncTimestamp: Long? = null
)
