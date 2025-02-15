package com.example.advancedandroidapp.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create user_profiles table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_profiles (
                user_id TEXT PRIMARY KEY NOT NULL,
                username TEXT NOT NULL,
                full_name TEXT NOT NULL,
                avatar_url TEXT,
                bio TEXT,
                phone_number TEXT,
                updated_at INTEGER NOT NULL
            )
        """)

        // Create locations table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS locations (
                id TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                description TEXT,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                address TEXT,
                category TEXT NOT NULL,
                rating REAL,
                photos TEXT,
                created_by TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
        """)

        // Create cached_locations table
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

        // Create user_settings table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_settings (
                userId TEXT PRIMARY KEY NOT NULL,
                notificationsEnabled INTEGER NOT NULL DEFAULT 1,
                darkModeEnabled INTEGER NOT NULL DEFAULT 0,
                language TEXT NOT NULL DEFAULT 'en',
                locationTrackingEnabled INTEGER NOT NULL DEFAULT 1,
                dataBackupEnabled INTEGER NOT NULL DEFAULT 1,
                lastSyncTimestamp INTEGER
            )
        """)

        // Create offline_actions table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS offline_actions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                type TEXT NOT NULL,
                entityType TEXT NOT NULL,
                entityId TEXT NOT NULL,
                data TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                synced INTEGER NOT NULL DEFAULT 0
            )
        """)
    }
}
