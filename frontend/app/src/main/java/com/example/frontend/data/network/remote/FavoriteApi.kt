package com.example.frontend.data.network.remote


import com.example.frontend.data.model.DeleteFavoriteResponse
import com.example.frontend.data.model.SaveFavoriteRequest
import com.example.frontend.data.model.SaveFavoriteResponse
import com.example.frontend.data.model.SavedProductDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FavoriteApi {

    @POST("api/favorites")
    suspend fun saveFavorite(
        @Query("uid") uid: String,
        @Body request: SaveFavoriteRequest
    ): Response<SaveFavoriteResponse>

    @GET("api/favorites")
    suspend fun getSavedProducts(
        @Query("uid") uid: String
    ): Response<List<SavedProductDto>>

    @DELETE("api/favorites/{productId}")
    suspend fun removeFavorite(
        @Path("productId") productId: Long,
        @Query("uid") uid: String
    ): Response<DeleteFavoriteResponse>


    @GET("api/favorites/{productId}/exists")
    suspend fun isFavorite(
        @Path("productId") productId: Long,
        @Query("uid") uid: String
    ): Response<Map<String, Boolean>>
}