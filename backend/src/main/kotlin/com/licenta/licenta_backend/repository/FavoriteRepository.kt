package com.licenta.licenta_backend.repository

import com.licenta.licenta_backend.model.Favorite
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FavoriteRepository : JpaRepository<Favorite, Long> {
    fun existsByUidAndProductId(uid: Long, productId: Long): Boolean
    fun findByUidAndProductId(uid: Long, productId: Long): Favorite?
    fun findAllByUid(uid: Long): List<Favorite>
    fun deleteByUidAndProductId(uid: Long, productId: Long)

    fun countByUid(userId: Long): Int
}