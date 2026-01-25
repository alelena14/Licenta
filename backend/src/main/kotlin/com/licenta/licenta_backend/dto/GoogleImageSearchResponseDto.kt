package com.licenta.licenta_backend.dto

data class GoogleImageSearchResponseDto(
    val items: List<GoogleImageItem>?
)

data class GoogleImageItem(
    val link: String
)