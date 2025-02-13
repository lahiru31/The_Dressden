# The Dressden Android App

## Project Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17 (Required - Do not use higher versions)
- Android SDK 34
- Gradle 8.9

### Build Environment
```bash
# Required Java version
java -version  # Should show Java 17

# Gradle version
./gradlew --version  # Should show Gradle 8.9
```

### Gradle Configuration

The project uses:
- Gradle 8.9
- Android Gradle Plugin 8.2.0
- JDK 17
- Latest AndroidX and support libraries

Build optimizations enabled:
- Parallel execution
- Configuration cache
- Build cache
- File system watching
- Kapt optimizations
- R8 full mode

### Build Types
- Debug: Uses development API
- Staging: Uses staging API
- Release: Uses production API with minification and shrinking

### Build Instructions
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run the following commands:
```bash
# Clean project
./gradlew clean

# Build debug variant
./gradlew :app:assembleDebug

# Build release variant
./gradlew :app:assembleRelease
```

### Dependencies
- AndroidX Core and AppCompat
- Material Design Components
- Architecture Components (ViewModel, LiveData)
- Firebase (Auth, Database, Crashlytics)
- Retrofit for networking
- Room for local database
- Coroutines for async operations
- Hilt for dependency injection
- Google Maps and Location services

### Features
- MVVM Architecture
- Clean Architecture principles
- Repository pattern
- Offline-first approach
- Background sync
- Location tracking
- Media handling

### Configuration Files
- settings.gradle: Repository and dependency management
- build.gradle: Project-wide configuration
- app/build.gradle: App-specific configuration
- gradle.properties: Build optimizations and settings

### Important Notes
1. Java Version:
   - Project is configured for Java 17
   - Using higher versions will cause compatibility issues

2. Gradle Version:
   - Project uses Gradle 8.9
   - Configuration cache is enabled
   - Custom cache directory configured

3. Android Configuration:
   - minSdk: 24
   - targetSdk: 34
   - compileSdk: 34

### License
This project is licensed under the MIT License - see the LICENSE file for details.
