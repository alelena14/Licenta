package com.example.frontend.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dagger.hilt.android.qualifiers.ApplicationContext

@Composable
fun RootNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.AUTH
    ) {
        authNavGraph(navController)
        mainNavGraph(navController)
    }
}