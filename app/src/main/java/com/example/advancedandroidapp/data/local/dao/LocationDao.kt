package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<Location>>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: String): Location?

    @Query("SELECT * FROM locations WHERE category = :category")
    fun getLocationsByCategory(category: String): Flow<List<Location>>

    @Query("SELECT * FROM locations WHERE rating >= :minRating")
    fun getLocationsByMinRating(minRating: Float): Flow<List<Location>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<Location>)

    @Update
    suspend fun updateLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    // Location Tags
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: LocationTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTagMap(tagMap: LocationTagMap)

    @Query("SELECT t.* FROM location_tags t INNER JOIN location_tag_map m ON t.id = m.tag_id WHERE m.location_id = :locationId")
    suspend fun getTagsForLocation(locationId: String): List<LocationTag>

    // Location Reviews
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: LocationReview)

    @Query("SELECT * FROM location_reviews WHERE location_id = :locationId ORDER BY created_at DESC")
    fun getReviewsForLocation(locationId: String): Flow<List<LocationReview>>

    @Query("SELECT AVG(rating) FROM location_reviews WHERE location_id = :locationId")
    suspend fun getAverageRatingForLocation(locationId: String): Float?

    // User Favorites
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: UserFavorite)

    @Delete
    suspend fun deleteFavorite(favorite: UserFavorite)

    @Query("SELECT l.* FROM locations l INNER JOIN user_favorites f ON l.id = f.location_id WHERE f.user_id = :userId")
    fun getFavoriteLocationsForUser(userId: String): Flow<List<Location>>

    @Query("SELECT EXISTS(SELECT 1 FROM user_favorites WHERE user_id = :userId AND location_id = :locationId)")
    suspend fun isLocationFavorited(userId: String, locationId: String): Boolean

    // Complex queries
    @Transaction
    @Query("SELECT * FROM locations WHERE id = :locationId")
    suspend fun getLocationWithDetails(locationId: String): LocationWithDetails

    @Transaction
    suspend fun insertLocationWithTags(location: Location, tags: List<LocationTag>) {
        insertLocation(location)
        tags.forEach { tag ->
            insertTag(tag)
            insertTagMap(LocationTagMap(location.id, tag.id))
        }
    }

    @Transaction
    suspend fun updateLocationRating(locationId: String) {
        val averageRating = getAverageRatingForLocation(locationId)
        if (averageRating != null) {
            // Update the location's rating
            getLocationById(locationId)?.let { location ->
                updateLocation(location.copy(rating = averageRating))
            }
        }
    }
}

data class LocationWithDetails(
    @Embedded val location: Location,
    @Relation(
        parentColumn = "id",
        entityColumn = "location_id",
        entity = LocationReview::class
    )
    val reviews: List<LocationReview>,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = LocationTagMap::class,
            parentColumn = "location_id",
            entityColumn = "tag_id"
        )
    )
    val tags: List<LocationTag>
)
