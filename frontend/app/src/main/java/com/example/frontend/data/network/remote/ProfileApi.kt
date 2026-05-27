package com.example.frontend.data.network.remote

import com.example.frontend.data.model.ProfileStatsDto
import com.example.frontend.data.model.SkinProfileDto
import com.example.frontend.data.model.UpdateSkinProfileRequest
import retrofit2.Response
import retrofit2.http.*

interface ProfileApi {

    @GET("api/profile")
    suspend fun getSkinProfile(
        @Header("Authorization") token: String
    ): SkinProfileDto

    @GET("api/profile/stats")
    suspend fun getStats(
        @Header("Authorization") token: String
    ): ProfileStatsDto

    @PUT("api/profile")
    suspend fun updateSkinProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateSkinProfileRequest
    ): SkinProfileDto

    @POST("api/profile/concerns/from-analysis")
    suspend fun saveConcernsFromAnalysis(
        @Header("Authorization") token: String,
        @Body concerns: List<String>
    ): Response<Unit>
}