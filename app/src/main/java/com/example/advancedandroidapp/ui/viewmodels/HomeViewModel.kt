package com.example.advancedandroidapp.ui.viewmodels

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advancedandroidapp.data.models.Location
import com.example.advancedandroidapp.data.models.UserProfile
import com.example.advancedandroidapp.data.repository.MainRepository
import com.example.advancedandroidapp.ui.adapters.LocationAdapter
import com.example.advancedandroidapp.utils.NetworkUtils
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.Manifest
import android.location.Location as AndroidLocation
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository,
    private val networkUtils: NetworkUtils,
    private val locationClient: FusedLocationProviderClient
) : ViewModel() {

    val locationAdapter = LocationAdapter { location ->
        // Handle location click
        _navigationEvent.value = NavigationEvent.ToLocationDetail(location.id)
    }

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _nearbyLocations = MutableStateFlow<List<Location>>(emptyList())
    val nearbyLocations = _nearbyLocations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent = _navigationEvent.asStateFlow()

    val isOnline = networkUtils.observeNetworkConnectivity()
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private var currentLocation: AndroidLocation? = null

    init {
        viewModelScope.launch {
            // Observe network status and refresh data when coming back online
            isOnline.collect { isOnline ->
                if (isOnline) {
                    refreshData()
                }
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get current user ID (implement according to your auth system)
                val userId = getCurrentUserId()
                
                // Fetch user profile
                repository.getUserProfile(userId).collect { profile ->
                    _userProfile.value = profile
                }

                // Get current location and fetch nearby locations
                currentLocation?.let { location ->
                    repository.getLocations(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        radius = SEARCH_RADIUS_METERS
                    ).collect { locations ->
                        _nearbyLocations.value = locations
                    }
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkAndRequestPermissions(activity: Activity) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (hasPermissions(activity, *permissions)) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun hasPermissions(activity: Activity, vararg permissions: String): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getCurrentLocation() {
        try {
            val cancellationToken = CancellationTokenSource().token
            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken)
                .addOnSuccessListener { location ->
                    location?.let {
                        currentLocation = it
                        refreshData()
                    }
                }
                .addOnFailureListener { e ->
                    _error.value = "Failed to get location: ${e.message}"
                }
        } catch (e: SecurityException) {
            _error.value = "Location permission not granted"
        }
    }

    private fun getCurrentUserId(): String {
        // Implement according to your auth system
        return ""
    }

    sealed class NavigationEvent {
        data class ToLocationDetail(val locationId: String) : NavigationEvent()
        object ToProfile : NavigationEvent()
        object ToSettings : NavigationEvent()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val SEARCH_RADIUS_METERS = 5000 // 5km radius
    }
}
