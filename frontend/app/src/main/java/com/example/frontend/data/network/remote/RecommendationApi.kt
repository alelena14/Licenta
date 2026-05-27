package com.example.frontend.data.network.remote

import com.example.frontend.data.model.AnalysisResponse
import com.example.frontend.data.model.RecommendationRequest
import com.example.frontend.data.model.RecommendationResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface RecommendationApi {

    // ── Text recommendations ─────────────────────────────────────────────────
    @POST("api/recommendations")
    suspend fun getRecommendationsByText(
        @Body request: RecommendationRequest
    ): RecommendationResponse

    // ── Step 1: analizeaza poza → intoarce doar concerns ────────────────────
    @Multipart
    @POST("api/recommendations/face")
    suspend fun analyzeFace(
        @Part file: MultipartBody.Part
    ): AnalysisResponse

    // ── Recommendations pe baza concerns confirmate de user ──────────────────
    @POST("api/recommendations/by-concerns")
    suspend fun getRecommendationsByConcerns(
        @Query("concerns") concerns: List<String>
    ): RecommendationResponse
}