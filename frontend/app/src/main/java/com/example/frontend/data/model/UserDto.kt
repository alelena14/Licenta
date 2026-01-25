package com.example.frontend.data.model

data class UserDto(
    val token: String,
    val email: String,
    val username: String? = null,
    val profileImageUrl: String?,
    val age: Int?
)
