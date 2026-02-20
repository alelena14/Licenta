package com.licenta.licenta_backend.model

import jakarta.persistence.*
import org.hibernate.annotations.Type

@Entity
@Table(name = "product_features")
class ProductFeatures(

    @Id
    @Column(name = "product_id")
    val productId: Long,

    @Column(name = "needs_vector", columnDefinition = "jsonb")
    val needsVector: String,

    @Column(name = "confidence_score")
    val confidenceScore: Double
)
