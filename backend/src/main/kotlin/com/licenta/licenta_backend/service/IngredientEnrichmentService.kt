package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.config.RapidApiProperties
import com.licenta.licenta_backend.dto.IngredientApiResponse
import com.licenta.licenta_backend.dto.IngredientApiWrapper
import com.licenta.licenta_backend.repository.IngredientRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Propagation
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class IngredientEnrichmentService(
    @Qualifier("rapidApiWebClient")
    private val webClient: WebClient,
    private val rapidApiProperties: RapidApiProperties,
    private val ingredientRepository: IngredientRepository
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun enrichIngredientById(ingredientId: Long) {

        val ingredient = ingredientRepository.findById(ingredientId)
            .orElse(null) ?: return

        val ingredientName = (ingredient.inciName ?: ingredient.name).trim()
        if (ingredientName.isBlank()) return

        val wrapper = webClient.get()
            .uri("/v1/ingredient/{name}", ingredientName)
            .header("X-RapidAPI-Key", rapidApiProperties.apiKey)
            .header("X-RapidAPI-Host", rapidApiProperties.host)
            .retrieve()
            .onStatus({ it.value() == 404 }) { Mono.empty() }
            .bodyToMono(IngredientApiWrapper::class.java)
            .block() ?: return

        val response = wrapper.ingredient ?: return

        ingredient.apply {
            inciName = response.inciName ?: inciName
            category = response.category ?: category
            comedogenicRating = response.comedogenicRating ?: comedogenicRating
            irritationPotential = response.irritationPotential ?: irritationPotential

            safetyScore = response.safetyScore?.toDoubleOrNull() ?: safetyScore

            function = response.function ?: function
            benefits = response.benefits ?: benefits
            concerns = response.concerns ?: concerns
            suitableFor = response.suitableFor ?: suitableFor
            avoidIf = response.avoidIf ?: avoidIf
            pairsWellWith = response.pairsWellWith ?: pairsWellWith
            avoidMixing = response.avoidMixing ?: avoidMixing
            description = response.description ?: description
            concentrationRange = response.concentrationRange ?: concentrationRange

            enrichedAt = Instant.now()
        }

    }
}
