package com.example.advancedandroidapp.data.models

import androidx.room.*
import com.example.advancedandroidapp.data.local.converters.DateConverter
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "created_at")
    @TypeConverters(DateConverter::class)
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    @TypeConverters(DateConverter::class)
    val updatedAt: Date = Date()
)

@Entity(
    tableName = "user_profiles",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserProfile(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    val username: String,
    
    @ColumnInfo(name = "full_name")
    val fullName: String,
    
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,
    
    val bio: String?,
    
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String?,
    
    @ColumnInfo(name = "updated_at")
    @TypeConverters(DateConverter::class)
    val updatedAt: Date = Date()
)

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
