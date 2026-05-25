package com.example.frontend.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val Violet = Color(0xFF2E2363)

@Composable
fun ProductCard(
    name: String,
    brand: String,
    type: String,
    url: String?,
    tags: List<String>,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F5FF)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {

        Column {

            // ───────── IMAGE SECTION ─────────

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.White)
            ) {

                // decorative circle
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                        .background(
                            Color(0xFFF1E8FF),
                            CircleShape
                        )
                )

                // sparkles
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 20.dp, end = 20.dp),
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    shadowElevation = 4.dp
                ) {

                    Text(
                        text = type,
                        color = Color(0xFF7B57E8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        )
                    )
                }

                Text(
                    text = "✦",
                    color = Color(0xFFE9D8FD),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 28.dp, bottom = 38.dp)
                )

                AsyncImage(
                    model = url,
                    contentDescription = name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // ───────── INFO SECTION ─────────

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    text = brand,
                    color = Color(0xFF9B7AE8),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = name,
                    color = Violet,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // ───────── TAGS ─────────

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    tags.take(3).forEach { tag ->

                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color(0xFFEDE5FF)
                        ) {

                            Text(
                                text = tag,
                                color = Color(0xFF7B57E8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 8.dp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Recommended for your skin concern",
                    color = Color(0xFF8B85A3),
                    fontSize = 13.sp
                )
            }
        }
    }
}