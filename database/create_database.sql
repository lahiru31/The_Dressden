-- Create and use the database
CREATE DATABASE IF NOT EXISTS dressden;
USE dressden;

-- Create Users table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create UserProfiles table
CREATE TABLE user_profiles (
    user_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    avatar_url TEXT,
    bio TEXT,
    phone_number VARCHAR(20),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create UserSettings table
CREATE TABLE user_settings (
    user_id VARCHAR(36) PRIMARY KEY,
    notifications_enabled BOOLEAN DEFAULT TRUE,
    dark_mode_enabled BOOLEAN DEFAULT FALSE,
    language VARCHAR(10) DEFAULT 'en',
    location_tracking_enabled BOOLEAN DEFAULT TRUE,
    data_backup_enabled BOOLEAN DEFAULT TRUE,
    last_sync_timestamp BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create Locations table
CREATE TABLE locations (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    address TEXT,
    category VARCHAR(50) NOT NULL,
    rating FLOAT,
    photos JSON,
    created_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Create CachedLocations table
CREATE TABLE cached_locations (
    id VARCHAR(36) PRIMARY KEY,
    location_id VARCHAR(36) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    address TEXT,
    timestamp BIGINT NOT NULL,
    sync_status ENUM('pending', 'synced', 'failed') DEFAULT 'pending',
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE
);

-- Create OfflineActions table
CREATE TABLE offline_actions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('CREATE', 'UPDATE', 'DELETE') NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    data JSON NOT NULL,
    timestamp BIGINT NOT NULL,
    synced BOOLEAN DEFAULT FALSE,
    retry_count INT DEFAULT 0
);

-- Create UserFavorites table (Many-to-Many relationship between Users and Locations)
CREATE TABLE user_favorites (
    user_id VARCHAR(36),
    location_id VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, location_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE
);

-- Create LocationTags table
CREATE TABLE location_tags (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create LocationTagMap table (Many-to-Many relationship between Locations and Tags)
CREATE TABLE location_tag_map (
    location_id VARCHAR(36),
    tag_id VARCHAR(36),
    PRIMARY KEY (location_id, tag_id),
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES location_tags(id) ON DELETE CASCADE
);

-- Create LocationReviews table
CREATE TABLE location_reviews (
    id VARCHAR(36) PRIMARY KEY,
    location_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    rating FLOAT NOT NULL,
    comment TEXT,
    photos JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for better query performance
CREATE INDEX idx_locations_category ON locations(category);
CREATE INDEX idx_locations_rating ON locations(rating);
CREATE INDEX idx_cached_locations_timestamp ON cached_locations(timestamp);
CREATE INDEX idx_offline_actions_timestamp ON offline_actions(timestamp);
CREATE INDEX idx_location_reviews_rating ON location_reviews(rating);

-- Grant permissions
GRANT ALL PRIVILEGES ON dressden.* TO 'root'@'localhost' IDENTIFIED BY '@Applesafari12';
FLUSH PRIVILEGES;
