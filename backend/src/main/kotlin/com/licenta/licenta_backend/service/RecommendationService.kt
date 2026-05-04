package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.model.Product
import com.licenta.licenta_backend.repository.IngredientConcernRepository
import com.licenta.licenta_backend.repository.ProductRepository
import com.licenta.licenta_backend.repository.ConcernRepository
import org.springframework.stereotype.Service
import kotlin.math.sqrt

// ─── DTOs ─────────────────────────────────────────────────────────────────────

data class IngredientContribution(
    val ingredientName: String,
    val mechanism: String,
    val evidenceLevel: String,
    val contribution: Double
)

data class ConcernScore(
    val concernCode: String,
    val concernName: String,
    val score: Double,
    val topIngredients: List<IngredientContribution>
)

data class RecommendedProduct(
    val product: Product,
    val totalScore: Double,
    val normalizedScore: Double,
    val concernBreakdown: List<ConcernScore>,
    val warnings: List<String>
)

// ─── SERVICE ──────────────────────────────────────────────────────────────────

@Service
class RecommendationService(
    private val productRepository: ProductRepository,
    private val ingredientConcernRepository: IngredientConcernRepository,
    private val concernRepository: ConcernRepository
) {

    companion object {
        private const val MAX_RESULTS = 10
        private const val MAX_PER_BRAND = 2
        private const val MIN_TOTAL_SCORE = 0.0
        private const val CONTRAINDICATION_THRESHOLD = -0.5
    }

    // multipliers

    private fun evidenceMultiplier(level: String): Double {
        return when (level) {
            "GOLD_STANDARD" -> 2.0
            "CLINICAL" -> 1.3
            "ANECDOTAL" -> 0.7
            else -> 1.0
        }
    }

    private fun mechanismMultiplier(mechanism: String): Double {
        return when (mechanism) {
            "TREATS" -> 1.0
            "SUPPORTS" -> 0.1
            "CONTRAINDICATES" -> 2.5
            else -> 0.0
        }
    }

    fun recommendProducts(
        concernIds: List<Long>,
        area: String
    ): List<RecommendedProduct> {

        if (concernIds.isEmpty()) return emptyList()

        val concernsById = concernRepository.findAllById(concernIds)
            .associateBy { it.id }

        val products = productRepository.findByAreaWithIngredients(area)
        if (products.isEmpty()) return emptyList()

        val relevantIcByIngredient = ingredientConcernRepository
            .findByConcernIdIn(concernIds)
            .groupBy { it.ingredientId }

        val scored = products.mapNotNull {
            scoreProduct(it, concernIds, concernsById, relevantIcByIngredient)
        }

        val filtered = scored
            .filter { it.totalScore > MIN_TOTAL_SCORE }
            .sortedByDescending { it.totalScore }

        if (filtered.isEmpty()) return emptyList()

        val maxScore = filtered.first().totalScore

        val normalized = filtered.map {
            it.copy(normalizedScore = (it.totalScore / maxScore).coerceIn(0.0, 1.0))
        }

        return selectDiverseProducts(normalized)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SCORING ENGINE
    // ─────────────────────────────────────────────────────────────────────────

    private fun scoreProduct(
        product: Product,
        concernIds: List<Long>,
        concernsById: Map<Long, com.licenta.licenta_backend.model.Concern>,
        relevantIcByIngredient: Map<Long, List<com.licenta.licenta_backend.model.IngredientConcern>>
    ): RecommendedProduct? {

        val ingredients = product.ingredients
        if (ingredients.isEmpty()) return null
        var contraindicationScore = 0.0

        val concernBreakdown = mutableListOf<ConcernScore>()
        val warnings = mutableListOf<String>()
        var totalScore = 0.0

        for (concernId in concernIds) {

            val concern = concernsById[concernId] ?: continue
            val contributions = mutableListOf<IngredientContribution>()
            var concernScore = 0.0
            var contraindicationScore = 0.0

            for (pi in ingredients) {

                val ingredient = pi.ingredient
                val icList = relevantIcByIngredient[ingredient.id] ?: continue
                val ic = icList.firstOrNull { it.concernId == concernId } ?: continue

                val weight = ic.weight
                val mechanism = ic.mechanism ?: "SUPPORTS"
                val evidence = ic.evidenceLevel ?: "ANECDOTAL"

                val finalContribution =
                    weight *
                            evidenceMultiplier(evidence) *
                            mechanismMultiplier(mechanism)

                if (mechanism == "CONTRAINDICATES") {
                    contraindicationScore += finalContribution

                    if (finalContribution < CONTRAINDICATION_THRESHOLD) {
                        warnings.add("⚠ ${ingredient.name} may worsen ${concern.displayName}")
                    }
                }


                contributions.add(
                    IngredientContribution(
                        ingredientName = ingredient.name,
                        mechanism = mechanism,
                        evidenceLevel = evidence,
                        contribution = finalContribution
                    )
                )
            }

            val top = contributions
                .sortedByDescending { it.contribution }
                .take(10)

            concernScore = top.sumOf { it.contribution }

            val penaltyFactor = when {
                contraindicationScore <= -3.0 -> 0.3
                contraindicationScore <= -1.5 -> 0.6
                contraindicationScore <= -0.5 -> 0.85
                else -> 1.0
            }

            concernScore *= penaltyFactor

            if (top.isNotEmpty()) {
                concernBreakdown.add(
                    ConcernScore(
                        concernCode = concern.code,
                        concernName = concern.displayName,
                        score = concernScore,
                        topIngredients = top.take(3)
                    )
                )
            }

            totalScore += concernScore
        }

        if (concernBreakdown.isEmpty()) return null

        // NORMALIZATION
        totalScore /= sqrt(ingredients.size.toDouble())

        return RecommendedProduct(
            product = product,
            totalScore = totalScore,
            normalizedScore = 0.0,
            concernBreakdown = concernBreakdown,
            warnings = warnings.distinct()
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DIVERSITY
    // ─────────────────────────────────────────────────────────────────────────

    private fun selectDiverseProducts(
        scored: List<RecommendedProduct>
    ): List<RecommendedProduct> {

        val selected = mutableListOf<RecommendedProduct>()
        val brandCount = mutableMapOf<String, Int>()

        for (item in scored) {

            if (selected.size >= MAX_RESULTS) break

            val brand = item.product.brand.lowercase()
            val count = brandCount.getOrDefault(brand, 0)

            if (count >= MAX_PER_BRAND) continue

            selected.add(item)
            brandCount[brand] = count + 1
        }

        return selected
    }
}