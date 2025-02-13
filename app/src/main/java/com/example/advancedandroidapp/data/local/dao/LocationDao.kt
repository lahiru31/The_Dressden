package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<Location>>

    @Query("SELECT * FROM locations WHERE id = :locationId")
    fun getLocationById(locationId: String): Flow<Location?>

    @Query("SELECT * FROM locations WHERE category = :category")
    fun getLocationsByCategory(category: String): Flow<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<Location>)

    @Update
    suspend fun updateLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()

    @Query("""
        SELECT * FROM locations 
        WHERE latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLng AND :maxLng
    """)
    fun getLocationsInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<List<Location>>

    @Query("""
        SELECT * FROM locations
        WHERE (name LIKE :query OR description LIKE :query)
        AND (:category IS NULL OR category = :category)
        ORDER BY 
            CASE 
                WHEN :sortBy = 'rating' THEN rating 
                WHEN :sortBy = 'name' THEN name 
                ELSE created_at 
            END
        LIMIT :limit
    """)
    fun searchLocations(
        query: String,
        category: String? = null,
        sortBy: String = "created_at",
        limit: Int = 20
    ): Flow<List<Location>>

    @Query("""
        SELECT * FROM locations
        WHERE created_by = :userId
        ORDER BY created_at DESC
    """)
    fun getUserLocations(userId: String): Flow<List<Location>>

    @Transaction
    suspend fun upsertLocation(location: Location) {
        val existingLocation = getLocationById(location.id).value
        if (existingLocation != null) {
            updateLocation(location)
        } else {
            insertLocation(location)
        }
    }
}
