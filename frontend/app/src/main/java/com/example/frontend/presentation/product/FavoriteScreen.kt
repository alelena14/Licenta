package com.example.frontend.presentation.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontend.data.model.SavedProductDto
import com.example.frontend.presentation.Screen
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

private val BgTop         = Color(0xFFCEB8F5)
private val BgBottom      = Color(0xFFF4EEFF)
private val Violet        = Color(0xFF3D1F8C)
private val VioletMid     = Color(0xFF6A3FB5)
private val VioletSoft    = Color(0xFF7B57E8)
private val VioletPale    = Color(0xFFF3EEFF)
private val VioletLabel   = Color(0xFF6B4FC8)
private val SubtitleGray  = Color(0xFF8B70B8)
private val GreenTypeBg   = Color(0xFFE8F5E2)
private val GreenTypeText = Color(0xFF2E6B1F)

@Composable
fun FavoriteScreen(
    navController: NavController,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val state    by viewModel.state.collectAsState()
    val filtered by viewModel.filtered.collectAsState()
    val search    = viewModel.search

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to BgTop,
                        0.4f to Color(0xFFDDD0FF),
                        1.0f to BgBottom
                    )
                )
            )
    ) {
        // Blob decorativ
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .blur(6.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color(0xFFD0B8FF).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Text("✦", color = Color.White, fontSize = 18.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 100.dp, top = 60.dp))
        Text("✦", color = Color(0xFF9B7BFF), fontSize = 13.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 70.dp, top = 90.dp))

        LazyVerticalGrid(
            columns        = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 100.dp),
            modifier       = Modifier.fillMaxSize()
        ) {

            // ── Header ───────────────────────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .padding(start = 4.dp, end = 16.dp, top = 22.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Buton back
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Violet
                        )
                    }

                    // Titlu centrat
                    Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                        Text(
                            text  = "Saved Products",
                            style = MaterialTheme.typography.titleMedium,
                            color = Violet
                        )
                        Text(
                            text     = "Your personal skincare collection ✦",
                            fontSize = 13.sp,
                            color    = SubtitleGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Badge cu numărul total — același ca înainte
                    if (state is FavoriteViewModel.State.Success) {
                        val count = (state as FavoriteViewModel.State.Success).products.size
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color.White.copy(alpha = 0.85f)
                        ) {
                            Text(
                                text       = "$count saved",
                                fontSize   = 12.sp,
                                color      = VioletMid,
                                fontWeight = FontWeight.Medium,
                                modifier   = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // ── Search bar ────────────────────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value         = search,
                    onValueChange = viewModel::onSearchChange,
                    placeholder   = {
                        Text("Search saved products...", color = Color(0xFFBBA8E0), fontSize = 13.sp)
                    },
                    leadingIcon   = {
                        Icon(Icons.Default.Search, null, tint = VioletSoft,
                            modifier = Modifier.size(18.dp))
                    },
                    trailingIcon  = {
                        if (search.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchChange("") }) {
                                Icon(Icons.Default.Clear, null,
                                    tint = Color(0xFFBBA8E0), modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine    = true,
                    shape         = RoundedCornerShape(50.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = VioletSoft,
                        unfocusedBorderColor    = Color(0xFFE0D4FF),
                        focusedContainerColor   = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
                        cursorColor             = VioletSoft,
                        focusedTextColor        = Violet,
                        unfocusedTextColor      = Violet
                    ),
                    modifier  = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(52.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                )
            }

            // ── Content ───────────────────────────────────────────────────────
            when (val s = state) {
                is FavoriteViewModel.State.Loading -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier         = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = VioletMid) }
                    }
                }

                is FavoriteViewModel.State.Error -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier         = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(s.message, color = SubtitleGray, fontSize = 14.sp)
                        }
                    }
                }

                is FavoriteViewModel.State.Success -> {
                    if (filtered.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier         = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("✦", fontSize = 32.sp, color = Color(0xFFD4C5F0))
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text     = if (search.isEmpty()) "No saved products yet."
                                        else "No results for \"$search\"",
                                        color    = SubtitleGray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(filtered, key = { it.favoriteId }) { product ->
                            SavedProductCard(
                                product  = product,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
                                onClick  = {
                                    product.productId.let { id ->
                                        viewModel.productStore.openFromList(id)
                                        navController.navigate(Screen.Product.route)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Saved Product Card ────────────────────────────────────────────────────────

@Composable
private fun SavedProductCard(
    product:  SavedProductDto,
    modifier: Modifier = Modifier,
    onClick:  () -> Unit
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image area
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFF0EBF8)),
                contentAlignment = Alignment.Center
            ) {
                if(!product.url.isNullOrBlank()) {
                    AsyncImage(
                        model = product.url,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = product.brand?.take(2)?.uppercase() ?: "?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4C5F0)
                    )
                }
                // Badge score — doar daca salvat din chat
                product.score?.let { score ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = VioletSoft
                    ) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(Icons.Default.Star, null,
                                tint = Color.White, modifier = Modifier.size(9.dp))
                            Text(
                                text       = "${(score * 100).toInt()}%",
                                fontSize   = 10.sp,
                                color      = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                // Type chip
                product.type?.let { type ->
                    Surface(shape = RoundedCornerShape(6.dp), color = GreenTypeBg) {
                        Text(
                            text       = type,
                            fontSize   = 10.sp,
                            color      = GreenTypeText,
                            fontWeight = FontWeight.SemiBold,
                            modifier   = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                }

                Text(
                    text       = product.brand ?: "",
                    fontSize   = 11.sp,
                    color      = VioletMid,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text       = product.name ?: "Unknown product",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Violet,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    modifier   = Modifier.padding(top = 2.dp)
                )

                Spacer(Modifier.height(6.dp))

                // Concerns (din chat) SAU afterUse (din product screen)
                val tagsToShow = if (!product.concerns.isNullOrBlank()) {
                    product.concerns.split(",").map { it.trim() }.take(2)
                } else {
                    product.afterUse.take(2)
                }

                if (tagsToShow.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        tagsToShow.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = VioletPale
                            ) {
                                Text(
                                    text     = tag,
                                    fontSize = 9.sp,
                                    color    = VioletLabel,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}