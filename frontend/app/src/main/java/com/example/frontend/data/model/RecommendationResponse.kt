package com.example.frontend.data.model

data class RecommendationResponse(
    val userConcerns: List<String>,
    val products: List<ProductRecommendation>
)