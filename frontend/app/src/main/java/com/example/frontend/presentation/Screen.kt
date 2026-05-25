package com.example.frontend.presentation

import android.net.Uri

sealed class Screen(val route: String) {

    object Main: Screen("main_screen")
    object Welcome: Screen("welcome_screen")
    object Login: Screen("login_screen")
    object Register: Screen("register_screen")
    object Home: Screen("home_screen")
    object Profile: Screen("profile_screen")
    object Recommendations: Screen("recommendations_screen")
    object Chat : Screen("chat") {
        val routeWithArgs = "chat?prefill={prefill}"
        fun withPrefill(message: String): String {
            val encoded = Uri.encode(message)
            return "chat?prefill=$encoded"
        }
    }
    object Product : Screen("product")

    object ProductList : Screen("product_list")
    object Favorite: Screen("favorite")

}