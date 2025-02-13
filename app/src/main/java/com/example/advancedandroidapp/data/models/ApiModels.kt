package com.example.advancedandroidapp.data.models

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error<T>(
        val code: Int,
        val message: String,
        val errorBody: ErrorResponse? = null
    ) : ApiResponse<T>()
    data class Loading<T>(val isLoading: Boolean = true) : ApiResponse<T>()
}

data class ErrorResponse(
    val status: Int,
    val message: String,
    val errors: List<String>? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class AuthenticationException(message: String) : Exception(message)

data class NetworkResult<out T>(
    val status: Status,
    val data: T?,
    val error: ErrorResponse?,
    val message: String?
) {
    companion object {
        fun <T> success(data: T?): NetworkResult<T> {
            return NetworkResult(Status.SUCCESS, data, null, null)
        }

        fun <T> error(message: String, error: ErrorResponse? = null): NetworkResult<T> {
            return NetworkResult(Status.ERROR, null, error, message)
        }

        fun <T> loading(): NetworkResult<T> {
            return NetworkResult(Status.LOADING, null, null, null)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class MediaUploadResponse(
    val url: String,
    val type: String,
    val size: Long,
    val uploadedAt: java.util.Date
)
