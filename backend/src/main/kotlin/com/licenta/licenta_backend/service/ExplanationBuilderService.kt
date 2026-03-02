package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.dto.IngredientExplanation
import com.licenta.licenta_backend.dto.ProductExplanationContext
import com.licenta.licenta_backend.model.Ingredient
import org.springframework.stereotype.Service

@Service
class ExplanationBuilderService {

    fun buildExplanationContext(
        productName: String,
        ingredients: List<Ingredient>,
        userConcerns: List<String>,
        userRoutineIngredients: List<String> = emptyList()
    ): ProductExplanationContext {

        val keyIngredients = mutableListOf<IngredientExplanation>()
        val warnings = mutableListOf<String>()
        val mixingConflicts = mutableListOf<String>()

        ingredients.forEach { ingredient ->

            val benefits = ingredient.benefits

            if (!benefits.isNullOrEmpty()) {
                keyIngredients.add(
                    IngredientExplanation(
                        name = ingredient.name,
                        benefits = benefits.toList(),
                        irritationPotential = ingredient.irritationPotential ?: "unknown",
                        role = determineIngredientRole(ingredient)
                    )
                )
            }

            // Warning pentru iritație
            if (ingredient.irritationPotential.equals("high", true)) {
                warnings.add(
                    "${ingredient.name} may cause irritation, especially for sensitive skin."
                )
            }

            // Conflict cu rutina userului
            ingredient.avoidMixing?.forEach { conflict ->
                if (userRoutineIngredients.contains(conflict)) {
                    mixingConflicts.add(
                        "Avoid mixing ${ingredient.name} with $conflict."
                    )
                }
            }
        }

        val usage = determineUsage(ingredients)
        val safetySummary = generateSafetySummary(ingredients)

        return ProductExplanationContext(
            productName = productName,
            matchedConcerns = userConcerns,
            keyIngredients = keyIngredients.take(5), // limităm ca să reducem tokens
            warnings = warnings.distinct(),
            mixingConflicts = mixingConflicts.distinct(),
            recommendedUsage = usage,
            safetySummary = safetySummary
        )
    }

    private fun determineIngredientRole(ingredient: Ingredient): String {
        return when (ingredient.category?.lowercase()) {
            "humectant" -> "Hydration support"
            "retinoid" -> "Cell turnover support"
            "aha", "bha" -> "Exfoliation support"
            "emollient" -> "Barrier support"
            else -> "Skin conditioning"
        }
    }

    private fun determineUsage(ingredients: List<Ingredient>): String {
        return when {
            ingredients.any { it.category.equals("retinoid", true) } ->
                "Use in the evening (PM only). Apply sunscreen during the day."

            ingredients.any {
                it.category.equals("aha", true) ||
                        it.category.equals("bha", true)
            } ->
                "Preferably use in the evening. Always apply SPF during the day."

            else -> "Can be used in the morning and evening."
        }
    }

    private fun generateSafetySummary(ingredients: List<Ingredient>): String {
        return when {
            ingredients.any { it.irritationPotential.equals("high", true) } ->
                "May not be suitable for very sensitive skin."

            ingredients.any { it.comedogenicRating != null && it.comedogenicRating!! >= 4 } ->
                "May not be ideal for acne-prone skin."

            else ->
                "Generally safe for most skin types."
        }
    }
}