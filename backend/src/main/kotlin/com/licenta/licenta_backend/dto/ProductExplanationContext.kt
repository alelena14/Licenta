package com.licenta.licenta_backend.dto

data class ProductExplanationContext(
    val productName: String,
    val matchedConcerns: List<String>,
    val keyIngredients: List<IngredientExplanation>,
    val warnings: List<String>,
    val mixingConflicts: List<String>,
    val recommendedUsage: String,
    val safetySummary: String
)

data class IngredientExplanation(
    val name: String,
    val benefits: List<String>,
    val irritationPotential: String,
    val role: String
)