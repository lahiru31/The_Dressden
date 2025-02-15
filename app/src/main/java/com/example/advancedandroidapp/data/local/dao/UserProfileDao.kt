package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles")
    fun getAllUserProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE user_id = :userId")
    fun getUserProfile(userId: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE username = :username")
    suspend fun getUserProfileByUsername(username: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Update
    suspend fun updateUserProfile(profile: UserProfile)

    @Delete
    suspend fun deleteUserProfile(profile: UserProfile)

    @Query("UPDATE user_profiles SET avatar_url = :avatarUrl WHERE user_id = :userId")
    suspend fun updateAvatarUrl(userId: String, avatarUrl: String)

    @Query("UPDATE user_profiles SET bio = :bio WHERE user_id = :userId")
    suspend fun updateBio(userId: String, bio: String)

    @Query("UPDATE user_profiles SET phone_number = :phoneNumber WHERE user_id = :userId")
    suspend fun updatePhoneNumber(userId: String, phoneNumber: String)

    @Query("SELECT EXISTS(SELECT 1 FROM user_profiles WHERE username = :username AND user_id != :excludeUserId)")
    suspend fun isUsernameExists(username: String, excludeUserId: String): Boolean

    @Transaction
    suspend fun updateProfileDetails(
        userId: String,
        username: String? = null,
        fullName: String? = null,
        bio: String? = null,
        phoneNumber: String? = null
    ) {
        getUserProfile(userId).collect { profile ->
            profile?.let {
                val updatedProfile = it.copy(
                    username = username ?: it.username,
                    fullName = fullName ?: it.fullName,
                    bio = bio ?: it.bio,
                    phoneNumber = phoneNumber ?: it.phoneNumber
                )
                updateUserProfile(updatedProfile)
            }
        }
    }
}
