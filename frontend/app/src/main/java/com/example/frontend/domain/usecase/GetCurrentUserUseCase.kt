package com.example.frontend.domain.usecase

import com.example.frontend.domain.model.User
import com.example.frontend.domain.repository.UserRepository
import com.example.frontend.data.model.UserDto
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userDto: UserDto): Result<User> {
        val result = repository.syncUserWithBackend(userDto)

        return result.map { response ->
            User(
                id = response.id,
                email = response.email,
                username = response.username,
                profileImageUrl = response.profileImageUrl,
                age = response.age
            )
        }
    }
}