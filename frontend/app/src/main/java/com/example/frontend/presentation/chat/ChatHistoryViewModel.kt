package com.example.frontend.presentation.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.data.model.SavedChatDetail
import com.example.frontend.data.model.SavedChatSummary
import com.example.frontend.domain.repository.SavedChatRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatHistoryState {
    object Loading : ChatHistoryState()
    data class Success(val chats: List<SavedChatSummary>) : ChatHistoryState()
    data class Error(val message: String) : ChatHistoryState()
}

@HiltViewModel
class ChatHistoryViewModel @Inject constructor(
    private val savedChatRepository: SavedChatRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    var state by mutableStateOf<ChatHistoryState>(ChatHistoryState.Loading)
        private set

    var deletingId by mutableStateOf<Long?>(null)
        private set

    var renamingChat by mutableStateOf<SavedChatSummary?>(null)
        private set

    // Conversatia incarcata pentru previzualizare / reincarcata in chat
    var loadedChat by mutableStateOf<SavedChatDetail?>(null)
        private set

    init { loadChats() }

    fun loadChats() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            state = ChatHistoryState.Loading
            state = try {
                ChatHistoryState.Success(savedChatRepository.getChats(uid))
            } catch (e: Exception) {
                ChatHistoryState.Error("Failed to load chats.")
            }
        }
    }

    fun deleteChat(id: Long) {
        val uid = auth.currentUser?.uid ?: return
        deletingId = id
        viewModelScope.launch {
            try {
                savedChatRepository.deleteChat(uid, id)
                // Actualizeaza lista local fara reload complet
                val current = (state as? ChatHistoryState.Success)?.chats ?: emptyList()
                state = ChatHistoryState.Success(current.filter { it.id != id })
            } catch (e: Exception) {
                // Fallback — reload
                loadChats()
            } finally {
                deletingId = null
            }
        }
    }

    fun renameChat(id: Long, newTitle: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                savedChatRepository.renameChat(uid, id, newTitle)
                // Actualizeaza titlul local
                val current = (state as? ChatHistoryState.Success)?.chats ?: emptyList()
                state = ChatHistoryState.Success(
                    current.map { if (it.id == id) it.copy(title = newTitle) else it }
                )
            } catch (e: Exception) {
                loadChats()
            } finally {
                renamingChat = null
            }
        }
    }

    fun loadChat(id: Long) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                loadedChat = savedChatRepository.getChat(uid, id)
            } catch (e: Exception) {
                loadedChat = null
            }
        }
    }

    fun openRenameDialog(chat: SavedChatSummary) { renamingChat = chat }
    fun closeRenameDialog() { renamingChat = null }
    fun clearLoadedChat() { loadedChat = null }
}