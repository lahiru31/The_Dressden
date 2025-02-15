package com.example.advancedandroidapp.data.models

import androidx.room.*
import com.example.advancedandroidapp.data.local.converters.DateConverter
import com.example.advancedandroidapp.data.local.converters.ListConverter
import java.util.Date

@Entity(
    tableName = "locations",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("category"),
        Index("rating"),
        Index("created_by")
    ]
)
data class Location(
    @PrimaryKey
    val id: String,
    
    val name: String,
    
    val description: String?,
    
    val latitude: Double,
    
    val longitude: Double,
    
    val address: String?,
    
    val category: String,
    
    val rating: Float?,
    
    @TypeConverters(ListConverter::class)
    val photos: List<String>?,
    
    @ColumnInfo(name = "created_by")
    val createdBy: String,
    
    @ColumnInfo(name = "created_at")
    @TypeConverters(DateConverter::class)
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    @TypeConverters(DateConverter::class)
    val updatedAt: Date = Date()
)

@Entity(tableName = "location_tags")
data class LocationTag(
    @PrimaryKey
    val id: String,
    
    val name: String
)

@Entity(
    tableName = "location_tag_map",
    primaryKeys = ["location_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = ["id"],
            childColumns = ["location_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocationTag::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("location_id"),
        Index("tag_id")
    ]
)
data class LocationTagMap(
    @ColumnInfo(name = "location_id")
    val locationId: String,
    
    @ColumnInfo(name = "tag_id")
    val tagId: String
)

@Entity(
    tableName = "location_reviews",
    foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = ["id"],
            childColumns = ["location_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("location_id"),
        Index("user_id"),
        Index("rating")
    ]
)
data class LocationReview(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "location_id")
    val locationId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    val rating: Float,
    
    val comment: String?,
    
    @TypeConverters(ListConverter::class)
    val photos: List<String>?,
    
    @ColumnInfo(name = "created_at")
    @TypeConverters(DateConverter::class)
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    @TypeConverters(DateConverter::class)
    val updatedAt: Date = Date()
)

@Entity(
    tableName = "user_favorites",
    primaryKeys = ["user_id", "location_id"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Location::class,
            parentColumns = ["id"],
            childColumns = ["location_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("user_id"),
        Index("location_id")
    ]
)
data class UserFavorite(
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "location_id")
    val locationId: String,
    
    @ColumnInfo(name = "created_at")
    @TypeConverters(DateConverter::class)
    val createdAt: Date = Date()
)
