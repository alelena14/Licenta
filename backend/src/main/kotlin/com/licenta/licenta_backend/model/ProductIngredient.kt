package com.licenta.licenta_backend.model

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "product_ingredient")
data class ProductIngredient(

    @EmbeddedId
    val id: ProductIngredientId = ProductIngredientId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    val ingredient: Ingredient,

    val concentration: Double? = null,
    val unit: String? = null,    // "%", "mg/ml" etc
    val orderIndex: Int? = null,

)

// cheie compusa
@Embeddable
data class ProductIngredientId(
    val productId: Long = 0,
    val ingredientId: Long = 0,
) : Serializable
