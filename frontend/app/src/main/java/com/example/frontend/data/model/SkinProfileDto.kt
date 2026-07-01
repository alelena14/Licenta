package com.example.frontend.data.model

data class ConcernDto(
    val code: String,
    val displayName: String
)

data class SkinTypeUi(
    val code: String,
    val displayName: String
)

data class SkinProfileDto(
    val skinType: String?,
    val concerns: List<ConcernDto>
)

data class UpdateSkinProfileRequest(
    val skinType: String?,
    val concerns: List<String>
)