package com.example.frontend.domain.usecase

import com.example.frontend.data.model.RecommendationResponse
import com.example.frontend.domain.repository.RecommendationRepository

class GetRecommendationsUseCase(
    private val repository: RecommendationRepository
) {

    suspend operator fun invoke(
        input: String
    ): RecommendationResponse {
        return repository.getRecommendationsByText(input)
    }
}