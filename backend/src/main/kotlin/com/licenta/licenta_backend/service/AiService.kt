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
    aiApiProperties: AiApiProperties,
    private val concernRepository: ConcernRepository
) {

    private val mapper = jacksonObjectMapper()

    private val webClient = WebClient.builder()
        .baseUrl("https://api.groq.com/openai/v1")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${aiApiProperties.apiKey}")
        .build()

    fun extractConcerns(userInput: String): Pair<List<String>, String> {

        val validCodes = concernRepository.findAllCodes()

        val prompt = """
            You are a dermatology classification engine.
            
            From the user description extract:
            
            1) One or more concern codes from the provided list.
            2) The target area: one of ["face","eyes","body","lips"].
            
            Rules:
            - Only return concern codes from the list.
            - Target area must be one of the predefined values.
            - Do NOT explain anything.
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

            if (response.isNullOrBlank()) return Pair(emptyList(), "face")

            val parsed: Map<String, Any> = mapper.readValue(response)

            val choices = parsed["choices"] as? List<*> ?: return Pair(emptyList(), "face")
            val firstChoice = choices.firstOrNull() as? Map<*, *> ?: return Pair(emptyList(), "face")
            val message = firstChoice["message"] as? Map<*, *> ?: return Pair(emptyList(), "face")
            val content = message["content"] as? String ?: return Pair(emptyList(), "face")

            val cleaned = content
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val result: Map<String, Any> = mapper.readValue(cleaned)

            val concerns = (result["concerns"] as? List<*>)
                ?.filterIsInstance<String>()
                ?.filter { validCodes.contains(it) }
                ?: emptyList()

            val area = result["area"] as? String ?: "face"

            Pair(concerns, area)

        } catch (e: Exception) {
            println("Groq error: ${e.message}")
            Pair(emptyList(), "face")
        }
    }
}