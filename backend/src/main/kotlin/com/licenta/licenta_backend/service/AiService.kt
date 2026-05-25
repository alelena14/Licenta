package com.licenta.licenta_backend.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.licenta.licenta_backend.dto.ChatIntent
import com.licenta.licenta_backend.dto.IntentType
import com.licenta.licenta_backend.repository.ConcernRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
    @Qualifier("aiPythonClient") private val aiPythonClient: WebClient,
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

    fun extractConcernsByArea(userInput: String): Map<String, List<String>> {

        val validCodes = concernRepository.findAllCodes()

        val prompt = """
        You are a dermatology classification engine.
 
        From the user description, extract skin concerns and group them by target area.
 
        Rules:
        - Only use concern codes from the valid list below.
        - Areas must be exactly: "face", "eyes" or "other".
        - A user can have concerns for MULTIPLE areas simultaneously.
          Example: "I have acne and eye bags" → { "face": ["acne"], "eyes": ["eye_bags"] }
        - dark_circles, eye_bags, puffiness, under_eye concerns always go to "eyes".
        - Everything else goes to "face".
        - Omit an area entirely if no concerns belong to it.
        - If the concern is about the body, return other.
        - If nothing matches return: {}
        - Return JSON only, no markdown, no explanation.
 
        Return format:
        {
          "face": ["concern_code1", "concern_code2"],
          "eyes": ["concern_code3"]
        }
 
        User description:
        $userInput
 
        Valid concern codes:
        ${validCodes.joinToString(", ")}
    """.trimIndent()

        val requestBody = buildRequestBody(
            userPrompt   = prompt,
            systemPrompt = "You are a precise classification engine. Return only valid JSON.",
            maxTokens    = 200
        )

        return try {
            val content = callGroq(requestBody) ?: return emptyMap()
            val raw: Map<String, Any> = mapper.readValue(content)

            raw.mapNotNull { (area, codes) ->
                if (area !in listOf("face", "eyes")) return@mapNotNull null
                val concernList = (codes as? List<*>)
                    ?.filterIsInstance<String>()
                    ?.filter { validCodes.contains(it) }
                    ?: return@mapNotNull null
                if (concernList.isEmpty()) return@mapNotNull null
                area to concernList
            }.toMap()

        } catch (e: Exception) {
            println("extractConcernsByArea error: ${e.message}")
            emptyMap()
        }
    }

    fun extractConcernsFromFace(file: MultipartFile): Pair<List<String>, String> {

        val aiResult = analyzeFace(file) ?: return Pair(emptyList(), "face")

        val predictions = aiResult["final_predictions"] as? List<Map<String, Any>> ?: emptyList()

        val validCodes = concernRepository.findAllCodes()

        val prompt = """
        You are a dermatology AI.

        Based on detected skin conditions from an image:
        ${predictions.joinToString("\n") { "${it["label"]}: ${it["confidence"]}" }}

        Map these into VALID concern codes from this list:
        ${validCodes.joinToString(", ")}

        Rules:
        - Only return concerns from the valid list
        - Be medically logical (e.g. eyebags → dark_circles)
        - Return 1-3 most relevant concerns
        - Also decide area: "face" or "eyes"

        Return JSON only:
        {
          "concerns": ["code1","code2"],
          "area": "face"
        }
    """.trimIndent()

        val requestBody = buildRequestBody(
            prompt,
            systemPrompt = "You are a dermatology classification engine. Return only JSON.",
            maxTokens = 200
        )

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
            println("extractConcernsFromFaceWithAI error: ${e.message}")
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
    // 4. analyzeFace — se analizeaza fata cu modelul ai
    // ─────────────────────────────────────────────────────────────────────────

    fun analyzeFace(file: MultipartFile): Map<String, Any>? {
        return try {
            val response = aiPythonClient.post()
                .uri("/analyze")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(
                    BodyInserters.fromMultipartData("file", file.resource)
                )
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

            if (response.isNullOrBlank()) return null

            mapper.readValue(response)

        } catch (e: Exception) {
            println("Face analysis error: ${e.message}")
            null
        }
    }

    fun detectIntent(message: String): ChatIntent {

        val prompt = """
        Analyze the user's skincare-related message and extract intent information.

        Return ONLY valid JSON in this format:

        {
          "type": "RECOMMENDATION",
          "concerns": [],
          "productType": null,
          "ingredient": null,
          "productName": null,
          "isFollowUp": false,
          "rawQuery": ""
        }

        Allowed intent types:
        - RECOMMENDATION
        - PRODUCT_QUESTION
        - INGREDIENT_QUESTION
        - CASUAL
        - BODY_CARE
        - UNKNOWN

        Rules:
        - CASUAL = greetings, thanks, small talk
        - BODY_CARE = body acne, body lotion, hands, legs, scalp, etc.
        - INGREDIENT_QUESTION = asks about ingredients like niacinamide, retinol, vitamin c
        - PRODUCT_QUESTION = asks about a specific product
        - RECOMMENDATION = user wants products/routine suggestions
        - UNKNOWN = unclear skincare intent

        Examples:

        User: "thank you!"
        {
          "type": "CASUAL",
          "concerns": [],
          "productType": null,
          "ingredient": null,
          "productName": null,
          "isFollowUp": true,
          "rawQuery": "thank you!"
        }

        User: "what does niacinamide do?"
        {
          "type": "INGREDIENT_QUESTION",
          "concerns": [],
          "productType": null,
          "ingredient": "niacinamide",
          "productName": null,
          "isFollowUp": false,
          "rawQuery": "what does niacinamide do?"
        }

        User: "recommend a serum for acne"
        {
          "type": "RECOMMENDATION",
          "concerns": ["acne"],
          "productType": "serum",
          "ingredient": null,
          "productName": null,
          "isFollowUp": false,
          "rawQuery": "recommend a serum for acne"
        }

        User message:
        $message
    """.trimIndent()

        val requestBody = buildRequestBody(
            userPrompt = message,
            systemPrompt = prompt,
            maxTokens = 250
        )

        val response = callGroq(requestBody) ?: return ChatIntent(
            type = IntentType.UNKNOWN,
            rawQuery = message
        )

        return try {
            mapper.readValue(response, ChatIntent::class.java)
        } catch (e: Exception) {
            ChatIntent(
                type = IntentType.UNKNOWN,
                rawQuery = message
            )
        }
    }
    // ─────────────────────────────────────────────────────────────────────────
    // Helper: apel Groq refolosibil
    // ─────────────────────────────────────────────────────────────────────────

    fun buildRequestBody(
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

    fun callGroq(requestBody: Map<String, Any>): String? {
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