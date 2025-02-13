# The Dressden Android App

## Project Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17 (Required - Do not use higher versions)
- Android SDK 34
- Gradle 8.9
- Android Gradle Plugin (AGP) 8.2.0

### Build Environment
```bash
# Required Java version
java -version  # Should show Java 17

# Gradle version
./gradlew --version  # Should show Gradle 8.9
```

### Configuration

1. **API Configuration**
   - Create a `local.properties` file in the project root
   - Add your API configuration:
   ```properties
   MAPS_API_KEY=your_google_maps_api_key
   ```

2. **Firebase Setup**
   - Add `google-services.json` to the `app` directory
   - Enable required Firebase services (Authentication, Realtime Database, Crashlytics)

3. **Release Signing**
   - Create a release keystore file
   - Configure signing credentials in environment variables:
   ```bash
   export KEYSTORE_PASSWORD=your_keystore_password
   export KEY_ALIAS=your_key_alias
   export KEY_PASSWORD=your_key_password
   ```

### Build Variants

The app has three build variants:
- **Debug**: Uses development API (`dev-api.yourdomain.com`)
- **Staging**: Uses staging API (`staging-api.yourdomain.com`)
- **Release**: Uses production API (`api.yourdomain.com`)

### Architecture

The app follows MVVM architecture with Clean Architecture principles:
- **Data Layer**: Repository pattern, Room Database, Retrofit API
- **Domain Layer**: Use cases and business logic
- **Presentation Layer**: ViewModels, Fragments, Activities

### Key Features

1. **Authentication**
   - Email/Password login
   - Google Sign-In
   - Biometric authentication

2. **Location Services**
   - Real-time location tracking
   - Geofencing
   - Maps integration

3. **Data Management**
   - Offline-first approach
   - Background sync
   - Cache management

4. **Media Handling**
   - Image/Video capture
   - Media compression
   - File management

### Permissions

The app requires the following permissions:
- Location (Fine and Coarse)
- Camera
- Media access (Photos and Videos)
- Notifications
- Internet
- Network State

### Testing

Run tests using:
```bash
./gradlew test        # Unit tests
./gradlew connectedAndroidTest  # Instrumentation tests
```

### Gradle Configuration

The project uses:
- Gradle 8.9 (Required - Do not use higher versions)
- Android Gradle Plugin 8.2.0
- JDK 17 compatibility (Required - Do not use higher versions)
- Latest AndroidX and support libraries

To update Gradle wrapper:
```bash
./gradlew wrapper --gradle-version 8.9
```

### Known Issues and TODOs

1. **Security**
   - [ ] Implement certificate pinning
   - [ ] Add ProGuard rules for release builds
   - [ ] Encrypt sensitive data in SharedPreferences

2. **Performance**
   - [ ] Implement image caching
   - [ ] Optimize database queries
   - [ ] Add pagination for large datasets

3. **Features**
   - [ ] Implement offline-first sync strategy
   - [ ] Add push notifications
   - [ ] Implement deep linking

### Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Important Notes

1. **Java Version Compatibility**
   - The project is configured to use Java 17
   - Using Java versions higher than 17 will cause compatibility issues
   - Make sure your Android Studio is using JDK 17

2. **Gradle Version Compatibility**
   - The project uses Gradle 8.9
   - Using Gradle versions higher than 8.9 may cause compatibility issues
   - The gradle-wrapper.properties is configured for Gradle 8.9

### License

This project is licensed under the MIT License - see the LICENSE file for details.
