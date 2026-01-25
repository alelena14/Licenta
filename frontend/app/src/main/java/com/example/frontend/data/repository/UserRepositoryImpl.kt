package com.example.frontend.data.repository

import com.example.frontend.data.model.UserDto
import com.example.frontend.data.network.UserApiService
import com.example.frontend.domain.repository.UserRepository
import com.example.frontend.data.model.UserResponse
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService
) : UserRepository {
    override suspend fun syncUserWithBackend(userDto: UserDto): Result<UserResponse> {
        return try {
            val response = apiService.syncUser(userDto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Eroare Server: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
