package com.example.frontend.data.model

data class ProductRecommendation(
    val id: Long,
    val name: String,
    val brand: String,
    val score: Double,
    val type: String,
    val tags: List<String>,
    val country: String?,
    val ingredients: List<String>,
    val explanation: String,
    val warnings: List<String>,
    val url: String?
)