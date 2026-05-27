package com.licenta.licenta_backend.controller

import com.licenta.licenta_backend.dto.ProductRecommendation
import com.licenta.licenta_backend.service.HomeRecommendationService
import com.google.firebase.auth.FirebaseAuth
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recommendations")
class HomeRecommendationController(
    private val homeRecommendationService: HomeRecommendationService
) {
    @GetMapping("/home")
    fun getHomeRecommendations(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<List<ProductRecommendation>> {
        val token = authHeader.removePrefix("Bearer ")
        val uid = FirebaseAuth.getInstance().verifyIdToken(token).uid
        return ResponseEntity.ok(homeRecommendationService.getHomeRecommendations(uid))
    }
}