package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.SavedChat
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SavedChatRepository : JpaRepository<SavedChat, Long> {

    fun countByUserId(userId: Long): Int

    @EntityGraph(attributePaths = ["messages"])
    fun findByUserIdOrderByUpdatedAtDesc(
        userId: Long
    ): List<SavedChat>

    fun existsByIdAndUserId(
        id: Long,
        userId: Long
    ): Boolean

    @EntityGraph(attributePaths = ["messages"])
    fun findByIdAndUserId(
        id: Long,
        userId: Long
    ): SavedChat?
}

