package com.example.frontend.data.model

import com.google.gson.annotations.SerializedName

data class ProductCardDto(
    @SerializedName("id")        val id: Long,
    @SerializedName("brand")     val brand: String,
    @SerializedName("name")      val name: String,
    @SerializedName("type")      val type: String,
    @SerializedName("area")      val area: String,
    @SerializedName("country")   val country: String?,
    @SerializedName("url")       val url: String?,
    @SerializedName("after_use") val afterUse: List<String>
)

data class ProductListResponse(
    @SerializedName("products") val products: List<ProductCardDto>,
    @SerializedName("total")    val total: Int
)

data class ProductDetailDto(
    @SerializedName("id")          val id:          Long,
    @SerializedName("brand")       val brand:       String,
    @SerializedName("name")        val name:        String,
    @SerializedName("type")        val type:        String,
    @SerializedName("area")        val area:        String,
    @SerializedName("country")     val country:     String?,
    @SerializedName("url")         val url:         String?,
    @SerializedName("after_use")   val afterUse:    List<String>,
    @SerializedName("ingredients") val ingredients: List<String>
)

data class HomeSection(
    val title: String,
    val products: List<ProductCardDto>
)