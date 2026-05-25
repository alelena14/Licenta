//package com.example.frontend.presentation.recommendations
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.animation.*
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.core.content.FileProvider
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavHostController
//import coil.compose.AsyncImage
//import com.example.frontend.presentation.Screen
//import com.example.frontend.presentation.component.ProductCard
//import java.io.File
//import java.io.FileOutputStream
//import androidx.core.graphics.scale
//
//
//enum class RecommendationMode { NONE, TEXT, PHOTO }
//
//enum class PhotoStep { PICK, PREVIEW, CONFIRM, RESULTS }
//
//fun String.formatConcernLabel(): String =
//    replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
//
//
//@Composable
//fun RecommendationScreen(
//    rootNavController: NavHostController,
//    viewModel: RecommendationViewModel = hiltViewModel()
//) {
//    val state = viewModel.uiState
//    val context = LocalContext.current
//
//    var mode by remember { mutableStateOf(RecommendationMode.NONE) }
//    var photoStep by remember { mutableStateOf(PhotoStep.PICK) }
//    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//    var selectedConcerns by remember { mutableStateOf<Set<String>>(emptySet()) }
//
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { selectedImageUri = it; photoStep = PhotoStep.PREVIEW }
//    }
//
//    var cameraOutputUri by remember { mutableStateOf<Uri?>(null) }
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture()
//    ) { success: Boolean ->
//        if (success) { selectedImageUri = cameraOutputUri; photoStep = PhotoStep.PREVIEW }
//    }
//
//
//        Column(
//            modifier = Modifier.fillMaxSize()
//                .clip(
//                    RoundedCornerShape(
//                        bottomStart = 30.dp,
//                        bottomEnd = 30.dp
//                    )
//                )
//        ) {
//
////            AnimatedVisibility(visible = mode != RecommendationMode.NONE) {
////                IconButton(
////                    onClick = {
////                        when {
////                            mode == RecommendationMode.PHOTO && photoStep == PhotoStep.CONFIRM ->
////                                photoStep = PhotoStep.PREVIEW
////                            mode == RecommendationMode.PHOTO && photoStep == PhotoStep.RESULTS ->
////                                photoStep = PhotoStep.CONFIRM
////                            else -> {
////                                mode = RecommendationMode.NONE
////                                photoStep = PhotoStep.PICK
////                                selectedImageUri = null
////                                selectedConcerns = emptySet()
////                            }
////                        }
////                    }
////                ) {
////                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
////                }
////            }
//
//            AnimatedContent(
//                targetState = mode,
//                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
//                label = "mode_transition"
//            ) { currentMode ->
//
//                when (currentMode) {
//
//                    RecommendationMode.NONE -> {
//                        RecommendationNoneScreen(
//                            onTextClick = { mode = RecommendationMode.TEXT },
//                            onPhotoClick = {
//                                mode = RecommendationMode.PHOTO
//                                photoStep = PhotoStep.PICK
//                                selectedImageUri = null
//                                selectedConcerns = emptySet()
//                            }
//                        )
//                    }
//
//                    RecommendationMode.TEXT -> {
//                        RecommendationTextScreen(
//                            viewModel = viewModel,
//                            rootNavController = rootNavController,
//                            onBack = { mode = RecommendationMode.NONE }
//                        )
//                    }
//
//                    RecommendationMode.PHOTO -> {
//                        AnimatedContent(
//                            targetState = photoStep,
//                            transitionSpec = {
//                                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) togetherWith
//                                        slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
//                            },
//                            label = "photo_step_transition"
//                        ) { step ->
//
//                            when (step) {
//
//                                PhotoStep.PICK -> {
//                                    PhotoPickScreen(
//                                        onBack = {
//                                            mode = RecommendationMode.NONE
//                                            photoStep = PhotoStep.PICK
//                                            selectedImageUri = null
//                                            selectedConcerns = emptySet()
//                                        },
//                                        onGalleryClick = { galleryLauncher.launch("image/*") },
//                                        onCameraClick = {
//                                            val file = File.createTempFile("photo_", ".jpg", context.cacheDir)
//                                            val uri = FileProvider.getUriForFile(
//                                                context, "${context.packageName}.provider", file
//                                            )
//                                            cameraOutputUri = uri
//                                            cameraLauncher.launch(uri)
//                                        }
//                                    )
//                                }
//
//                                PhotoStep.PREVIEW -> {
//                                    PhotoPreviewScreen(
//                                        imageUri = selectedImageUri!!,
//                                        isLoading = state is RecommendationUiState.Loading,
//                                        errorMessage = (state as? RecommendationUiState.Error)?.message,
//                                        onBack = { photoStep = PhotoStep.PICK },
//                                        onRetake = {
//                                            selectedImageUri = null
//                                            photoStep = PhotoStep.PICK
//                                        },
//                                        onAnalyse = {
//                                            selectedImageUri?.let { uri ->
//                                                val compressedFile = compressImage(context, uri)
//                                                viewModel.analyzePhoto(compressedFile) { concerns ->
//                                                    selectedConcerns = concerns.toSet()
//                                                    photoStep = PhotoStep.CONFIRM
//                                                }
//                                            }
//                                        }
//                                    )
//                                }
//
//                                // ── CONFIRM: user editează concerns ───────────
//                                PhotoStep.CONFIRM -> {
//                                    val allConcerns = viewModel.detectedConcerns
//                                    PhotoConfirmScreen(
//                                        imageUri = selectedImageUri!!,
//                                        allConcerns = allConcerns,
//                                        selectedConcerns = selectedConcerns,
//                                        onGetRecs = {
//                                            photoStep = PhotoStep.RESULTS
//                                            viewModel.getRecommendationsFromConcerns(
//                                                selectedConcerns.toList()
//                                            )
//                                        }
//                                    )
//                                }
//
//                                PhotoStep.RESULTS -> {
//                                    Column(modifier = Modifier.fillMaxSize()) {
//
//                                        selectedImageUri?.let { uri ->
//                                            Row(
//                                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
//                                                verticalAlignment = Alignment.CenterVertically,
//                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
//                                            ) {
//                                                AsyncImage(
//                                                    model = uri, contentDescription = null,
//                                                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
//                                                    contentScale = ContentScale.Crop
//                                                )
//                                                Text("Recommendations for your photo", style = MaterialTheme.typography.titleMedium)
//                                            }
//                                        }
//
//                                        when (state) {
//                                            is RecommendationUiState.Idle -> {}
//                                            is RecommendationUiState.Loading -> {
//                                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
//                                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                                                        CircularProgressIndicator()
//                                                        Text("Finding the best products...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
//                                                    }
//                                                }
//                                            }
//                                            is RecommendationUiState.Success -> {
//                                                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                                                    items(state.response.products) { product ->
//
//                                                        ProductCard(
//                                                            name = product.name, brand = product.brand, url = product.url,
//                                                            type = product.type,
//                                                            tags = product.tags,
//                                                            onClick = {
//                                                                rootNavController.navigate(
//                                                                    Screen.Product.createRoute(Uri.encode(product.name), Uri.encode(product.brand), Uri.encode(product.url ?: ""))
//                                                                )
//                                                            }
//                                                        )
//                                                    }
//                                                }
//                                            }
//                                            is RecommendationUiState.Error -> {
//                                                Text(state.message, color = MaterialTheme.colorScheme.error)
//                                                Spacer(modifier = Modifier.height(8.dp))
//                                                OutlinedButton(onClick = { photoStep = PhotoStep.CONFIRM }) { Text("Try again") }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//
//fun compressImage(context: Context, uri: Uri, maxSize: Int = 1280, quality: Int = 85): File {
//    val inputStream = context.contentResolver.openInputStream(uri)
//    val originalBitmap = BitmapFactory.decodeStream(inputStream)
//    val ratio = minOf(maxSize.toFloat() / originalBitmap.width, maxSize.toFloat() / originalBitmap.height)
//    val resizedBitmap = originalBitmap.scale(
//        (originalBitmap.width * ratio).toInt(),
//        (originalBitmap.height * ratio).toInt()
//    )
//    val file = File(context.cacheDir, "compressed_image.jpg")
//    FileOutputStream(file).use { out -> resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out) }
//    return file
//}
//
//
