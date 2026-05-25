package com.example.frontend.presentation.product

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.frontend.data.model.ProductRecommendation
import kotlinx.coroutines.flow.collectLatest

// ── Colours ───────────────────────────────────────────────────────────────────

private val Violet      = Color(0xFF3D1F8C)
private val VioletMid   = Color(0xFF6A3FB5)
private val VioletSoft  = Color(0xFF7C5CBF)
private val VioletLight = Color(0xFFD4C5F0)
private val VioletPale  = Color(0xFFF3EEFF)

private val GreenTagBg     = Color(0xFFEAF3DE)
private val GreenTagText   = Color(0xFF27500A)
private val GreenBadgeBg   = Color(0xFFE8F5E9)
private val GreenBadgeText = Color(0xFF2E7D32)
private val RedTagBg       = Color(0xFFFCEBEB)
private val RedTagText     = Color(0xFF7B1010)
private val WhyCardBg      = Color(0xFFF1F8F1)
private val DividerColor   = Color(0xFFEEEEEE)

// ── Tag classification ────────────────────────────────────────────────────────

private val GOOD_TAGS = setOf(
    "Anti-Aging", "Scar Healing", "Brightening", "Good For Oily Skin",
    "Skin Texture", "Reduces Irritation", "Redness Reducing",
    "Dark Spots", "Reduces Large Pores", "Acne Fighting", "Hydrating"
)
private val BAD_TAGS = setOf(
    "Drying", "May Worsen Oily Skin", "Acne Trigger",
    "Irritating", "Eczema", "Rosacea"
)

private fun tagIsGood(tag: String) = GOOD_TAGS.contains(tag)
private fun tagIsBad(tag: String)  = BAD_TAGS.contains(tag)

// ── Screen entry point ────────────────────────────────────────────────────────

@Composable
fun ProductScreen(
    navController: NavController,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val product    by viewModel.product.collectAsState()
    val saveState  by viewModel.saveState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Snackbar feedback
    LaunchedEffect(Unit) {
        viewModel.saveState.collectLatest { state ->
            when (state) {
                is SaveState.Saved   -> snackbarHostState.showSnackbar("Saved!")
                is SaveState.Removed -> snackbarHostState.showSnackbar("Deleted.")
                is SaveState.Error   -> snackbarHostState.showSnackbar(state.message)
                else                 -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData    = data,
                    containerColor  = Violet,
                    contentColor    = Color.White,
                    shape           = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        product?.let { p ->
            ProductContent(
                product        = p,
                score          = viewModel.score,
                concerns       = viewModel.concerns,
                isFavorite     = isFavorite,
                saveState      = saveState,
                onBack         = { navController.popBackStack() },
                onToggleSave   = { viewModel.toggleFavorite() },
                modifier       = Modifier.padding(innerPadding)
            )
        } ?: Box(
            Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = VioletMid, modifier = Modifier.size(40.dp))
        }
    }
}

// ── Main content ──────────────────────────────────────────────────────────────

@Composable
private fun ProductContent(
    product:      ProductRecommendation,
    score:        Double?,
    concerns:     List<String>,
    isFavorite:   Boolean,
    saveState:    SaveState,
    onBack:       () -> Unit,
    onToggleSave: () -> Unit,
    modifier:     Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF6F1FF), Color(0xFFF2ECFB), Color(0xFFEDE7F6))
                )
            )
            .verticalScroll(rememberScrollState())
    ) {

        // ── Top bar ──────────────────────────────────────────────────────────
        TopBar(
            isFavorite   = isFavorite,
            saveState    = saveState,
            onBack       = onBack,
            onToggleSave = onToggleSave
        )

        // ── Image ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
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
                    text = product.brand.take(2).uppercase(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4C5F0)
                )
            }
        }

        // ── Brand + name + badges ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(product.brand, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = VioletMid)

            Spacer(Modifier.height(4.dp))

            Text(
                text       = product.name,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = Violet,
                lineHeight = 28.sp
            )
            Spacer(Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                if (concerns.isNotEmpty()) ConcernBadge(concerns = concerns)
                if (score != null)         ScoreBadge(score = score)
                TypeChip(type = product.type)
                if (product.country != null) CountryTag(product.country)
            }
        }

        Spacer(Modifier.height(8.dp))

        SectionCard { TagsSection(tags = product.tags) }
        Spacer(Modifier.height(8.dp))

        SectionCard { IngredientsSection(ingredients = product.ingredients) }
        Spacer(Modifier.height(8.dp))

        if (concerns.isEmpty() && product.explanation.isNotBlank()) {
            SectionCard {
                ExpandableSection(title = "What it helps with") {
                    Text(
                        text       = product.explanation,
                        fontSize   = 14.sp,
                        color      = VioletSoft,
                        lineHeight = 22.sp,
                        modifier   = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (product.warnings.isNotEmpty()) {
            SectionCard { WarningsSection(warnings = product.warnings) }
            Spacer(Modifier.height(8.dp))
        }

        if (concerns.isNotEmpty() && product.explanation.isNotBlank()) {
            WhyCard(explanation = product.explanation)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(40.dp))
    }
}

// ── Top bar ─────────────────────────────────────────────────────────

@Composable
private fun TopBar(
    isFavorite:   Boolean,
    saveState:    SaveState,
    onBack:       () -> Unit,
    onToggleSave: () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Back
        IconButton(
            onClick  = onBack,
            modifier = Modifier.size(40.dp).clip(CircleShape).background(VioletPale)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = VioletMid)
        }

        // Save / Loading
        IconButton(
            onClick  = onToggleSave,
            enabled  = saveState !is SaveState.Loading,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isFavorite) VioletMid else VioletPale)
        ) {
            if (saveState is SaveState.Loading) {
                CircularProgressIndicator(
                    color       = VioletMid,
                    modifier    = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector        = if (isFavorite) Icons.Filled.Bookmark
                    else Icons.Filled.BookmarkBorder,
                    contentDescription = if (isFavorite) "Saved" else "Save",
                    tint               = if (isFavorite) Color.White else VioletMid
                )
            }
        }
    }
}

// ── Badges ────────────────────────────────────────────────────────────────────

@Composable
private fun ConcernBadge(concerns: List<String>) {
    Surface(shape = RoundedCornerShape(20.dp), color = GreenBadgeBg) {
        Text(
            text       = "Great for ${concerns.take(2).joinToString(" & ") { it.replace("_", " ") }}",
            fontSize   = 12.sp,
            color      = GreenBadgeText,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun ScoreBadge(score: Double) {
    Surface(shape = RoundedCornerShape(20.dp), color = VioletPale) {
        Text(
            text       = "${(score * 100).toInt()}% match",
            fontSize   = 12.sp,
            color      = VioletMid,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun TypeChip(type: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE6F1FB)) {
        Text(
            text     = type,
            fontSize = 11.sp,
            color    = Color(0xFF185FA5),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun CountryTag(country: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE6F1FB)) {
        Text(
            text     = country,
            fontSize = 11.sp,
            color    = Color(0xFF185FA5),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// ── Section card wrapper ──────────────────────────────────────────────────────

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) { content() }
}

// ── Tags ──────────────────────────────────────────────────────────────────────

@Composable
private fun TagsSection(tags: List<String>) {
    val goodTags    = tags.filter { tagIsGood(it) }
    val badTags     = tags.filter { tagIsBad(it) }
    val unknownTags = tags.filter { !tagIsGood(it) && !tagIsBad(it) }
    val allSorted   = goodTags + badTags + unknownTags

    val initialCount = 4
    var showAll by remember { mutableStateOf(false) }
    val visible = if (showAll) allSorted else allSorted.take(initialCount)

    ExpandableSection(title = "Tags", initiallyExpanded = true) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement   = Arrangement.spacedBy(6.dp)
            ) {
                visible.forEach { tag ->
                    TagChip(tag = tag, good = tagIsGood(tag), bad = tagIsBad(tag))
                }
            }
            if (allSorted.size > initialCount) {
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick        = { showAll = !showAll },
                    contentPadding = PaddingValues(0.dp),
                    modifier       = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector        = if (showAll) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null, tint = VioletMid, modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text  = if (showAll) "Show less" else "Show more (${allSorted.size - initialCount})",
                        color = VioletMid, fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TagChip(tag: String, good: Boolean, bad: Boolean) {
    val bg     = when { good -> GreenTagBg;   bad -> RedTagBg;   else -> Color(0xFFF5F5F5) }
    val color  = when { good -> GreenTagText; bad -> RedTagText; else -> Color(0xFF555555) }
    val prefix = when { good -> "+";          bad -> "-";        else -> "" }

    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            if (prefix.isNotEmpty()) {
                Text(prefix, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
                Spacer(Modifier.width(4.dp))
            }
            Text(tag, fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Ingredients ───────────────────────────────────────────────────────────────

@Composable
private fun IngredientsSection(ingredients: List<String>) {
    val initialCount = 5
    var showAll by remember { mutableStateOf(false) }
    val visible = if (showAll) ingredients else ingredients.take(initialCount)

    ExpandableSection(title = "Ingredients", initiallyExpanded = true) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            visible.forEachIndexed { index, ingredient ->
                Text(
                    text     = ingredient,
                    fontSize = 13.sp,
                    color    = VioletSoft,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                )
                if (index < visible.lastIndex) {
                    HorizontalDivider(thickness = 0.5.dp, color = DividerColor)
                }
            }
            if (ingredients.size > initialCount) {
                Spacer(Modifier.height(6.dp))
                TextButton(
                    onClick        = { showAll = !showAll },
                    contentPadding = PaddingValues(0.dp),
                    modifier       = Modifier.padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector        = if (showAll) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null, tint = VioletMid, modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text  = if (showAll) "Show less" else "Show more (${ingredients.size - initialCount})",
                        color = VioletMid, fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// ── Warnings ──────────────────────────────────────────────────────────────────

@Composable
private fun WarningsSection(warnings: List<String>) {
    ExpandableSection(title = "Warnings", initiallyExpanded = true) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            warnings.forEach { warning ->
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    verticalAlignment     = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("!", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RedTagText,
                        modifier = Modifier.padding(top = 1.dp))
                    Text(warning, fontSize = 13.sp, color = RedTagText, lineHeight = 20.sp)
                }
            }
        }
    }
}

// ── Why card ──────────────────────────────────────────────────────────────────

@Composable
private fun WhyCard(explanation: String) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = WhyCardBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier              = Modifier.padding(16.dp),
            verticalAlignment     = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier         = Modifier.size(40.dp).clip(CircleShape).background(GreenBadgeBg),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", fontSize = 18.sp, color = GreenBadgeText)
            }
            Column {
                Text("Why you'll love it", fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = GreenBadgeText)
                Spacer(Modifier.height(4.dp))
                Text(explanation, fontSize = 13.sp, color = Color(0xFF3D5C3D), lineHeight = 20.sp)
            }
        }
    }
}

// ── Expandable section ────────────────────────────────────────────────────────

@Composable
private fun ExpandableSection(
    title:             String,
    initiallyExpanded: Boolean = false,
    content:           @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Violet)
            Icon(
                imageVector        = if (expanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint               = VioletLight
            )
        }

        AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
            Column {
                HorizontalDivider(thickness = 0.5.dp, color = DividerColor)
                content()
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}