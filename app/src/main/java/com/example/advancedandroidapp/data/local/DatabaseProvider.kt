package com.example.advancedandroidapp.data.local

import android.content.Context
import androidx.room.Room
import com.example.advancedandroidapp.data.local.migrations.MIGRATION_1_2

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
            INSTANCE = instance
            instance
        }
    }

    fun destroyInstance() {
        INSTANCE = null
    }
}
