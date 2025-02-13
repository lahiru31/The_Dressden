package com.example.advancedandroidapp.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advancedandroidapp.data.models.UserProfile
import com.example.advancedandroidapp.data.repository.MainRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: MainRepository,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                auth.currentUser?.uid?.let { userId ->
                    repository.getUserProfile(userId).collect { profile ->
                        _userProfile.value = profile
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateUserProfile(profile)
                _isEditMode.value = false
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to update profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Upload image to Firebase Storage
                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
                val imageRef = storage.reference
                    .child("profile_images")
                    .child(userId)
                    .child("profile_${System.currentTimeMillis()}.jpg")

                val uploadTask = imageRef.putFile(imageUri).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

                // Update profile with new image URL
                _userProfile.value?.let { currentProfile ->
                    val updatedProfile = currentProfile.copy(
                        avatarUrl = downloadUrl
                    )
                    updateProfile(updatedProfile)
                }

            } catch (e: Exception) {
                _error.value = "Failed to update profile image: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                auth.signOut()
                // Clear local data
                repository.clearUserData()
                // Navigate to auth screen (handled by observer in fragment)
            } catch (e: Exception) {
                _error.value = "Failed to sign out: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setEditMode(enabled: Boolean) {
        _isEditMode.value = enabled
    }

    fun clearError() {
        _error.value = null
    }
}
