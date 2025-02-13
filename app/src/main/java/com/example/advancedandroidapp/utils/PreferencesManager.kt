package com.example.advancedandroidapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.example.advancedandroidapp.data.models.User
import com.example.advancedandroidapp.data.models.UserSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    // User Authentication
    var authToken: String?
        get() = prefs.getString(KEY_AUTH_TOKEN, null)
        set(value) = prefs.edit { putString(KEY_AUTH_TOKEN, value) }

    var currentUser: User?
        get() = prefs.getString(KEY_CURRENT_USER, null)?.let {
            try {
                gson.fromJson(it, User::class.java)
            } catch (e: Exception) {
                null
            }
        }
        set(value) = prefs.edit {
            putString(KEY_CURRENT_USER, value?.let { gson.toJson(it) })
        }

    // User Settings
    var userSettings: UserSettings?
        get() = prefs.getString(KEY_USER_SETTINGS, null)?.let {
            try {
                gson.fromJson(it, UserSettings::class.java)
            } catch (e: Exception) {
                null
            }
        }
        set(value) = prefs.edit {
            putString(KEY_USER_SETTINGS, value?.let { gson.toJson(it) })
        }

    // App Settings
    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit { putBoolean(KEY_DARK_MODE, value) }

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit { putString(KEY_LANGUAGE, value) }

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_NOTIFICATIONS_ENABLED, value) }

    var locationTrackingEnabled: Boolean
        get() = prefs.getBoolean(KEY_LOCATION_TRACKING_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_LOCATION_TRACKING_ENABLED, value) }

    // Search History
    var searchHistory: Set<String>
        get() = prefs.getStringSet(KEY_SEARCH_HISTORY, emptySet()) ?: emptySet()
        set(value) = prefs.edit { putStringSet(KEY_SEARCH_HISTORY, value) }

    fun addToSearchHistory(query: String) {
        val history = searchHistory.toMutableSet()
        history.add(query)
        if (history.size > MAX_SEARCH_HISTORY_SIZE) {
            history.remove(history.first())
        }
        searchHistory = history
    }

    // Cache Management
    var lastSyncTimestamp: Long
        get() = prefs.getLong(KEY_LAST_SYNC, 0)
        set(value) = prefs.edit { putLong(KEY_LAST_SYNC, value) }

    var cacheExpirationTime: Long
        get() = prefs.getLong(KEY_CACHE_EXPIRATION, DEFAULT_CACHE_EXPIRATION)
        set(value) = prefs.edit { putLong(KEY_CACHE_EXPIRATION, value) }

    // First Time Launch
    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit { putBoolean(KEY_FIRST_LAUNCH, value) }

    // App Version
    var lastKnownVersion: Int
        get() = prefs.getInt(KEY_LAST_KNOWN_VERSION, 0)
        set(value) = prefs.edit { putInt(KEY_LAST_KNOWN_VERSION, value) }

    fun clearAuthData() {
        prefs.edit {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_CURRENT_USER)
        }
    }

    fun clearAllData() {
        prefs.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "advanced_android_app_prefs"
        
        // Keys
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_USER_SETTINGS = "user_settings"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_LOCATION_TRACKING_ENABLED = "location_tracking_enabled"
        private const val KEY_SEARCH_HISTORY = "search_history"
        private const val KEY_LAST_SYNC = "last_sync"
        private const val KEY_CACHE_EXPIRATION = "cache_expiration"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LAST_KNOWN_VERSION = "last_known_version"

        // Constants
        private const val MAX_SEARCH_HISTORY_SIZE = 10
        private const val DEFAULT_CACHE_EXPIRATION = 24 * 60 * 60 * 1000L // 24 hours
    }
}
