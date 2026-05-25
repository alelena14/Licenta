package com.example.frontend.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.frontend.navigation.bottomBar.BottomBarItem
import com.example.frontend.navigation.bottomBar.BottomNavBar
import com.example.frontend.presentation.chat.ChatScreen
import com.example.frontend.presentation.chat.ChatViewModel
import com.example.frontend.presentation.product.ProductListScreen
import com.example.frontend.presentation.profile.ProfileScreen
import com.example.frontend.presentation.ui.AppScreen

@Composable
fun MainScreen(
    rootNavController: NavHostController
) {

    val navController = rememberNavController()
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
                HomeScreen(rootNavController)
            }

            composable(BottomBarItem.Profile.route) {
                ProfileScreen(rootNavController, navController)
            }

            composable(BottomBarItem.ProductList.route) {
                ProductListScreen(rootNavController)
            }

            composable(BottomBarItem.Chat.route) { backStackEntry ->
                val chatViewModel: ChatViewModel = hiltViewModel(backStackEntry)
                ChatScreen(
                    rootNavController = rootNavController,
                    viewModel = chatViewModel
                )
            }
        }
    }
    }
}


