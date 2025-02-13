package com.example.advancedandroidapp.data.api

import com.example.advancedandroidapp.data.models.Location
import com.example.advancedandroidapp.data.models.User
import com.example.advancedandroidapp.data.models.UserProfile
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    companion object {
        const val BASE_URL = "https://your-api-domain.com/api/"
    }

    // User endpoints
    @POST("auth/login")
    suspend fun login(
        @Body credentials: Map<String, String>
    ): Response<User>

    @POST("auth/register")
    suspend fun register(
        @Body userData: Map<String, String>
    ): Response<User>

    @GET("users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfile>

    @PUT("users/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body profile: UserProfile
    ): Response<UserProfile>

    // Location endpoints
    @GET("locations")
    suspend fun getLocations(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Int
    ): Response<List<Location>>

    @GET("locations/{id}")
    suspend fun getLocationById(
        @Path("id") locationId: String
    ): Response<Location>

    @POST("locations")
    suspend fun createLocation(
        @Header("Authorization") token: String,
        @Body location: Location
    ): Response<Location>

    @PUT("locations/{id}")
    suspend fun updateLocation(
        @Header("Authorization") token: String,
        @Path("id") locationId: String,
        @Body location: Location
    ): Response<Location>

    @DELETE("locations/{id}")
    suspend fun deleteLocation(
        @Header("Authorization") token: String,
        @Path("id") locationId: String
    ): Response<Unit>

    // Media endpoints
    @Multipart
    @POST("media/upload")
    suspend fun uploadMedia(
        @Header("Authorization") token: String,
        @Part("file") file: okhttp3.MultipartBody.Part,
        @Part("type") type: okhttp3.RequestBody
    ): Response<Map<String, String>>

    // Settings endpoints
    @GET("settings")
    suspend fun getUserSettings(
        @Header("Authorization") token: String
    ): Response<Map<String, Any>>

    @PUT("settings")
    suspend fun updateUserSettings(
        @Header("Authorization") token: String,
        @Body settings: Map<String, Any>
    ): Response<Map<String, Any>>
}
