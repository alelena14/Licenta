package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.controller.FavoriteAlreadyExistsException
import com.licenta.licenta_backend.controller.FavoriteNotFoundException
import com.licenta.licenta_backend.dto.SaveFavoriteRequest
import com.licenta.licenta_backend.dto.SaveFavoriteResponse
import com.licenta.licenta_backend.model.Favorite
import com.licenta.licenta_backend.repository.FavoriteRepository
import com.licenta.licenta_backend.repository.ProductRepository
import com.licenta.licenta_backend.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class FavoriteService(
    private val favoriteRepository: FavoriteRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun save(firebaseUid: String, request: SaveFavoriteRequest): SaveFavoriteResponse {
        val user = userRepository.findByFirebaseUid(firebaseUid)
            ?: throw IllegalArgumentException("User not found.")
        if (favoriteRepository.existsByUidAndProductId(user.id, request.productId)) {
            throw FavoriteAlreadyExistsException("The product is already saved in favorites.")
        }
        val favorite = Favorite(
            uid         = user.id,
            productId   = request.productId,
            score       = request.score,
            concerns    = request.concerns.joinToString(",").takeIf { it.isNotEmpty() },
            explanation = request.explanation
        )
        val saved = favoriteRepository.save(favorite)
        return SaveFavoriteResponse(id = saved.id, productId = saved.productId, savedAt = saved.savedAt)
    }

    fun exists(firebaseUid: String, productId: Long): Boolean {
        val user = userRepository.findByFirebaseUid(firebaseUid) ?: return false
        return favoriteRepository.existsByUidAndProductId(user.id, productId)
    }

    @Transactional
    fun remove(firebaseUid: String, productId: Long) {
        val user = userRepository.findByFirebaseUid(firebaseUid)
            ?: throw IllegalArgumentException("User not found.")
        if (!favoriteRepository.existsByUidAndProductId(user.id, productId)) {
            throw FavoriteNotFoundException("The product is not saved in favorites.")
        }
        favoriteRepository.deleteByUidAndProductId(user.id, productId)
    }

    fun getAllForUser(firebaseUid: String): List<SaveFavoriteResponse> {
        val user = userRepository.findByFirebaseUid(firebaseUid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        return favoriteRepository.findAllByUid(user.id).map { fav ->
            val product = productRepository.findById(fav.productId).orElse(null)
            SaveFavoriteResponse(
                id          = fav.id,
                productId   = fav.productId,
                savedAt     = fav.savedAt,
                name        = product?.name,
                brand       = product?.brand,
                type        = product?.type,
                afterUse    = productRepository.findAfterUseLabelsByProductId(product.id),
                score       = fav.score,
                concerns    = fav.concerns,
                explanation = fav.explanation
            )
        }
    }
}