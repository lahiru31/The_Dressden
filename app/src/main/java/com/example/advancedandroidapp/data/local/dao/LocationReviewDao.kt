package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.LocationReview
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationReviewDao {
    @Query("SELECT * FROM location_reviews WHERE location_id = :locationId ORDER BY created_at DESC")
    fun getReviewsForLocation(locationId: String): Flow<List<LocationReview>>

    @Query("SELECT * FROM location_reviews WHERE user_id = :userId ORDER BY created_at DESC")
    fun getReviewsByUser(userId: String): Flow<List<LocationReview>>

    @Query("SELECT * FROM location_reviews WHERE id = :id")
    suspend fun getReviewById(id: String): LocationReview?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: LocationReview)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<LocationReview>)

    @Update
    suspend fun updateReview(review: LocationReview)

    @Delete
    suspend fun deleteReview(review: LocationReview)

    @Query("SELECT AVG(rating) FROM location_reviews WHERE location_id = :locationId")
    suspend fun getAverageRatingForLocation(locationId: String): Float?

    @Query("SELECT COUNT(*) FROM location_reviews WHERE location_id = :locationId")
    suspend fun getReviewCountForLocation(locationId: String): Int

    @Query("SELECT * FROM location_reviews WHERE location_id = :locationId AND user_id = :userId")
    suspend fun getUserReviewForLocation(locationId: String, userId: String): LocationReview?

    @Transaction
    suspend fun upsertReview(review: LocationReview) {
        val existingReview = getUserReviewForLocation(review.locationId, review.userId)
        if (existingReview != null) {
            updateReview(review)
        } else {
            insertReview(review)
        }
    }
}
