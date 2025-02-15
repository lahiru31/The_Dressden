package com.example.advancedandroidapp.data.local.dao

import androidx.room.*
import com.example.advancedandroidapp.data.models.OfflineAction
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineActionDao {
    @Query("SELECT * FROM offline_actions ORDER BY timestamp ASC")
    fun getAllOfflineActions(): Flow<List<OfflineAction>>

    @Query("SELECT * FROM offline_actions WHERE status = :status ORDER BY timestamp ASC")
    fun getOfflineActionsByStatus(status: String): Flow<List<OfflineAction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfflineAction(action: OfflineAction)

    @Update
    suspend fun updateOfflineAction(action: OfflineAction)

    @Delete
    suspend fun deleteOfflineAction(action: OfflineAction)

    @Query("DELETE FROM offline_actions WHERE status = :status")
    suspend fun deleteOfflineActionsByStatus(status: String)

    @Query("DELETE FROM offline_actions")
    suspend fun deleteAllOfflineActions()

    @Query("SELECT COUNT(*) FROM offline_actions WHERE status = :status")
    fun getPendingActionsCount(status: String): Flow<Int>
}
