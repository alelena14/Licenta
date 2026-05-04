package com.example.frontend.data.repository

import com.example.frontend.data.model.RecommendationRequest
import com.example.frontend.data.model.RecommendationResponse
import com.example.frontend.data.network.remote.RecommendationApi
import com.example.frontend.domain.repository.RecommendationRepository

class RecommendationRepositoryImpl(
    private val api: RecommendationApi
) : RecommendationRepository {

    override suspend fun getRecommendations(
        userInput: String
    ): RecommendationResponse {

        return api.getRecommendations(
            RecommendationRequest(userInput)
        )
    }
}