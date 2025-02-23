package com.example.advancedandroidapp.data.models

import androidx.room.*
import com.example.advancedandroidapp.data.local.converters.DateConverter
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "created_at")
    @TypeConverters(DateConverter::class)
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    @TypeConverters(DateConverter::class)
    val updatedAt: Date = Date()
)
