package com.example.advancedandroidapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.advancedandroidapp.data.local.dao.LocationDao
import com.example.advancedandroidapp.data.local.dao.UserProfileDao
import com.example.advancedandroidapp.data.models.Location
import com.example.advancedandroidapp.data.models.UserProfile
import com.example.advancedandroidapp.data.local.converters.DateConverter

@Database(
    entities = [
        Location::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        const val DATABASE_NAME = "the_dressden_db"
    }
}
