package com.example.advancedandroidapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.advancedandroidapp.data.local.dao.*
import com.example.advancedandroidapp.data.local.converters.*
import com.example.advancedandroidapp.data.models.*

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
    value = [
        DateConverter::class,
        ListConverter::class,
        LocationConverter::class,
        UserProfileConverter::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun cachedLocationDao(): CachedLocationDao
    abstract fun offlineActionDao(): OfflineActionDao

    companion object {
        const val DATABASE_NAME = "the_dressden_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add UserSettings table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_settings (
                        userId TEXT PRIMARY KEY NOT NULL,
                        theme TEXT NOT NULL DEFAULT 'system',
                        notificationsEnabled INTEGER NOT NULL DEFAULT 1,
                        locationTrackingEnabled INTEGER NOT NULL DEFAULT 0,
                        lastUpdated INTEGER NOT NULL DEFAULT 0
                    )
                """)

                // Add CachedLocation table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS cached_locations (
                        id TEXT PRIMARY KEY NOT NULL,
                        latitude REAL NOT NULL,
                        longitude REAL NOT NULL,
                        address TEXT,
                        timestamp INTEGER NOT NULL,
                        syncStatus TEXT NOT NULL DEFAULT 'pending'
                    )
                """)

                // Add OfflineAction table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS offline_actions (
                        id TEXT PRIMARY KEY NOT NULL,
                        type TEXT NOT NULL,
                        data TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        status TEXT NOT NULL DEFAULT 'pending',
                        retryCount INTEGER NOT NULL DEFAULT 0
                    )
                """)
            }
        }
    }
}
