package com.licenta.licenta_backend.service

import com.google.api.client.util.Value
import com.licenta.licenta_backend.config.SerpApiProperties
import com.licenta.licenta_backend.dto.ApiImageResponse
import com.licenta.licenta_backend.repository.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class ProductImageSearchService(
    @Qualifier("serpApiWebClient")
    private val webClient: WebClient,
    private val serpApiProperties: SerpApiProperties
) {

    fun findProductImage(brand: String, name: String): String? {
        val query = "$brand $name product packaging"

        val response = webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/search.json")
                    .queryParam("engine", "google_images")
                    .queryParam("q", query)
                    .queryParam("api_key", serpApiProperties.apiKey)
                    .build()
            }
            .retrieve()
            .bodyToMono(ApiImageResponse::class.java)
            .block()

        return response?.images_results?.firstOrNull()?.original
    }
}

