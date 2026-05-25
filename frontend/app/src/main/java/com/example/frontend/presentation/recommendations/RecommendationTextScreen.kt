//package com.example.frontend.presentation.recommendations
//
//import android.net.Uri
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import com.example.frontend.presentation.Screen
//import com.example.frontend.presentation.component.ProductCard
//
//private val Violet = Color(0xFF3D1F8C)
//private val VioletMid = Color(0xFF6A3FB5)
//private val VioletSoft = Color(0xFF7C5CBF)
//
//@Composable
//fun RecommendationTextScreen(
//    viewModel: RecommendationViewModel,
//    rootNavController: NavHostController,
//    onBack: () -> Unit
//) {
//
//    var userInput by remember { mutableStateOf("") }
//    val state = viewModel.uiState
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    listOf(
//                        MaterialTheme.colorScheme.surface,
//                        MaterialTheme.colorScheme.background
//                    )
//                )
//            )
//    ) {
//
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 20.dp),
//            verticalArrangement = Arrangement.spacedBy(0.dp)
//        ) {
//
//            // ───────────────── HEADER ─────────────────
//
//            item {
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 16.dp)
//                        .height(56.dp)
//                ) {
//
//                    IconButton(
//                        onClick = onBack,
//                        modifier = Modifier.align(Alignment.CenterStart)
//                    ) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Violet
//                        )
//                    }
//
//                    Text(
//                        text = "SkinAI",
//                        modifier = Modifier.align(Alignment.Center),
//                        color = Violet,
//                        fontWeight = FontWeight.ExtraBold,
//                        fontSize = 22.sp,
//                        letterSpacing = (-1).sp
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(6.dp))
//
//                Text(
//                    text = "Tell us your concern and we'll recommend\nthe best skincare for you.",
//                    color = MaterialTheme.colorScheme.onSecondary,
//                    fontSize = 13.sp,
//                    lineHeight = 19.sp
//                )
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                // ───────────── INPUT ─────────────
//
//                OutlinedTextField(
//                    value = userInput,
//                    onValueChange = { userInput = it },
//                    modifier = Modifier.fillMaxWidth(),
//                    placeholder = {
//                        Text(
//                            "e.g. I have painful acne",
//                            color = MaterialTheme.colorScheme.onSurface,
//                            fontSize = 15.sp
//                        )
//                    },
//                    trailingIcon = {
//                        Text(
//                            "✦",
//                            color = VioletSoft,
//                            fontSize = 18.sp,
//                            modifier = Modifier.padding(end = 12.dp)
//                        )
//                    },
//                    shape = RoundedCornerShape(16.dp),
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedBorderColor = VioletMid,
//                        unfocusedBorderColor = MaterialTheme.colorScheme.surface,
//                        focusedContainerColor = MaterialTheme.colorScheme.background,
//                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
//                        focusedTextColor = Violet,
//                        unfocusedTextColor = Violet,
//                        cursorColor = VioletMid
//                    ),
//                    singleLine = false,
//                    minLines = 1,
//                    maxLines = 4
//                )
//
//                Spacer(modifier = Modifier.height(10.dp))
//
//                // ───────────── BUTTON ─────────────
//
//                Button(
//                    onClick = {
//                        viewModel.getRecommendations(userInput)
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(48.dp),
//                    shape = RoundedCornerShape(50.dp),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Transparent
//                    ),
//                    contentPadding = PaddingValues(0.dp),
//                    enabled = userInput.isNotBlank()
//                            && state !is RecommendationUiState.Loading
//                ) {
//
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(
//                                Brush.horizontalGradient(
//                                    listOf(
//                                        VioletMid,
//                                        Color(0xFF9B59D0)
//                                    )
//                                ),
//                                shape = RoundedCornerShape(50.dp)
//                            ),
//                        contentAlignment = Alignment.Center
//                    ) {
//
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//
//                            Text(
//                                text = "Get Recommendations",
//                                color = Color.White,
//                                fontWeight = FontWeight.SemiBold,
//                                fontSize = 14.sp
//                            )
//
//                            Text(
//                                "✦",
//                                color = Color.White.copy(alpha = 0.85f),
//                                fontSize = 14.sp
//                            )
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//            }
//
//            // ───────────────── STATES ─────────────────
//
//            when (state) {
//
//                is RecommendationUiState.Idle -> {}
//
//                is RecommendationUiState.Loading -> {
//
//                    item {
//
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 40.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CircularProgressIndicator(color = VioletMid)
//                        }
//                    }
//                }
//
//                is RecommendationUiState.Success -> {
//
//                    item {
//
//                        Text(
//                            text = "Recommended for you",
//                            color = Violet,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 17.sp
//                        )
//
//                        Text(
//                            text = "Based on your concern",
//                            color = VioletSoft,
//                            fontSize = 12.sp
//                        )
//
//                        Spacer(modifier = Modifier.height(14.dp))
//                    }
//
//                    items(state.response.products) { product ->
//
//                        ProductCard(
//                            name = product.name,
//                            brand = product.brand,
//                            tags = product.tags,
//                            type = product.type,
//                            url = product.url,
//                            onClick = {
//                                rootNavController.navigate(
//                                    Screen.Product.createRoute(
//                                        Uri.encode(product.name),
//                                        Uri.encode(product.brand),
//                                        Uri.encode(product.url ?: "")
//                                    )
//                                )
//                            }
//                        )
//
//                        Spacer(modifier = Modifier.height(14.dp))
//                    }
//                }
//
//                is RecommendationUiState.Error -> {
//
//                    item {
//
//                        Text(
//                            text = state.message,
//                            color = MaterialTheme.colorScheme.error,
//                            fontSize = 13.sp
//                        )
//                    }
//                }
//            }
//
//            // bottom spacing
//            item {
//                Spacer(modifier = Modifier.height(100.dp))
//            }
//        }
//    }
//}