package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.model.IngredientConcern
import com.licenta.licenta_backend.repository.ConcernRepository
import com.licenta.licenta_backend.repository.IngredientConcernRepository
import com.licenta.licenta_backend.repository.IngredientRepository
import org.springframework.stereotype.Service

@Service
class IngredientConcernService(
    private val ingredientRepository: IngredientRepository,
    private val concernRepository: ConcernRepository,
    private val ingredientConcernRepository: IngredientConcernRepository,
    private val aiService: AiService
) {

    fun backfillIngredientConcerns(delayMs: Long = 4_000L, batchSize: Int = 500) {

        val all = ingredientRepository.findAllWithoutConcerns()
        val ingredients = all.take(batchSize)
        println("Found ${all.size} total without concerns → processing ${ingredients.size} in this batch")

        val concernsMap = concernRepository.findAll().associateBy { it.code }

        var processed = 0
        var skipped = 0
        var errors = 0

        for (ingredient in ingredients) {
            try {
                val classification = aiService.classifyIngredient(ingredient.name)

                // Ingrediente neidentificate sau cu confidence scazut → skip complet
                if (!classification.identified || classification.concerns.isEmpty()) {
                    println("Skipped (unidentified): ${ingredient.name}")
                    skipped++
                    Thread.sleep(300)
                    continue
                }

                classification.concerns.forEach { (concernCode, concernData) ->

                    val concern = concernsMap[concernCode]
                    if (concern == null) {
                        println("  Unknown concern code '$concernCode' for ${ingredient.name}")
                        return@forEach
                    }

                    val exists = ingredientConcernRepository
                        .existsByIngredientIdAndConcernId(ingredient.id, concern.id)

                    if (!exists) {
                        ingredientConcernRepository.save(
                            IngredientConcern(
                                ingredientId = ingredient.id,
                                concernId = concern.id,
                                weight = concernData.weight,           // weight calibrat final
                                mechanism = concernData.mechanism.name,
                                evidenceLevel = concernData.evidenceLevel.name,
                                needsReview = classification.needsReview,
                                reasoning = concernData.reasoning
                            )
                        )
                    }
                }

                println("✓ Processed: ${ingredient.name} " +
                        "(${classification.concerns.size} concerns, " +
                        "needsReview=${classification.needsReview})")

                processed++

                Thread.sleep(delayMs)

            } catch (e: Exception) {
                println("Error at '${ingredient.name}': ${e.message}")
                errors++
                Thread.sleep(1000) // pauză mai lungă după eroare
            }
        }

        println("\n=== Backfill completed ===")
        println("  Processed : $processed")
        println("  Skipped   : $skipped")
        println("  Errors    : $errors")
    }


    fun validatePendingConcerns(delayMs: Long = 4_000L) {

        val pending = ingredientConcernRepository.findAllNeedingReview()
        println("Found ${pending.size} ingredient-concerns needing review")

        val concernsById = concernRepository.findAll().associateBy { it.id }
        val ingredientsById = ingredientRepository.findAll().associateBy { it.id }

        var corrected = 0
        var confirmed = 0

        for (ic in pending) {
            try {
                val ingredientName = ingredientsById[ic.ingredientId]?.name ?: continue
                val concernCode = concernsById[ic.concernId]?.code ?: continue

                val existing = ConcernClassification(
                    mechanism = Mechanism.valueOf(ic.mechanism ?: "SUPPORTS"),
                    weight = ic.weight,
                    evidenceLevel = EvidenceLevel.valueOf(ic.evidenceLevel ?: "ANECDOTAL")
                )

                val validation = aiService.validateClassification(ingredientName, concernCode, existing)

                if (!validation.validated) {
                    // Aplica corectiile
                    val newMechanism = validation.correctedMechanism ?: existing.mechanism
                    val newEvidence = validation.correctedEvidenceLevel ?: existing.evidenceLevel
                    val newRawWeight = validation.correctedWeight ?: ic.weight


                    ingredientConcernRepository.save(
                        ic.copy(
                            mechanism = newMechanism.name,
                            evidenceLevel = newEvidence.name,
                            weight = newRawWeight,
                            needsReview = false,  // marcat ca validat
                            reasoning = validation.reason
                        )
                    )

                    println("  ✓ Corrected: $ingredientName → $concernCode " +
                            "(${existing.mechanism} → $newMechanism, reason: ${validation.reason})")
                    corrected++

                } else {
                    // Confirmat corect → scoate din coada de review
                    ingredientConcernRepository.save(ic.copy(needsReview = false))
                    confirmed++
                }

                Thread.sleep(delayMs)

            } catch (e: Exception) {
                println(" Validation error for ic=${ic.ingredientId}/${ic.concernId}: ${e.message}")
            }
        }

        println("\n=== Validation completed ===")
        println("  Confirmed : $confirmed")
        println("  Corrected : $corrected")
    }



    fun reclassifyIngredient(ingredientName: String): String {

        val ingredient = ingredientRepository.findByName(ingredientName)
            ?: return "Ingredient '$ingredientName' not found in DB"

        val concernsMap = concernRepository.findAll().associateBy { it.code }
        val classification = aiService.classifyIngredient(ingredientName)

        if (!classification.identified) {
            return "Could not identify ingredient '$ingredientName' (confidence too low)"
        }

        // Șterge clasificările vechi
        ingredientConcernRepository.deleteByIngredientId(ingredient.id)

        // Salvează clasificările noi
        classification.concerns.forEach { (concernCode, concernData) ->
            val concern = concernsMap[concernCode] ?: return@forEach
            ingredientConcernRepository.save(
                IngredientConcern(
                    ingredientId = ingredient.id,
                    concernId = concern.id,
                    weight = concernData.weight,
                    mechanism = concernData.mechanism.name,
                    evidenceLevel = concernData.evidenceLevel.name,
                    needsReview = classification.needsReview,
                    reasoning = concernData.reasoning
                )
            )
        }

        return buildString {
            appendLine("Reclassified '$ingredientName':")
            classification.concerns.forEach { (code, data) ->
                appendLine("  $code → ${data.mechanism} | ${data.evidenceLevel} | weight=${String.format("%.3f", data.weight)}")
                if (data.reasoning.isNotBlank()) appendLine("    reasoning: ${data.reasoning}")
            }
            if (classification.needsReview) appendLine("  ⚠ Marked for review (all ANECDOTAL)")
        }
    }
}
