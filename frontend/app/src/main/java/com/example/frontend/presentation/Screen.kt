package com.example.frontend.presentation

sealed class Screen(val route: String) {

    object Main: Screen("main_screen")
    object Welcome: Screen("welcome_screen")
    object Login: Screen("login_screen")
    object Register: Screen("register_screen")
    object Home: Screen("home_screen")
    object Profile: Screen("profile_screen")
    object Recommendations : Screen("recommendations_screen")
}