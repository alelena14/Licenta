package com.licenta.licenta_backend.service

import com.google.api.client.util.Value
import com.licenta.licenta_backend.dto.GoogleImageSearchResponseDto
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class GoogleImageSearchService(
    @Value("\${google.api.key}")
    private val apiKey: String,

    @Value("\${google.custom-search.cx-id}")
    private val cxId: String,

    private val restTemplate: RestTemplate
) {

    fun findBestImage(query: String): String? {
        val url = UriComponentsBuilder
            .fromHttpUrl("https://www.googleapis.com/customsearch/v1")
            .queryParam("key", apiKey)
            .queryParam("cx", cxId)
            .queryParam("q", query)
            .queryParam("searchType", "image")
            .queryParam("num", 1)
            .build()
            .toUri()

        val response = restTemplate.getForObject(
            url,
            GoogleImageSearchResponseDto::class.java
        )

        return response?.items?.firstOrNull()?.link
    }
}
