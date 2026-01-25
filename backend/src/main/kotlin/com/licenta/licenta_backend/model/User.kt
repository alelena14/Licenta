package com.licenta.licenta_backend.model
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val firebaseUid: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = true, unique = true)
    val username: String? = null,

    @Column(nullable = true)
    val profileImageUrl: String? = null,

    @Column(nullable = true)
    val age: Int? = null
) {

    fun toResponse() = UserResponse(
        id = id,
        email = email,
        username = username,
        profileImageUrl = profileImageUrl,
        age = age
    )
}


