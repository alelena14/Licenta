package com.licenta.licenta_backend.controller

import com.licenta.licenta_backend.service.AiService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/ai")
class AiController(private val aiService: AiService) {

    @PostMapping("/face")
    fun analyzeFace(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        val result = aiService.analyzeFace(file)
            ?: return ResponseEntity.status(500).body("AI error")

        return ResponseEntity.ok(result)
    }
}