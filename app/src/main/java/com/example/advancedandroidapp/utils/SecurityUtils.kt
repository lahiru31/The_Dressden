package com.example.advancedandroidapp.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityUtils @Inject constructor(
    private val context: Context
) {
    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    private val encryptedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun encryptData(data: String, alias: String = DEFAULT_KEY_ALIAS): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey(alias))

        val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        val combinedBytes = cipher.iv + encryptedBytes

        return Base64.encodeToString(combinedBytes, Base64.DEFAULT)
    }

    fun decryptData(encryptedData: String, alias: String = DEFAULT_KEY_ALIAS): String {
        val combined = Base64.decode(encryptedData, Base64.DEFAULT)
        val cipher = Cipher.getInstance(TRANSFORMATION)

        // Extract IV and encrypted data
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = combined.copyOfRange(GCM_IV_LENGTH, combined.size)

        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(alias),
            GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
        )

        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    fun hashString(input: String, algorithm: String = "SHA-256"): String {
        val bytes = MessageDigest
            .getInstance(algorithm)
            .digest(input.toByteArray())
        return bytes.fold("") { str, byte ->
            str + "%02x".format(byte)
        }
    }

    fun secureRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun saveSecurely(key: String, value: String) {
        encryptedPreferences.edit().putString(key, value).apply()
    }

    fun getSecurely(key: String, defaultValue: String? = null): String? {
        return encryptedPreferences.getString(key, defaultValue)
    }

    fun removeSecurely(key: String) {
        encryptedPreferences.edit().remove(key).apply()
    }

    fun clearSecureStorage() {
        encryptedPreferences.edit().clear().apply()
    }

    private fun getOrCreateSecretKey(alias: String): SecretKey {
        keyStore.getKey(alias, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val DEFAULT_KEY_ALIAS = "default_encryption_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_SIZE = 256
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 16
        private const val ENCRYPTED_PREFS_FILE_NAME = "encrypted_preferences"

        // Token encryption constants
        private const val TOKEN_KEY_ALIAS = "auth_token_key"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val ACCESS_TOKEN_KEY = "access_token"
    }
}

sealed class SecurityResult<out T> {
    data class Success<T>(val data: T) : SecurityResult<T>()
    data class Error(val exception: Exception) : SecurityResult<Nothing>()
}

data class EncryptedData(
    val data: String,
    val iv: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedData

        if (data != other.data) return false
        if (!iv.contentEquals(other.iv)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + iv.contentHashCode()
        return result
    }
}

interface Encryption {
    fun encrypt(data: String): EncryptedData
    fun decrypt(encryptedData: EncryptedData): String
}

interface Hashing {
    fun hash(data: String): String
    fun verify(data: String, hash: String): Boolean
}

class SecurityException(message: String, cause: Throwable? = null) : Exception(message, cause)
