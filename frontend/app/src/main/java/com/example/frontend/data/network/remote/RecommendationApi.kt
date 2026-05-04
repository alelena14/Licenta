package com.example.frontend.data.network.remote

import com.example.frontend.data.model.RecommendationRequest
import com.example.frontend.data.model.RecommendationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RecommendationApi {

    @POST("api/recommendations")
    suspend fun getRecommendations(
        @Body request: RecommendationRequest
    ): RecommendationResponse
}