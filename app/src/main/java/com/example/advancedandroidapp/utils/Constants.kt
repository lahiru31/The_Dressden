package com.example.advancedandroidapp.utils

import java.util.concurrent.TimeUnit

object Constants {
    // API Constants
    const val BASE_URL = "https://your-api-domain.com/api/" // Updated to match the API service
    const val API_TIMEOUT = 30L // seconds
    const val API_VERSION = "v1"
    
    // Network Constants
    const val RETRY_MAX_ATTEMPTS = 3
    const val RETRY_INITIAL_DELAY = 100L // milliseconds
    const val RETRY_MAX_DELAY = 1000L // milliseconds
    const val RETRY_FACTOR = 2.0
    
    // Cache Constants
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB
    const val CACHE_MAX_AGE = TimeUnit.DAYS.toSeconds(7) // 7 days
    const val CACHE_MAX_STALE = TimeUnit.DAYS.toSeconds(30) // 30 days
    
    // Database Constants
    const val DATABASE_NAME = "app_database"
    const val DATABASE_VERSION = 1
    
    // Location Constants
    const val LOCATION_UPDATE_INTERVAL = 10000L // 10 seconds
    const val LOCATION_FASTEST_INTERVAL = 5000L // 5 seconds
    const val LOCATION_MAX_WAIT_TIME = 60000L // 1 minute
    
    // Work Manager Constants
    const val SYNC_WORK_NAME = "sync_work"
    const val SYNC_INTERVAL = 15L // minutes
    const val LOCATION_WORK_NAME = "location_work"
    
    // Notification Constants
    object NotificationChannels {
        const val DEFAULT_CHANNEL_ID = "default_channel"
        const val DEFAULT_CHANNEL_NAME = "Default"
        const val LOCATION_CHANNEL_ID = "location_channel"
        const val LOCATION_CHANNEL_NAME = "Location Updates"
        const val SYNC_CHANNEL_ID = "sync_channel"
        const val SYNC_CHANNEL_NAME = "Sync Updates"
    }
    
    // Shared Preferences Constants
    const val PREF_FILE_NAME = "app_preferences"
    const val PREF_USER_TOKEN = "user_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    const val PREF_LOCATION_TRACKING = "location_tracking_enabled"
    const val PREF_DARK_MODE = "dark_mode_enabled"
    const val PREF_NOTIFICATIONS = "notifications_enabled"
    const val PREF_LANGUAGE = "app_language"
    const val PREF_LAST_SYNC = "last_sync_timestamp"
    
    // Media Constants
    const val MAX_IMAGE_SIZE = 1024 * 1024 // 1 MB
    const val IMAGE_QUALITY = 80 // JPEG quality
    const val MAX_VIDEO_DURATION = 60L // seconds
    
    // Error Messages
    object ErrorMessages {
        const val NO_INTERNET = "No internet connection available"
        const val SERVER_ERROR = "Server error occurred"
        const val TIMEOUT_ERROR = "Request timed out"
        const val UNKNOWN_ERROR = "An unknown error occurred"
        const val AUTH_ERROR = "Authentication failed"
        const val LOCATION_PERMISSION_DENIED = "Location permission is required"
        const val MEDIA_PERMISSION_DENIED = "Media permission is required"
        const val NOTIFICATION_PERMISSION_DENIED = "Notification permission is required"
    }
}
