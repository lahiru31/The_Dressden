package com.example.advancedandroidapp.utils

object Constants {
    // API Constants
    const val BASE_URL = "https://your-api-domain.com/api/"
    const val API_TIMEOUT = 30L // seconds
    const val API_CACHE_SIZE = 10L * 1024 * 1024 // 10 MB
    const val API_VERSION = "v1"

    // Firebase Constants
    const val FIREBASE_COLLECTION_USERS = "users"
    const val FIREBASE_COLLECTION_LOCATIONS = "locations"
    const val FIREBASE_COLLECTION_PROFILES = "profiles"
    const val FIREBASE_STORAGE_PROFILE_IMAGES = "profile_images"
    const val FIREBASE_STORAGE_LOCATION_IMAGES = "location_images"

    // Database Constants
    const val DATABASE_NAME = "advanced_android_app.db"
    const val DATABASE_VERSION = 1

    // Location Constants
    const val LOCATION_UPDATE_INTERVAL = 10000L // 10 seconds
    const val LOCATION_FASTEST_INTERVAL = 5000L // 5 seconds
    const val LOCATION_DISPLACEMENT = 10f // 10 meters
    const val DEFAULT_ZOOM_LEVEL = 15f
    const val SEARCH_RADIUS = 5000 // 5 kilometers

    // Permission Request Codes
    const val PERMISSION_LOCATION_REQUEST_CODE = 1001
    const val PERMISSION_CAMERA_REQUEST_CODE = 1002
    const val PERMISSION_STORAGE_REQUEST_CODE = 1003
    const val PERMISSION_NOTIFICATION_REQUEST_CODE = 1004

    // Activity Request Codes
    const val REQUEST_IMAGE_CAPTURE = 2001
    const val REQUEST_IMAGE_PICK = 2002
    const val REQUEST_GOOGLE_SIGN_IN = 2003
    const val REQUEST_LOCATION_SETTINGS = 2004

    // Notification Channel IDs
    const val CHANNEL_ID_DEFAULT = "default_channel"
    const val CHANNEL_ID_LOCATION = "location_channel"
    const val CHANNEL_ID_CHAT = "chat_channel"

    // Shared Preferences Keys
    const val PREF_USER_TOKEN = "user_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_DARK_MODE = "dark_mode"
    const val PREF_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val PREF_LOCATION_TRACKING = "location_tracking"
    const val PREF_LANGUAGE = "language"
    const val PREF_FIRST_LAUNCH = "first_launch"

    // Cache Constants
    const val CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 hours
    const val MAX_CACHE_SIZE = 50 * 1024 * 1024L // 50 MB
    const val MAX_FILE_SIZE = 10 * 1024 * 1024L // 10 MB

    // UI Constants
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_USERNAME_LENGTH = 30
    const val MAX_BIO_LENGTH = 150
    const val DEBOUNCE_TIME = 300L // milliseconds
    const val ANIMATION_DURATION = 300L // milliseconds
    const val IMAGE_COMPRESSION_QUALITY = 80
    const val MAX_IMAGE_DIMENSION = 1920
    const val THUMBNAIL_SIZE = 200

    // Error Messages
    object ErrorMessages {
        const val NO_INTERNET = "No internet connection"
        const val SERVER_ERROR = "Server error occurred"
        const val TIMEOUT_ERROR = "Request timed out"
        const val UNKNOWN_ERROR = "An unknown error occurred"
        const val INVALID_CREDENTIALS = "Invalid email or password"
        const val WEAK_PASSWORD = "Password should be at least 6 characters"
        const val INVALID_EMAIL = "Invalid email format"
        const val EMAIL_ALREADY_EXISTS = "Email already exists"
        const val USER_NOT_FOUND = "User not found"
        const val LOCATION_PERMISSION_DENIED = "Location permission required"
        const val CAMERA_PERMISSION_DENIED = "Camera permission required"
        const val STORAGE_PERMISSION_DENIED = "Storage permission required"
    }

    // Success Messages
    object SuccessMessages {
        const val PROFILE_UPDATED = "Profile updated successfully"
        const val LOCATION_SAVED = "Location saved successfully"
        const val IMAGE_UPLOADED = "Image uploaded successfully"
        const val PASSWORD_RESET = "Password reset email sent"
        const val SIGN_IN_SUCCESS = "Signed in successfully"
        const val SIGN_UP_SUCCESS = "Account created successfully"
        const val SIGN_OUT_SUCCESS = "Signed out successfully"
    }

    // Date Formats
    object DateFormats {
        const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
        const val DISPLAY_TIME_FORMAT = "HH:mm"
        const val DISPLAY_DATE_TIME_FORMAT = "MMM dd, yyyy HH:mm"
        const val FILE_NAME_DATE_FORMAT = "yyyyMMdd_HHmmss"
    }
}
