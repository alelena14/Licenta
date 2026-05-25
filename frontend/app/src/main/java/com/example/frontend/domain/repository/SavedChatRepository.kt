package com.example.frontend.domain.repository

import com.example.frontend.data.model.*
import com.example.frontend.data.network.remote.SavedChatApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedChatRepository @Inject constructor(
    private val api: SavedChatApi
) {
    suspend fun getChats(uid: String): List<SavedChatSummary> =
        api.getChats(uid)

    suspend fun getChat(uid: String, id: Long): SavedChatDetail =
        api.getChat(id, uid)

    suspend fun saveChat(uid: String, request: SaveChatRequest): SavedChatSummary =
        api.saveChat(uid, request)

    suspend fun renameChat(uid: String, id: Long, title: String) =
        api.renameChat(id, uid, RenameChatRequest(title))

    suspend fun deleteChat(uid: String, id: Long) =
        api.deleteChat(id, uid)
}