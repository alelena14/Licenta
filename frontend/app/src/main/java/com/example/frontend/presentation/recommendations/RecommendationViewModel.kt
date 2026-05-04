package com.example.frontend.presentation.recommendations

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.ProductRecommendation
import com.example.frontend.domain.usecase.GetRecommendationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val getRecommendationsUseCase: GetRecommendationsUseCase
) : ViewModel() {

    var uiState by mutableStateOf<RecommendationUiState>(
        RecommendationUiState.Idle
    )
        private set

    fun getRecommendations(input: String) {

        viewModelScope.launch {

            uiState = RecommendationUiState.Loading

            try {

                val response = getRecommendationsUseCase(input)

                uiState = RecommendationUiState.Success(
                    products = response.products,
                    concerns = response.userConcerns
                )

            } catch (e: Exception) {

                uiState = RecommendationUiState.Error(
                    message = e.message ?: "Unknown error"
                )
            }
        }
    }
}
sealed class RecommendationUiState {

    object Idle : RecommendationUiState()

    object Loading : RecommendationUiState()

    data class Success(
        val products: List<ProductRecommendation>,
        val concerns: List<String>
    ) : RecommendationUiState()

    data class Error(
        val message: String
    ) : RecommendationUiState()
}