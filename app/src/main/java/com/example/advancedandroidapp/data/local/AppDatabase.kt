package com.example.advancedandroidapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.advancedandroidapp.data.models.*
import com.example.advancedandroidapp.data.local.converters.DateConverter
import com.example.advancedandroidapp.data.local.converters.ListConverter
import com.example.advancedandroidapp.data.local.dao.*

@Database(
    entities = [
        UserProfile::class,
        Location::class,
        UserSettings::class,
        CachedLocation::class,
        OfflineAction::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun locationDao(): LocationDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun cachedLocationDao(): CachedLocationDao
    abstract fun offlineActionDao(): OfflineActionDao

    companion object {
        private const val DATABASE_NAME = "advanced_android_app.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(object : RoomDatabase.Callback() {
                    // Add any necessary database callbacks here
                })
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
