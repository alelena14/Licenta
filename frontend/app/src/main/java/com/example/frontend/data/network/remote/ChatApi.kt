package com.example.frontend.data.network.remote

import com.example.frontend.data.model.ChatRequest
import com.example.frontend.data.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {

    @POST("api/chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}