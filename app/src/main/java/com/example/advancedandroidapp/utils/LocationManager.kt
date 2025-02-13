package com.example.advancedandroidapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager
) {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL)
            .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL)
            .setMaxUpdateDelayMillis(MAX_UPDATE_DELAY)
            .build()
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult {
        return try {
            if (!permissionManager.hasLocationPermissions()) {
                return LocationResult.MissingPermission
            }

            val cancellationToken = CancellationTokenSource().token
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken
            ).await()

            location?.let {
                LocationResult.Success(it)
            } ?: LocationResult.Error("Unable to get location")

        } catch (e: Exception) {
            LocationResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flow<LocationResult> = callbackFlow {
        if (!permissionManager.hasLocationPermissions()) {
            trySend(LocationResult.MissingPermission)
            close()
            return@callbackFlow
        }

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: android.location.LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(LocationResult.Success(location))
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    trySend(LocationResult.Error("Location is not available"))
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            ).addOnFailureListener { e ->
                trySend(LocationResult.Error(e.message ?: "Failed to request location updates"))
                close(e)
            }
        } catch (e: SecurityException) {
            trySend(LocationResult.Error("Location permission not granted"))
            close(e)
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            startLatitude,
            startLongitude,
            endLatitude,
            endLongitude,
            results
        )
        return results[0]
    }

    fun formatDistance(meters: Float): String {
        return when {
            meters < 1000 -> "${meters.toInt()}m"
            else -> String.format("%.1fkm", meters / 1000)
        }
    }

    companion object {
        private const val UPDATE_INTERVAL = 10000L // 10 seconds
        private const val FASTEST_UPDATE_INTERVAL = 5000L // 5 seconds
        private const val MAX_UPDATE_DELAY = 20000L // 20 seconds
    }
}

sealed class LocationResult {
    data class Success(val location: Location) : LocationResult()
    data class Error(val message: String) : LocationResult()
    object MissingPermission : LocationResult()
}

data class LocationState(
    val location: Location? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long
)

interface LocationRepository {
    suspend fun getCurrentLocation(): LocationResult
    fun getLocationUpdates(): Flow<LocationResult>
    fun stopLocationUpdates()
    fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float
}
