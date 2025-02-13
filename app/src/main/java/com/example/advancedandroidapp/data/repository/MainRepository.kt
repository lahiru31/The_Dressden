package com.example.advancedandroidapp.data.repository

import android.content.Context
import com.example.advancedandroidapp.data.api.ApiService
import com.example.advancedandroidapp.data.local.AppDatabase
import com.example.advancedandroidapp.data.models.*
import com.example.advancedandroidapp.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
    private val networkUtils: NetworkUtils
) {
    // User Profile Operations
    suspend fun login(email: String, password: String): ApiResponse<User> {
        return try {
            val response = apiService.login(mapOf(
                "email" to email,
                "password" to password
            ))
            if (response.isSuccessful && response.body() != null) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            ApiResponse.Error(-1, e.message ?: "Unknown error occurred")
        }
    }

    fun getUserProfile(userId: String) = flow {
        // Emit cached data first
        emitAll(appDatabase.userProfileDao().getUserProfile(userId))

        // If network is available, fetch fresh data
        if (networkUtils.isNetworkAvailable()) {
            try {
                val response = apiService.getUserProfile("Bearer ${getCurrentUserToken()}")
                if (response.isSuccessful && response.body() != null) {
                    // Update cache
                    appDatabase.userProfileDao().insertUserProfile(response.body()!!)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }.flowOn(Dispatchers.IO)

    // Location Operations
    fun getLocations(latitude: Double, longitude: Double, radius: Int) = flow {
        // Emit cached data first
        emitAll(appDatabase.locationDao().getAllLocations())

        // If network is available, fetch fresh data
        if (networkUtils.isNetworkAvailable()) {
            try {
                val response = apiService.getLocations(latitude, longitude, radius)
                if (response.isSuccessful && response.body() != null) {
                    // Update cache
                    appDatabase.locationDao().insertLocations(response.body()!!)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun createLocation(location: Location): ApiResponse<Location> {
        return try {
            if (!networkUtils.isNetworkAvailable()) {
                // Store offline action
                appDatabase.offlineActionDao().insertOfflineAction(
                    OfflineAction(
                        type = "CREATE",
                        entityType = "Location",
                        entityId = location.id,
                        data = location.toString()
                    )
                )
                ApiResponse.Success(location)
            } else {
                val response = apiService.createLocation("Bearer ${getCurrentUserToken()}", location)
                if (response.isSuccessful && response.body() != null) {
                    appDatabase.locationDao().insertLocation(response.body()!!)
                    ApiResponse.Success(response.body()!!)
                } else {
                    ApiResponse.Error(response.code(), response.message())
                }
            }
        } catch (e: Exception) {
            ApiResponse.Error(-1, e.message ?: "Unknown error occurred")
        }
    }

    // Media Operations
    suspend fun uploadMedia(file: File, type: String): ApiResponse<MediaUploadResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = RequestBody.create(
                    okhttp3.MediaType.parse("multipart/form-data"),
                    file
                )
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val typeBody = RequestBody.create(
                    okhttp3.MediaType.parse("text/plain"),
                    type
                )

                val response = apiService.uploadMedia(
                    "Bearer ${getCurrentUserToken()}",
                    body,
                    typeBody
                )

                if (response.isSuccessful && response.body() != null) {
                    ApiResponse.Success(MediaUploadResponse(
                        url = response.body()!!["url"]!!,
                        type = type,
                        size = file.length(),
                        uploadedAt = java.util.Date()
                    ))
                } else {
                    ApiResponse.Error(response.code(), response.message())
                }
            } catch (e: Exception) {
                ApiResponse.Error(-1, e.message ?: "Unknown error occurred")
            }
        }
    }

    // Settings Operations
    fun getUserSettings(userId: String) = flow {
        // Emit cached settings
        emitAll(appDatabase.userSettingsDao().getUserSettings(userId))

        // If network available, fetch fresh settings
        if (networkUtils.isNetworkAvailable()) {
            try {
                val response = apiService.getUserSettings("Bearer ${getCurrentUserToken()}")
                if (response.isSuccessful && response.body() != null) {
                    // Update cache
                    val settings = UserSettings(
                        userId = userId,
                        notificationsEnabled = response.body()!!["notifications_enabled"] as Boolean,
                        darkModeEnabled = response.body()!!["dark_mode_enabled"] as Boolean,
                        language = response.body()!!["language"] as String,
                        locationTrackingEnabled = response.body()!!["location_tracking_enabled"] as Boolean,
                        dataBackupEnabled = response.body()!!["data_backup_enabled"] as Boolean,
                        lastSyncTimestamp = System.currentTimeMillis()
                    )
                    appDatabase.userSettingsDao().insertUserSettings(settings)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun getCurrentUserToken(): String {
        // Implement token retrieval logic
        return ""
    }
}
