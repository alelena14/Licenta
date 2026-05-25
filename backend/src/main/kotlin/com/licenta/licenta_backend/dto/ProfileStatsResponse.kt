package com.licenta.licenta_backend.dto

data class ProfileStatsResponse(
    val conversations: Int,
    val savedProducts: Int,
    val skinConcerns: Int
)