package com.example.frontend.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontend.navigation.bottomBar.BottomBarItem
import com.example.frontend.presentation.Screen
import com.example.frontend.presentation.ui.AppScreen
import com.example.frontend.presentation.ui.DarkGreen
import com.example.frontend.presentation.ui.PaleGreen
import com.example.frontend.presentation.ui.VibrantGreen
import com.example.frontend.data.model.ChatPrefillStore
private val BgTop        = Color(0xFFCEB8F5)
private val BgBottom     = Color(0xFFF4EEFF)
private val Violet       = Color(0xFF3D1F8C)
private val VioletMid    = Color(0xFF6A3FB5)
private val VioletSoft   = Color(0xFF7B57E8)
private val VioletPale   = Color(0xFFF3EEFF)
private val SubtitleGray = Color(0xFF8B70B8)
private val CardWhite    = Color(0xFFFFFFFF)

private val SKIN_TYPES = listOf("Oily", "Dry", "Combination", "Normal", "Sensitive")
private val CONCERNS   = listOf(
    "Dull Skin", "Hyperpigmentation", "Fine Lines", "Wrinkles", "PIH", "Inflammatory Acne",
    "Whiteheads", "Uneven Skin Tone", "Sebaceous Filaments", "Irritated Skin",
    "Hormonal Acne", "Rosacea Prone", "Dehydrated Skin", "Damaged Barrier",
    "Under Eye Bags", "Blackheads", "Loss of Firmness",
    "Enlarged Pores", "Congested Skin", "Melasma", "Dark Circles", "Comedonal Acne",
    "Eczema Prone", "Flaky Skin"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    rootNavController: NavController,
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.profileState.collectAsState()
    val stats = viewModel.stats
    val skinProfile = viewModel.skinProfile
    var clicked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    LaunchedEffect(clicked) {
        if (clicked) {
            val concerns = skinProfile?.concerns
                ?.joinToString(", ") { it.replace("_", " ") } ?: ""
            val skinType = skinProfile?.skinType ?: ""

            ChatPrefillStore.pendingPrefill = buildString {
                append("Please recommend skincare products for me. ")
                if (skinType.isNotBlank()) append("My skin type is $skinType. ")
                if (concerns.isNotBlank()) append("My main concerns are: $concerns.")
            }

            navController.navigate(BottomBarItem.Chat.route) {
                launchSingleTop = true
            }
            clicked = false
        }
    }
    AppScreen { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgTop, BgBottom)))
        ) {
            when (val s = state) {
                is ProfileState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = VioletSoft)
                }

                is ProfileState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(s.message, color = Color.Red)
                }

                is ProfileState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // ── Header ─────────────────────────────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF5B2FD4), Color(0xFF9B7BFF), Color(0xFFCEB8F5))
                                    )
                                )
                        ) {
                            // Sparkles decorative
                            Text("✦", color = Color.White.copy(alpha = 0.3f), fontSize = 24.sp,
                                modifier = Modifier.align(Alignment.TopStart).padding(start = 24.dp, top = 32.dp))
                            Text("✦", color = Color.White.copy(alpha = 0.2f), fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.TopEnd).padding(end = 40.dp, top = 48.dp))
                            Text("✦", color = Color.White.copy(alpha = 0.15f), fontSize = 18.sp,
                                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp, bottom = 40.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {


                                // Username
                                Text(
                                    text       = s.user.username ?: "SkinAI User",
                                    fontSize   = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = Color.White
                                )

                                Spacer(Modifier.height(4.dp))

                                // Email
                                Text(
                                    text     = s.user.email,
                                    fontSize = 13.sp,
                                    color    = Color.White.copy(alpha = 0.75f)
                                )

                                Spacer(Modifier.height(10.dp))

                                // Badge SkinAI Member
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier          = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint     = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text       = "SkinAI Member",
                                            fontSize   = 12.sp,
                                            color      = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // ── Stats row ─────────────────────────────────────────
                        Card(
                            modifier  = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .offset(y = (-20).dp),
                            shape     = RoundedCornerShape(20.dp),
                            colors    = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier              = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 18.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    icon = Icons.Outlined.ChatBubbleOutline,
                                    count = stats?.conversations ?: 0,
                                    label = "Conversations",
                                    color = VioletSoft,
                                    sublabel = "Your chats"
                                )
                                Divider(
                                    modifier  = Modifier
                                        .height(40.dp)
                                        .width(1.dp),
                                    color     = Color(0xFFEEEEEE)
                                )
                                StatItem(
                                    icon = Icons.Outlined.FavoriteBorder,
                                    count = stats?.savedProducts ?: 0,
                                    label = "Saved Products",
                                    color= DarkGreen,
                                    sublabel = "Your favorites"
                                )
                                Divider(
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(1.dp),
                                    color    = Color(0xFFEEEEEE)
                                )
                                StatItem(
                                    icon = Icons.Outlined.AutoAwesome,
                                    count = stats?.skinConcerns ?: 0,
                                    label = "Skin Concerns",
                                    color = VibrantGreen,
                                    sublabel = "We're tracking"
                                )
                            }
                        }

                        // ── Skin profile card ─────────────────────────────────
                        Card(
                            modifier  = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape     = RoundedCornerShape(20.dp),
                            colors    = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            ) {
                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Your skin profile",
                                            fontWeight = FontWeight.Bold,
                                            fontSize   = 16.sp,
                                            color      = Violet
                                        )
                                    }
                                    IconButton(onClick = { viewModel.openEdit() }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = VioletSoft
                                        )
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                val profile = viewModel.skinProfile
                                if (profile?.skinType == null && profile?.concerns.isNullOrEmpty()) {
                                    Text(
                                        "No profile yet. Tap edit to add your skin info.",
                                        color    = SubtitleGray,
                                        fontSize = 13.sp
                                    )
                                } else {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement   = Arrangement.spacedBy(8.dp)
                                    ) {
                                        profile.skinType?.let {
                                            ProfileChip(it.replaceFirstChar { c -> c.uppercase() })
                                        }
                                        profile.concerns.forEach { concern ->
                                            ProfileChip(concern.replace("_", " "))
                                        }

                                    }

                                    Spacer(Modifier.height(8.dp))

                                    Button(
                                        onClick = { clicked = true },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp)
                                            .height(32.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B57E8))
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Get Recommendations", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }

                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // ── Menu items ────────────────────────────────────────
                        Card(
                            modifier  = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape     = RoundedCornerShape(20.dp),
                            colors    = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                MenuRow(label = "Saved Products", onClick = { rootNavController.navigate("favorite") })
                                HorizontalDivider(color = Color(0xFFF0EBF8), thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp))
                                MenuRow(label = "Chat History",   onClick = { rootNavController.navigate("chat_history") })
                                HorizontalDivider(color = Color(0xFFF0EBF8), thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp))
                                MenuRow(label = "About SkinAI",   onClick = { /* TODO */ })
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // ── Log out ───────────────────────────────────────────
                        OutlinedButton(
                            onClick  = {
                                rootNavController.navigate(Screen.Login.route) { popUpTo(0) }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape    = RoundedCornerShape(16.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFE53935)
                            ),
                            border   = androidx.compose.foundation.BorderStroke(
                                1.dp, Color(0xFFE53935)
                            )
                        ) {
                            Text("Log Out", fontWeight = FontWeight.Medium)
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                }

                else -> {}
            }
        }

        // ── Bottom sheet editare ──────────────────────────────────────────────
        if (viewModel.isEditing) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.closeEdit() },
                containerColor   = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Edit skin profile",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                        color      = Violet
                    )
                    Spacer(Modifier.height(20.dp))

                    Text("Skin type", fontWeight = FontWeight.Medium,
                        color = Violet, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(8.dp)
                    ) {
                        SKIN_TYPES.forEach { type ->
                            SelectableChip(
                                label    = type,
                                selected = viewModel.editSkinType == type,
                                onClick  = { viewModel.onSkinTypeSelected(type) }
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text("Concerns", fontWeight = FontWeight.Medium,
                        color = Violet, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(8.dp)
                    ) {
                        CONCERNS.forEach { concern ->
                            SelectableChip(
                                label    = concern.replace("_", " "),
                                selected = concern in viewModel.editConcerns,
                                onClick  = { viewModel.onConcernToggled(concern) }
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    Button(
                        onClick  = { viewModel.saveProfile() },
                        enabled  = !viewModel.isSaving,
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = VioletSoft)
                    ) {
                        if (viewModel.isSaving) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(18.dp),
                                color       = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ── Componente helper ─────────────────────────────────────────────────────────

@Composable
fun StatItem(
    icon: ImageVector,
    count: Int,
    color: Color,
    label: String,
    sublabel: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )
        Text(
            text = sublabel,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun MenuRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 15.sp, color = Violet, fontWeight = FontWeight.Medium)
        Text("›", fontSize = 20.sp, color = SubtitleGray)
    }
}

@Composable
private fun ProfileChip(text: String) {
    Surface(shape = RoundedCornerShape(50.dp), color = VioletPale) {
        Text(
            text     = text,
            color    = VioletMid,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SelectableChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape    = RoundedCornerShape(50.dp),
        color    = if (selected) VioletSoft else VioletPale,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text       = label,
            color      = if (selected) Color.White else VioletMid,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize   = 13.sp,
            modifier   = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}