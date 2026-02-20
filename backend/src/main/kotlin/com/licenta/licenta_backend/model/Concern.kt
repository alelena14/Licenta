package com.licenta.licenta_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "concerns")
data class Concern(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val code: String,

    val displayName: String,

    val category: String
)