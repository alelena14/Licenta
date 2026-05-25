package com.licenta.licenta_backend.controller

import com.licenta.licenta_backend.dto.SaveFavoriteRequest
import com.licenta.licenta_backend.dto.SaveFavoriteResponse
import com.licenta.licenta_backend.dto.FavoriteExistsResponse
import com.licenta.licenta_backend.dto.DeleteFavoriteResponse
import com.licenta.licenta_backend.service.FavoriteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/favorites")
class FavoriteController(
    private val favoriteService: FavoriteService
) {

    @PostMapping
    fun save(
        @RequestParam uid: String,
        @RequestBody request: SaveFavoriteRequest
    ): ResponseEntity<SaveFavoriteResponse> {
        val response = favoriteService.save(uid, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{productId}/exists")
    fun exists(
        @RequestParam uid: String,
        @PathVariable productId: Long
    ): ResponseEntity<FavoriteExistsResponse> {
        return ResponseEntity.ok(FavoriteExistsResponse(favoriteService.exists(uid, productId)))
    }

    @DeleteMapping("/{productId}")
    fun remove(
        @RequestParam uid: String,
        @PathVariable productId: Long
    ): ResponseEntity<DeleteFavoriteResponse> {
        favoriteService.remove(uid, productId)
        return ResponseEntity.ok(DeleteFavoriteResponse("Removed from favorites."))
    }

    @GetMapping
    fun getAll(
        @RequestParam uid: String
    ): ResponseEntity<List<SaveFavoriteResponse>> {
        return ResponseEntity.ok(favoriteService.getAllForUser(uid))
    }
}

class FavoriteAlreadyExistsException(message: String) : RuntimeException(message)
class FavoriteNotFoundException(message: String) : RuntimeException(message)

@RestControllerAdvice
class FavoriteExceptionHandler {

    @ExceptionHandler(FavoriteAlreadyExistsException::class)
    fun handleAlreadyExists(ex: FavoriteAlreadyExistsException) =
        ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("error" to ex.message))

    @ExceptionHandler(FavoriteNotFoundException::class)
    fun handleNotFound(ex: FavoriteNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to ex.message))
}