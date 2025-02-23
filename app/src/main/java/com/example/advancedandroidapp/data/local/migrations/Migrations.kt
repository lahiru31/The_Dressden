package com.example.advancedandroidapp.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create users table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY NOT NULL,
                email TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
        """)

        // Create user_profiles table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_profiles (
                user_id TEXT PRIMARY KEY NOT NULL,
                username TEXT NOT NULL,
                full_name TEXT NOT NULL,
                avatar_url TEXT,
                bio TEXT,
                phone_number TEXT,
                updated_at INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
            )
        """)

        // Create user_settings table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_settings (
                user_id TEXT PRIMARY KEY NOT NULL,
                notifications_enabled INTEGER NOT NULL DEFAULT 1,
                dark_mode_enabled INTEGER NOT NULL DEFAULT 0,
                language TEXT NOT NULL DEFAULT 'en',
                location_tracking_enabled INTEGER NOT NULL DEFAULT 1,
                data_backup_enabled INTEGER NOT NULL DEFAULT 1,
                last_sync_timestamp INTEGER,
                FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
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
                updated_at INTEGER NOT NULL,
                FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE CASCADE
            )
        """)

        // Create location_tags table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS location_tags (
                id TEXT PRIMARY KEY NOT NULL,
                name TEXT NOT NULL
            )
        """)

        // Create location_tag_map table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS location_tag_map (
                location_id TEXT NOT NULL,
                tag_id TEXT NOT NULL,
                PRIMARY KEY (location_id, tag_id),
                FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE CASCADE,
                FOREIGN KEY (tag_id) REFERENCES location_tags (id) ON DELETE CASCADE
            )
        """)

        // Create location_reviews table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS location_reviews (
                id TEXT PRIMARY KEY NOT NULL,
                location_id TEXT NOT NULL,
                user_id TEXT NOT NULL,
                rating REAL NOT NULL,
                comment TEXT,
                photos TEXT,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE CASCADE,
                FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
            )
        """)

        // Create user_favorites table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS user_favorites (
                user_id TEXT NOT NULL,
                location_id TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                PRIMARY KEY (user_id, location_id),
                FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE CASCADE
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
                sync_status TEXT NOT NULL DEFAULT 'pending'
            )
        """)

        // Create offline_actions table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS offline_actions (
                id TEXT PRIMARY KEY NOT NULL,
                type TEXT NOT NULL,
                data TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                status TEXT NOT NULL DEFAULT 'pending',
                retry_count INTEGER NOT NULL DEFAULT 0
            )
        """)

        // Create necessary indices
        database.execSQL("CREATE INDEX IF NOT EXISTS index_locations_category ON locations(category)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_locations_rating ON locations(rating)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_locations_created_by ON locations(created_by)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_location_reviews_rating ON location_reviews(rating)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_location_reviews_location_id ON location_reviews(location_id)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_location_reviews_user_id ON location_reviews(user_id)")
    }
}
