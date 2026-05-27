package com.example.frontend.data.network.remote

import com.example.frontend.data.model.ProductRecommendation
import retrofit2.http.GET
import retrofit2.http.Header

interface HomeApi {

    @GET("api/recommendations/home")
    suspend fun getHomeRecommendations(
        @Header("Authorization") token: String
    ): List<ProductRecommendation>
}