package com.licenta.licenta_backend.controller

import com.licenta.licenta_backend.dto.ChatRequest
import com.licenta.licenta_backend.dto.ChatResponse
import com.licenta.licenta_backend.service.ChatService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService
) {

    @PostMapping
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        return chatService.chat(request)
    }
}