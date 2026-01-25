package com.licenta.licenta_backend.dto

data class UserDto(
    val token: String,
    val email: String,
    val username: String? = null,
    val profileImageUrl: String?,
    val age: Int?
)