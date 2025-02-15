package com.example.advancedandroidapp.data.local.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.example.advancedandroidapp.data.models.Location

@ProvidedTypeConverter
class LocationConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): Location? {
        if (value == null) return null
        return gson.fromJson(value, Location::class.java)
    }

    @TypeConverter
    fun fromLocation(location: Location?): String? {
        if (location == null) return null
        return gson.toJson(location)
    }
}
