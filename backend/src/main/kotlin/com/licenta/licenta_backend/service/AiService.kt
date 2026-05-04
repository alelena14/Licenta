package com.licenta.licenta_backend.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.licenta.licenta_backend.repository.ConcernRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

// ─── Data classes ────────────────────────────────────────────────────────────

enum class Mechanism { TREATS, PREVENTS, SUPPORTS, CONTRAINDICATES, NONE }
enum class EvidenceLevel { GOLD_STANDARD, CLINICAL, ANECDOTAL, NONE }

data class ConcernClassification(
    val mechanism: Mechanism,
    val weight: Double,
    val evidenceLevel: EvidenceLevel,
    val reasoning: String = ""
)

data class IngredientClassification(
    val ingredient: String,
    val identified: Boolean,
    val confidence: Double,
    val commonName: String? = null,
    val concerns: Map<String, ConcernClassification> = emptyMap(),
    val needsReview: Boolean = false
) {
    companion object {
        fun unknown(name: String) = IngredientClassification(
            ingredient = name,
            identified = false,
            confidence = 0.0
        )
    }
}

data class ValidationResult(
    val validated: Boolean,
    val correctedMechanism: Mechanism? = null,
    val correctedWeight: Double? = null,
    val correctedEvidenceLevel: EvidenceLevel? = null,
    val reason: String = ""
)

// ─── Service ──────────────────────────────────────────────────────────────────

@Service
class AiService(
    @Qualifier("groqClient") private val groqWebClient: WebClient,
    private val concernRepository: ConcernRepository
) {

    private val mapper = jacksonObjectMapper()

    // ─────────────────────────────────────────────────────────────────────────
    // 1. extractConcerns
    // ─────────────────────────────────────────────────────────────────────────

    fun extractConcerns(userInput: String): Pair<List<String>, String> {

        val validCodes = concernRepository.findAllCodes()

        val prompt = """
            You are a dermatology classification engine.
            
            From the user description extract:
            1) One or more concern codes from the provided list.
            2) The target area: one of ["face","eyes"].
            
            Rules:
            - Only return concern codes from the list.
            - Target area must be one of the predefined values.
            - Do NOT explain anything.
            - dark circles and under eye bags have the area 'eyes'
            - Return JSON only:
            
            {
              "concerns":["code1","code2"],
              "area":"face"
            }
            
            If nothing matches:
            {
              "concerns":[],
              "area":"face"
            }
            
            User description:
            $userInput
            
            Valid concern codes:
            ${validCodes.joinToString(", ")}
        """.trimIndent()

        val requestBody = buildRequestBody(prompt, systemPrompt = "You are a precise classification engine. Return only JSON.", maxTokens = 200)

        return try {
            val content = callGroq(requestBody) ?: return Pair(emptyList(), "face")
            val result: Map<String, Any> = mapper.readValue(content)

            val concerns = (result["concerns"] as? List<*>)
                ?.filterIsInstance<String>()
                ?.filter { validCodes.contains(it) }
                ?: emptyList()

            val area = result["area"] as? String ?: "face"
            Pair(concerns, area)

        } catch (e: Exception) {
            println("extractConcerns error: ${e.message}")
            Pair(emptyList(), "face")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. classifyIngredient
    // ─────────────────────────────────────────────────────────────────────────

    fun classifyIngredient(ingredientName: String): IngredientClassification {

        val validCodes = concernRepository.findAllCodes()

        val prompt = """
            Skincare ingredient classifier. Ingredient: "$ingredientName"
            Valid concerns: ${validCodes.joinToString(", ")}
            
            Return JSON only:
            {
              "identified": true,
              "confidence": 0.9,
              "concerns": {
                "concern_code": {
                  "mechanism": "TREATS|PREVENTS|SUPPORTS|CONTRAINDICATES",
                  "weight": 0.0-1.0,
                  "evidence_level": "GOLD_STANDARD|CLINICAL|ANECDOTAL"
                }
              }
            }
            
            Evidence level definitions (STRICT):
            - GOLD_STANDARD: multiple RCTs, FDA/EMA approved for this specific use
            - CLINICAL: at least one peer-reviewed clinical study
            - ANECDOTAL: industry/community consensus, no strong clinical backing
            - NONE: no evidence → omit this concern entirely
            
            Rules: TREATS=root cause proven, PREVENTS=reduces risk, SUPPORTS=indirect, CONTRAINDICATES=worsens.
            TREATS max weight 1.0, SUPPORTS max 0.5. Omit concerns with no relationship.
            If unrecognized: identified=false, empty concerns.
        """.trimIndent()

        val requestBody = buildRequestBody(
            prompt,
            systemPrompt = "You are a cosmetic dermatology expert. Return only valid JSON, no markdown.",
            maxTokens = 800
        )

        return try {
            val content = callGroq(requestBody) ?: return IngredientClassification.unknown(ingredientName)
            val raw: Map<String, Any> = mapper.readValue(content)

            val identified = raw["identified"] as? Boolean ?: false
            val confidence = (raw["confidence"] as? Number)?.toDouble() ?: 0.0

            if (!identified || confidence < 0.6) {
                println("Ingredient not identified or low confidence: $ingredientName (confidence=$confidence)")
                return IngredientClassification.unknown(ingredientName)
            }

            val commonName = raw["common_name"] as? String
            val rawConcerns = raw["concerns"] as? Map<*, *> ?: emptyMap<String, Any>()

            val concerns = rawConcerns.mapNotNull { (key, value) ->
                val code = key as? String ?: return@mapNotNull null
                if (!validCodes.contains(code)) return@mapNotNull null

                val concernMap = value as? Map<*, *> ?: return@mapNotNull null

                val mechanism = runCatching {
                    Mechanism.valueOf((concernMap["mechanism"] as? String) ?: "NONE")
                }.getOrDefault(Mechanism.NONE)

                val evidenceLevel = runCatching {
                    EvidenceLevel.valueOf((concernMap["evidence_level"] as? String) ?: "NONE")
                }.getOrDefault(EvidenceLevel.NONE)

                if (mechanism == Mechanism.NONE || evidenceLevel == EvidenceLevel.NONE) {
                    return@mapNotNull null
                }

                val rawWeight = (concernMap["weight"] as? Number)?.toDouble() ?: 0.0
                val reasoning = concernMap["reasoning"] as? String ?: ""

                code to ConcernClassification(
                    mechanism = mechanism,
                    weight = rawWeight,
                    evidenceLevel = evidenceLevel,
                    reasoning = reasoning
                )
            }.toMap()

            // Ingredientele cu doar date ANECDOTAL primesc flag needsReview
            val needsReview = concerns.values.all { it.evidenceLevel == EvidenceLevel.ANECDOTAL }

            IngredientClassification(
                ingredient = ingredientName,
                identified = true,
                confidence = confidence,
                commonName = commonName,
                concerns = concerns,
                needsReview = needsReview
            )

        } catch (e: Exception) {
            println("classifyIngredient error for '$ingredientName': ${e.message}")
            IngredientClassification.unknown(ingredientName)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. validateClassification — double-check pentru date incerte
    // ─────────────────────────────────────────────────────────────────────────

    fun validateClassification(
        ingredientName: String,
        concern: String,
        existing: ConcernClassification
    ): ValidationResult {

        val prompt = """
            You are a cosmetic dermatology expert.

            A previous analysis classified the ingredient "$ingredientName" as:
            - Concern: $concern
            - Mechanism: ${existing.mechanism}
            - Weight: ${existing.weight}
            - Evidence level: ${existing.evidenceLevel}

            Your job: validate or correct this classification.

            Answer ONLY with JSON (no markdown):
            {
              "validated": true|false,
              "corrected_mechanism": "TREATS|PREVENTS|SUPPORTS|CONTRAINDICATES|NONE",
              "corrected_weight": 0.0-1.0,
              "corrected_evidence_level": "GOLD_STANDARD|CLINICAL|ANECDOTAL|NONE",
              "reason": "one sentence"
            }

            Key checks:
            - Is TREATS correctly assigned? (root cause intervention only, very strict)
            - Is GOLD_STANDARD justified? (requires multiple RCTs or FDA/EMA approval)
            - Is the weight realistic within its mechanism ceiling?

            If validated=true, still return all fields (can repeat the original values).
        """.trimIndent()

        val requestBody = buildRequestBody(
            prompt,
            systemPrompt = "You are a cosmetic dermatology expert. Return only valid JSON.",
            maxTokens = 300
        )

        return try {
            val content = callGroq(requestBody) ?: return ValidationResult(validated = true)
            val raw: Map<String, Any> = mapper.readValue(content)

            val validated = raw["validated"] as? Boolean ?: true
            val reason = raw["reason"] as? String ?: ""

            if (validated) {
                return ValidationResult(validated = true, reason = reason)
            }

            val correctedMechanism = runCatching {
                Mechanism.valueOf((raw["corrected_mechanism"] as? String) ?: "")
            }.getOrNull()

            val correctedWeight = (raw["corrected_weight"] as? Number)?.toDouble()

            val correctedEvidenceLevel = runCatching {
                EvidenceLevel.valueOf((raw["corrected_evidence_level"] as? String) ?: "")
            }.getOrNull()

            ValidationResult(
                validated = false,
                correctedMechanism = correctedMechanism,
                correctedWeight = correctedWeight,
                correctedEvidenceLevel = correctedEvidenceLevel,
                reason = reason
            )

        } catch (e: Exception) {
            println("validateClassification error: ${e.message}")
            ValidationResult(validated = true, reason = "validation failed, keeping original")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper: apel Groq refolosibil
    // ─────────────────────────────────────────────────────────────────────────

    private fun buildRequestBody(
        userPrompt: String,
        systemPrompt: String = "You return only JSON.",
        maxTokens: Int = 500
    ): Map<String, Any> = mapOf(
        "model" to "llama-3.1-8b-instant",
        "messages" to listOf(
            mapOf("role" to "system", "content" to systemPrompt),
            mapOf("role" to "user", "content" to userPrompt)
        ),
        "temperature" to 0.0,
        "max_tokens" to maxTokens
    )

    private fun callGroq(requestBody: Map<String, Any>): String? {
        return try {
            val response = groqWebClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus({ it.isError }) { clientResponse ->
                    clientResponse.bodyToMono(String::class.java)
                        .map { RuntimeException("Groq API error: $it") }
                }
                .bodyToMono(String::class.java)
                .block()

            if (response.isNullOrBlank()) return null

            val parsed: Map<String, Any> = mapper.readValue(response)
            val choices = parsed["choices"] as? List<*> ?: return null
            val firstChoice = choices.firstOrNull() as? Map<*, *> ?: return null
            val message = firstChoice["message"] as? Map<*, *> ?: return null
            val content = message["content"] as? String ?: return null

            content.replace("```json", "").replace("```", "").trim()

        } catch (e: Exception) {
            println("Groq call error: ${e.message}")
            null
        }
    }
}