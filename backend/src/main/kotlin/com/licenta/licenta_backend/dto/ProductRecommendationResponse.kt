package com.licenta.licenta_backend.dto

data class ProductRecommendationResponse(
    val id: Long,
    val name: String,
    val brand: String,
    val score: Double,          // 0.0 - 1.0 normalizat, pentru UI (progress bar etc)
    val explanation: String,    // "For acne: salicylic acid (treats, gold standard); ..."
    val warnings: List<String>  // "Alcohol Denat may worsen sensitivity (clinical)"
)