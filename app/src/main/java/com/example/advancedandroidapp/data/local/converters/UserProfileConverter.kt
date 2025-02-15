package com.example.advancedandroidapp.data.local.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.example.advancedandroidapp.data.models.UserProfile

@ProvidedTypeConverter
class UserProfileConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): UserProfile? {
        if (value == null) return null
        return gson.fromJson(value, UserProfile::class.java)
    }

    @TypeConverter
    fun fromUserProfile(profile: UserProfile?): String? {
        if (profile == null) return null
        return gson.toJson(profile)
    }
}
