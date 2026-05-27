package com.example.frontend.presentation.recommendations

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.domain.repository.RecommendationRepository
import com.example.frontend.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
import java.io.FileOutputStream

sealed class SkinAnalysisState {
    object Idle : SkinAnalysisState()
    object Analysing : SkinAnalysisState()
    data class ConcernsReady(val concerns: List<String>) : SkinAnalysisState()
    data class Error(val message: String) : SkinAnalysisState()
    object Saved : SkinAnalysisState()
}

@HiltViewModel
class SkinAnalysisViewModel @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
    private val profileRepository: ProfileRepository,
    private val auth: FirebaseAuth,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _state = MutableStateFlow<SkinAnalysisState>(SkinAnalysisState.Idle)
    val state: StateFlow<SkinAnalysisState> = _state.asStateFlow()

    var selectedUri by mutableStateOf<Uri?>(null)
        private set

    // Concerns detectate — expuse pt PhotoConfirmScreen
    var detectedConcerns by mutableStateOf<List<String>>(emptyList())
        private set

    fun onPhotoSelected(uri: Uri) {
        selectedUri = uri
    }

    // Step 1
    fun analysePhoto(uri: Uri) {
        viewModelScope.launch {
            _state.value = SkinAnalysisState.Analysing
            try {
                val file = compressImage(uri)
                val response = recommendationRepository.analyzePhoto(file)
                detectedConcerns = response.userConcerns
                _state.value = SkinAnalysisState.ConcernsReady(response.userConcerns)
            } catch (e: Exception) {
                _state.value = SkinAnalysisState.Error(e.message ?: "Analysis failed")
            }
        }
    }

    // Step 2
    fun saveConcernsToProfile(confirmedConcerns: List<String>) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${auth.currentUser?.getIdToken(true)?.await()?.token}"
                profileRepository.saveConcernsFromAnalysis(token, confirmedConcerns)
                _state.value = SkinAnalysisState.Saved
            } catch (e: Exception) {
                // silent fail
                _state.value = SkinAnalysisState.Saved
            }
        }
    }

    fun reset() {
        _state.value = SkinAnalysisState.Idle
        selectedUri = null
        detectedConcerns = emptyList()
    }

    private fun compressImage(uri: Uri, maxSize: Int = 1280, quality: Int = 85): File {
        val inputStream = appContext.contentResolver.openInputStream(uri)
        val original = BitmapFactory.decodeStream(inputStream)
        val ratio = minOf(maxSize.toFloat() / original.width, maxSize.toFloat() / original.height)
        val resized = original.scale(
            (original.width * ratio).toInt(),
            (original.height * ratio).toInt()
        )
        val file = File(appContext.cacheDir, "skin_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            resized.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        return file
    }
}