package com.example.advancedandroidapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advancedandroidapp.data.models.User
import com.example.advancedandroidapp.data.repository.MainRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: MainRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isSignUpMode = MutableStateFlow(false)
    val isSignUpMode: StateFlow<Boolean> = _isSignUpMode

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        // Check if user is already signed in
        auth.currentUser?.let { user ->
            _authState.value = AuthState.Success(
                User(
                    id = user.uid,
                    email = user.email ?: "",
                    token = user.getIdToken(false).result?.token ?: "",
                    createdAt = java.util.Date()
                )
            )
        }
    }

    fun toggleAuthMode() {
        _isSignUpMode.value = !_isSignUpMode.value
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { firebaseUser ->
                    val token = firebaseUser.getIdToken(false).await().token ?: ""
                    val user = User(
                        id = firebaseUser.uid,
                        email = email,
                        token = token,
                        createdAt = java.util.Date()
                    )
                    repository.saveUserLocally(user)
                    _authState.value = AuthState.Success(user)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { firebaseUser ->
                    val token = firebaseUser.getIdToken(false).await().token ?: ""
                    val user = User(
                        id = firebaseUser.uid,
                        email = email,
                        token = token,
                        createdAt = java.util.Date()
                    )
                    repository.saveUserLocally(user)
                    _authState.value = AuthState.Success(user)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val result = auth.signInWithCredential(credential).await()
                result.user?.let { firebaseUser ->
                    val token = firebaseUser.getIdToken(false).await().token ?: ""
                    val user = User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        token = token,
                        createdAt = java.util.Date()
                    )
                    repository.saveUserLocally(user)
                    _authState.value = AuthState.Success(user)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google sign in failed")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.ResetEmailSent
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Password reset failed")
            } finally {
                _isLoading.value = false
            }
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        data class Success(val user: User) : AuthState()
        data class Error(val message: String) : AuthState()
        object ResetEmailSent : AuthState()
    }
}
