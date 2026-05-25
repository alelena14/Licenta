package com.licenta.licenta_backend.controller

import com.licenta.licenta_backend.dto.*
import com.licenta.licenta_backend.service.SavedChatService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chats")
class SavedChatController(
    private val savedChatService: SavedChatService
) {

    // GET /api/chats?uid=xxx — lista de conversatii ale userului
    @GetMapping
    fun getChats(@RequestParam uid: String): ResponseEntity<List<SavedChatSummary>> {
        return ResponseEntity.ok(savedChatService.getChats(uid))
    }

    // GET /api/chats/{id}?uid=xxx — conversatie completa
    @GetMapping("/{id}")
    fun getChat(
        @PathVariable id: Long,
        @RequestParam uid: String
    ): ResponseEntity<SavedChatDetail> {
        val chat = savedChatService.getChat(uid, id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(chat)
    }

    // POST /api/chats?uid=xxx — salveaza conversatie noua
    @PostMapping
    fun saveChat(
        @RequestParam uid: String,
        @RequestBody request: SaveChatRequest
    ): ResponseEntity<SavedChatSummary> {
        return ResponseEntity.ok(savedChatService.saveChat(uid, request))
    }

    // PATCH /api/chats/{id}?uid=xxx — redenumeste
    @PatchMapping("/{id}")
    fun renameChat(
        @PathVariable id: Long,
        @RequestParam uid: String,
        @RequestBody request: RenameChatRequest
    ): ResponseEntity<Void> {
        val success = savedChatService.renameChat(uid, id, request)
        return if (success) ResponseEntity.ok().build()
        else ResponseEntity.notFound().build()
    }

    // DELETE /api/chats/{id}?uid=xxx — sterge
    @DeleteMapping("/{id}")
    fun deleteChat(
        @PathVariable id: Long,
        @RequestParam uid: String
    ): ResponseEntity<Void> {
        val success = savedChatService.deleteChat(uid, id)
        return if (success) ResponseEntity.ok().build()
        else ResponseEntity.notFound().build()
    }
}