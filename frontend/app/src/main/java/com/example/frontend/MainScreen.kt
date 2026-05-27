package com.example.frontend

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend.navigation.bottomBar.BottomBarItem
import com.example.frontend.navigation.bottomBar.BottomNavBar
import com.example.frontend.presentation.home.HomeScreen
import com.example.frontend.presentation.chat.ChatScreen
import com.example.frontend.presentation.chat.ChatViewModel
import com.example.frontend.presentation.product.ProductListScreen
import com.example.frontend.presentation.profile.ProfileScreen
import com.example.frontend.presentation.recommendations.PhotoPickScreen
import com.example.frontend.presentation.recommendations.SkinAnalysisViewModel
import com.example.frontend.presentation.ui.AppScreen
import java.io.File
import com.example.frontend.presentation.recommendations.SkinAnalysisState
import com.example.frontend.presentation.recommendations.PhotoConfirmScreen
import com.example.frontend.presentation.recommendations.PhotoPreviewScreen

@Composable
fun MainScreen(
    rootNavController: NavHostController
) {
    val navController = rememberNavController()
    val chatViewModel: ChatViewModel = hiltViewModel()
    val appContext = LocalContext.current

    val skinAnalysisVm: SkinAnalysisViewModel = hiltViewModel()

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            skinAnalysisVm.onPhotoSelected(it)
            navController.navigate("photo_preview")
        }
    }

    val cameraOutputUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraOutputUri.value?.let {
                skinAnalysisVm.onPhotoSelected(it)
                navController.navigate("photo_preview")
            }
        }
    }

    AppScreen { c ->
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,

        bottomBar = {
            BottomNavBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = BottomBarItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(BottomBarItem.Home.route) {
                HomeScreen(rootNavController, navController)
            }

            composable(BottomBarItem.Profile.route) {
                ProfileScreen(rootNavController, navController)
            }

            composable(BottomBarItem.ProductList.route) {
                ProductListScreen(rootNavController)
            }

            composable(BottomBarItem.Chat.route) {
                ChatScreen(
                    rootNavController = rootNavController,
                    viewModel = chatViewModel
                )
            }

            composable("skin_analysis") {
                PhotoPickScreen(
                    onBack         = { navController.popBackStack() },
                    onGalleryClick = { galleryLauncher.launch("image/*") },
                    onCameraClick  = {
                        val file = File.createTempFile("skin_", ".jpg", appContext.cacheDir)
                        val uri = FileProvider.getUriForFile(
                            appContext, "${appContext.packageName}.provider", file
                        )
                        cameraOutputUri.value = uri
                        cameraLauncher.launch(uri)
                    }
                )
            }

            composable("photo_preview") {
                val state by skinAnalysisVm.state.collectAsState()
                val uri = skinAnalysisVm.selectedUri

                uri?.let {
                    PhotoPreviewScreen(
                        imageUri     = it,
                        isLoading    = state is SkinAnalysisState.Analysing,
                        errorMessage = (state as? SkinAnalysisState.Error)?.message,
                        onBack       = { navController.popBackStack() },
                        onRetake     = { navController.popBackStack() },
                        onAnalyse    = { skinAnalysisVm.analysePhoto(it) }
                    )
                }

                LaunchedEffect(state) {
                    if (state is SkinAnalysisState.ConcernsReady) {
                        navController.navigate("photo_confirm")
                    }
                }
            }

            composable("photo_confirm") {
                val state by skinAnalysisVm.state.collectAsState()

                skinAnalysisVm.selectedUri?.let {
                    PhotoConfirmScreen(
                        imageUri         = it,
                        allConcerns      = skinAnalysisVm.detectedConcerns,
                        selectedConcerns = skinAnalysisVm.detectedConcerns.toSet(),
                        onGetRecs        = { confirmed ->
                            skinAnalysisVm.saveConcernsToProfile(confirmed)
                        }
                    )
                }

                LaunchedEffect(state) {
                    if (state is SkinAnalysisState.Saved) {
                        skinAnalysisVm.reset()
                        navController.navigate(BottomBarItem.Home.route) {
                            popUpTo("skin_analysis") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
    }
}


