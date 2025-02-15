# Database Setup Instructions

## MySQL Database Setup

1. Login to MySQL as root:
```bash
mysql -u root -p
```
When prompted, enter the password: `@Applesafari12`

2. Create and set up the database:
```bash
mysql -u root -p"@Applesafari12" < create_database.sql
```

Or you can run these commands manually in MySQL:

```sql
CREATE DATABASE IF NOT EXISTS dressden;
USE dressden;
```

3. Verify the database was created:
```sql
SHOW DATABASES;
```

4. Verify the tables were created:
```sql
USE dressden;
SHOW TABLES;
```

Expected tables:
- users
- user_profiles
- user_settings
- locations
- cached_locations
- offline_actions
- user_favorites
- location_tags
- location_tag_map
- location_reviews

5. Verify the permissions:
```sql
SHOW GRANTS FOR 'root'@'localhost';
```

## Database Connection Details

- Database Name: `dressden`
- Username: `root`
- Password: `@Applesafari12`
- Host: `localhost`
- Port: `3306` (default MySQL port)

## Android Room Database

The Android app uses Room as an abstraction layer over SQLite. The database will be automatically created when the app first runs. No additional setup is required for the local database.

The database name in the Android app is: `dressden_db`

## Troubleshooting

If you encounter any permission issues:
```sql
GRANT ALL PRIVILEGES ON dressden.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

To reset the database:
```sql
DROP DATABASE dressden;
```
Then run the create_database.sql script again.
