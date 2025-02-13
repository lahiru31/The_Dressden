package com.example.advancedandroidapp.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

class ListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        if (value == null) return null
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        if (list == null) return null
        return gson.toJson(list)
    }
}

class MapConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): Map<String, Any>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: Map<String, Any>?): String? {
        if (map == null) return null
        return gson.toJson(map)
    }
}

class LocationConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): com.example.advancedandroidapp.data.models.Location? {
        if (value == null) return null
        return gson.fromJson(value, com.example.advancedandroidapp.data.models.Location::class.java)
    }

    @TypeConverter
    fun fromLocation(location: com.example.advancedandroidapp.data.models.Location?): String? {
        if (location == null) return null
        return gson.toJson(location)
    }
}

class UserProfileConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): com.example.advancedandroidapp.data.models.UserProfile? {
        if (value == null) return null
        return gson.fromJson(value, com.example.advancedandroidapp.data.models.UserProfile::class.java)
    }

    @TypeConverter
    fun fromUserProfile(profile: com.example.advancedandroidapp.data.models.UserProfile?): String? {
        if (profile == null) return null
        return gson.toJson(profile)
    }
}
