package com.example.frontend.presentation.recommendations

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.ProductRecommendation
import com.example.frontend.data.model.RecommendationResponse
import com.example.frontend.domain.repository.RecommendationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val repository: RecommendationRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    var uiState: RecommendationUiState by mutableStateOf(RecommendationUiState.Idle)
        private set

    // Concerns detectate de AI — expuse pt ecranul de confirmare
    var detectedConcerns: List<String> by mutableStateOf(emptyList())
        private set

    // ── Text flow ────────────────────────────────────────────────────────────
    fun getRecommendations(input: String) {
        viewModelScope.launch {
            uiState = RecommendationUiState.Loading
            uiState = try {
                val products = repository.getRecommendationsByText(input)
                RecommendationUiState.Success(products)
            } catch (e: Exception) {
                RecommendationUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    // ── Photo flow — Step 1: analizează poza, întoarce concerns ─────────────
    fun analyzePhoto(file: File, onConcernsReady: (List<String>) -> Unit) {
        viewModelScope.launch {
            uiState = RecommendationUiState.Loading
            try {
                val analysisResponse = repository.analyzePhoto(file)
                detectedConcerns = analysisResponse.userConcerns
                uiState = RecommendationUiState.Idle
                onConcernsReady(analysisResponse.userConcerns)
            } catch (e: Exception) {
                uiState = RecommendationUiState.Error(e.message ?: "Analysis failed")
            }
        }
    }

    // ── Photo flow — Step 2: recomandări pe concerns confirmate de user ──────
    fun getRecommendationsFromConcerns(confirmedConcerns: List<String>) {
        viewModelScope.launch {
            uiState = RecommendationUiState.Loading
            uiState = try {
                val response = repository.getRecommendationsByConcerns(confirmedConcerns)
                RecommendationUiState.Success(response)
            } catch (e: Exception) {
                RecommendationUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}

sealed class RecommendationUiState {
    object Idle : RecommendationUiState()
    object Loading : RecommendationUiState()
    data class Success(val response: RecommendationResponse) : RecommendationUiState()
    data class Error(val message: String) : RecommendationUiState()
}