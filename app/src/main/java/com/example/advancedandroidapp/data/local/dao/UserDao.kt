package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.User
import com.example.advancedandroidapp.data.models.UserProfile
import com.example.advancedandroidapp.data.models.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao {
    @Query("SELECT * FROM users")
    abstract fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    abstract suspend fun getUserById(id: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    abstract suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUser(user: User)

    @Update
    abstract suspend fun updateUser(user: User)

    @Delete
    abstract suspend fun deleteUser(user: User)

    @Transaction
    @Query("""
        SELECT u.*, p.* 
        FROM users u 
        LEFT JOIN user_profiles p ON u.id = p.user_id 
        WHERE u.id = :userId
    """)
    abstract suspend fun getUserWithProfile(userId: String): Map<User, UserProfile?>

    @Transaction
    @Query("""
        SELECT u.*, s.* 
        FROM users u 
        LEFT JOIN user_settings s ON u.id = s.user_id 
        WHERE u.id = :userId
    """)
    abstract suspend fun getUserWithSettings(userId: String): Map<User, UserSettings?>

    @Transaction
    open suspend fun insertUserWithProfile(user: User, profile: UserProfile) {
        insertUser(user)
        // UserProfileDao will handle profile insertion
    }

    @Transaction
    open suspend fun insertUserWithSettings(user: User, settings: UserSettings) {
        insertUser(user)
        // UserSettingsDao will handle settings insertion
    }
}
