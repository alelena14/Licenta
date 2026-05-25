package com.example.frontend.data.repository

import android.content.Context
import android.net.Uri
import com.example.frontend.data.model.AnalysisResponse
import com.example.frontend.data.model.RecommendationRequest
import com.example.frontend.data.model.RecommendationResponse
import com.example.frontend.data.network.remote.RecommendationApi
import com.example.frontend.domain.repository.RecommendationRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepositoryImpl @Inject constructor(
    private val api: RecommendationApi
) : RecommendationRepository {

    // ── Text ─────────────────────────────────────────────────────────────────
    override suspend fun getRecommendationsByText(input: String): RecommendationResponse {
        return api.getRecommendationsByText(RecommendationRequest(userInput = input))
    }
    // ── Photo Step 1: analizează → concerns ──────────────────────────────────
    override suspend fun analyzePhoto(file: File): AnalysisResponse {
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
            name = "file",
            filename = file.name,
            body = requestBody
        )
        return api.analyzeFace(part)
    }

    // ── Photo — Step 2: trimite concerns confirmate, primești produse ─────────
    override suspend fun getRecommendationsByConcerns(concerns: List<String>): RecommendationResponse {
        return api.getRecommendationsByConcerns(concerns)
    }
}