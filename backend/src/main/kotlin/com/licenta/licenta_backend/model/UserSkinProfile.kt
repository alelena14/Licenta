package com.licenta.licenta_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "user_skin_profiles")
data class UserSkinProfile(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    val user: User,

    @Column(nullable = true)
    var skinType: String? = null
)