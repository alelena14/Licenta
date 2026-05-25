package com.licenta.licenta_backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductCardDto(
    val id: Long,
    val brand: String,
    val name: String,
    val type: String,
    val area: String,
    val country: String?,
    val url: String?,
    @JsonProperty("after_use")
    val afterUse: List<String>
)

data class ProductListResponse(
    val products: List<ProductCardDto>,
    val total: Int
)

data class ProductDetailDto(
    val id:          Long,
    val brand:       String,
    val name:        String,
    val type:        String,
    val area:        String,
    val country:     String?,
    val url:         String?,
    @JsonProperty("after_use")
    val afterUse:    List<String>,
    val ingredients: List<String>
)