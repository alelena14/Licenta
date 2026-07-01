package com.example.frontend.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext

@Composable
fun RootNavGraph(
    navController: NavHostController
) {
    val startDestination =
        if (FirebaseAuth.getInstance().currentUser != null) {
            Routes.ROOT
        } else {
            Routes.AUTH
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(navController)
        mainNavGraph(navController)
    }
}