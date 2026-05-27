package com.example.frontend.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.frontend.MainScreen
import com.example.frontend.navigation.bottomBar.BottomBarItem
import com.example.frontend.presentation.Screen
import com.example.frontend.presentation.chat.ChatHistoryScreen
import com.example.frontend.presentation.product.ProductListScreen
import com.example.frontend.presentation.product.ProductScreen
import com.example.frontend.presentation.product.FavoriteScreen
import com.example.frontend.presentation.recommendations.SkinAnalysisState
import com.example.frontend.presentation.recommendations.PhotoConfirmScreen
import com.example.frontend.presentation.recommendations.PhotoPickScreen
import com.example.frontend.presentation.recommendations.PhotoPreviewScreen
import com.example.frontend.presentation.recommendations.SkinAnalysisViewModel

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController
) {
    navigation(
        route = Routes.ROOT,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Home.route) {
            MainScreen(rootNavController = navController)
        }

        composable(Screen.Product.route) {
            ProductScreen(navController = navController)
        }

        composable(Screen.ProductList.route) {
            ProductListScreen(navController)
        }

        composable("chat_history") {
            ChatHistoryScreen(navController = navController)
        }

        composable(Screen.Favorite.route) {
            FavoriteScreen(navController)
        }

    }
}