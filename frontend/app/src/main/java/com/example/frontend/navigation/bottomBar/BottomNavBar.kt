package com.example.frontend.navigation.bottomBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavHostController) {

    val screens = listOf(
        BottomBarItem.Home,
        BottomBarItem.Profile,
        BottomBarItem.Recommendations
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.surface
            )
        ) {

            NavigationBar(
                containerColor = Color.Transparent,
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

        onClick = {
            navController.navigate(screen.route) {

                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }

                launchSingleTop = true
                restoreState = true
            }
        },

        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent,
            selectedIconColor = colors.primary,
            selectedTextColor = colors.primary,
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Gray
        ),

        icon = {

            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .background(
                        if (isSelected)
                            colors.primary.copy(alpha = 0.12f)
                        else
                            Color.Transparent,
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {

                Icon(
                    imageVector = screen.icon,
                    contentDescription = screen.title
                )

            }

        },

        label = {
            Text(screen.title)
        }

    )
}