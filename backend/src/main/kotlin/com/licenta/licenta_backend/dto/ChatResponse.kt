package com.licenta.licenta_backend.dto

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
    val products: List<ProductRecommendation> = emptyList(),
    val detectedConcerns: List<String> = emptyList()
)

enum class IntentType {
    RECOMMENDATION,
    PRODUCT_QUESTION,
    INGREDIENT_QUESTION,
    CASUAL,
    BODY_CARE,
    UNKNOWN
}

// ── Intent extras din mesajul userului ───────────────────────────────────────
data class ChatIntent(
    val type: IntentType,
    val concerns: List<String> = emptyList(),
    val productType: String? = null,
    val ingredient: String? = null,
    val productName: String? = null,
    val isFollowUp: Boolean = false,
    val rawQuery: String
)