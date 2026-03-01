package com.licenta.licenta_backend.runner

import com.licenta.licenta_backend.repository.IngredientRepository
import com.licenta.licenta_backend.service.IngredientEnrichmentService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = "app.ingredient-runner",
    name = ["enabled"],
    havingValue = "true"
)
class IngredientEnrichmentRunner(
    private val ingredientRepository: IngredientRepository,
    private val enrichmentService: IngredientEnrichmentService
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {

        val ingredientIds = ingredientRepository
            .findTopUnenrichedIngredients(100)
            .map { it.id }

        for (id in ingredientIds) {
            enrichmentService.enrichIngredientById(id)
            Thread.sleep(5_000)
        }
    }
}
