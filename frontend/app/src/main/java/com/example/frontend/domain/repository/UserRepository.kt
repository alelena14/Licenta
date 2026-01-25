package com.example.frontend.domain.repository

import com.example.frontend.data.model.UserDto
import com.example.frontend.data.model.UserResponse

interface UserRepository {
    suspend fun syncUserWithBackend(userDto: UserDto): Result<UserResponse>
}