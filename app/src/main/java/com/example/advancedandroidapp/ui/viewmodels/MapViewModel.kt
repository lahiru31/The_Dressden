package com.example.advancedandroidapp.ui.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advancedandroidapp.data.models.Location as AppLocation
import com.example.advancedandroidapp.data.repository.MainRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: MainRepository,
    private val locationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _locations = MutableStateFlow<List<AppLocation>>(emptyList())
    val locations = _locations.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Set<String>>(emptySet())
    private val searchQuery = MutableStateFlow<String?>(null)

    private var currentLocationLatLng: LatLng? = null

    init {
        // Combine filters and search query to update locations
        combine(
            _selectedFilters,
            searchQuery
        ) { filters, query ->
            loadLocations(filters, query)
        }.launchIn(viewModelScope)
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = locationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                location?.let {
                    _currentLocation.value = it
                    currentLocationLatLng = LatLng(it.latitude, it.longitude)
                    // Load nearby locations
                    loadNearbyLocations(it)
                }
            } catch (e: Exception) {
                _error.value = "Failed to get current location: ${e.message}"
            }
        }
    }

    private suspend fun loadNearbyLocations(location: Location) {
        try {
            _isLoading.value = true
            repository.getLocations(
                latitude = location.latitude,
                longitude = location.longitude,
                radius = SEARCH_RADIUS_METERS
            ).collect { locations ->
                _locations.value = filterLocations(locations)
            }
        } catch (e: Exception) {
            _error.value = "Failed to load locations: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun loadLocations(filters: Set<String>, query: String?) {
        try {
            _isLoading.value = true
            currentLocationLatLng?.let { latLng ->
                repository.getLocations(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude,
                    radius = SEARCH_RADIUS_METERS
                ).collect { locations ->
                    _locations.value = filterLocations(locations, filters, query)
                }
            }
        } catch (e: Exception) {
            _error.value = "Failed to load locations: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    private fun filterLocations(
        locations: List<AppLocation>,
        filters: Set<String> = _selectedFilters.value,
        query: String? = searchQuery.value
    ): List<AppLocation> {
        return locations.filter { location ->
            val matchesFilter = filters.isEmpty() || filters.contains(location.category)
            val matchesQuery = query.isNullOrBlank() ||
                    location.name.contains(query, ignoreCase = true) ||
                    location.description?.contains(query, ignoreCase = true) == true
            matchesFilter && matchesQuery
        }
    }

    fun searchLocations(query: String?) {
        searchQuery.value = query
    }

    fun updateFilters(filterIds: List<Int>) {
        _selectedFilters.value = filterIds.map { id ->
            when (id) {
                R.id.chip_restaurant -> "restaurant"
                R.id.chip_hotel -> "hotel"
                R.id.chip_attraction -> "attraction"
                R.id.chip_shopping -> "shopping"
                else -> ""
            }
        }.toSet()
    }

    fun prepareNewLocation(latLng: LatLng) {
        // Store the selected location for the add location screen
        // This could be handled through SavedStateHandle or a shared ViewModel
    }

    companion object {
        private const val SEARCH_RADIUS_METERS = 5000 // 5km radius
    }
}
