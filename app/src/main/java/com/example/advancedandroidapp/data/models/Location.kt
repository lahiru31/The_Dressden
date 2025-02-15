package com.example.advancedandroidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val category: String,
    val imageUrl: String?,
    val rating: Float = 0f,
    val createdBy: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
