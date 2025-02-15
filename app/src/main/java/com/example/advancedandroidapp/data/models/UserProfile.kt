package com.example.advancedandroidapp.data.models

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.example.advancedandroidapp.data.local.converters.DateConverter
import java.util.Date

@Entity(tableName = "user_profiles")
@TypeConverters(DateConverter::class)
data class UserProfile(
    @PrimaryKey
    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("full_name")
    @ColumnInfo(name = "full_name")
    val fullName: String,

    @SerializedName("avatar_url")
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @SerializedName("bio")
    val bio: String?,

    @SerializedName("phone_number")
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String?,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)
