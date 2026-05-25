package com.example.frontend.navigation.bottomBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.frontend.presentation.Screen



sealed class BottomBarItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Home : BottomBarItem(
        route = Screen.Home.route,
        icon = Icons.Default.Home,
        title = "Home"
    )

    object Profile : BottomBarItem(
        route = Screen.Profile.route,
        icon = Icons.Default.Person,
        title = "Profile"
    )

    object ProductList : BottomBarItem(
        route = Screen.ProductList.route,
        icon = Icons.Default.Face,
        title = "Products"
    )

    object Chat : BottomBarItem(
        route = Screen.Chat.route,
        icon = Icons.Default.ChatBubbleOutline,
        title = "SkinAi"
    )
}