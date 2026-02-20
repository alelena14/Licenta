package com.licenta.licenta_backend.model

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "ingredients")
class Ingredient(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String,

    var inciName: String? = null,

    @Column(columnDefinition = "text")
    var category: String? = null,

    var comedogenicRating: Int? = null,

    @Column(columnDefinition = "text")
    var irritationPotential: String? = null,

    var safetyScore: Double? = null,

    /* ---------- JSONB FIELDS ---------- */

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var function: List<String>? = null,

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var benefits: List<String>? = null,

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var concerns: List<String>? = null,

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var suitableFor: List<String>? = null,

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var avoidIf: List<String>? = null,

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var pairsWellWith: List<String>? = null,

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var avoidMixing: List<String>? = null,

    /* ---------- TEXT FIELDS ---------- */

    @Column(columnDefinition = "text")
    var description: String? = null,

    @Column(columnDefinition = "text")
    var concentrationRange: String? = null,

    /* ---------- AUDIT ---------- */

    var enrichedAt: Instant? = null,

    @CreationTimestamp
    var createdAt: Instant? = null,

    @UpdateTimestamp
    var updatedAt: Instant? = null
)
