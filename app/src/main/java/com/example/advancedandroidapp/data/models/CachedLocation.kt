package com.example.advancedandroidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_locations")
data class CachedLocation(
    @PrimaryKey
    val id: String,
    
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val syncStatus: String = "pending"
)
