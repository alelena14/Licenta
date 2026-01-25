package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByFirebaseUid(firebaseUid: String): User?
}

