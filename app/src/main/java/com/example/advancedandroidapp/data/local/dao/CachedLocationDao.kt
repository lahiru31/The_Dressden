package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.CachedLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedLocationDao {
    @Query("SELECT * FROM cached_locations ORDER BY timestamp DESC")
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

    @Query("DELETE FROM cached_locations WHERE timestamp < :timestamp")
    suspend fun deleteOldCachedLocations(timestamp: Long)

    @Query("DELETE FROM cached_locations")
    suspend fun deleteAllCachedLocations()
}
