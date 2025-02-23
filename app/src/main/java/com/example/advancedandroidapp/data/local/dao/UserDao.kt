package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.User
import com.example.advancedandroidapp.data.models.UserProfile
import com.example.advancedandroidapp.data.models.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: String): Flow<User>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""
        SELECT * FROM users
        LEFT JOIN user_profiles ON users.id = user_profiles.user_id
        WHERE users.id = :userId
    """)
    fun getUserWithProfile(userId: String): Flow<UserWithProfile>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""
        SELECT * FROM users
        LEFT JOIN user_settings ON users.id = user_settings.user_id
        WHERE users.id = :userId
    """)
    fun getUserWithSettings(userId: String): Flow<UserWithSettings>

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

data class UserWithProfile(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )
    val profile: UserProfile?
)

data class UserWithSettings(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )
    val settings: UserSettings?
)
