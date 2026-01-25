package com.example.frontend.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.frontend.presentation.Screen
import com.example.frontend.presentation.profile.ProfileScreen

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController
) {
    navigation(
        route = Routes.ROOT,
        startDestination = Screen.Profile.route
    ) {
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }
    }
}


