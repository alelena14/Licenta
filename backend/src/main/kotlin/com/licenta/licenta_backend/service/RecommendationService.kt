package com.licenta.licenta_backend.service

import org.springframework.stereotype.Service
import com.licenta.licenta_backend.repository.RecommendationRepository

@Service
class RecommendationService(
    private val recommendationRepository: RecommendationRepository
) {

    fun recommendProducts(concerns: List<String>) =
        recommendationRepository.findRecommendedProducts(concerns)
}