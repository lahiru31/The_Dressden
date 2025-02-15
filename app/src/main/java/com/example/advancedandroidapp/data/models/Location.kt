package com.example.advancedandroidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import com.example.advancedandroidapp.data.local.converters.DateConverter
import com.example.advancedandroidapp.data.local.converters.ListConverter
import com.google.gson.annotations.SerializedName
import java.util.Date

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("address")
    val address: String?,

    @SerializedName("category")
    val category: String,

    @SerializedName("rating")
    @ColumnInfo(name = "rating")
    val rating: Float?,

    @SerializedName("photos")
    @TypeConverters(ListConverter::class)
    val photos: List<String>?,

    @SerializedName("created_by")
    @ColumnInfo(name = "created_by")
    val createdBy: String,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    @TypeConverters(DateConverter::class)
    val createdAt: Date,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    @TypeConverters(DateConverter::class)
    val updatedAt: Date
)
