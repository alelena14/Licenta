package com.example.frontend.presentation.chat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.graphics.scale
import com.example.frontend.data.model.ChatMessage
import com.example.frontend.data.model.ProductRecommendation
import com.example.frontend.data.model.SaveChatMessageDto
import com.example.frontend.data.model.SaveChatRequest
import com.example.frontend.domain.repository.ChatRepository
import com.example.frontend.domain.repository.RecommendationRepository
import com.example.frontend.domain.repository.SavedChatRepository
import com.example.frontend.presentation.product.ProductStore
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

data class UiMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String,
    val content: String,
    val photoUri: Uri? = null,
    val products: List<ProductRecommendation> = emptyList(),
    val detectedConcerns: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val recommendationRepository: RecommendationRepository,
    private val savedChatRepository: SavedChatRepository,
    private val auth: FirebaseAuth,
    @ApplicationContext private val appContext: Context,
    val productStore: ProductStore
) : ViewModel() {

    val messages = mutableStateListOf<UiMessage>()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var showSaveDialog by mutableStateOf(false)
        private set

    var saveSuccess by mutableStateOf<Boolean?>(null)
        private set

    private val sessionId = UUID.randomUUID().toString()

    init {
        messages.add(
            UiMessage(
                role = "assistant",
                content = "Hi! I'm SkinAI\n\nTell me about your skin concerns or attach a photo and I'll recommend the best products for you."
            )
        )
    }

    fun sendMessage(userInput: String) {
        if (userInput.isBlank() || isLoading) return
        messages.add(UiMessage(role = "user", content = userInput.trim()))
        errorMessage = null
        isLoading = true
        viewModelScope.launch {
            try {
                val response = chatRepository.sendMessage(messages = buildCleanHistory(), sessionId = sessionId)
                messages.add(UiMessage(role = "assistant", content = response.reply, products = response.products, detectedConcerns = response.detectedConcerns))
            } catch (e: Exception) {
                errorMessage = "Something went wrong. Please try again."
                messages.removeLastOrNull()
            } finally { isLoading = false }
        }
    }

    fun sendPhoto(uri: Uri) {
        if (isLoading) return
        messages.add(UiMessage(role = "user", content = "", photoUri = uri))
        errorMessage = null
        isLoading = true
        viewModelScope.launch {
            try {
                val compressedFile = compressImage(uri)
                val analysisResponse = recommendationRepository.analyzePhoto(compressedFile)
                val concerns = analysisResponse.userConcerns
                if (concerns.isEmpty()) {
                    messages.add(UiMessage(role = "assistant", content = "I couldn't detect any specific skin concerns from this photo. Could you try a clearer photo with better lighting, or describe your concerns in text?"))
                    isLoading = false
                    return@launch
                }
                val concernsFormatted = concerns.joinToString(", ") { it.replace("_", " ") }
                messages.add(UiMessage(role = "assistant", content = "I analyzed your photo and detected these skin concerns: $concernsFormatted.\n\nLet me find the best products for you...", detectedConcerns = concerns))
                val photoText = "I have the following skin concerns detected from my photo: $concernsFormatted. Please recommend products for these concerns."
                val response = chatRepository.sendMessage(messages = buildCleanHistory(photoReplacementText = photoText), sessionId = sessionId)
                messages.add(UiMessage(role = "assistant", content = response.reply, products = response.products, detectedConcerns = response.detectedConcerns))
            } catch (e: Exception) {
                errorMessage = "Photo analysis failed. Please try again."
                messages.removeLastOrNull()
            } finally { isLoading = false }
        }
    }

    // ── Salveaza conversatia ──────────────────────────────────────────────────
    fun saveChat(title: String) {
        val uid = auth.currentUser?.uid ?: run {
            errorMessage = "You must be logged in to save chats."
            return
        }
        if (messages.none { it.role == "user" }) {
            errorMessage = "Nothing to save yet. Start a conversation first!"
            return
        }
        viewModelScope.launch {
            try {
                val messageDtos = messages
                    .filter { it.content.isNotBlank() || it.photoUri != null }
                    .filter { !isSystemMessage(it.content) }
                    .map { msg ->
                        SaveChatMessageDto(
                            role      = msg.role,
                            content   = msg.content,
                            photoUri  = msg.photoUri?.toString(),
                            timestamp = msg.timestamp
                        )
                    }
                savedChatRepository.saveChat(
                    uid     = uid,
                    request = SaveChatRequest(
                        title    = title.ifBlank { generateTitle() },
                        messages = messageDtos
                    )
                )
                saveSuccess = true
                showSaveDialog = false
            } catch (e: Exception) {
                errorMessage = "Failed to save chat. Please try again."
                saveSuccess = false
            }
        }
    }

    fun openSaveDialog()  { showSaveDialog = true }
    fun closeSaveDialog() { showSaveDialog = false }
    fun clearSaveSuccess() { saveSuccess = null }

    private fun generateTitle(): String =
        messages.firstOrNull { it.role == "user" && it.content.isNotBlank() }
            ?.content?.take(40)?.trim() ?: "Skincare chat"

    private fun buildCleanHistory(photoReplacementText: String? = null): List<ChatMessage> {
        val result = mutableListOf<ChatMessage>()
        for (msg in messages) {
            val content = when {
                msg.photoUri != null && photoReplacementText != null -> photoReplacementText
                msg.photoUri != null -> continue
                msg.content.isBlank() -> continue
                msg.role == "assistant" && isSystemMessage(msg.content) -> continue
                else -> msg.content
            }
            val last = result.lastOrNull()
            if (last != null && last.role == msg.role) {
                result[result.lastIndex] = ChatMessage(role = last.role, content = "${last.content}\n${content}")
            } else {
                result.add(ChatMessage(role = msg.role, content = content))
            }
        }
        if (result.lastOrNull()?.role != "user") return result.filter { it.role == "user" }.takeLast(1)
        return result
    }

    private fun isSystemMessage(content: String): Boolean =
        listOf("Hi! I'm SkinAI", "I analyzed your photo", "Let me find the best products", "analyzing")
            .any { content.contains(it, ignoreCase = true) }

    fun clearError() { errorMessage = null }

    fun clearConversation() {
        messages.clear()
        messages.add(UiMessage(role = "assistant", content = "Hi! I'm SkinAI\n\nTell me about your skin concerns or attach a photo and I'll recommend the best products for you."))
    }

    private fun compressImage(uri: Uri, maxSize: Int = 1280, quality: Int = 85): File {
        val inputStream = appContext.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        val ratio = minOf(maxSize.toFloat() / originalBitmap.width, maxSize.toFloat() / originalBitmap.height)
        val resized = originalBitmap.scale((originalBitmap.width * ratio).toInt(), (originalBitmap.height * ratio).toInt())
        val file = File(appContext.cacheDir, "chat_photo_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out -> resized.compress(Bitmap.CompressFormat.JPEG, quality, out) }
        return file
    }

    fun loadChat(chatId: Long) {

        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {

            try {

                val chat = savedChatRepository.getChat(
                    uid = uid,
                    id = chatId
                )

                messages.clear()

                chat.messages.forEach { msg ->

                    messages.add(
                        UiMessage(
                            role = msg.role,
                            content = msg.content ?: "",
                            timestamp = msg.timestamp
                        )
                    )
                }

            } catch (e: Exception) {

                errorMessage = "Failed to load chat."

            }
        }
    }
}