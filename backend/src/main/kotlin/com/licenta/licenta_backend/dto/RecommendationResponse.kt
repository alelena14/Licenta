package com.licenta.licenta_backend.dto

data class RecommendationResponse(
    val userConcerns: List<String>,
    val products: List<ProductRecommendationResponse>
)