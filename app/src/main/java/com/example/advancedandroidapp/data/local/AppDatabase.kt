package com.example.advancedandroidapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.advancedandroidapp.data.local.converters.*
import com.example.advancedandroidapp.data.local.dao.*
import com.example.advancedandroidapp.data.models.*

@Database(
    entities = [
        User::class,
        UserProfile::class,
        UserSettings::class,
        Location::class,
        LocationTag::class,
        LocationTagMap::class,
        LocationReview::class,
        UserFavorite::class,
        CachedLocation::class,
        OfflineAction::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    DateConverter::class,
    ListConverter::class,
    LocationConverter::class,
    UserProfileConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun locationDao(): LocationDao
    abstract fun locationTagDao(): LocationTagDao
    abstract fun locationReviewDao(): LocationReviewDao
    abstract fun cachedLocationDao(): CachedLocationDao
    abstract fun offlineActionDao(): OfflineActionDao

    companion object {
        const val DATABASE_NAME = "dressden_db"
    }
}
