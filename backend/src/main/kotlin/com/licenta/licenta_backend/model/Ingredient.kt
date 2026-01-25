package com.licenta.licenta_backend.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "ingredients")
data class Ingredient(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,
    val inciName: String? = null,
    val category: String? = null,
    val comedogenicRating: Int? = null,
    val irritationPotential: String? = null,
    val safetyScore: Double? = null,

    @Column(columnDefinition = "jsonb")
    val function: List<String>? = null,

    @Column(columnDefinition = "jsonb")
    val benefits: List<String>? = null,

    @Column(columnDefinition = "jsonb")
    val concerns: List<String>? = null,

    @Column(columnDefinition = "jsonb")
    val suitableFor: List<String>? = null,

    @Column(columnDefinition = "jsonb")
    val avoidIf: List<String>? = null,

    @Column(columnDefinition = "jsonb")
    val pairsWellWith: List<String>? = null,

    @Column(columnDefinition = "jsonb")
    val avoidMixing: List<String>? = null,

    val description: String? = null,
    val concentrationRange: String? = null,

    @CreationTimestamp
    val createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    val updatedAt: Instant = Instant.now(),
)
