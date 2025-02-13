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
    suspend fun login(email: String, password: String): ApiResponse<User> = withContext(Dispatchers.IO) {
        try {
            if (!networkUtils.isNetworkAvailable()) {
                return@withContext ApiResponse.Error(
                    code = -1,
                    message = "No internet connection available"
                )
            }

            executeWithRetry {
                val response = apiService.login(mapOf(
                    "email" to email,
                    "password" to password
                ))
                
                when {
                    response.isSuccessful && response.body() != null -> {
                        val user = response.body()!!
                        // Cache user data
                        appDatabase.userDao().insertUser(user)
                        ApiResponse.Success(user)
                    }
                    response.code() == 401 -> {
                        ApiResponse.Error(
                            code = 401,
                            message = "Invalid credentials",
                            errorBody = parseErrorResponse(response.errorBody())
                        )
                    }
                    else -> {
                        ApiResponse.Error(
                            code = response.code(),
                            message = response.message(),
                            errorBody = parseErrorResponse(response.errorBody())
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Login failed")
            ApiResponse.Error(
                code = -1,
                message = e.localizedMessage ?: "An unexpected error occurred"
            )
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

    private suspend fun executeWithRetry<T>(
        maxAttempts: Int = 3,
        initialDelay: Long = 100,
        maxDelay: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(maxAttempts - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                Timber.e(e, "Attempt ${attempt + 1} failed")
                if (e is IOException) {
                    delay(currentDelay)
                    currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                } else {
                    throw e
                }
            }
        }
        return block() // last attempt
    }

    private fun getCurrentUserToken(): String {
        return preferencesManager.getToken() ?: throw AuthenticationException("No valid token found")
    }

    private fun parseErrorResponse(errorBody: ResponseBody?): ErrorResponse? {
        return try {
            errorBody?.string()?.let { 
                Gson().fromJson(it, ErrorResponse::class.java)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse error response")
            null
        }
    }

    private suspend fun cleanupOldCache() {
        val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        appDatabase.apply {
            locationDao().deleteLocationsOlderThan(thirtyDaysAgo)
            userProfileDao().deleteProfilesOlderThan(thirtyDaysAgo)
        }
    }
}
