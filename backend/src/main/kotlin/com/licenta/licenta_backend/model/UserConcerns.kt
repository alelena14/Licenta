package com.licenta.licenta_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "user_concerns")
data class UserConcern(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "profile_id")
    val profile: UserSkinProfile,

    @Column(nullable = false)
    val concernCode: String
)