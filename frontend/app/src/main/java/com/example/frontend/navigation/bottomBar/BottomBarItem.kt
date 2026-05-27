package com.example.frontend.navigation.bottomBar

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.frontend.presentation.Screen
import androidx.compose.ui.res.vectorResource
import com.example.frontend.R


sealed class BottomBarItem(
    val route: String,
    val iconRes: Int,
    val title: String
) {
    object Home : BottomBarItem(
        route = Screen.Home.route,
        iconRes = R.drawable.home_icon,
        title = "Home"
    )

    object Profile : BottomBarItem(
        route = Screen.Profile.route,
        iconRes = R.drawable.profile_icon,
        title = "Profile"
    )

    object ProductList : BottomBarItem(
        route = Screen.ProductList.route,
        iconRes = R.drawable.product_icon,
        title = "Products"
    )

    object Chat : BottomBarItem(
        route = Screen.Chat.route,
        iconRes = R.drawable.chat_icon,
        title = "SkinAi"
    )
}