package com.example.advancedandroidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_actions")
data class OfflineAction(
    @PrimaryKey
    val id: String,
    
    val type: String,
    val data: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending",
    val retryCount: Int = 0
)
