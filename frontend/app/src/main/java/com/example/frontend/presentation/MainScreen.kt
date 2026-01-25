package com.example.frontend.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun MainScreen(
    navController: NavHostController
){

    Scaffold{innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {

        }
    }
}