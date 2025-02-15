package com.example.advancedandroidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Date

// User related models
data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("created_at")
    val createdAt: Date
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("full_name")
    @ColumnInfo(name = "full_name")
    val fullName: String,
    @SerializedName("avatar_url")
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("phone_number")
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String?,
    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)

// Location related models
@Entity(tableName = "locations")
data class Location(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("address")
    val address: String?,
    @SerializedName("category")
    val category: String,
    @SerializedName("rating")
    val rating: Float?,
    @SerializedName("photos")
    @TypeConverters(ListConverter::class)
    val photos: List<String>?,
    @SerializedName("created_by")
    @ColumnInfo(name = "created_by")
    val createdBy: String,
    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)

// Settings related models
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

// Media related models
data class MediaUploadResponse(
    @SerializedName("url")
    val url: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("size")
    val size: Long,
    @SerializedName("uploaded_at")
    val uploadedAt: Date
)

// API Response wrapper
sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}

// Local database entities
@Entity(tableName = "cached_locations")
data class CachedLocation(
    @PrimaryKey
    val id: String,
    val location: Location,
    val lastUpdated: Date
)

@Entity(tableName = "offline_actions")
data class OfflineAction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // CREATE, UPDATE, DELETE
    val entityType: String, // Location, UserProfile, etc.
    val entityId: String,
    val data: String, // JSON serialized data
    val timestamp: Date,
    val synced: Boolean = false
)
