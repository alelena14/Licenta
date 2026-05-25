package com.licenta.licenta_backend.dto

import java.time.LocalDateTime

// ── Request: salveaza o conversatie noua ──────────────────────────────────────
data class SaveChatRequest(
    val title: String,
    val messages: List<SaveChatMessageDto>
)

// ── Request: redenumeste o conversatie ────────────────────────────────────────
data class RenameChatRequest(
    val title: String
)

// ── Un mesaj individual in conversatia salvata ────────────────────────────────
data class SaveChatMessageDto(
    val role: String,
    val content: String,
    val photoUri: String? = null,
    val timestamp: Long
)

// ── Response: lista de conversatii (preview, fara mesaje) ────────────────────
data class SavedChatSummary(
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val messageCount: Int,
    val lastMessage: String       // preview — primul rand din ultimul mesaj
)

// ── Response: conversatie completa cu toate mesajele ─────────────────────────
data class SavedChatDetail(
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val messages: List<SaveChatMessageDto>
)