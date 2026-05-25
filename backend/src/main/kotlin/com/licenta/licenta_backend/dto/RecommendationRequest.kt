package com.licenta.licenta_backend.dto

data class RecommendationRequest(
    val userInput: String
)

data class RecommendationResponse(
    val userConcerns: List<String>,
    val products: List<ProductRecommendation>
)