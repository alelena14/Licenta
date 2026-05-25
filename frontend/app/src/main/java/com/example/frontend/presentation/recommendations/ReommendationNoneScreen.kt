package com.example.frontend.presentation.recommendations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.frontend.R


private val Violet   = Color(0xFF3D1F8C)


@Composable
fun RecommendationNoneScreen(
    onTextClick: () -> Unit,
    onPhotoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.background)
                )
            )

    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ── Header section ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Sparkle decorations
                Text(
                    text = "✦",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 36.dp, top = 40.dp)
                )
                Text(
                    text = "✦",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                    fontSize = 25.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 52.dp, top = 56.dp)
                )
                Text(
                    text = "✦",
                    color = Color(0xFFE91E8C).copy(alpha = 0.25f),
                    fontSize = 10.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 40.dp, top = 80.dp)
                )

                // Product image
                Image(
                    painter = painterResource(id = R.drawable.rec_none),
                    contentDescription = null,
                    modifier = Modifier
                        .size(280.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 90.dp)
                        .offset(x = 35.dp),
                    contentScale = ContentScale.Fit
                )

                // Text
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 28.dp, top = 90.dp)
                        .width(190.dp)
                        .zIndex(1f)
                ) {

                    Text(
                        text = "What's your\nskin concern?",
                        color = Violet,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        letterSpacing = (-0.8).sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tell us what you're looking to improve and we'll recommend the best skincare for you.",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Cards section ────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OptionCard(
                    iconRes = R.drawable.pencil,
                    title = "Enter your concern",
                    subtitle = "Describe your skin concern\nin your own words",
                    onClick = onTextClick
                )

                OptionCard(
                    iconRes = R.drawable.camera,
                    title = "Attach a photo",
                    subtitle = "Upload a clear photo of your skin\nfor better recommendations",
                    onClick = onPhotoClick
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun OptionCard(
    iconRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}