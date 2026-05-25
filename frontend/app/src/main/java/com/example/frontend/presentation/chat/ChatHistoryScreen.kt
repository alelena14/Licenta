package com.example.frontend.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.frontend.data.model.SavedChatSummary
import com.example.frontend.presentation.ui.AppScreen
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val BgTop      = Color(0xFFF0EBF8)
private val BgBottom   = Color(0xFFEDE7F6)
private val Violet     = Color(0xFF3D1F8C)
private val VioletMid  = Color(0xFF6A3FB5)
private val VioletSoft = Color(0xFF7C5CBF)
private val VioletLight= Color(0xFFD4C5F0)
private val VioletPale = Color(0xFFF3EEFF)
private val CardBg     = Color(0xFFFAF8FF)


@Composable
fun ChatHistoryScreen(
    navController: NavHostController,
    viewModel: ChatHistoryViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val renamingChat = viewModel.renamingChat
    val loadedChat = viewModel.loadedChat
    var renameInput by remember(renamingChat) { mutableStateOf(renamingChat?.title ?: "") }

    AppScreen { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgTop, BgBottom)))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Content ───────────────────────────────────────────────────
                when (state) {
                    is ChatHistoryState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = VioletMid)
                        }
                    }

                    is ChatHistoryState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(state.message, color = VioletSoft, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(onClick = { viewModel.loadChats() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }

                    is ChatHistoryState.Success -> {

                        if (loadedChat != null) {

                            // ─────────────────────────────────────────────
                            // CHAT DETAIL VIEW
                            // ─────────────────────────────────────────────

                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {

                                Surface(
                                    color = Color.White.copy(alpha = 0.9f),
                                    shadowElevation = 2.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        IconButton(
                                            onClick = {
                                                viewModel.clearLoadedChat()
                                            }
                                        ) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                null,
                                                tint = Violet
                                            )
                                        }

                                        Text(
                                            text = loadedChat.title,
                                            color = Violet,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {

                                    items(loadedChat.messages) { message ->

                                        val isUser = message.role == "user"

                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = if (isUser)
                                                Alignment.CenterEnd
                                            else
                                                Alignment.CenterStart
                                        ) {

                                            Surface(
                                                shape = RoundedCornerShape(18.dp),
                                                color = if (isUser)
                                                    VioletMid
                                                else
                                                    Color.White.copy(alpha = 0.95f),
                                                tonalElevation = 2.dp
                                            ) {

                                                Text(
                                                    text = message.content ?: "",
                                                    color = if (isUser)
                                                        Color.White
                                                    else
                                                        Violet,
                                                    modifier = Modifier.padding(14.dp),
                                                    fontSize = 14.sp,
                                                    lineHeight = 20.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                        } else {
                            // ── Header ───────────────────────────────────────────────────
                            Surface(color = Color.White.copy(alpha = 0.85f), shadowElevation = 4.dp) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Violet)
                                    }
                                    Text(
                                        text = "Saved Chats",
                                        color = Violet,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // ─────────────────────────────────────────────
                            // CHAT LIST VIEW
                            // ─────────────────────────────────────────────

                            if (state.chats.isEmpty()) {

                                EmptyHistory()

                            } else {

                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {

                                    items(state.chats, key = { it.id }) { chat ->

                                        ChatHistoryCard(
                                            chat = chat,
                                            isDeleting = viewModel.deletingId == chat.id,

                                            onClick = {
                                                viewModel.loadChat(chat.id)
                                            },

                                            onDelete = {
                                                viewModel.deleteChat(chat.id)
                                            },

                                            onRename = {
                                                viewModel.openRenameDialog(chat)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Rename dialog ─────────────────────────────────────────────────
            if (renamingChat != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.closeRenameDialog() },
                    containerColor = CardBg,
                    title = {
                        Text("Rename chat", color = Violet, fontWeight = FontWeight.Bold)
                    },
                    text = {
                        OutlinedTextField(
                            value = renameInput,
                            onValueChange = { renameInput = it },
                            singleLine = true,
                            placeholder = { Text("Chat title") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VioletMid,
                                unfocusedBorderColor = VioletLight,
                                focusedTextColor = Violet,
                                unfocusedTextColor = Violet,
                                cursorColor = VioletMid
                            )
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (renameInput.isNotBlank()) {
                                    viewModel.renameChat(renamingChat.id, renameInput)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VioletMid)
                        ) { Text("Save", color = Color.White) }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.closeRenameDialog() }) {
                            Text("Cancel", color = VioletSoft)
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun ChatHistoryCard(
    chat: SavedChatSummary,
    isDeleting: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier.size(44.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFFA98BFF), Color(0xFF6A3FB5))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Forum, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }

            // Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title,
                    color = Violet,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = chat.lastMessage.ifBlank { "No messages" },
                    color = VioletSoft,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text("·", color = VioletLight, fontSize = 11.sp)
                    Text(
                        text = "${chat.messageCount} messages",
                        color = VioletLight,
                        fontSize = 11.sp
                    )
                }
            }

            // Actions
            if (isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = VioletMid,
                    strokeWidth = 2.dp
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onRename,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.DriveFileRenameOutline, null,
                            tint = VioletLight, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, null,
                            tint = Color(0xFFE57373), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}


@Composable
private fun EmptyHistory() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                tint = VioletSoft,
                modifier = Modifier.padding(6.dp).size(30.dp)
            )
            Text(
                text = "No saved chats yet",
                color = Violet,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )
            Text(
                text = "Save a conversation from the chat screen\nto find it here later.",
                color = VioletSoft,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

