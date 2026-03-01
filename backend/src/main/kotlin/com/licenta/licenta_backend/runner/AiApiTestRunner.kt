package com.licenta.licenta_backend.runner

import com.licenta.licenta_backend.repository.ConcernRepository
import com.licenta.licenta_backend.service.AiService
import com.licenta.licenta_backend.service.RecommendationService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "app.rec-runner",
    name = ["enabled"],
    havingValue = "true"
)
class AiApiTestRunner(
    private val aiService: AiService,
    private val recommendationService: RecommendationService,
    private val concernRepository: ConcernRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {

//        val testInput = "I have bumps on my nose and very oily skin"
//
//        println("User input: $testInput")
//
//        var (concernCodes, userArea) = aiService.extractConcerns(testInput)
//
//        println("Extracted concerns: $concernCodes")
//        println("Detected area: $userArea")
//
//        if (concernCodes.isEmpty()) {
//            println("No concerns detected. Exiting.")
//            return
//        }

        var concernCodes = listOf(
            "oily_skin",
            "acne_comedonal",
            "enlarged_pores",
            "sebaceous_filaments"
        )
        val userArea = "face"

        val concernEntities = concernRepository.findByCodeIn(concernCodes)

        if (concernEntities.isEmpty()) {
            println("No concerns found in DB.")
            return
        }

        val concernIds = concernEntities.map { it.id }

        val products = recommendationService.recommendProducts(
            userConcernCodes = concernCodes,
            userConcernIds = concernIds,
            userArea = userArea
        )

        println("\nRecommended products:")

        products.forEachIndexed { index, product ->
            println("${index + 1}. ${product.name} - ${product.id}")
        }
    }
}