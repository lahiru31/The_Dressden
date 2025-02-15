package com.example.advancedandroidapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advancedandroidapp.data.models.CachedLocation
import com.example.advancedandroidapp.data.models.Location
import com.example.advancedandroidapp.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    private val _cachedLocations = MutableLiveData<List<CachedLocation>>()
    val cachedLocations: LiveData<List<CachedLocation>> = _cachedLocations

    private val _locations = MutableLiveData<List<Location>>()
    val locations: LiveData<List<Location>> = _locations

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadCachedLocations()
        loadLocations()
    }

    private fun loadCachedLocations() {
        viewModelScope.launch {
            try {
                val locations = repository.getCachedLocations()
                _cachedLocations.value = locations
            } catch (e: Exception) {
                _error.value = "Error loading cached locations: ${e.message}"
            }
        }
    }

    private fun loadLocations() {
        viewModelScope.launch {
            try {
                val locations = repository.getLocations()
                _locations.value = locations
            } catch (e: Exception) {
                _error.value = "Error loading locations: ${e.message}"
            }
        }
    }

    fun saveCachedLocation(location: CachedLocation) {
        viewModelScope.launch {
            try {
                repository.saveCachedLocation(location)
                loadCachedLocations() // Reload to update UI
            } catch (e: Exception) {
                _error.value = "Error saving cached location: ${e.message}"
            }
        }
    }

    fun saveLocation(location: Location) {
        viewModelScope.launch {
            try {
                repository.saveLocation(location)
                loadLocations() // Reload to update UI
            } catch (e: Exception) {
                _error.value = "Error saving location: ${e.message}"
            }
        }
    }

    fun deleteCachedLocation(location: CachedLocation) {
        viewModelScope.launch {
            try {
                repository.deleteCachedLocation(location)
                loadCachedLocations() // Reload to update UI
            } catch (e: Exception) {
                _error.value = "Error deleting cached location: ${e.message}"
            }
        }
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            try {
                repository.deleteLocation(location)
                loadLocations() // Reload to update UI
            } catch (e: Exception) {
                _error.value = "Error deleting location: ${e.message}"
            }
        }
    }

    fun clearCachedLocations() {
        viewModelScope.launch {
            try {
                repository.clearCachedLocations()
                loadCachedLocations() // Reload to update UI
            } catch (e: Exception) {
                _error.value = "Error clearing cached locations: ${e.message}"
            }
        }
    }
}
