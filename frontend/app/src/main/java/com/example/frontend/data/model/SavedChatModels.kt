package com.example.frontend.data.model

import java.time.LocalDateTime

data class SaveChatRequest(
    val title: String,
    val messages: List<SaveChatMessageDto>
)

data class RenameChatRequest(
    val title: String
)

data class SaveChatMessageDto(
    val role: String,
    val content: String,
    val photoUri: String? = null,
    val timestamp: Long
)

data class SavedChatSummary(
    val id: Long,
    val title: String,
    val createdAt: String,
    val updatedAt: String,
    val messageCount: Int,
    val lastMessage: String
)

data class SavedChatDetail(
    val id: Long,
    val title: String,
    val createdAt: String,
    val updatedAt: String,
    val messages: List<SaveChatMessageDto>
)