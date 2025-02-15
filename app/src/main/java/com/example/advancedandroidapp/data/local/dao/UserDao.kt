package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.User
import com.example.advancedandroidapp.data.models.UserProfile
import com.example.advancedandroidapp.data.models.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithProfile(userId: String): Map<User, UserProfile?>

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithSettings(userId: String): Map<User, UserSettings?>

    @Transaction
    suspend fun insertUserWithProfile(user: User, profile: UserProfile) {
        insertUser(user)
        userProfileDao.insertUserProfile(profile)
    }

    @Transaction
    suspend fun insertUserWithSettings(user: User, settings: UserSettings) {
        insertUser(user)
        userSettingsDao.insertUserSettings(settings)
    }
}
