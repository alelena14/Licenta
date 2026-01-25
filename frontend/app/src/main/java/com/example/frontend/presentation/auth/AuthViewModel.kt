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
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val token = result.user?.getIdToken(true)?.await()?.token ?: ""

                val userDto = UserDto(token, email, null, null, null)
                val syncResult = userRepository.syncUserWithBackend(userDto)

                if (syncResult.isSuccess) {
                    _authState.value = AuthState.Success(token)
                } else {
                    val errorMessage = syncResult.exceptionOrNull()?.message ?: "Eroare necunoscuta backend"
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Eroare Login")
            }
        }
    }

    fun register(
        email: String,
        password: String,
        username: String,
        age: Int,
        profileImageUrl: String? = null
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val token = result.user?.getIdToken(true)?.await()?.token ?: ""

                val userDto = UserDto(token, email, username, profileImageUrl, age)
                val syncResult = userRepository.syncUserWithBackend(userDto)

                if (syncResult.isSuccess) {
                    _authState.value = AuthState.Success(token)
                } else {
                    // ȘI AICI MODIFICI:
                    val errorMessage = syncResult.exceptionOrNull()?.message ?: "Sincronizarea a esuat"
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Eroare la inregistrare")
            }
        }
    }

}