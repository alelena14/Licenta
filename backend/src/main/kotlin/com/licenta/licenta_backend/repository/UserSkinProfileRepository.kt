package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.UserSkinProfile
import org.springframework.data.jpa.repository.JpaRepository

interface UserSkinProfileRepository :
    JpaRepository<UserSkinProfile, Long> {

    fun findByUserId(userId: Long): UserSkinProfile?
}