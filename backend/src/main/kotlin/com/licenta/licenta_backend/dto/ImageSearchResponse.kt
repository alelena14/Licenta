package com.licenta.licenta_backend.dto

data class ApiImageResponse(
    val images_results: List<ApiImageResult> = emptyList()
)

data class ApiImageResult(
    val original: String,
    val thumbnail: String? = null,
    val source: String? = null
)
