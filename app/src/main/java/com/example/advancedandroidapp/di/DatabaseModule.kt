package com.example.advancedandroidapp.di

import android.content.Context
import androidx.room.Room
import com.example.advancedandroidapp.data.local.AppDatabase
import com.example.advancedandroidapp.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    fun provideUserSettingsDao(database: AppDatabase): UserSettingsDao {
        return database.userSettingsDao()
    }

    @Provides
    fun provideLocationDao(database: AppDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    fun provideLocationTagDao(database: AppDatabase): LocationTagDao {
        return database.locationTagDao()
    }

    @Provides
    fun provideLocationReviewDao(database: AppDatabase): LocationReviewDao {
        return database.locationReviewDao()
    }

    @Provides
    fun provideCachedLocationDao(database: AppDatabase): CachedLocationDao {
        return database.cachedLocationDao()
    }

    @Provides
    fun provideOfflineActionDao(database: AppDatabase): OfflineActionDao {
        return database.offlineActionDao()
    }
}
