package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.dto.ProductRecommendation
import com.licenta.licenta_backend.repository.*
import org.springframework.stereotype.Service

@Service
class HomeRecommendationService(
    val userRepository: UserRepository,
    val profileRepository: UserSkinProfileRepository,
    val concernRepository: UserConcernRepository,
    val skinConcernRepository: ConcernRepository,
    val recommendationService: RecommendationService,
    val productRepository: ProductRepository
) {
    fun getHomeRecommendations(firebaseUid: String): List<ProductRecommendation> {
        val user = userRepository.findByFirebaseUid(firebaseUid) ?: return emptyList()

        val profile = profileRepository.findByUserId(user.id) ?: return emptyList()

        val concerns = concernRepository.findAllByProfileId(profile.id)
            .map { it.concernCode }

        if (concerns.isEmpty()) { return emptyList() }

        val concernIds = skinConcernRepository.findAll()
            .filter { concern ->
                concerns.any { userConcern ->
                    userConcern.equals(concern.code, ignoreCase = true) ||
                            userConcern.equals(concern.displayName, ignoreCase = true)
                }
            }
            .map { it.id }

        if (concernIds.isEmpty()) { return emptyList() }

        val recs = recommendationService.recommendProducts(concernIds, "face")

        return recs.take(6).map { rec ->
            ProductRecommendation(
                id          = rec.product.id,
                name        = rec.product.name,
                brand       = rec.product.brand,
                type        = rec.product.type,
                country     = rec.product.country,
                tags        = productRepository.findAfterUseLabelsByProductId(rec.product.id),
                ingredients = productRepository.findIngredients(rec.product.id),
                score       = rec.normalizedScore,
                explanation = rec.product.name,
                warnings    = rec.warnings,
                url         = rec.product.url
            )
        }
    }
}