package com.example.advancedandroidapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.advancedandroidapp.data.local.dao.LocationDao
import com.example.advancedandroidapp.data.local.dao.UserProfileDao
import com.example.advancedandroidapp.data.models.Location
import com.example.advancedandroidapp.data.models.UserProfile
import com.example.advancedandroidapp.data.models.UserSettings
import com.example.advancedandroidapp.data.models.CachedLocation
import com.example.advancedandroidapp.data.models.OfflineAction
import com.example.advancedandroidapp.data.local.converters.DateConverter
import com.example.advancedandroidapp.data.local.converters.ListConverter
import com.example.advancedandroidapp.data.local.converters.LocationConverter
import com.example.advancedandroidapp.data.local.converters.UserProfileConverter

@Database(
    entities = [
        Location::class,
        UserProfile::class,
        UserSettings::class,
        CachedLocation::class,
        OfflineAction::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(
    DateConverter::class,
    ListConverter::class,
    LocationConverter::class,
    UserProfileConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        const val DATABASE_NAME = "the_dressden_db"
    }
}
