package com.example.frontend.presentation

sealed class Screen(val route: String) {

    object Login: Screen("login_screen")
    object Register: Screen("register_screen")
    object Profile: Screen("profile_screen")
}