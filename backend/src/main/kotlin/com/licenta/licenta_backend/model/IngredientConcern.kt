package com.licenta.licenta_backend.model

import jakarta.persistence.*

@Entity
@Table(
    name = "ingredient_concern",
    uniqueConstraints = [UniqueConstraint(columnNames = ["ingredient_id", "concern_id"])]
)
data class IngredientConcern(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "ingredient_id")
    val ingredientId: Long,

    @Column(name = "concern_id")
    val concernId: Long,

    val weight: Double,

    val mechanism: String? = null,

    val evidenceLevel: String? = null,

    val needsReview: Boolean = false,

    val reasoning: String? = null
)