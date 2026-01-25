package com.example.frontend.domain.repository

interface AuthRepository {

    suspend fun register(
        email: String,
        password: String,
        username: String,
        age: Int?,
        country: String,
        bio: String,
        photoUrl: String?
    ): Result<Unit>

    suspend fun login(email: String, password: String): Result<Unit>
}