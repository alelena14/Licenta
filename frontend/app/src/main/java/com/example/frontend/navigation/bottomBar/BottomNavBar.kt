package com.example.frontend.navigation.bottomBar

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val screens = listOf(
        BottomBarItem.Home,
        BottomBarItem.Profile,
        BottomBarItem.ProductList,
        BottomBarItem.Chat

    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val colors = MaterialTheme.colorScheme

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),

        containerColor = colors.surface,

        windowInsets = NavigationBarDefaults.windowInsets,

        tonalElevation = 0.dp
    ) {

        screens.forEach { screen ->

            BottomBarItemView(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.BottomBarItemView(
    screen: BottomBarItem,
    currentDestination: NavDestination?,
    navController: NavHostController
) {

    val isSelected = currentDestination?.hierarchy?.any {
        it.route == screen.route
    } == true

    val colors = MaterialTheme.colorScheme

    NavigationBarItem(

        selected = isSelected,

        interactionSource = remember { MutableInteractionSource() },

        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        alwaysShowLabel = false,

        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent,
            selectedIconColor = colors.primary,
            selectedTextColor = colors.primary,
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Gray
        ),

        icon = {

            Icon(
                imageVector = screen.icon,
                contentDescription = screen.title
            )

        },

        label = {
            Text(text = screen.title,
                modifier = Modifier.padding(bottom = 3.dp))
        }

    )
}