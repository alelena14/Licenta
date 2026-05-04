package com.example.frontend.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.frontend.presentation.MainScreen
import com.example.frontend.presentation.Screen

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

    }
}