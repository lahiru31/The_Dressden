package com.example.advancedandroidapp.utils

import android.util.Patterns
import java.util.regex.Pattern

object ValidationUtils {
    // Email validation
    fun isValidEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult.Error("Email cannot be empty")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> 
                ValidationResult.Error("Invalid email format")
            else -> ValidationResult.Success
        }
    }

    // Password validation
    fun isValidPassword(password: String): ValidationResult {
        val passwordPattern = Pattern.compile(
            "^" +
            "(?=.*[0-9])" +          // at least 1 digit
            "(?=.*[a-z])" +          // at least 1 lower case letter
            "(?=.*[A-Z])" +          // at least 1 upper case letter
            "(?=.*[@#$%^&+=])" +     // at least 1 special character
            "(?=\\S+$)" +            // no white spaces
            ".{8,}" +                // at least 8 characters
            "$"
        )

        return when {
            password.isEmpty() -> ValidationResult.Error("Password cannot be empty")
            password.length < Constants.MIN_PASSWORD_LENGTH -> 
                ValidationResult.Error("Password must be at least ${Constants.MIN_PASSWORD_LENGTH} characters")
            !passwordPattern.matcher(password).matches() ->
                ValidationResult.Error(
                    "Password must contain at least one digit, " +
                    "one lowercase letter, one uppercase letter, " +
                    "one special character, and no spaces"
                )
            else -> ValidationResult.Success
        }
    }

    // Password confirmation validation
    fun doPasswordsMatch(password: String, confirmPassword: String): ValidationResult {
        return when {
            password != confirmPassword -> 
                ValidationResult.Error("Passwords do not match")
            else -> ValidationResult.Success
        }
    }

    // Username validation
    fun isValidUsername(username: String): ValidationResult {
        val usernamePattern = Pattern.compile("^[a-zA-Z0-9_]{3,30}$")
        
        return when {
            username.isEmpty() -> ValidationResult.Error("Username cannot be empty")
            username.length < 3 -> 
                ValidationResult.Error("Username must be at least 3 characters")
            username.length > Constants.MAX_USERNAME_LENGTH -> 
                ValidationResult.Error("Username cannot exceed ${Constants.MAX_USERNAME_LENGTH} characters")
            !usernamePattern.matcher(username).matches() ->
                ValidationResult.Error("Username can only contain letters, numbers, and underscores")
            else -> ValidationResult.Success
        }
    }

    // Phone number validation
    fun isValidPhoneNumber(phoneNumber: String): ValidationResult {
        return when {
            phoneNumber.isEmpty() -> ValidationResult.Error("Phone number cannot be empty")
            !Patterns.PHONE.matcher(phoneNumber).matches() ->
                ValidationResult.Error("Invalid phone number format")
            else -> ValidationResult.Success
        }
    }

    // URL validation
    fun isValidUrl(url: String): ValidationResult {
        return when {
            url.isEmpty() -> ValidationResult.Error("URL cannot be empty")
            !Patterns.WEB_URL.matcher(url).matches() ->
                ValidationResult.Error("Invalid URL format")
            else -> ValidationResult.Success
        }
    }

    // Text length validation
    fun isValidTextLength(text: String, maxLength: Int, fieldName: String): ValidationResult {
        return when {
            text.length > maxLength ->
                ValidationResult.Error("$fieldName cannot exceed $maxLength characters")
            else -> ValidationResult.Success
        }
    }

    // Location coordinates validation
    fun isValidLatitude(latitude: Double): ValidationResult {
        return when {
            latitude < -90 || latitude > 90 ->
                ValidationResult.Error("Latitude must be between -90 and 90")
            else -> ValidationResult.Success
        }
    }

    fun isValidLongitude(longitude: Double): ValidationResult {
        return when {
            longitude < -180 || longitude > 180 ->
                ValidationResult.Error("Longitude must be between -180 and 180")
            else -> ValidationResult.Success
        }
    }

    // File size validation
    fun isValidFileSize(sizeInBytes: Long, maxSizeInBytes: Long): ValidationResult {
        return when {
            sizeInBytes > maxSizeInBytes ->
                ValidationResult.Error("File size exceeds maximum allowed size of ${maxSizeInBytes / (1024 * 1024)}MB")
            else -> ValidationResult.Success
        }
    }

    // Image dimensions validation
    fun isValidImageDimensions(width: Int, height: Int, maxDimension: Int): ValidationResult {
        return when {
            width > maxDimension || height > maxDimension ->
                ValidationResult.Error("Image dimensions cannot exceed ${maxDimension}px")
            else -> ValidationResult.Success
        }
    }

    // Form validation helper
    fun validateForm(vararg validations: ValidationResult): ValidationResult {
        validations.forEach { validation ->
            if (validation is ValidationResult.Error) {
                return validation
            }
        }
        return ValidationResult.Success
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

data class FormValidation(
    val isValid: Boolean,
    val errors: Map<String, String> = emptyMap()
)

interface Validator<T> {
    fun validate(value: T): ValidationResult
}

class EmailValidator : Validator<String> {
    override fun validate(value: String) = ValidationUtils.isValidEmail(value)
}

class PasswordValidator : Validator<String> {
    override fun validate(value: String) = ValidationUtils.isValidPassword(value)
}

class UsernameValidator : Validator<String> {
    override fun validate(value: String) = ValidationUtils.isValidUsername(value)
}
