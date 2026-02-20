package com.licenta.licenta_backend.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.licenta.licenta_backend.config.AiApiProperties
import com.licenta.licenta_backend.repository.ConcernRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class AiService(
    private val aiApiProperties: AiApiProperties,
    private val concernRepository: ConcernRepository
) {

    private val mapper = jacksonObjectMapper()

    private val webClient = WebClient.builder()
        .baseUrl("https://api.groq.com/openai/v1")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${aiApiProperties.apiKey}")
        .build()

    fun extractConcerns(userInput: String): List<String> {

        val validCodes = concernRepository.findAllCodes()

        val prompt = """
            You are a dermatology classification engine.
            
            Map the user description strictly to one or more concern codes from the provided list.
            
            Rules:
            - Only return concern codes from the list.
            - Do NOT explain anything.
            - Return JSON only:
              {"concerns":["code1","code2"]}
            - If nothing matches, return:
              {"concerns":[]}
            
            User description:
            $userInput
            
            Valid concern codes:
            ${validCodes.joinToString(", ")}
        """.trimIndent()

        val requestBody = mapOf(
            "model" to "llama-3.1-8b-instant",
            "messages" to listOf(
                mapOf("role" to "system", "content" to "You are a precise classification engine. Return only JSON."),
                mapOf("role" to "user", "content" to prompt)
            ),
            "temperature" to 0.0,
            "max_tokens" to 200
        )

        return try {

            val response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus({ it.isError }) { clientResponse ->
                    clientResponse.bodyToMono(String::class.java)
                        .map { RuntimeException("Groq API error: $it") }
                }
                .bodyToMono(String::class.java)
                .block()

            if (response.isNullOrBlank()) return emptyList()

            val parsed: Map<String, Any> = mapper.readValue(response)

            val choices = parsed["choices"] as? List<*> ?: return emptyList()
            val firstChoice = choices.firstOrNull() as? Map<*, *> ?: return emptyList()
            val message = firstChoice["message"] as? Map<*, *> ?: return emptyList()
            val content = message["content"] as? String ?: return emptyList()

            val cleaned = content
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val result: Map<String, List<String>> = mapper.readValue(cleaned)

            result["concerns"]
                ?.filter { validCodes.contains(it) }
                ?: emptyList()

        } catch (e: Exception) {
            println("Groq error: ${e.message}")
            emptyList()
        }
    }
}