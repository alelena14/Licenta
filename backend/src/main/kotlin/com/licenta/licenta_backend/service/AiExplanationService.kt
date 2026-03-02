package com.licenta.licenta_backend.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.licenta.licenta_backend.config.AiApiProperties
import com.licenta.licenta_backend.dto.ProductExplanationContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class AiExplanationService(
    @Qualifier("groqClient")
    private val webClient: WebClient,
    private val aiProperties: AiApiProperties
) {

    private val mapper = jacksonObjectMapper()

    fun generateExplanation(context: ProductExplanationContext): String {

        val prompt = buildPrompt(context)

        val requestBody = mapOf(
            "model" to "llama-3.3-70b-versatile",
            "messages" to listOf(
                mapOf("role" to "system", "content" to "You are a skincare formulation expert."),
                mapOf("role" to "user", "content" to prompt)
            ),
            "temperature" to 0.4,
            "max_tokens" to 400
        )

        val response = webClient.post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer ${aiProperties.apiKey}")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        val choices = response?.get("choices") as List<*>
        val message = (choices[0] as Map<*, *>)["message"] as Map<*, *>

        return message["content"].toString()
    }

    private fun buildPrompt(context: ProductExplanationContext): String {
        val jsonContext = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(context)

        return """
        Explain why this product is suitable for the user's concerns.
        
        Use ONLY the structured data below.
        Do NOT invent ingredients.
        Keep the explanation under 150 words.
        Mention:
        - why it helps
        - when to use it
        - warnings if any
        
        Structured Data:
        $jsonContext
                """.trimIndent()
            }
}