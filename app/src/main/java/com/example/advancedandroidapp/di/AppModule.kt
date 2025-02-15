package com.example.advancedandroidapp.di

import android.content.Context
import androidx.room.Room
import com.example.advancedandroidapp.data.api.ApiService
import com.example.advancedandroidapp.data.local.AppDatabase
import com.example.advancedandroidapp.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .addMigrations(AppDatabase.MIGRATION_1_2)
        .enableMultiInstanceInvalidation()
        .fallbackToDestructiveMigration() // Only as last resort if migration fails
        .build()
    }

    @Provides
    @Singleton
    fun provideNetworkUtils(
        @ApplicationContext context: Context
    ): NetworkUtils {
        return NetworkUtils(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(appDatabase: AppDatabase) = appDatabase.userProfileDao()

    @Provides
    @Singleton
    fun provideLocationDao(appDatabase: AppDatabase) = appDatabase.locationDao()

    @Provides
    @Singleton
    fun provideUserSettingsDao(appDatabase: AppDatabase) = appDatabase.userSettingsDao()

    @Provides
    @Singleton
    fun provideCachedLocationDao(appDatabase: AppDatabase) = appDatabase.cachedLocationDao()

    @Provides
    @Singleton
    fun provideOfflineActionDao(appDatabase: AppDatabase) = appDatabase.offlineActionDao()
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = com.google.firebase.auth.FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase() = com.google.firebase.database.FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage() = com.google.firebase.storage.FirebaseStorage.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ) = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideGeocoder(
        @ApplicationContext context: Context
    ) = android.location.Geocoder(context)
}

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ) = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun providePreferencesEditor(
        sharedPreferences: android.content.SharedPreferences
    ) = sharedPreferences.edit()
}
