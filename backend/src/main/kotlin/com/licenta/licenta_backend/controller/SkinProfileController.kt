package com.licenta.licenta_backend.controller

import com.google.firebase.auth.FirebaseAuth
import com.licenta.licenta_backend.dto.ProfileStatsResponse
import com.licenta.licenta_backend.dto.SkinProfileResponse
import com.licenta.licenta_backend.dto.UpdateSkinProfileRequest
import com.licenta.licenta_backend.service.SkinProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/profile")
class SkinProfileController(
    private val skinProfileService: SkinProfileService
) {

    @GetMapping
    fun getProfile(
        @RequestHeader("Authorization") authHeader: String
    ): SkinProfileResponse {

        val token = authHeader.removePrefix("Bearer ").trim()

        val decoded = FirebaseAuth.getInstance()
            .verifyIdToken(token)

        return skinProfileService.getProfile(decoded.uid)
    }

    @GetMapping("/stats")
    fun getStats(@RequestHeader("Authorization") authHeader: String): ResponseEntity<ProfileStatsResponse> {
        val token = authHeader.removePrefix("Bearer ")
        val uid = FirebaseAuth.getInstance().verifyIdToken(token).uid
        return ResponseEntity.ok(skinProfileService.getStats(uid))
    }

    @PutMapping
    fun updateProfile(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody request: UpdateSkinProfileRequest
    ): SkinProfileResponse {

        val token = authHeader.removePrefix("Bearer ").trim()

        val decoded = FirebaseAuth.getInstance()
            .verifyIdToken(token)

        return skinProfileService.updateProfile(
            decoded.uid,
            request
        )
    }

    @PostMapping("/concerns/from-analysis")
    fun saveConcernsFromAnalysis(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody concerns: List<String>
    ): ResponseEntity<Void> {
        val token = authHeader.removePrefix("Bearer ")
        val uid = FirebaseAuth.getInstance().verifyIdToken(token).uid
        skinProfileService.saveConcernsFromAnalysis(uid, concerns)
        return ResponseEntity.ok().build()
    }
}