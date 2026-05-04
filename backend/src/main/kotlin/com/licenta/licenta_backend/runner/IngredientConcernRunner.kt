package com.licenta.licenta_backend.runner

import com.licenta.licenta_backend.service.IngredientConcernService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component


@Component
@ConditionalOnProperty(
    prefix = "app.ai-backfill",
    name = ["enabled"],
    havingValue = "true"
)
class IngredientConcernRunner(
    private val ingredientConcernService: IngredientConcernService,

    @Value("\${app.ai-backfill.batch-size:500}")
    private val batchSize: Int,

    @Value("\${app.ai-backfill.delay-ms:4000}")
    private val delayMs: Long,

    @Value("\${app.ai-backfill.pause-between-phases-ms:30000}")
    private val pauseBetweenPhasesMs: Long
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        println("=== Ingredient Backfill Runner ===")
        println("  Batch size : $batchSize ingredients")
        println("  Delay      : ${delayMs}ms/ingredient (~${60_000 / delayMs} ingredients/min)")
        println("  Est. time  : ~${estimateMinutes(batchSize, delayMs)} min for this batch")
        println("  Tokens/day : ~${batchSize * 800} (limit: 500,000)")
        println()

        // ── Phase 1: Backfill ────────────────────────────────────────────────
        println("=== Phase 1: Backfill ===")
        ingredientConcernService.backfillIngredientConcerns(
            delayMs = delayMs,
            batchSize = batchSize
        )

        // ── Pauza intre faze ─────────────────────────────────────────────────
        println("\n Waiting ${pauseBetweenPhasesMs / 1000}s before validation phase...")
        Thread.sleep(pauseBetweenPhasesMs)

        // ── Phase 2: Validate (doar needsReview=true din batch-ul curent) ───
        println("\n=== Phase 2: Validate pending ===")
        ingredientConcernService.validatePendingConcerns(
            delayMs = delayMs
        )

        println("\n=== All done ===")
    }

    private fun estimateMinutes(batch: Int, delay: Long): Long {
        val phase1 = batch * delay
        val phase2 = (batch * 0.35 * delay).toLong()
        return (phase1 + phase2 + pauseBetweenPhasesMs) / 60_000
    }
}