package com.example.advancedandroidapp.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    fun logEvent(event: AnalyticsEvent) {
        val bundle = Bundle().apply {
            event.parameters.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                }
            }
        }
        firebaseAnalytics.logEvent(event.name, bundle)
    }

    fun setUserProperty(property: UserProperty) {
        firebaseAnalytics.setUserProperty(property.key, property.value)
    }

    fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
        crashlytics.setUserId(userId)
    }

    fun logError(throwable: Throwable, customAttributes: Map<String, String> = emptyMap()) {
        customAttributes.forEach { (key, value) ->
            crashlytics.setCustomKey(key, value)
        }
        crashlytics.recordException(throwable)
    }

    fun logError(message: String, customAttributes: Map<String, String> = emptyMap()) {
        customAttributes.forEach { (key, value) ->
            crashlytics.setCustomKey(key, value)
        }
        crashlytics.log(message)
    }
}

sealed class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any> = emptyMap()
) {
    // Authentication Events
    object SignUpStarted : AnalyticsEvent("sign_up_started")
    object SignUpCompleted : AnalyticsEvent("sign_up_completed")
    object SignUpFailed : AnalyticsEvent("sign_up_failed")
    object SignInStarted : AnalyticsEvent("sign_in_started")
    object SignInCompleted : AnalyticsEvent("sign_in_completed")
    object SignInFailed : AnalyticsEvent("sign_in_failed")
    object SignOutCompleted : AnalyticsEvent("sign_out_completed")
    
    // Profile Events
    data class ProfileUpdated(val fields: List<String>) : AnalyticsEvent(
        "profile_updated",
        mapOf("updated_fields" to fields.joinToString(","))
    )
    object ProfilePhotoUpdated : AnalyticsEvent("profile_photo_updated")
    
    // Location Events
    data class LocationAdded(val category: String) : AnalyticsEvent(
        "location_added",
        mapOf("category" to category)
    )
    data class LocationViewed(val locationId: String) : AnalyticsEvent(
        "location_viewed",
        mapOf("location_id" to locationId)
    )
    data class LocationShared(val locationId: String) : AnalyticsEvent(
        "location_shared",
        mapOf("location_id" to locationId)
    )
    
    // Search Events
    data class SearchPerformed(
        val query: String,
        val category: String?,
        val resultCount: Int
    ) : AnalyticsEvent(
        "search_performed",
        mapOf(
            "query" to query,
            "category" to (category ?: "all"),
            "result_count" to resultCount
        )
    )
    
    // Map Events
    data class MapRegionChanged(
        val latitude: Double,
        val longitude: Double,
        val zoomLevel: Float
    ) : AnalyticsEvent(
        "map_region_changed",
        mapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "zoom_level" to zoomLevel
        )
    )
    
    // Feature Usage Events
    object CameraOpened : AnalyticsEvent("camera_opened")
    object GalleryOpened : AnalyticsEvent("gallery_opened")
    data class PermissionGranted(val permission: String) : AnalyticsEvent(
        "permission_granted",
        mapOf("permission" to permission)
    )
    data class PermissionDenied(val permission: String) : AnalyticsEvent(
        "permission_denied",
        mapOf("permission" to permission)
    )
    
    // Error Events
    data class ApiError(
        val endpoint: String,
        val errorCode: Int,
        val errorMessage: String
    ) : AnalyticsEvent(
        "api_error",
        mapOf(
            "endpoint" to endpoint,
            "error_code" to errorCode,
            "error_message" to errorMessage
        )
    )
    
    // Performance Events
    data class ScreenLoaded(
        val screenName: String,
        val loadTimeMs: Long
    ) : AnalyticsEvent(
        "screen_loaded",
        mapOf(
            "screen_name" to screenName,
            "load_time_ms" to loadTimeMs
        )
    )
}

data class UserProperty(
    val key: String,
    val value: String
) {
    companion object {
        fun userType(type: String) = UserProperty("user_type", type)
        fun preferredLanguage(language: String) = UserProperty("preferred_language", language)
        fun notificationsEnabled(enabled: Boolean) = UserProperty("notifications_enabled", enabled.toString())
        fun darkModeEnabled(enabled: Boolean) = UserProperty("dark_mode_enabled", enabled.toString())
        fun appVersion(version: String) = UserProperty("app_version", version)
    }
}
