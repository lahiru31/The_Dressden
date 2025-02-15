package com.example.advancedandroidapp.data.local.converters

import androidx.room.TypeConverter
import com.example.advancedandroidapp.data.models.Location
import com.google.gson.Gson

class LocationConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromLocation(location: Location?): String? {
        return location?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toLocation(value: String?): Location? {
        return value?.let { gson.fromJson(it, Location::class.java) }
    }
}
