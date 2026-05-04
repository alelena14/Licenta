package com.example.frontend.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontend.navigation.Routes
import com.example.frontend.presentation.Screen
import com.example.frontend.presentation.component.GradientButton
import com.example.frontend.presentation.ui.AppScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate(Routes.ROOT) {
                popUpTo(Routes.AUTH) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    AppScreen { c ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.onSecondary
                        )
                    )
                )
        ) {

            Column {

                Spacer(modifier = Modifier.height(80.dp))

                Text(
                    text = "Hello\nSign in!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 24.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF4F4F4)
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {

                        Spacer(modifier = Modifier.height(20.dp))

                        c.AuthTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        c.AuthTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password"
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        GradientButton(
                            text = "SIGN IN",
                            loading = authState is AuthState.Loading
                        ) {
                            viewModel.login(email, password)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth().align(Alignment.End)
                        ) {

                            Text("Don't have account? ", modifier = Modifier.align(Alignment.End))


                            Text(
                                text = "Sign up",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.align(Alignment.End).clickable {
                                    navController.navigate(Screen.Register.route)
                                }
                            )
                        }

                        if (authState is AuthState.Error) {

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = (authState as AuthState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}