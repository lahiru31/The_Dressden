package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE user_id = :userId")
    fun getUserSettings(userId: String): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(settings: UserSettings)

    @Update
    suspend fun updateUserSettings(settings: UserSettings)

    @Delete
    suspend fun deleteUserSettings(settings: UserSettings)

    @Query("UPDATE user_settings SET notifications_enabled = :enabled WHERE user_id = :userId")
    suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean)

    @Query("UPDATE user_settings SET dark_mode_enabled = :enabled WHERE user_id = :userId")
    suspend fun updateDarkModeEnabled(userId: String, enabled: Boolean)

    @Query("UPDATE user_settings SET language = :language WHERE user_id = :userId")
    suspend fun updateLanguage(userId: String, language: String)

    @Query("UPDATE user_settings SET location_tracking_enabled = :enabled WHERE user_id = :userId")
    suspend fun updateLocationTrackingEnabled(userId: String, enabled: Boolean)

    @Query("UPDATE user_settings SET data_backup_enabled = :enabled WHERE user_id = :userId")
    suspend fun updateDataBackupEnabled(userId: String, enabled: Boolean)

    @Query("UPDATE user_settings SET last_sync_timestamp = :timestamp WHERE user_id = :userId")
    suspend fun updateLastSyncTimestamp(userId: String, timestamp: Long)

    @Transaction
    suspend fun createDefaultSettings(userId: String) {
        val defaultSettings = UserSettings(
            userId = userId,
            notificationsEnabled = true,
            darkModeEnabled = false,
            language = "en",
            locationTrackingEnabled = true,
            dataBackupEnabled = true
        )
        insertUserSettings(defaultSettings)
    }
}
