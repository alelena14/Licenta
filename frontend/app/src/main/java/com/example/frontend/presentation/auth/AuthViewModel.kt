package com.example.frontend.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.UserDto
import com.example.frontend.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.FirebaseTooManyRequestsException

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email.")
            return
        }

        if (password.isBlank()) {
            _authState.value = AuthState.Error("Please enter your password.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val token = result.user?.getIdToken(true)?.await()?.token ?: ""

                val userDto = UserDto(token, email, null)
                val syncResult = userRepository.syncUserWithBackend(userDto)

                if (syncResult.isSuccess) {
                    _authState.value = AuthState.Success(token)
                } else {
                    val errorMessage = syncResult.exceptionOrNull()?.message ?: "Unknown error"
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {

                val message = when (e) {

                    is FirebaseAuthInvalidCredentialsException ->
                        "Incorrect email or password."

                    is FirebaseNetworkException ->
                        "No internet connection. Please try again."

                    is FirebaseTooManyRequestsException ->
                        "Too many login attempts. Please try again later."

                    else ->
                        "Unable to sign in. Please try again."
                }

                _authState.value = AuthState.Error(message)
            }
        }
    }

    fun register(
        email: String,
        password: String,
        username: String
    ) {
        if (username.isBlank()) {
            _authState.value = AuthState.Error("Please enter a username.")
            return
        }

        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email.")
            return
        }

        if (password.isBlank()) {
            _authState.value = AuthState.Error("Please enter a password.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val token = result.user?.getIdToken(true)?.await()?.token ?: ""

                val userDto = UserDto(token, email, username)
                val syncResult = userRepository.syncUserWithBackend(userDto)

                if (syncResult.isSuccess) {
                    _authState.value = AuthState.Success(token)
                } else {
                    val errorMessage = syncResult.exceptionOrNull()?.message ?: "Sincronizarea a esuat"
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {

                val message = when (e) {

                    is FirebaseAuthUserCollisionException ->
                        "An account with this email already exists."

                    is FirebaseAuthWeakPasswordException ->
                        "Password must contain at least 6 characters."

                    is FirebaseAuthInvalidCredentialsException ->
                        "Please enter a valid email address."

                    is FirebaseNetworkException ->
                        "No internet connection. Please try again."

                    else ->
                        "Unable to create account. Please try again."
                }

                _authState.value = AuthState.Error(message)
            }
        }
    }

    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(
                        task.exception?.localizedMessage
                            ?: "Failed to send reset email"
                    )
                }
            }
    }

}