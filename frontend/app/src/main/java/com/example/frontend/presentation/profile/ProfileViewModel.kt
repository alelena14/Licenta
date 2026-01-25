package com.example.frontend.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.domain.model.User
import com.example.frontend.domain.usecase.GetCurrentUserUseCase
import com.example.frontend.data.model.UserDto
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState


    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _profileState.value = ProfileState.Error("Sesiune expirata. Te rugam sa te autentifici din nou.")
                    return@launch
                }

                val email = currentUser.email
                val token = currentUser.getIdToken(true).await().token

                if (token != null && email != null) {
                    val userDto = UserDto(token, email, null, null, null)
                    getCurrentUserUseCase(userDto).onSuccess { user ->
                        _profileState.value = ProfileState.Success(user)
                    }.onFailure {
                        _profileState.value = ProfileState.Error(it.message ?: "Eroare server")
                    }
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Eroare: ${e.localizedMessage}")
            }
        }
    }
}

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}