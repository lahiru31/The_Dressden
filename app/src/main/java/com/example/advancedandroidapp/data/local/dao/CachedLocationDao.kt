package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.CachedLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedLocationDao {
    @Query("SELECT * FROM cached_locations ORDER BY lastUpdated DESC")
    fun getAllCachedLocations(): Flow<List<CachedLocation>>

    @Query("SELECT * FROM cached_locations WHERE id = :id")
    suspend fun getCachedLocationById(id: String): CachedLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedLocation(location: CachedLocation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedLocations(locations: List<CachedLocation>)

    @Update
    suspend fun updateCachedLocation(location: CachedLocation)

    @Delete
    suspend fun deleteCachedLocation(location: CachedLocation)

    @Query("DELETE FROM cached_locations WHERE lastUpdated < :timestamp")
    suspend fun deleteOldCachedLocations(timestamp: Date)

    @Query("DELETE FROM cached_locations")
    suspend fun deleteAllCachedLocations()
}
