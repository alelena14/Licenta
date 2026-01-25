package com.example.frontend.data.model

data class UserResponse(
    val id: Long,
    val email: String,
    val username: String?,
    val profileImageUrl: String?,
    val age: Int?
)