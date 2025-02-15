package com.example.advancedandroidapp.data.repository

import com.example.advancedandroidapp.data.local.dao.CachedLocationDao
import com.example.advancedandroidapp.data.local.dao.LocationDao
import com.example.advancedandroidapp.data.models.CachedLocation
import com.example.advancedandroidapp.data.models.Location
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val cachedLocationDao: CachedLocationDao
) {
    suspend fun getCachedLocations(): List<CachedLocation> {
        return cachedLocationDao.getAllCachedLocations()
    }

    suspend fun getCachedLocationById(id: String): CachedLocation? {
        return cachedLocationDao.getCachedLocationById(id)
    }

    suspend fun saveCachedLocation(location: CachedLocation) {
        cachedLocationDao.insertCachedLocation(location)
    }

    suspend fun saveCachedLocations(locations: List<CachedLocation>) {
        cachedLocationDao.insertCachedLocations(locations)
    }

    suspend fun updateCachedLocation(location: CachedLocation) {
        cachedLocationDao.updateCachedLocation(location)
    }

    suspend fun deleteCachedLocation(location: CachedLocation) {
        cachedLocationDao.deleteCachedLocation(location)
    }

    suspend fun deleteOldCachedLocations(timestamp: Long) {
        cachedLocationDao.deleteOldCachedLocations(timestamp)
    }

    suspend fun clearCachedLocations() {
        cachedLocationDao.deleteAllCachedLocations()
    }

    // Location methods
    suspend fun getLocations(): List<Location> {
        return locationDao.getAllLocations()
    }

    suspend fun getLocationById(id: String): Location? {
        return locationDao.getLocationById(id)
    }

    suspend fun saveLocation(location: Location) {
        locationDao.insertLocation(location)
    }

    suspend fun saveLocations(locations: List<Location>) {
        locationDao.insertLocations(locations)
    }

    suspend fun updateLocation(location: Location) {
        locationDao.updateLocation(location)
    }

    suspend fun deleteLocation(location: Location) {
        locationDao.deleteLocation(location)
    }
}
