package com.example.frontend.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.SkinProfileDto
import com.example.frontend.data.model.ProfileStatsDto
import com.example.frontend.data.model.UpdateSkinProfileRequest
import com.example.frontend.domain.model.User
import com.example.frontend.domain.usecase.GetCurrentUserUseCase
import com.example.frontend.data.model.UserDto
import com.example.frontend.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState

    var skinProfile by mutableStateOf<SkinProfileDto?>(null)
        private set

    var isEditing by mutableStateOf(false)
        private set
    var editSkinType by mutableStateOf<String?>(null)
        private set
    var editConcerns by mutableStateOf<Set<String>>(emptySet())
        private set
    var isSaving by mutableStateOf(false)
        private set

    var stats by mutableStateOf<ProfileStatsDto?>(null)
        private set

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            skinProfile = profileRepository.getSkinProfile()
            try {
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _profileState.value = ProfileState.Error("Expired session.")
                    return@launch
                }
                val email = currentUser.email
                val token = currentUser.getIdToken(true).await().token
                if (token != null && email != null) {
                    val userDeferred = async { getCurrentUserUseCase(UserDto(token, email, null, null, null)) }
                    val statsDeferred = async { profileRepository.getStats(token) }

                    userDeferred.await().onSuccess { user ->
                        _profileState.value = ProfileState.Success(user)
                    }.onFailure {
                        _profileState.value = ProfileState.Error(it.message ?: "Server error")
                    }

                    statsDeferred.await().onSuccess { statsDto ->
                        stats = statsDto
                    }.onFailure {}
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    fun openEdit() {
        editSkinType  = skinProfile?.skinType
        editConcerns  = skinProfile?.concerns?.toSet() ?: emptySet()
        isEditing     = true
    }

    fun closeEdit() {
        isEditing = false
    }

    fun onSkinTypeSelected(type: String) {
        editSkinType = if (editSkinType == type) null else type
    }

    fun onConcernToggled(concern: String) {
        editConcerns = if (concern in editConcerns)
            editConcerns - concern
        else
            editConcerns + concern
    }

    fun saveProfile() {
        viewModelScope.launch {
            isSaving = true
            try {
                val updated = profileRepository.updateSkinProfile(
                    UpdateSkinProfileRequest(
                        skinType = editSkinType,
                        concerns = editConcerns.toList()
                    )
                )

                skinProfile = updated

                val token = auth.currentUser
                    ?.getIdToken(true)
                    ?.await()
                    ?.token

                if (token != null) {
                    profileRepository.getStats(token)
                        .onSuccess { statsDto ->
                            stats = statsDto
                        }
                }

                isEditing = false

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSaving = false
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