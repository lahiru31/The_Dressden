package com.example.advancedandroidapp.data.models

import androidx.room.*
import com.example.advancedandroidapp.data.local.converters.DateConverter
import java.util.Date

@Entity(tableName = "offline_actions")
@TypeConverters(DateConverter::class)
data class OfflineAction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val type: String, // CREATE, UPDATE, DELETE
    val entityType: String, // Location, UserProfile, etc.
    val entityId: String,
    val data: String, // JSON serialized data
    val timestamp: Date,
    val synced: Boolean = false
)
