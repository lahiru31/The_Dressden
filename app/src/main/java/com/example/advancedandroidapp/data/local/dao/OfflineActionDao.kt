package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.ActionType
import com.example.advancedandroidapp.data.models.OfflineAction
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineActionDao {
    @Query("SELECT * FROM offline_actions ORDER BY timestamp DESC")
    fun getAllOfflineActions(): Flow<List<OfflineAction>>

    @Query("SELECT * FROM offline_actions WHERE synced = 0 ORDER BY timestamp ASC")
    fun getPendingOfflineActions(): Flow<List<OfflineAction>>

    @Query("SELECT * FROM offline_actions WHERE id = :id")
    suspend fun getOfflineActionById(id: Long): OfflineAction?

    @Query("SELECT * FROM offline_actions WHERE entity_type = :entityType AND entity_id = :entityId")
    suspend fun getOfflineActionsForEntity(entityType: String, entityId: String): List<OfflineAction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfflineAction(action: OfflineAction): Long

    @Update
    suspend fun updateOfflineAction(action: OfflineAction)

    @Delete
    suspend fun deleteOfflineAction(action: OfflineAction)

    @Query("DELETE FROM offline_actions WHERE synced = 1 AND timestamp < :timestamp")
    suspend fun deleteSyncedActionsBefore(timestamp: Long)

    @Query("UPDATE offline_actions SET synced = 1 WHERE id = :id")
    suspend fun markActionAsSynced(id: Long)

    @Query("UPDATE offline_actions SET retry_count = retry_count + 1 WHERE id = :id")
    suspend fun incrementRetryCount(id: Long)

    @Transaction
    suspend fun createOfflineAction(
        type: ActionType,
        entityType: String,
        entityId: String,
        data: String
    ): Long {
        val action = OfflineAction(
            type = type,
            entityType = entityType,
            entityId = entityId,
            data = data,
            timestamp = System.currentTimeMillis()
        )
        return insertOfflineAction(action)
    }
}
