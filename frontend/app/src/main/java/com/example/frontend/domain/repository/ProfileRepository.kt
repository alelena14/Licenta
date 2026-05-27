package com.example.frontend.domain.repository

import com.example.frontend.data.model.ProfileStatsDto
import com.example.frontend.data.model.SkinProfileDto
import com.example.frontend.data.model.UpdateSkinProfileRequest

interface ProfileRepository {

    suspend fun getSkinProfile(): SkinProfileDto

    suspend fun updateSkinProfile(request: UpdateSkinProfileRequest): SkinProfileDto

    suspend fun getStats(token: String): Result<ProfileStatsDto>

    suspend fun saveConcernsFromAnalysis(token: String, concerns: List<String>): Result<Unit>
}