package com.example.frontend.domain.repository

import com.example.frontend.data.model.RecommendationResponse

interface RecommendationRepository {

    suspend fun getRecommendations(
        userInput: String
    ): RecommendationResponse
}