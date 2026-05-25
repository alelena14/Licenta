package com.licenta.licenta_backend.controller

import com.licenta.licenta_backend.dto.AnalysisResponse
import com.licenta.licenta_backend.dto.ProductRecommendation
import com.licenta.licenta_backend.dto.RecommendationRequest
import com.licenta.licenta_backend.dto.RecommendationResponse
import com.licenta.licenta_backend.repository.ConcernRepository
import com.licenta.licenta_backend.repository.ProductRepository
import com.licenta.licenta_backend.service.AiService
import com.licenta.licenta_backend.service.RecommendationService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/recommendations")
class RecommendationController(
    private val aiService: AiService,
    private val recommendationService: RecommendationService,
    private val concernRepository: ConcernRepository,
    private val productRepository: ProductRepository
) {

    @PostMapping
    fun getRecommendations(
        @RequestBody request: RecommendationRequest
    ): RecommendationResponse {

        // ── 1. Extrage concerns si area din input-ul userului ─────────────────
        val (concernCodes, area) = aiService.extractConcerns(request.userInput)

        if (concernCodes.isEmpty()) {
            return RecommendationResponse(
                userConcerns = emptyList(),
                products = emptyList()
            )
        }

        // ── 2. Fetch concern entities ─────────────────────────────────────────
        val concernIds = concernRepository.findByCodeIn(concernCodes).map { it.id }

        // ── 3. Recomandari cu scoring ─────────────────────────────────────────
        val recommended = recommendationService.recommendProducts(concernIds, area)


        // ── 4. Mapeaza la response ────────────────────────────────────────────
        val results = recommended.map { rec ->
            
            // Construieste explicatia din concernBreakdown — fara apel AI extra
            val explanation = buildExplanation(rec)

            ProductRecommendation(
                id = rec.product.id,
                name = rec.product.name,
                brand = rec.product.brand,
                score = rec.normalizedScore,
                type = rec.product.type,
                country = rec.product.country,
                tags = productRepository.findAfterUseLabelsByProductId(rec.product.id),
                ingredients = productRepository.findIngredients(rec.product.id),
                explanation = explanation,
                warnings = rec.warnings,
                url = rec.product.url
            )

        }

        return RecommendationResponse(
            userConcerns = concernCodes,
            products = results
        )
    }

    @PostMapping("/by-concerns")
    fun getRecommendxationsByConcerns(
        @RequestParam("concerns") concerns: List<String> // display name pentru concerns
    ): RecommendationResponse {

        // ── 1. Extrage concerns si area din input-ul userului ─────────────────
        val (concernCodes, area) = aiService.extractConcerns(concerns.joinToString(", " ))

        if (concernCodes.isEmpty()) {
            return RecommendationResponse(
                userConcerns = emptyList(),
                products = emptyList()
            )
        }

        // ── 2. Fetch concern entities ─────────────────────────────────────────
        val concernIds = concernRepository.findByCodeIn(concernCodes).map { it.id }

        // ── 3. Recomandari cu scoring ─────────────────────────────────────────
        val recommended = recommendationService.recommendProducts(concernIds, area)


        // ── 4. Mapeaza la response ────────────────────────────────────────────
        val results = recommended.map { rec ->

            // Construieste explicatia din concernBreakdown — fara apel AI extra
            val explanation = buildExplanation(rec)

            ProductRecommendation(
                id = rec.product.id,
                name = rec.product.name,
                brand = rec.product.brand,
                score = rec.normalizedScore,
                type = rec.product.type,
                country = rec.product.country,
                tags = productRepository.findAfterUseLabelsByProductId(rec.product.id),
                ingredients = productRepository.findIngredients(rec.product.id),
                explanation = explanation,
                warnings = rec.warnings,
                url = rec.product.url
            )

        }

        return RecommendationResponse(
            userConcerns = concernCodes,
            products = results
        )
    }

    @PostMapping("/face")
    fun getConcernsFromFace(
        @RequestParam("file") file: MultipartFile
    ): AnalysisResponse {

        val (concernCodes, _) = aiService.extractConcernsFromFace(file)

        val concernNames = concernRepository.findByCodeIn(concernCodes).map { it.displayName }

        if (concernNames.isEmpty()) {
            return AnalysisResponse(
                userConcerns = emptyList()
            )
        }

        return AnalysisResponse(
            userConcerns = concernNames
        )
    }

    private fun buildExplanation(rec: com.licenta.licenta_backend.service.RecommendedProduct): String {
        val sb = StringBuilder()

        rec.concernBreakdown.forEach { concernScore ->
            val topIngredients = concernScore.topIngredients
                .filter { it.contribution > 0 }  // exclude CONTRAINDICATES din explicație
                .take(3)

            if (topIngredients.isEmpty()) return@forEach

            sb.append("For ${concernScore.concernName}: ")

            val parts = topIngredients.map { ing ->
                val mechanismLabel = when (ing.mechanism) {
                    "TREATS"    -> "treats"
                    "PREVENTS"  -> "helps prevent"
                    "SUPPORTS"  -> "supports"
                    else        -> "helps with"
                }
                "${ing.ingredientName} ($mechanismLabel, ${ing.evidenceLevel.lowercase().replace("_", " ")})"
            }

            sb.append(parts.joinToString("; "))
            sb.append(". ")
        }

        return sb.toString().trim()
    }
}