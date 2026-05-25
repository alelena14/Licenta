package com.example.frontend.data.model

// ── Request de la frontend ────────────────────────────────────────────────────
data class ChatRequest(
    val messages: List<ChatMessage>,        // tot istoricul conversatiei
    val sessionId: String = "default"
)

// ── Un mesaj individual ───────────────────────────────────────────────────────
data class ChatMessage(
    val role: String,       // "user" sau "assistant"
    val content: String
)

// ── Raspuns catre frontend ────────────────────────────────────────────────────
data class ChatResponse(
    val reply: String,
    val products: List<ProductRecommendation> = emptyList(),   // produsele recomandate
    val detectedConcerns: List<String> = emptyList()
)

object ChatPrefillStore {
    var pendingPrefill: String = ""
}

