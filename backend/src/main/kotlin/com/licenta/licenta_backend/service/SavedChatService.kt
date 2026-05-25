package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.dto.*
import com.licenta.licenta_backend.model.SavedChat
import com.licenta.licenta_backend.model.SavedChatMessage
import com.licenta.licenta_backend.repository.SavedChatRepository
import com.licenta.licenta_backend.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SavedChatService(
    private val savedChatRepository: SavedChatRepository,
    private val userRepository: UserRepository
) {

    // ── Lista conversatii (preview) ───────────────────────────────────────────
    fun getChats(firebaseUid: String): List<SavedChatSummary> {
        val user = userRepository.findByFirebaseUid(firebaseUid)
            ?: return emptyList()

        return savedChatRepository
            .findByUserIdOrderByUpdatedAtDesc(user.id)
            .map { chat ->
                val lastMsg = chat.messages.lastOrNull()
                SavedChatSummary(
                    id           = chat.id,
                    title        = chat.title,
                    createdAt    = chat.createdAt,
                    updatedAt    = chat.updatedAt,
                    messageCount = chat.messages.size,
                    lastMessage  = lastMsg?.content?.take(60) ?: ""
                )
            }
    }

    // ── Conversatie completa ──────────────────────────────────────────────────
    fun getChat(firebaseUid: String, chatId: Long): SavedChatDetail? {
        val user = userRepository.findByFirebaseUid(firebaseUid) ?: return null

        if (!savedChatRepository.existsByIdAndUserId(chatId, user.id)) return null

        val chat = savedChatRepository.findByIdAndUserId(
            chatId,
            user.id
        ) ?: return null

        return chat.toDetail()
    }

    // ── Salveaza conversatie noua ─────────────────────────────────────────────
    @Transactional
    fun saveChat(firebaseUid: String, request: SaveChatRequest): SavedChatSummary {
        val user = userRepository.findByFirebaseUid(firebaseUid)
            ?: throw IllegalArgumentException("User not found")

        val savedChat = SavedChat(
            user      = user,
            title     = request.title.take(100),    // max 100 chars pentru titlu
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Adaugam mesajele
        request.messages.forEachIndexed { index, msgDto ->

            val sanitizedContent = when {

                !msgDto.photoUri.isNullOrBlank() -> {
                    "[Image uploaded for skin analysis]"
                }

                msgDto.content.isBlank() -> {
                    "[Empty message]"
                }

                else -> {
                    msgDto.content
                }
            }

            savedChat.messages.add(
                SavedChatMessage(
                    savedChat = savedChat,
                    role      = msgDto.role,
                    content   = sanitizedContent,

                    position  = index,
                    timestamp = msgDto.timestamp
                )
            )
        }

        val saved = savedChatRepository.save(savedChat)

        return SavedChatSummary(
            id           = saved.id,
            title        = saved.title,
            createdAt    = saved.createdAt,
            updatedAt    = saved.updatedAt,
            messageCount = saved.messages.size,
            lastMessage  = saved.messages.lastOrNull()?.content?.take(60) ?: ""
        )
    }

    // ── Redenumeste conversatie ───────────────────────────────────────────────
    @Transactional
    fun renameChat(firebaseUid: String, chatId: Long, request: RenameChatRequest): Boolean {
        val user = userRepository.findByFirebaseUid(firebaseUid) ?: return false
        if (!savedChatRepository.existsByIdAndUserId(chatId, user.id)) return false

        val chat = savedChatRepository.findById(chatId).orElse(null) ?: return false

        savedChatRepository.save(
            chat.copy(
                title     = request.title.take(100),
                updatedAt = LocalDateTime.now()
            )
        )
        return true
    }

    // ── Sterge conversatie ────────────────────────────────────────────────────
    @Transactional
    fun deleteChat(firebaseUid: String, chatId: Long): Boolean {
        val user = userRepository.findByFirebaseUid(firebaseUid) ?: return false
        if (!savedChatRepository.existsByIdAndUserId(chatId, user.id)) return false

        savedChatRepository.deleteById(chatId)
        return true
    }

    // ── Helper mapping ────────────────────────────────────────────────────────
    private fun SavedChat.toDetail() = SavedChatDetail(
        id        = id,
        title     = title,
        createdAt = createdAt,
        updatedAt = updatedAt,
        messages  = messages.map { msg ->
            SaveChatMessageDto(
                role      = msg.role,
                content   = msg.content,
                timestamp = msg.timestamp
            )
        }
    )
}