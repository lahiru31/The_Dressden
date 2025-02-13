package com.example.advancedandroidapp.di

import android.content.Context
import com.example.advancedandroidapp.data.api.ApiService
import com.example.advancedandroidapp.utils.Constants
import com.example.advancedandroidapp.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        return Cache(context.cacheDir, Constants.CACHE_SIZE)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .method(original.method, original.body)

            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideCacheInterceptor(networkUtils: NetworkUtils): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            
            if (networkUtils.isNetworkAvailable()) {
                request = request.newBuilder()
                    .header("Cache-Control", "public, max-age=${Constants.CACHE_MAX_AGE}")
                    .build()
            } else {
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=${Constants.CACHE_MAX_STALE}")
                    .build()
            }
            
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        authInterceptor: Interceptor,
        cacheInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(authInterceptor)
            .addInterceptor(cacheInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.API_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.API_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.API_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("${Constants.BASE_URL}${Constants.API_VERSION}/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
