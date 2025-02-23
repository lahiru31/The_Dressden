package com.example.advancedandroidapp.data.api

import com.example.advancedandroidapp.utils.NetworkException
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject

class ErrorHandlingCallAdapterFactory @Inject constructor() : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        // Ensure we're dealing with a Call type
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        check(returnType is ParameterizedType) {
            "Call return type must be parameterized as Call<Foo> or Call<out Foo>"
        }

        val responseType = getParameterUpperBound(0, returnType)
        return ErrorHandlingCallAdapter<Any>(responseType)
    }
}

private class ErrorHandlingCallAdapter<R>(
    private val responseType: Type
) : CallAdapter<R, Call<R>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): Call<R> = ErrorHandlingCall(call)
}

private class ErrorHandlingCall<R>(
    private val delegate: Call<R>
) : Call<R> {

    override fun enqueue(callback: retrofit2.Callback<R>) {
        delegate.enqueue(object : retrofit2.Callback<R> {
            override fun onResponse(call: Call<R>, response: Response<R>) {
                when {
                    response.isSuccessful -> callback.onResponse(call, response)
                    response.code() in 500..599 -> callback.onFailure(call, NetworkException.ServerError)
                    else -> callback.onFailure(
                        call,
                        NetworkException.HttpError(
                            code = response.code(),
                            message = response.message()
                        )
                    )
                }
            }

            override fun onFailure(call: Call<R>, t: Throwable) {
                val exception = when (t) {
                    is NetworkException -> t
                    is java.net.UnknownHostException,
                    is java.net.ConnectException -> NetworkException.NoConnectivity
                    else -> NetworkException.UnknownError(t.message)
                }
                callback.onFailure(call, exception)
            }
        })
    }

    override fun execute(): Response<R> {
        return try {
            val response = delegate.execute()
            when {
                response.isSuccessful -> response
                response.code() in 500..599 -> throw NetworkException.ServerError
                else -> throw NetworkException.HttpError(
                    code = response.code(),
                    message = response.message()
                )
            }
        } catch (e: Exception) {
            throw when (e) {
                is NetworkException -> e
                is java.net.UnknownHostException,
                is java.net.ConnectException -> NetworkException.NoConnectivity
                else -> NetworkException.UnknownError(e.message)
            }
        }
    }

    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun cancel() = delegate.cancel()
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun request(): okhttp3.Request = delegate.request()
    override fun timeout(): okio.Timeout = delegate.timeout()
    override fun clone(): Call<R> = ErrorHandlingCall(delegate.clone())
}
