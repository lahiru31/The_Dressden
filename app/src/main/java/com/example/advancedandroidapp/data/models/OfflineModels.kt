package com.example.advancedandroidapp.data.models

import androidx.room.*

@Entity(
    tableName = "cached_locations",
    foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = ["id"],
            childColumns = ["location_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("location_id"),
        Index("timestamp")
    ]
)
data class CachedLocation(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "location_id")
    val locationId: String,
    
    val latitude: Double,
    
    val longitude: Double,
    
    val address: String?,
    
    val timestamp: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

enum class SyncStatus {
    PENDING,
    SYNCED,
    FAILED
}

@Entity(
    tableName = "offline_actions",
    indices = [Index("timestamp")]
)
data class OfflineAction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val type: ActionType,
    
    @ColumnInfo(name = "entity_type")
    val entityType: String,
    
    @ColumnInfo(name = "entity_id")
    val entityId: String,
    
    val data: String, // JSON string
    
    val timestamp: Long = System.currentTimeMillis(),
    
    val synced: Boolean = false,
    
    @ColumnInfo(name = "retry_count")
    val retryCount: Int = 0
)

enum class ActionType {
    CREATE,
    UPDATE,
    DELETE
}
