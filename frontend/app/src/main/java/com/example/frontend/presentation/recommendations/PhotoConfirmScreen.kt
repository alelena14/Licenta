//package com.example.frontend.presentation.recommendations
//
//import android.net.Uri
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.zIndex
//import coil.compose.AsyncImage
//import kotlin.collections.minus
//import kotlin.collections.plus
//
//private val Violet       = Color(0xFF3D1F8C)
//
//@Composable
//fun PhotoConfirmScreen(
//    imageUri: Uri,
//    allConcerns: List<String>,
//    selectedConcerns: Set<String>,
//    onGetRecs: () -> Unit
//) {
//
//    var selected by remember {
//        mutableStateOf(selectedConcerns)
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    listOf(
//                        Color(0xFFF7F4FF),
//                        Color.White
//                    )
//                )
//            )
//    ) {
//
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Top
//        ) {
//            item {
//
//                Spacer(modifier = Modifier.height(32.dp))
//
//                // ───────── PHOTO ─────────
//
//                Box(
//                    contentAlignment = Alignment.Center
//                ) {
//
//                    Text(
//                        text = "✦",
//                        color = Color(0xFFCBA8FF),
//                        fontSize = 22.sp,
//                        modifier = Modifier
//                            .offset(x = (-90).dp, y = (30).dp)
//                            .zIndex(2f)
//                    )
//
//                    Text(
//                        text = "✦",
//                        color = Color(0xFF9375C0),
//                        fontSize = 16.sp,
//                        modifier = Modifier
//                            .offset(x = (-110).dp, y = (15).dp)
//                            .zIndex(2f)
//                    )
//
//                    Text(
//                        text = "✦",
//                        color = Color(0xFF9D8BD9),
//                        fontSize = 25.sp,
//                        modifier = Modifier
//                            .offset(x = (-100).dp, y = (-10).dp)
//                            .zIndex(2f)
//                    )
//
//                    AsyncImage(
//                        model = imageUri,
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(width = 110.dp, height = 140.dp)
//                            .clip(RoundedCornerShape(24.dp)),
//                        contentScale = ContentScale.Crop
//                    )
//
//                    Text(
//                        text = "✦",
//                        color = Color(0xFFC57BCC),
//                        fontSize = 22.sp,
//                        modifier = Modifier
//                            .offset(x = (110).dp, y = (-30).dp)
//                            .zIndex(2f)
//                    )
//
//                    Text(
//                        text = "✦",
//                        color = Color(0xFFF1A1DB),
//                        fontSize = 18.sp,
//                        modifier = Modifier
//                            .offset(x = 140.dp, y = (-55).dp)
//                            .zIndex(2f)
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(14.dp))
//
//                // ───────── TITLE ─────────
//
//                Text(
//                    text = "The AI model detected",
//                    color = MaterialTheme.colorScheme.onSecondary,
//                    style = MaterialTheme.typography.headlineMedium,
//                    fontSize = 24.sp,
//                    lineHeight = 38.sp,
//                    textAlign = TextAlign.Center,
//                    letterSpacing = (-1).sp
//                )
//
//
//                Text(
//                    text = "these concerns.",
//                    color = MaterialTheme.colorScheme.primary,
//                    style = MaterialTheme.typography.headlineMedium,
//                    fontSize = 24.sp,
//                    lineHeight = 38.sp,
//                    textAlign = TextAlign.Center,
//                    letterSpacing = (-1).sp
//                )
//
//                Spacer(modifier = Modifier.height(18.dp))
//
//                Text(
//                    text = "Do you think they are accurate?\nDeselect any that don't apply or keep all.",
//                    color = MaterialTheme.colorScheme.primary,
//                    style = MaterialTheme.typography.bodyLarge,
//                    lineHeight = 24.sp,
//                    textAlign = TextAlign.Center
//                )
//
//                Spacer(modifier = Modifier.height(36.dp))
//
//                // ───────── CONCERNS ─────────
//
//                allConcerns.forEach { concern ->
//
//                    val isSelected = concern in selected
//
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 10.dp),
//                        shape = RoundedCornerShape(24.dp),
//                        colors = CardDefaults.cardColors(
//                            containerColor = Color.White
//                        ),
//                        elevation = CardDefaults.cardElevation(
//                            defaultElevation = 4.dp
//                        ),
//                        onClick = {
//                            selected = if (isSelected) {
//                                selected - concern
//                            } else {
//                                selected + concern
//                            }
//                        }
//                    ) {
//
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(
//                                    horizontal = 14.dp,
//                                    vertical = 12.dp
//                                ),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//
//                            Box(
//                                modifier = Modifier
//                                    .size(30.dp)
//                                    .background(
//                                        Color(0xFFF2EBFF),
//                                        CircleShape
//                                    ),
//                                contentAlignment = Alignment.Center
//                            ) {
//
//                                Icon(
//                                    imageVector = Icons.Default.Check,
//                                    contentDescription = null,
//                                    tint = if (isSelected)
//                                        Color(0xFF8B5CF6)
//                                    else
//                                        Color.Transparent
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(12.dp))
//
//                            Text(
//                                text = concern.formatConcernLabel(),
//                                color = MaterialTheme.colorScheme.onSecondary,
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//
//                            Spacer(modifier = Modifier.weight(1f))
//
//                            Text(
//                                text = "✦",
//                                color = Color(0xFFC58EFF),
//                                fontSize = 22.sp
//                            )
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                // ───────── BUTTON ─────────
//
//                Button(
//                    onClick = onGetRecs,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(48.dp),
//                    shape = RoundedCornerShape(50.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Transparent
//                    ),
//                    contentPadding = PaddingValues(0.dp),
//                    enabled = selected.isNotEmpty()
//                ) {
//
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(
//                                Brush.horizontalGradient(
//                                    listOf(
//                                        Color(0xFF8B5CF6),
//                                        Color(0xFF5B2BE0)
//                                    )
//                                ),
//                                shape = RoundedCornerShape(50.dp)
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(12.dp)
//                        ) {
//
//                            Text(
//                                text = "✦",
//                                color = Color.White,
//                                fontSize = 16.sp
//                            )
//
//                            Text(
//                                text = "Get Recommendations",
//                                color = Color.White,
//                                fontWeight = FontWeight.Bold,
//                                fontSize = 14.sp
//                            )
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(40.dp))
//            }
//        }
//    }
//}