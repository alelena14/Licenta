package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.UserConcern
import org.springframework.data.jpa.repository.JpaRepository

interface UserConcernRepository :
    JpaRepository<UserConcern, Long> {

    fun findAllByProfileId(profileId: Long): List<UserConcern>

    fun deleteAllByProfileId(profileId: Long)
}