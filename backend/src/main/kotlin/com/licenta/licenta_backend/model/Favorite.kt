package com.licenta.licenta_backend.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = [UniqueConstraint(columnNames = ["uid", "product_id"])]
)
class Favorite(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val uid: Long,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "score")
    val score: Double? = null,

    // concerns stocate ca CSV: "acne,oily_skin"
    @Column(name = "concerns")
    val concerns: String? = null,

    @Column(name = "explanation", columnDefinition = "TEXT")
    val explanation: String? = null,

    @Column(name = "saved_at", nullable = false)
    val savedAt: Instant = Instant.now()
)