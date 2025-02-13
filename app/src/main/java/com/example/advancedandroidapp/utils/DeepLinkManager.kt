package com.example.advancedandroidapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepLinkManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsManager: AnalyticsManager
) {
    private var navController: NavController? = null

    fun setNavController(controller: NavController) {
        navController = controller
    }

    fun handleDeepLink(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        return handleDeepLink(uri)
    }

    fun handleDeepLink(uri: Uri): Boolean {
        analyticsManager.logEvent(AnalyticsEvent.DeepLinkReceived(uri.toString()))

        return when {
            isLocationDeepLink(uri) -> handleLocationDeepLink(uri)
            isProfileDeepLink(uri) -> handleProfileDeepLink(uri)
            isSearchDeepLink(uri) -> handleSearchDeepLink(uri)
            else -> handleUnknownDeepLink(uri)
        }
    }

    fun createLocationDeepLink(locationId: String): Uri {
        return Uri.Builder()
            .scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(PATH_LOCATION)
            .appendPath(locationId)
            .build()
    }

    fun createProfileDeepLink(userId: String): Uri {
        return Uri.Builder()
            .scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(PATH_PROFILE)
            .appendPath(userId)
            .build()
    }

    fun createSearchDeepLink(query: String, category: String? = null): Uri {
        return Uri.Builder()
            .scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(PATH_SEARCH)
            .appendQueryParameter(PARAM_QUERY, query)
            .apply {
                category?.let {
                    appendQueryParameter(PARAM_CATEGORY, it)
                }
            }
            .build()
    }

    fun navigateToLocation(locationId: String) {
        val request = NavDeepLinkRequest.Builder
            .fromUri(createLocationDeepLink(locationId))
            .build()
        navController?.navigate(request)
    }

    fun navigateToProfile(userId: String) {
        val request = NavDeepLinkRequest.Builder
            .fromUri(createProfileDeepLink(userId))
            .build()
        navController?.navigate(request)
    }

    fun navigateToSearch(query: String, category: String? = null) {
        val request = NavDeepLinkRequest.Builder
            .fromUri(createSearchDeepLink(query, category))
            .build()
        navController?.navigate(request)
    }

    private fun isLocationDeepLink(uri: Uri): Boolean {
        return uri.pathSegments.firstOrNull() == PATH_LOCATION
    }

    private fun isProfileDeepLink(uri: Uri): Boolean {
        return uri.pathSegments.firstOrNull() == PATH_PROFILE
    }

    private fun isSearchDeepLink(uri: Uri): Boolean {
        return uri.pathSegments.firstOrNull() == PATH_SEARCH
    }

    private fun handleLocationDeepLink(uri: Uri): Boolean {
        val locationId = uri.pathSegments.getOrNull(1) ?: return false
        navigateToLocation(locationId)
        return true
    }

    private fun handleProfileDeepLink(uri: Uri): Boolean {
        val userId = uri.pathSegments.getOrNull(1) ?: return false
        navigateToProfile(userId)
        return true
    }

    private fun handleSearchDeepLink(uri: Uri): Boolean {
        val query = uri.getQueryParameter(PARAM_QUERY) ?: return false
        val category = uri.getQueryParameter(PARAM_CATEGORY)
        navigateToSearch(query, category)
        return true
    }

    private fun handleUnknownDeepLink(uri: Uri): Boolean {
        analyticsManager.logEvent(AnalyticsEvent.UnknownDeepLink(uri.toString()))
        return false
    }

    fun shareLocation(locationId: String, locationName: String) {
        val deepLink = createLocationDeepLink(locationId).toString()
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out $locationName: $deepLink")
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share Location")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }

    companion object {
        private const val SCHEME = "advancedapp"
        private const val AUTHORITY = "example.com"
        
        private const val PATH_LOCATION = "location"
        private const val PATH_PROFILE = "profile"
        private const val PATH_SEARCH = "search"
        
        private const val PARAM_QUERY = "query"
        private const val PARAM_CATEGORY = "category"
    }
}

sealed class DeepLinkResult {
    object Success : DeepLinkResult()
    data class Error(val message: String) : DeepLinkResult()
}

data class DeepLinkData(
    val type: DeepLinkType,
    val parameters: Map<String, String>
)

enum class DeepLinkType {
    LOCATION,
    PROFILE,
    SEARCH,
    UNKNOWN
}

sealed class AnalyticsEvent(
    name: String,
    parameters: Map<String, Any> = emptyMap()
) {
    data class DeepLinkReceived(val uri: String) : AnalyticsEvent(
        "deep_link_received",
        mapOf("uri" to uri)
    )

    data class UnknownDeepLink(val uri: String) : AnalyticsEvent(
        "unknown_deep_link",
        mapOf("uri" to uri)
    )
}
