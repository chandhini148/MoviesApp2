package com.example.moviesapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isAuthenticated =
        MutableStateFlow(auth.currentUser != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _resetPasswordSuccess = MutableStateFlow<String?>(null)
    val resetPasswordSuccess: StateFlow<String?> = _resetPasswordSuccess


    // ----------------------------------------------------
    // REGISTER
    // ----------------------------------------------------
    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit
    ) {
        _errorMessage.value = null

        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isBlank()) {
            _errorMessage.value = "Please enter your name"
            return
        }

        if (trimmedEmail.isBlank()) {
            _errorMessage.value = "Please enter your email"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _errorMessage.value = "The email address format is invalid. Please enter a valid email."
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "Password must contain at least 6 characters"
            return
        }

        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match"
            return
        }

        _isLoading.value = true

        // Create account in Firebase Authentication
        auth.createUserWithEmailAndPassword(
            trimmedEmail,
            password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser

                if (firebaseUser != null) {
                    // Update user profile display name
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(trimmedName)
                        .build()
                    firebaseUser.updateProfile(profileUpdates)

                    val uid = firebaseUser.uid

                    // Data to save in Firestore
                    val userData = hashMapOf(
                        "name" to trimmedName,
                        "email" to trimmedEmail
                    )

                    // Save user data in Firestore
                    db.collection("users")
                        .document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            _isLoading.value = false
                            _isAuthenticated.value = true
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            Log.w("AuthViewModel", "Firestore user write failed: ${exception.message}")
                            // Firebase Auth account creation succeeded; proceed to avoid blocking user
                            _isLoading.value = false
                            _isAuthenticated.value = true
                            onSuccess()
                        }

                } else {
                    _isLoading.value = false
                    _isAuthenticated.value = true
                    onSuccess()
                }

            } else {
                _isLoading.value = false
                _errorMessage.value = getRegisterErrorMessage(task.exception)
            }
        }
    }


    // ----------------------------------------------------
    // LOGIN
    // ----------------------------------------------------
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        _errorMessage.value = null

        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            _errorMessage.value = "Please enter your email"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _errorMessage.value = "The email address format is invalid. Please enter a valid email."
            return
        }

        if (password.isBlank()) {
            _errorMessage.value = "Please enter your password"
            return
        }

        _isLoading.value = true

        // Note: password is NOT trimmed or modified
        auth.signInWithEmailAndPassword(
            trimmedEmail,
            password
        ).addOnCompleteListener { task ->
            _isLoading.value = false

            if (task.isSuccessful) {
                _isAuthenticated.value = true
                onSuccess()
            } else {
                _errorMessage.value = getLoginErrorMessage(task.exception)
            }
        }
    }


    // ----------------------------------------------------
    // LOGOUT
    // ----------------------------------------------------
    fun logout() {
        auth.signOut()
        _isAuthenticated.value = false
        _errorMessage.value = null
        _resetPasswordSuccess.value = null
    }


    // ----------------------------------------------------
    // FORGOT / RESET PASSWORD
    // ----------------------------------------------------
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit = {}
    ) {
        _errorMessage.value = null
        _resetPasswordSuccess.value = null

        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            _errorMessage.value = "Please enter your email address"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _errorMessage.value = "Please enter a valid email address"
            return
        }

        _isLoading.value = true

        auth.sendPasswordResetEmail(trimmedEmail)
            .addOnCompleteListener { task ->
                _isLoading.value = false

                if (task.isSuccessful) {
                    _resetPasswordSuccess.value =
                        "Password reset link has been sent to $trimmedEmail. Please check your inbox and follow the instructions."
                    onSuccess()
                } else {
                    _errorMessage.value =
                        task.exception?.localizedMessage
                            ?: task.exception?.message
                            ?: "Failed to send reset email. Please try again."
                }
            }
    }


    // ----------------------------------------------------
    // CLEAR MESSAGES
    // ----------------------------------------------------
    fun clearError() {
        _errorMessage.value = null
        _resetPasswordSuccess.value = null
    }


    // ----------------------------------------------------
    // ERROR MAPPING HELPERS
    // ----------------------------------------------------
    private fun getLoginErrorMessage(exception: Throwable?): String {
        if (exception == null) return "Login failed. Please try again."

        return when (exception) {
            is FirebaseNetworkException -> {
                "Network error. Please check your internet connection and try again."
            }
            is FirebaseAuthInvalidUserException -> {
                when (exception.errorCode) {
                    "ERROR_USER_DISABLED" -> "This user account has been disabled. Please contact support."
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email. Please check your email or register."
                    else -> "No account found with this email, or the account has been disabled."
                }
            }
            is FirebaseAuthInvalidCredentialsException -> {
                when (exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "The email address format is invalid. Please enter a valid email."
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again or use 'Forgot Password?' to reset it."
                    "ERROR_INVALID_CREDENTIAL" -> "Incorrect email or password. Please verify your credentials or register if you haven't created an account."
                    else -> {
                        val msg = exception.message.orEmpty()
                        if (msg.contains("malformed", ignoreCase = true) ||
                            msg.contains("expired", ignoreCase = true) ||
                            msg.contains("credential", ignoreCase = true)
                        ) {
                            "Incorrect email or password. Please verify your credentials or use 'Forgot Password?' to reset it."
                        } else {
                            exception.localizedMessage ?: "Invalid email or password."
                        }
                    }
                }
            }
            is FirebaseTooManyRequestsException -> {
                "Too many unsuccessful login attempts. Access to this account has been temporarily disabled. Please reset your password or try again later."
            }
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "The email address format is invalid. Please enter a valid email."
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again or use 'Forgot Password?' to reset it."
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email. Please register first."
                    "ERROR_USER_DISABLED" -> "This user account has been disabled. Please contact support."
                    "ERROR_TOO_MANY_REQUESTS" -> "Too many unsuccessful login attempts. Access to this account has been temporarily disabled. Please reset your password or try again later."
                    "ERROR_OPERATION_NOT_ALLOWED" -> "Email and Password authentication is not enabled in Firebase Console. Please enable it in Authentication > Sign-in method."
                    "ERROR_INVALID_CREDENTIAL" -> "Incorrect email or password. Please verify your credentials or register if you haven't created an account."
                    else -> exception.localizedMessage ?: exception.message ?: "Authentication failed. Please try again."
                }
            }
            else -> {
                val msg = exception.message.orEmpty()
                if (msg.contains("network", ignoreCase = true)) {
                    "Network error. Please check your internet connection."
                } else if (msg.contains("malformed", ignoreCase = true) ||
                    msg.contains("expired", ignoreCase = true) ||
                    msg.contains("credential", ignoreCase = true)
                ) {
                    "Incorrect email or password. Please verify your credentials or use 'Forgot Password?' to reset it."
                } else {
                    exception.localizedMessage ?: exception.message ?: "Login failed. Please try again."
                }
            }
        }
    }

    private fun getRegisterErrorMessage(exception: Throwable?): String {
        if (exception == null) return "Registration failed. Please try again."

        return when (exception) {
            is FirebaseNetworkException -> {
                "Network error. Please check your internet connection and try again."
            }
            is FirebaseAuthUserCollisionException -> {
                "An account already exists with this email address. Please log in instead."
            }
            is FirebaseAuthWeakPasswordException -> {
                "The password is too weak. Please use at least 6 characters."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                "The email address format is invalid. Please check your email."
            }
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists with this email address. Please log in instead."
                    "ERROR_OPERATION_NOT_ALLOWED" -> "Email/Password sign-up is not enabled in Firebase Console. Please enable it in Authentication > Sign-in method."
                    "ERROR_WEAK_PASSWORD" -> "The password is too weak. Please use at least 6 characters."
                    "ERROR_INVALID_EMAIL" -> "The email address format is invalid. Please check your email."
                    else -> exception.localizedMessage ?: exception.message ?: "Registration failed. Please try again."
                }
            }
            else -> exception.localizedMessage ?: exception.message ?: "Registration failed. Please try again."
        }
    }
}