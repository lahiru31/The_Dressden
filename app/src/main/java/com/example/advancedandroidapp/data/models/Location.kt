package com.example.advancedandroidapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
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
    val rating: Float?,

    @SerializedName("photos")
    val photos: List<String>?,

    @SerializedName("created_by")
    @ColumnInfo(name = "created_by")
    val createdBy: String,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: Date,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)
