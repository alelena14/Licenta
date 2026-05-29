package com.example.frontend.presentation.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.frontend.data.model.ProductCardDto
import com.example.frontend.data.model.ProductRecommendation
import com.example.frontend.presentation.Screen
import com.example.frontend.presentation.product.ProductStore
import javax.inject.Inject

// ── Colours ───────────────────────────────────────────────────────────────────

private val BgTop         = Color(0xFFCEB8F5)
private val BgBottom      = Color(0xFFF4EEFF)
private val Violet        = Color(0xFF3D1F8C)
private val VioletMid     = Color(0xFF6A3FB5)
private val VioletSoft    = Color(0xFF7B57E8)
private val VioletPale    = Color(0xFFF3EEFF)
private val VioletLabel   = Color(0xFF6B4FC8)
private val SubtitleGray  = Color(0xFF8B70B8)
private val SparkleWhite  = Color(0xFFFFFFFF)
private val SparkleViolet = Color(0xFF9B7BFF)
private val GreenTypeBg   = Color(0xFFE8F5E2)
private val GreenTypeText = Color(0xFF2E6B1F)

@Composable
fun ProductListScreen(
    navController: NavController,
    viewModel:     ProductListViewModel = hiltViewModel()
) {
    val state       by viewModel.state.collectAsState()
    val search      by viewModel.search.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val tags        by viewModel.tags.collectAsState()

    var filtersVisible by remember { mutableStateOf(false) }

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
        // ── Blobs decorativi ──────────────────────────────────────────────────
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

        // ── Sparkles ──────────────────────────────────────────────────────────
        Text("✦", color = SparkleWhite, fontSize = 18.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 100.dp, top = 60.dp))
        Text("✦", color = SparkleViolet, fontSize = 13.sp,
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
                        .padding(start = 12.dp, end = 16.dp, top = 22.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text  = "Products",
                            style = MaterialTheme.typography.titleMedium,
                            color = Violet
                        )
                        Text(
                            text     = "Find skincare that fits your skin ✦",
                            fontSize = 13.sp,
                            color    = SubtitleGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Surface(
                        shape    = RoundedCornerShape(50.dp),
                        color    = if (filtersVisible) VioletSoft else Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.clickable { filtersVisible = !filtersVisible }
                    ) {
                        Text(
                            text       = if (filtersVisible) "Hide filters" else "Filters",
                            fontSize   = 12.sp,
                            color      = if (filtersVisible) Color.White else VioletMid,
                            fontWeight = FontWeight.Medium,
                            modifier   = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // ── Search bar ────────────────────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value         = search,
                        onValueChange = viewModel::onSearchChange,
                        placeholder   = { Text("Search products...", color = Color(0xFFBBA8E0), fontSize = 13.sp) },
                        leadingIcon   = {
                            Icon(Icons.Default.Search, null, tint = VioletSoft, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon  = {
                            if (search.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchChange("") }) {
                                    Icon(Icons.Default.Clear, null, tint = Color(0xFFBBA8E0), modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine    = true,
                        shape         = RoundedCornerShape(50.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = VioletSoft,
                            unfocusedBorderColor = Color(0xFFE0D4FF),
                            focusedContainerColor   = Color.White,
                            unfocusedContainerColor = Color.White.copy(alpha = 0.85f),
                            cursorColor          = VioletSoft,
                            focusedTextColor     = Violet,
                            unfocusedTextColor   = Violet
                        ),
                        modifier      = Modifier
                            .weight(1f)
                            .height(52.dp),
                        textStyle     = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )
                }
            }

            // ── Banner motivational ───────────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier  = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    shape     = RoundedCornerShape(20.dp),
                    colors    = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.75f)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(48.dp)
                                .background(VioletPale, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = VioletSoft,
                                modifier = Modifier.padding(6.dp).size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text       = "Small steps, glowing skin ✦",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Violet
                            )
                            Text(
                                text     = "Every product you choose brings you closer to the skin you love.",
                                fontSize = 11.sp,
                                color    = SubtitleGray,
                                modifier = Modifier.padding(top = 3.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            item(span = { GridItemSpan(2) }) {
                val selectedType by viewModel.selectedType.collectAsState()

                if (filtersVisible) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {

                            // Header cu Clear all
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Filter",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Violet
                                )
                                if (selectedTag != null || selectedType != null) {
                                    TextButton(
                                        onClick = { viewModel.clearFilters() },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Clear all", color = VioletSoft, fontSize = 12.sp)
                                    }
                                }
                            }

                            // After use tags
                            Text(
                                text = "By effect",
                                fontSize = 12.sp,
                                color = SubtitleGray,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 8.dp,
                                    bottom = 4.dp
                                )
                            )
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                tags.forEach { tag ->
                                    FilterChip(
                                        label = tag,
                                        selected = selectedTag == tag,
                                        onClick = { viewModel.onTagSelected(tag) }
                                    )
                                }
                            }

                            // Product types
                            Text(
                                text = "By type",
                                fontSize = 12.sp,
                                color = SubtitleGray,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 8.dp,
                                    bottom = 4.dp
                                )
                            )
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                viewModel.productTypes.forEach { type ->
                                    FilterChip(
                                        label = type,
                                        selected = selectedType == type,
                                        onClick = { viewModel.onTypeSelected(type) }
                                    )
                                }
                            }
                        }
                }
            }

            // ── Products grid ─────────────────────────────────────────────────
            when (val s = state) {
                is ProductListState.Loading -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier         = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = VioletMid)
                        }
                    }
                }

                is ProductListState.Error -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier         = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(s.message, color = SubtitleGray, fontSize = 14.sp)
                        }
                    }
                }

                is ProductListState.Home -> {

                    item(span = { GridItemSpan(2) }) {

                        Column {

                            s.sections.forEach { section ->

                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {

                                    Text(
                                        text = section.title,
                                        color = Violet,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier
                                            .horizontalScroll(rememberScrollState())
                                            .padding(horizontal = 12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {

                                        section.products.forEach { product ->

                                            Box(
                                                modifier = Modifier.width(180.dp)
                                            ) {

                                                ProductCard(
                                                    product = product,
                                                    onClick = {
                                                        viewModel.productStore.openFromList(product.id)
                                                        navController.navigate(Screen.Product.route)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is ProductListState.Success -> {
                    if (s.products.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier         = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No products found.", color = SubtitleGray, fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(s.products, key = { it.id }) { product ->
                            ProductCard(
                                product  = product,
                                modifier = Modifier.padding(
                                    start  = 8.dp,
                                    end    = 8.dp,
                                    bottom = 12.dp
                                ),
                                onClick = {
                                    viewModel.productStore.openFromList(product.id)
                                    navController.navigate(Screen.Product.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Product card ──────────────────────────────────────────────────────────────

@Composable
private fun ProductCard(
    product:  ProductCardDto,
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (!product.url.isNullOrBlank()) {
                    AsyncImage(
                        model = product.url,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = product.brand.take(2).uppercase(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD4C5F0)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                // Type chip
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GreenTypeBg
                ) {
                    Text(
                        text     = product.type,
                        fontSize = 10.sp,
                        color    = GreenTypeText,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                Spacer(Modifier.height(5.dp))

                Text(
                    text       = product.brand,
                    fontSize   = 11.sp,
                    color      = VioletMid,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text       = product.name,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Violet,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    modifier   = Modifier.padding(top = 2.dp)
                )

                // AfterUse tags
                if (product.afterUse.isNotEmpty()) {

                    val totalChars = product.afterUse.take(2).sumOf { it.length }

                    Spacer(Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {

                        val tagsToShow =
                            if (totalChars > 25)
                                product.afterUse.take(1)
                            else
                                product.afterUse.take(2)

                        tagsToShow.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = VioletPale
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 9.sp,
                                    color = VioletLabel,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(
                                        horizontal = 6.dp,
                                        vertical = 3.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Filter chip ───────────────────────────────────────────────────────────────

@Composable
private fun FilterChip(
    label:      String,
    selected: Boolean,
    onClick:  () -> Unit
) {
    Surface(
        shape   = RoundedCornerShape(50.dp),
        color   = if (selected) VioletSoft else Color.White.copy(alpha = 0.85f),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text     = label,
            fontSize = 12.sp,
            color    = if (selected) Color.White else VioletMid,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}