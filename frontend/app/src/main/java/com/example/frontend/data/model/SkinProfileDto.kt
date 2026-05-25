package com.example.frontend.data.model

data class SkinProfileDto(
    val skinType: String?,
    val concerns: List<String>
)

data class UpdateSkinProfileRequest(
    val skinType: String?,
    val concerns: List<String>
)