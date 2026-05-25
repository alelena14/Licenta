package com.example.frontend.data.model

import com.google.gson.annotations.SerializedName

// ── Request — trimis la POST /favorites ──────────────────────────────────────

data class SaveFavoriteRequest(
    @SerializedName("product_id")   val productId: Long,
    @SerializedName("score")        val score: Double?,          // null daca nu vine din chat
    @SerializedName("concerns")     val concerns: List<String>,  // gol daca nu vine din chat
    @SerializedName("explanation")  val explanation: String?     // null daca nu vine din chat
)

// ── Response — returnat de backend ───────────────────────────────────────────

data class SaveFavoriteResponse(
    @SerializedName("id")           val id: String,
    @SerializedName("product_id")   val productId: String,
    @SerializedName("saved_at")     val savedAt: String
)

data class SavedProductDto(
    @SerializedName("id")          val favoriteId:  Long,
    @SerializedName("product_id")  val productId:   Long,
    @SerializedName("saved_at")    val savedAt:     String,
    @SerializedName("name")        val name:        String?,
    @SerializedName("brand")       val brand:       String?,
    @SerializedName("type")        val type:        String?,
    @SerializedName("url")         val url:         String?,
    @SerializedName("after_use")   val afterUse:    List<String> = emptyList(),
    @SerializedName("score")       val score:       Double?,
    @SerializedName("concerns")    val concerns:    String?,
    @SerializedName("explanation") val explanation: String?
)

// ── Response — returnat de DELETE /favorites/{productId} ─────────────────────

data class DeleteFavoriteResponse(
    @SerializedName("message")      val message: String
)