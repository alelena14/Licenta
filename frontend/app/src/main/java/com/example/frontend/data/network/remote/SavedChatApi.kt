package com.example.frontend.data.network.remote

import com.example.frontend.data.model.RenameChatRequest
import com.example.frontend.data.model.SaveChatRequest
import com.example.frontend.data.model.SavedChatDetail
import com.example.frontend.data.model.SavedChatSummary
import retrofit2.http.*

interface SavedChatApi {

    @GET("api/chats")
    suspend fun getChats(@Query("uid") uid: String): List<SavedChatSummary>

    @GET("api/chats/{id}")
    suspend fun getChat(
        @Path("id") id: Long,
        @Query("uid") uid: String
    ): SavedChatDetail

    @POST("api/chats")
    suspend fun saveChat(
        @Query("uid") uid: String,
        @Body request: SaveChatRequest
    ): SavedChatSummary

    @PATCH("api/chats/{id}")
    suspend fun renameChat(
        @Path("id") id: Long,
        @Query("uid") uid: String,
        @Body request: RenameChatRequest
    )

    @DELETE("api/chats/{id}")
    suspend fun deleteChat(
        @Path("id") id: Long,
        @Query("uid") uid: String
    )
}