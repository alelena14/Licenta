package com.example.frontend.domain.repository

import android.content.Context
import com.example.frontend.data.model.AnalysisResponse
import com.example.frontend.data.model.RecommendationResponse
import java.io.File

interface RecommendationRepository {
    suspend fun getRecommendationsByText(input: String): RecommendationResponse

    // ── Photo — Step 1: trimite poza, primești concerns ──────────────────────
    suspend fun analyzePhoto(file: File): AnalysisResponse

    // ── Photo — Step 2: trimite concerns confirmate, primești produse ─────────
    suspend fun getRecommendationsByConcerns(concerns: List<String>): RecommendationResponse
}