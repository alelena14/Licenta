package com.example.frontend.presentation.home

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.frontend.data.model.ProductRecommendation
import com.example.frontend.navigation.bottomBar.BottomBarItem
import com.example.frontend.presentation.Screen
import com.example.frontend.presentation.ui.AppScreen
import com.example.frontend.R
import com.example.frontend.presentation.ui.DarkGreen
import com.example.frontend.presentation.ui.PaleGreen
import com.example.frontend.presentation.ui.VibrantGreen

private val BgStart   = Color(0xFFF0EBFF)
private val BgEnd     = Color(0xFFFFFFFF)
private val Violet    = Color(0xFF3D1F8C)
private val VioletMid = Color(0xFF6A3FB5)
private val VioletSoft= Color(0xFF7B57E8)
private val VioletPale= Color(0xFFF3EEFF)
private val SubGray   = Color(0xFF8B70B8)

@Composable
fun HomeScreen(
    rootNavController: NavController,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("NAV_DEBUG", "HomeScreen composed")
    }

    AppScreen { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(BgStart, BgEnd)))
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFEDE5FF), Color(0xFFF7F3FF), Color.Transparent)
                        )
                    )
            ) {
                // Bubbles decorative
                Text("✦", color = VioletSoft.copy(alpha = 0.25f), fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 12.dp, end = 16.dp))
                Text("✦", color = VioletSoft.copy(alpha = 0.15f), fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp, end = 40.dp))

                // Text stanga
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp)
                        .fillMaxWidth(0.55f)
                ) {
                    Text(
                        text = "Welcome back,",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Text(
                        text = "Glow Getter",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 30.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Your skin is your story.\nLet's make it radiant.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubGray,
                        lineHeight = 19.sp
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.home_girl),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(160.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // ── Quick actions ─────────────────────────────────────────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickAction(
                        icon  = Icons.Default.Face,
                        label = "Skin Analysis",
                        onClick = { navController.navigate("skin_analysis") }
                    )
                    QuickAction(
                        icon  = Icons.Default.AutoAwesome,
                        label = "AI Assistant",
                        onClick = { navController.navigate(BottomBarItem.Chat.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        }
                    )
                    QuickAction(
                        icon  = Icons.Default.Favorite,
                        label = "Favorites",
                        onClick = { rootNavController.navigate("favorite") }
                    )
                    QuickAction(
                        icon  = Icons.Default.Star,
                        label = "Products",
                        onClick = { navController.navigate(BottomBarItem.ProductList.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        } }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Recommended for you ───────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Recommended for you",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Violet
                )
                TextButton(onClick = { navController.navigate(BottomBarItem.ProductList.route) }) {
                    Text("View all →", color = VioletSoft, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            when (val s = state) {
                is HomeViewModel.State.Loading -> {
                    Box(
                        modifier         = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = VioletSoft) }
                }

                is HomeViewModel.State.Empty -> {
                    Card(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape     = RoundedCornerShape(16.dp),
                        colors    = CardDefaults.cardColors(containerColor = VioletPale),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier            = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("✦", fontSize = 28.sp, color = VioletSoft.copy(alpha = 0.4f))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Add your skin concerns in Profile\nto get personalized recommendations.",
                                color       = SubGray,
                                fontSize    = 13.sp,
                                lineHeight  = 20.sp,
                                textAlign   = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { navController.navigate(BottomBarItem.Profile.route) },
                                colors  = ButtonDefaults.buttonColors(containerColor = VioletSoft),
                                shape   = RoundedCornerShape(50.dp)
                            ) {
                                Text("Set up my profile", color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }

                is HomeViewModel.State.Success -> {
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(s.products) { product ->
                            HomeProductCard(
                                product = product,
                                onClick = {
                                    viewModel.productStore.openFromList(product.id)
                                    rootNavController.navigate("product")
                                }
                            )
                        }
                    }
                }

                else -> {}
            }

            Spacer(Modifier.height(20.dp))

            // ── Banner rutina ─────────────────────────────────────────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color(0xFFE6E1FF)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Consistency is the key to results",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp,
                            color      = Violet
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Your future skin will thank you.",
                            fontSize  = 12.sp,
                            color     = SubGray
                        )
                    }
                    Button(
                        onClick = { navController.navigate(BottomBarItem.Chat.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        } },
                        colors  = ButtonDefaults.buttonColors(containerColor = VioletSoft),
                        shape   = RoundedCornerShape(50.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Start →", color = Color.White, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Quick Action ──────────────────────────────────────────────────────────────

@Composable
private fun QuickAction(
    icon:    ImageVector,
    label:   String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier         = Modifier
                .size(52.dp)
                .background(PaleGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = DarkGreen, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 11.sp, color = SubGray, fontWeight = FontWeight.Medium)
    }
}

// ── Home Product Card ─────────────────────────────────────────────────────────

@Composable
private fun HomeProductCard(
    product: ProductRecommendation,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column {
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (!product.url.isNullOrBlank()) {
                    coil.compose.AsyncImage(
                        model              = product.url,
                        contentDescription = product.name,
                        modifier           = Modifier.fillMaxSize().padding(8.dp),
                        contentScale       = ContentScale.Fit
                    )
                } else {
                    Text(
                        text       = product.brand.take(2).uppercase(),
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color(0xFFD4C5F0)
                    )
                }
                // Score badge
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp),
                    shape    = RoundedCornerShape(8.dp),
                    color    = VibrantGreen
                ) {
                    Text(
                        text     = "${(product.score * 100).toInt()}%",
                        fontSize = 9.sp,
                        color    = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(product.brand, fontSize = 10.sp, color = VioletMid,
                    fontWeight = FontWeight.Medium)
                Text(
                    text       = product.name,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Violet,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = product.type,
                    fontSize = 10.sp,
                    color    = SubGray
                )
            }
        }
    }
}