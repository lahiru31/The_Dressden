package com.example.advancedandroidapp.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInHelper @Inject constructor(
    private val context: Context
) {
    private var googleSignInClient: GoogleSignInClient? = null

    fun getGoogleSignInClient(): GoogleSignInClient {
        if (googleSignInClient == null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_CLIENT_ID)
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(context, gso)
        }
        return googleSignInClient!!
    }

    fun signOut() {
        googleSignInClient?.signOut()
    }

    fun revokeAccess() {
        googleSignInClient?.revokeAccess()
    }

    fun getLastSignedInAccount() = GoogleSignIn.getLastSignedInAccount(context)

    companion object {
        // Replace this with your actual Web Client ID from Google Cloud Console
        private const val WEB_CLIENT_ID = "your-web-client-id.apps.googleusercontent.com"
    }
}

sealed class GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult()
    data class Error(val message: String) : GoogleSignInResult()
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class SignOutResult {
    object Success : SignOutResult()
    data class Error(val message: String) : SignOutResult()
}

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)

data class UserData(
    val userId: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?
)

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): AuthResult
    suspend fun signOut(): SignOutResult
    suspend fun getCurrentUser(): UserData?
    suspend fun isUserAuthenticated(): Boolean
}
