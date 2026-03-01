package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.model.Product
import com.licenta.licenta_backend.repository.ConcernTagMapRepository
import com.licenta.licenta_backend.repository.RecommendationRepository
import org.springframework.stereotype.Service
import kotlin.math.sqrt

@Service
class RecommendationService(
    private val recommendationRepository: RecommendationRepository,
    private val concernTagMapRepository: ConcernTagMapRepository
) {

    fun recommendProducts(
        userConcernCodes: List<String>,
        userConcernIds: List<Long>,
        userArea: String
    ): List<Product> {

        if (userConcernIds.isEmpty()) return emptyList()

        val candidates = recommendationRepository
            .findCandidateProducts(userConcernCodes, userArea)

        if (candidates.isEmpty()) return emptyList()

        // Semantic scores
        val semanticScores = recommendationRepository
            .findSemanticScores(userConcernIds, userArea)
            .associateBy(
                { it.getProductId() },
                { it.getSemanticScore() }
            )

        // Tag weight map
        val concernTagWeights = concernTagMapRepository
            .findByIdConcernIdIn(userConcernIds)

        val weightMap = concernTagWeights.associate {
            Pair(it.id.concernId to it.id.tagId, it.weight)
        }

        val scoredProducts = candidates.mapNotNull { product ->

            val tagScore = calculateTagScore(
                product = product,
                userConcernIds = userConcernIds,
                weightMap = weightMap
            )

            if (tagScore == Int.MIN_VALUE) return@mapNotNull null

            val semanticScore = semanticScores[product.id] ?: 0.0

            // Hybrid combination
            val finalScore =
                semanticScore * 0.8 +
                        tagScore * 0.2

            product to finalScore
        }

        return scoredProducts
            .sortedByDescending { it.second }
            .take(10)
            .map { it.first }
    }


    private fun calculateTagScore(
        product: Product,
        userConcernIds: List<Long>,
        weightMap: Map<Pair<Long, Long>, Int>
    ): Int {

        if (product.afterUse.isEmpty()) return Int.MIN_VALUE

        var positive = 0
        var negative = 0
        var matchedConcerns = 0

        for (concernId in userConcernIds) {

            var concernScore = 0

            for (tag in product.afterUse) {
                val weight = weightMap[concernId to tag.id] ?: 0
                concernScore += weight
            }

            if (concernScore > 0) {
                matchedConcerns++
                positive += concernScore
            }

            if (concernScore < 0) {
                negative += concernScore
            }
        }

        if (negative <= -4) return Int.MIN_VALUE

        val minRequired = maxOf(1, userConcernIds.size / 2)
        if (matchedConcerns < minRequired) return Int.MIN_VALUE

        val rawScore = positive + negative

        val tagNormalization =
            sqrt(product.afterUse.size.toDouble())

        val concernNormalization =
            sqrt(userConcernIds.size.toDouble())

        val normalizedScore =
            rawScore / (tagNormalization * concernNormalization)

        return normalizedScore.toInt()
    }
}