package com.example.advancedandroidapp.data.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class User(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("token")
    val token: String,
    
    @SerializedName("created_at")
    val createdAt: Date
)

data class MediaUploadResponse(
    @SerializedName("url")
    val url: String,
    
    @SerializedName("type")
    val type: String,
    
    @SerializedName("size")
    val size: Long,
    
    @SerializedName("uploaded_at")
    val uploadedAt: Date
)

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}
