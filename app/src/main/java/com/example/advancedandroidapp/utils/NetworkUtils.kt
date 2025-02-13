package com.example.advancedandroidapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkUtils @Inject constructor(private val context: Context) {

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun isNetworkAvailable(): Boolean {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun getNetworkType(): NetworkType {
        val networkCapabilities = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return NetworkType.NONE

        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            else -> NetworkType.NONE
        }
    }

    fun observeNetworkConnectivity(): Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val isConnected = when {
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> true
                    else -> false
                }
                trySend(isConnected)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Initial network state
        trySend(isNetworkAvailable())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    fun isHighSpeedConnection(): Boolean {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    actNw.linkDownstreamBandwidthKbps >= HIGH_SPEED_THRESHOLD_KBPS
                } else {
                    true // Cannot determine speed on older Android versions
                }
            }
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun isMeteredConnection(): Boolean {
        val networkCapabilities = connectivityManager.activeNetwork ?: return true
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return true
        return !actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
    }

    companion object {
        private const val HIGH_SPEED_THRESHOLD_KBPS = 1000 // 1 Mbps threshold
    }
}

enum class NetworkType {
    WIFI,
    CELLULAR,
    ETHERNET,
    NONE
}

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Exception) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

sealed class NetworkException : Exception() {
    object NoConnectivity : NetworkException()
    object ServerError : NetworkException()
    data class HttpError(val code: Int, val message: String) : NetworkException()
    data class UnknownError(override val message: String?) : NetworkException()
}
