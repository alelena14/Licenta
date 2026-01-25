package com.licenta.licenta_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "after_use")
data class AfterUse(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    val label: String,
)

