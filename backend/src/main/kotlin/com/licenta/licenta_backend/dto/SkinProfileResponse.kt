package com.licenta.licenta_backend.dto

data class SkinProfileResponse(
    val skinType: String?,
    val concerns: List<String>
)

data class UpdateSkinProfileRequest(
    val skinType: String?,
    val concerns: List<String>
)
