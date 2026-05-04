package com.example.frontend.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.frontend.presentation.Screen
import com.example.frontend.presentation.auth.LoginScreen
import com.example.frontend.presentation.auth.RegisterScreen
import com.example.frontend.presentation.auth.WelcomeScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController
) {
    navigation(
        route = Routes.AUTH,
        startDestination = Screen.Welcome.route
    ) {

        composable(Screen.Welcome.route) {
            WelcomeScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

    }
}

