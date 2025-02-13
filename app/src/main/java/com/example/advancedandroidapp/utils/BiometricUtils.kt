package com.example.advancedandroidapp.utils

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val biometricManager = BiometricManager.from(context)

    fun canAuthenticate(): BiometricStatus {
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricStatus.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                BiometricStatus.NotAvailable("No biometric hardware")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricStatus.NotAvailable("Biometric hardware unavailable")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricStatus.NotEnrolled
            else -> BiometricStatus.NotAvailable("Unknown error")
        }
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        description: String,
        negativeButtonText: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .setConfirmationRequired(false)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun showBiometricPromptForEncryption(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        description: String,
        negativeButtonText: String,
        onSuccess: (BiometricPrompt.CryptoObject) -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                result.cryptoObject?.let { onSuccess(it) }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText(negativeButtonText)
            .setConfirmationRequired(false)
            .build()

        // Create crypto object here and pass it to authenticate
        // This is just a placeholder - implement actual crypto object creation based on your needs
        // biometricPrompt.authenticate(promptInfo, cryptoObject)
        biometricPrompt.authenticate(promptInfo)
    }

    companion object {
        private val BIOMETRIC_STRONG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        } else {
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        }
    }
}

sealed class BiometricStatus {
    object Available : BiometricStatus()
    object NotEnrolled : BiometricStatus()
    data class NotAvailable(val reason: String) : BiometricStatus()
}

sealed class BiometricResult {
    object Success : BiometricResult()
    data class Error(val message: String) : BiometricResult()
    object Failed : BiometricResult()
    object Canceled : BiometricResult()
}

interface BiometricAuthListener {
    fun onBiometricAuthSuccess()
    fun onBiometricAuthError(error: String)
    fun onBiometricAuthFailed()
}

data class BiometricPromptInfo(
    val title: String,
    val subtitle: String,
    val description: String,
    val negativeButtonText: String
)
