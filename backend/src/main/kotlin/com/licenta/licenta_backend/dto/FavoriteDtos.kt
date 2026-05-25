package com.licenta.licenta_backend.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class SaveFavoriteRequest(
    @JsonProperty("product_id") val productId: Long,
    @JsonProperty("score")       val score: Double? = null,
    @JsonProperty("concerns")    val concerns: List<String> = emptyList(),
    @JsonProperty("explanation") val explanation: String? = null
)

data class SaveFavoriteResponse(
    @JsonProperty("id")          val id: Long,
    @JsonProperty("product_id")  val productId: Long,
    @JsonProperty("saved_at")    val savedAt: Instant,

    @JsonProperty("name")        val name: String? = null,
    @JsonProperty("brand")       val brand: String? = null,
    @JsonProperty("type")        val type: String? = null,
    @JsonProperty("after_use")   val afterUse: List<String> = emptyList(),
    @JsonProperty("url")         val url: String? = null,
    @JsonProperty("score")       val score: Double? = null,
    @JsonProperty("concerns")    val concerns: String? = null,
    @JsonProperty("explanation") val explanation: String? = null
)

data class FavoriteExistsResponse(
    @JsonProperty("isFavorite") val isFavorite: Boolean
)

data class DeleteFavoriteResponse(
    val message: String
)