package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE user_id = :userId")
    fun getUserProfile(userId: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfiles(profiles: List<UserProfile>)

    @Update
    suspend fun updateUserProfile(profile: UserProfile)

    @Delete
    suspend fun deleteUserProfile(profile: UserProfile)

    @Query("DELETE FROM user_profiles")
    suspend fun deleteAllProfiles()

    @Query("SELECT COUNT(*) FROM user_profiles WHERE user_id = :userId")
    suspend fun profileExists(userId: String): Int

    @Transaction
    suspend fun upsertProfile(profile: UserProfile) {
        val exists = profileExists(profile.userId)
        if (exists > 0) {
            updateUserProfile(profile)
        } else {
            insertUserProfile(profile)
        }
    }

    @Query("""
        SELECT * FROM user_profiles 
        WHERE username LIKE :query 
        OR full_name LIKE :query 
        LIMIT :limit
    """)
    fun searchProfiles(query: String, limit: Int = 20): Flow<List<UserProfile>>
}
