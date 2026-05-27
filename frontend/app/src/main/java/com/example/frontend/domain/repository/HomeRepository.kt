package com.example.frontend.domain.repository

import com.example.frontend.data.model.ProductRecommendation

interface HomeRepository {
    suspend fun getRecommendations(): List<ProductRecommendation>
}