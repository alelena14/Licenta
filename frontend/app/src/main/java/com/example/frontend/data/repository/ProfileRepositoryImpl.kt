package com.example.frontend.data.repository

import com.example.frontend.data.model.ProfileStatsDto
import com.example.frontend.data.model.SkinProfileDto
import com.example.frontend.data.model.UpdateSkinProfileRequest
import com.example.frontend.data.network.remote.ProfileApi
import com.example.frontend.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi,
    private val auth: FirebaseAuth
) : ProfileRepository {

    override suspend fun getSkinProfile(): SkinProfileDto {

        val token = auth.currentUser
            ?.getIdToken(true)
            ?.await()
            ?.token
            ?: throw RuntimeException("User not authenticated")

        return api.getSkinProfile(
            "Bearer $token"
        )
    }

    override suspend fun updateSkinProfile(request: UpdateSkinProfileRequest): SkinProfileDto {
        val token = "Bearer ${auth.currentUser?.getIdToken(true)?.await()?.token}"
        return api.updateSkinProfile(token, request)
    }

    override suspend fun getStats(token: String): Result<ProfileStatsDto> {
        return try {
            val stats = api.getStats("Bearer $token")
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveConcernsFromAnalysis(token: String, concerns: List<String>): Result<Unit> {
        return try {
            val response = api.saveConcernsFromAnalysis(token, concerns)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}