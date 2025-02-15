package com.example.advancedandroidapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advancedandroidapp.data.models.*
import com.example.advancedandroidapp.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations

    private val _selectedLocation = MutableStateFlow<LocationWithDetails?>(null)
    val selectedLocation: StateFlow<LocationWithDetails?> = _selectedLocation

    private val _reviews = MutableStateFlow<List<LocationReview>>(emptyList())
    val reviews: StateFlow<List<LocationReview>> = _reviews

    private val _favorites = MutableStateFlow<List<Location>>(emptyList())
    val favorites: StateFlow<List<Location>> = _favorites

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadLocations() {
        viewModelScope.launch {
            try {
                repository.getAllLocations().collect {
                    _locations.value = it
                }
            } catch (e: Exception) {
                _error.value = "Error loading locations: ${e.message}"
            }
        }
    }

    fun loadLocationsByCategory(category: String) {
        viewModelScope.launch {
            try {
                repository.getLocationsByCategory(category).collect {
                    _locations.value = it
                }
            } catch (e: Exception) {
                _error.value = "Error loading locations by category: ${e.message}"
            }
        }
    }

    fun loadLocationDetails(locationId: String) {
        viewModelScope.launch {
            try {
                val details = repository.getLocationWithDetails(locationId)
                _selectedLocation.value = details
                loadLocationReviews(locationId)
            } catch (e: Exception) {
                _error.value = "Error loading location details: ${e.message}"
            }
        }
    }

    fun loadLocationReviews(locationId: String) {
        viewModelScope.launch {
            try {
                repository.getLocationReviews(locationId).collect {
                    _reviews.value = it
                }
            } catch (e: Exception) {
                _error.value = "Error loading reviews: ${e.message}"
            }
        }
    }

    fun loadUserFavorites(userId: String) {
        viewModelScope.launch {
            try {
                repository.getUserFavorites(userId).collect {
                    _favorites.value = it
                }
            } catch (e: Exception) {
                _error.value = "Error loading favorites: ${e.message}"
            }
        }
    }

    fun addReview(review: LocationReview) {
        viewModelScope.launch {
            try {
                repository.addReview(review)
                loadLocationReviews(review.locationId)
                loadLocationDetails(review.locationId)
            } catch (e: Exception) {
                _error.value = "Error adding review: ${e.message}"
            }
        }
    }

    fun toggleFavorite(userId: String, locationId: String) {
        viewModelScope.launch {
            try {
                if (repository.isLocationFavorited(userId, locationId)) {
                    repository.removeFromFavorites(userId, locationId)
                } else {
                    repository.addToFavorites(userId, locationId)
                }
                loadUserFavorites(userId)
            } catch (e: Exception) {
                _error.value = "Error toggling favorite: ${e.message}"
            }
        }
    }

    fun saveLocation(location: Location, tags: List<LocationTag>? = null) {
        viewModelScope.launch {
            try {
                if (tags != null) {
                    repository.saveLocationWithTags(location, tags)
                } else {
                    repository.saveLocation(location)
                }
                loadLocations()
            } catch (e: Exception) {
                _error.value = "Error saving location: ${e.message}"
            }
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            try {
                repository.deleteLocation(location)
                loadLocations()
            } catch (e: Exception) {
                _error.value = "Error deleting location: ${e.message}"
            }
        }
    }

    // Cached location operations
    fun saveCachedLocation(location: CachedLocation) {
        viewModelScope.launch {
            try {
                repository.saveCachedLocation(location)
            } catch (e: Exception) {
                _error.value = "Error saving cached location: ${e.message}"
            }
        }
    }

    fun clearOldCachedLocations(timestamp: Long) {
        viewModelScope.launch {
            try {
                repository.deleteOldCachedLocations(timestamp)
            } catch (e: Exception) {
                _error.value = "Error clearing old cached locations: ${e.message}"
            }
        }
    }
}
