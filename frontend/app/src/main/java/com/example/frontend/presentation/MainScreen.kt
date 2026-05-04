package com.example.frontend.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.frontend.navigation.bottomBar.BottomBarItem

import com.example.frontend.navigation.bottomBar.BottomNavBar
import com.example.frontend.presentation.profile.ProfileScreen
import com.example.frontend.presentation.recommendations.RecommendationScreen

@Composable
fun MainScreen(
    rootNavController: NavHostController
) {

    val navController = rememberNavController()

    Scaffold(
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
                ProfileScreen(rootNavController)
            }

            composable(Screen.Recommendations.route) {
                RecommendationScreen(rootNavController)
            }
        }

    }
}