package com.example.advancedandroidapp.data.api

import com.example.advancedandroidapp.utils.NetworkException
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ErrorHandlingCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        val callType = getParameterUpperBound(0, returnType as ParameterizedType)
        return ErrorHandlingCallAdapter<Any>(callType)
    }

    private class ErrorHandlingCallAdapter<R>(
        private val responseType: Type
    ) : CallAdapter<R, Call<R>> {

        override fun responseType(): Type = responseType

        override fun adapt(call: Call<R>): Call<R> {
            return ErrorHandlingCall(call)
        }
    }

    private class ErrorHandlingCall<R>(
        private val delegate: Call<R>
    ) : Call<R> by delegate {

        override fun execute() = try {
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
            when (e) {
                is NetworkException -> throw e
                is java.net.UnknownHostException,
                is java.net.ConnectException -> throw NetworkException.NoConnectivity
                else -> throw NetworkException.UnknownError(e.message)
            }
        }

        override fun enqueue(callback: retrofit2.Callback<R>) {
            delegate.enqueue(object : retrofit2.Callback<R> {
                override fun onResponse(call: Call<R>, response: retrofit2.Response<R>) {
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
    }
}
