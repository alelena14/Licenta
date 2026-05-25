package com.example.frontend.presentation.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.frontend.data.model.ChatPrefillStore
import com.example.frontend.data.model.ProductRecommendation
import com.example.frontend.presentation.Screen
import com.example.frontend.presentation.product.ProductStore
import com.example.frontend.presentation.ui.AppScreen
import com.example.frontend.presentation.ui.DarkGreen
import com.example.frontend.presentation.ui.PaleGreen
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BgTop       = Color(0xFFF0EBF8)
private val BgBottom    = Color(0xFFEDE7F6)
private val Violet      = Color(0xFF3D1F8C)
private val VioletMid   = Color(0xFF6A3FB5)
private val VioletSoft  = Color(0xFF7C5CBF)
private val VioletLight = Color(0xFFD4C5F0)
private val VioletPale  = Color(0xFFF3EEFF)
private val CardBg      = Color(0xFFFAF8FF)
private val BotBubbleBg = Color(0xFFFFFFFF)
private val InputBg     = Color(0xFFFAF8FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    rootNavController: NavHostController,
    loadId: Long = -1L,
    viewModel: ChatViewModel = hiltViewModel()
) {
    var inputText by remember { mutableStateOf("") }

    val messages = viewModel.messages
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val context = LocalContext.current


    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showPhotoSheet by remember { mutableStateOf(false) }
    var cameraOutputUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.sendPhoto(it) } }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean -> if (success) cameraOutputUri?.let { viewModel.sendPhoto(it) } }

    val showSaveDialog = viewModel.showSaveDialog
    val saveSuccess = viewModel.saveSuccess

    LaunchedEffect(loadId) {

        if (loadId != -1L) {
            viewModel.loadChat(loadId)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    LaunchedEffect(Unit) {
        if (ChatPrefillStore.pendingPrefill.isNotBlank()) {
            inputText = ChatPrefillStore.pendingPrefill
            ChatPrefillStore.pendingPrefill = ""
        }
    }

    // ── Bottom sheet sursa poza ───────────────────────────────────────────────
    if (showPhotoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 24.dp).padding(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Analyse your skin",
                    color = Violet,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Take or choose a clear photo of your skin for AI analysis.",
                    color = VioletSoft,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Galerie
                OutlinedButton(
                    onClick = {
                        showPhotoSheet = false
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, VioletLight),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VioletMid)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Choose from gallery", fontWeight = FontWeight.SemiBold)
                }

                // Camera
                Button(
                    onClick = {
                        showPhotoSheet = false
                        val file = File.createTempFile("chat_", ".jpg", context.cacheDir)
                        val uri = FileProvider.getUriForFile(
                            context, "${context.packageName}.provider", file
                        )
                        cameraOutputUri = uri
                        cameraLauncher.launch(uri)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.horizontalGradient(listOf(VioletMid, Color(0xFF9B59D0))),
                            RoundedCornerShape(14.dp)
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Text("Take a photo", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    AppScreen { _ ->
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFFF6F1FF), Color(0xFFF2ECFB), Color(0xFFEDE7F6)))
            )
        ) {
            Box(modifier = Modifier.size(220.dp).offset(x = 220.dp, y = 120.dp)
                .background(Color.White.copy(alpha = 0.18f), CircleShape))
            Box(modifier = Modifier.size(140.dp).offset(x = (-40).dp, y = 500.dp)
                .background(Color.White.copy(alpha = 0.14f), CircleShape))

            Column(modifier = Modifier.fillMaxSize()) {

                ChatHeader(
                    onClearClick = { viewModel.clearConversation() },
                    onSaveClick = { viewModel.openSaveDialog() },
                    onHistoryClick = { rootNavController.navigate("chat_history") }
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { message ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                        ) {
                            MessageBubble(
                                message = message,
                                onProductClick = { product ->
                                    viewModel.productStore.openFromChat(
                                        product = product,
                                        score = product.score,
                                        concerns = message.detectedConcerns
                                    )

                                    rootNavController.navigate(Screen.Product.route)

                                }
                            )
                        }
                    }

                    if (messages.size <= 1) {
                        item {
                            WelcomeSuggestions(onSuggestionClick = { viewModel.sendMessage(it) })
                        }
                    }

                    if (isLoading) {
                        item { TypingIndicator() }
                    }
                }

                if (showSaveDialog) {
                    SaveChatDialog(
                        suggestedTitle = viewModel.messages
                            .firstOrNull { it.role == "user" && it.content.isNotBlank() }
                            ?.content?.take(40) ?: "Skincare chat",
                        onSave = { title -> viewModel.saveChat(title) },
                        onDismiss = { viewModel.closeSaveDialog() }
                    )
                }

                LaunchedEffect(saveSuccess) {
                    if (saveSuccess == true) {
                        // snackbar sau toast
                        viewModel.clearSaveSuccess()
                    }
                }
                AnimatedVisibility(visible = errorMessage != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .background(Color(0xFFFFEEEE), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp, modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss", fontSize = 12.sp)
                        }
                    }
                }

                ChatInputBar(
                    value = inputText,
                    onValueChange = { inputText = it },
                    onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                            scope.launch {
                                if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    },
                    onPhotoClick = { showPhotoSheet = true },
                    isLoading = isLoading
                )
            }
        }
    }
}


@Composable
private fun ChatHeader(
    onClearClick: () -> Unit,
    onSaveClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Surface(color = Color.White.copy(alpha = 0.85f), shadowElevation = 4.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(
                    Brush.verticalGradient(listOf(Color(0xFFA98BFF), Color(0xFF6A3FB5))), CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("SkinAI", color = Violet, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Your skincare assistant", color = VioletSoft, fontSize = 11.sp)
            }
            // History
            IconButton(onClick = onHistoryClick) {
                Icon(Icons.Default.History, "Saved chats", tint = VioletLight)
            }

            // Save
            IconButton(onClick = onSaveClick) {
                Icon(Icons.Default.BookmarkBorder, "Save chat", tint = VioletLight)
            }

            // Clear
            IconButton(onClick = onClearClick) {
                Icon(Icons.Default.Delete, "Clear conversation", tint = VioletLight)
            }
        }
    }
}


@Composable
private fun MessageBubble(
    message: UiMessage,
    onProductClick: (ProductRecommendation) -> Unit
) {
    val isUser = message.role == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // ── Bula cu poza ─────────────────────────────────────────────────────
        if (isUser && message.photoUri != null) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp))
            ) {
                AsyncImage(
                    model = message.photoUri,
                    contentDescription = "Skin photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Label overlay
                Box(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0x99000000))))
                        .padding(8.dp)
                ) {
                    Text("Sent for analysis", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }

        } else {
            // ── Bula text ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(
                        topStart = 18.dp, topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    ))
                    .background(
                        if (isUser)
                            Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)))
                        else
                            Brush.linearGradient(listOf(Color.White, PaleGreen))
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    color = if (isUser) Color.White else Violet,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = formatTimestamp(message.timestamp),
            fontSize = 10.sp,
            color = if (isUser) VioletSoft else DarkGreen,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        // Concerns chips
        if (!isUser && message.detectedConcerns.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(start = 4.dp)) {
                message.detectedConcerns.take(3).forEach { concern ->
                    Box(
                        modifier = Modifier.background(VioletPale, RoundedCornerShape(50.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(concern.replace("_", " "), color = VioletSoft, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Produse
        if (!isUser && message.products.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                message.products.forEach { product ->
                    ChatProductCard(product = product, onClick = { onProductClick(product) })
                }
            }
        }
    }
}


@Composable
private fun ChatProductCard(product: ProductRecommendation, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(280.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(product.brand, color = VioletMid, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(product.name, color = Violet, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Box(modifier = Modifier.padding(start = 8.dp).background(PaleGreen, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("${"%.0f".format(product.score * 100)}%", color = DarkGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (product.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(product.explanation, color = VioletSoft, fontSize = 11.sp, lineHeight = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            if (product.warnings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("⚠️ ${product.warnings.first()}", color = Color(0xFFB45309), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (product.url != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("View product →", color = VioletMid, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}


@Composable
private fun TypingIndicator() {
    var dotCount by remember { mutableStateOf(1) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(400)
            dotCount = (dotCount % 3) + 1
        }
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp))
            .background(BotBubbleBg).padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(".".repeat(dotCount), color = VioletSoft, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onPhotoClick: () -> Unit,
    isLoading: Boolean
) {
    Surface(color = Color.White.copy(alpha = 0.9f), shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Buton poza
            Box(
                modifier = Modifier.size(46.dp)
                    .background(VioletPale, CircleShape)
                    .clickable(enabled = !isLoading) { onPhotoClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    "Attach photo",
                    tint = if (isLoading) VioletLight else VioletMid,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Input
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about your skin...", color = VioletLight, fontSize = 14.sp) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VioletMid, unfocusedBorderColor = VioletLight,
                    focusedContainerColor = InputBg, unfocusedContainerColor = InputBg,
                    focusedTextColor = Violet, unfocusedTextColor = Violet, cursorColor = VioletMid
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                singleLine = true, maxLines = 1
            )

            // Send
            Box(
                modifier = Modifier.size(46.dp)
                    .background(
                        if (!isLoading && value.isNotBlank())
                            Brush.verticalGradient(listOf(Color(0xFF9B7BFF), Color(0xFF6A3FB5)))
                        else
                            Brush.verticalGradient(listOf(VioletLight, VioletLight)),
                        CircleShape
                    )
                    .clickable(enabled = !isLoading && value.isNotBlank()) { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}


@Composable
private fun WelcomeSuggestions(onSuggestionClick: (String) -> Unit) {
    val suggestions = listOf(
        "Recommend a moisturizer for sensitive skin",
        "What helps with acne and redness?",
        "Find a sunscreen for oily skin"
    )
    val icons = listOf(Icons.Default.WaterDrop, Icons.Default.StarBorder, Icons.Default.LightMode)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("✦", color = VioletMid.copy(alpha = 0.30f), fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp, bottom = 5.dp))
            Text("Try asking me something like:", color = VioletSoft,
                style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 6.dp))
            Text("✦", color = VioletMid.copy(alpha = 0.35f), fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp, bottom = 5.dp))
        }
        suggestions.forEachIndexed { index, suggestion ->
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Card(
                    modifier = Modifier.width(240.dp).clickable { onSuggestionClick(suggestion) },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).background(VioletPale, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                            Icon(icons[index], null, tint = VioletMid, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(suggestion, color = Violet, fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun SaveChatDialog(
    suggestedTitle: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var titleInput by remember { mutableStateOf(suggestedTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Save this chat",
                color = Violet,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Give your conversation a name so you can find it later.",
                    color = VioletSoft,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    singleLine = true,
                    placeholder = { Text("e.g. Acne routine advice") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VioletMid,
                        unfocusedBorderColor = VioletLight,
                        focusedTextColor = Violet,
                        unfocusedTextColor = Violet,
                        cursorColor = VioletMid
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(titleInput) },
                enabled = titleInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VioletMid)
            ) {
                Text("Save", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = VioletSoft)
            }
        }
    )
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun Modifier.shadow4dp(): Modifier = this.then(Modifier.background(Color.White))