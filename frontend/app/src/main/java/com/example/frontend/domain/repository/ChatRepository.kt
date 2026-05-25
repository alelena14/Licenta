package com.example.frontend.domain.repository

import com.example.frontend.data.model.ChatMessage
import com.example.frontend.data.model.ChatRequest
import com.example.frontend.data.model.ChatResponse
import com.example.frontend.data.network.remote.ChatApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val api: ChatApi
) {
    suspend fun sendMessage(
        messages: List<ChatMessage>,
        sessionId: String = "default"
    ): ChatResponse {
        return api.chat(ChatRequest(messages = messages, sessionId = sessionId))
    }
}