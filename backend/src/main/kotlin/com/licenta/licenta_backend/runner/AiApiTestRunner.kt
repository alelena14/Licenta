package com.licenta.licenta_backend.runner

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
    private val recommendationService: RecommendationService
) : CommandLineRunner {

    override fun run(vararg args: String?) {

//        val testInput = "I have bumps on my nose and very oily skin"
//
//        println("User input: $testInput")
//
//        // Extragem concerns
//        val concerns = aiService.extractConcerns(testInput)
//        println("Extracted concerns: $concerns")
//
//        if (concerns.isEmpty()) {
//            println("No concerns detected. Exiting.")
//            return
//        }

        val concerns = listOf(
            "oily_skin",
            "acne_comedonal",
            "enlarged_pores",
            "sebaceous_filaments"
        )

        val products = recommendationService.recommendProducts(concerns)

        println("\nRecommended products:")

        products.forEachIndexed { index, product ->
            println("${index + 1}. ${product.name}")
        }
    }
}