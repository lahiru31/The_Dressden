package com.example.advancedandroidapp.data.local.converters

import androidx.room.TypeConverter
import com.example.advancedandroidapp.data.models.UserProfile
import com.google.gson.Gson

class UserProfileConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromUserProfile(userProfile: UserProfile?): String? {
        return userProfile?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toUserProfile(value: String?): UserProfile? {
        return value?.let { gson.fromJson(it, UserProfile::class.java) }
    }
}
