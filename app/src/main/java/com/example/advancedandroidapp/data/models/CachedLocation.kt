package com.example.advancedandroidapp.data.models

import androidx.room.*
import com.example.advancedandroidapp.data.local.converters.DateConverter
import com.example.advancedandroidapp.data.local.converters.LocationConverter
import java.util.Date

@Entity(tableName = "cached_locations")
@TypeConverters(DateConverter::class, LocationConverter::class)
data class CachedLocation(
    @PrimaryKey
    val id: String,
    
    val location: Location,
    
    val lastUpdated: Date
)
