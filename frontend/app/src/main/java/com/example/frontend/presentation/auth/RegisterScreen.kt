package com.example.frontend.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontend.navigation.Routes
import com.example.frontend.presentation.Screen

// ── Culori ─────────────────────────────────────────────────────────────────
private val BgVioletTop   = Color(0xFFB399E8)
private val BgVioletMid   = Color(0xFFCEB8F5)
private val BgWhiteBottom = Color(0xFFF4EEFF)
private val Violet        = Color(0xFF3D1F8C)
private val VioletSoft    = Color(0xFF7B57E8)
private val VioletLabel   = Color(0xFF6B4FC8)
private val SubtitleGray  = Color(0xFF8B70B8)
private val SparkleWhite  = Color(0xFFFFFFFF)
private val SparkleViolet = Color(0xFF9B7BFF)
private val FieldBg       = Color(0xFFF7F3FF)
private val FieldBorder   = Color(0xFFE0D4FF)

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate(Routes.ROOT) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to BgVioletTop,
                        0.35f to BgVioletMid,
                        0.65f to Color(0xFFE8DAFF),
                        1.0f to BgWhiteBottom
                    )
                )
            )
    ) {

        // ── Blobs ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.TopStart)
                .offset(x = (-40).dp, y = (-30).dp)
                .blur(4.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.55f),
                            Color(0xFFD2B4FF).copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = 60.dp)
                .blur(3.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color(0xFFC8B0FF).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-200).dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color(0xFFC3A0FF).copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        // ── Sparkles ─────────────────────────────────────────────────────────
        Text("✦", color = SparkleWhite, fontSize = 20.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 80.dp, top = 100.dp))
        Text("✦", color = SparkleWhite, fontSize = 13.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 55.dp, top = 136.dp))
        Text("✦", color = SparkleViolet, fontSize = 28.sp,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp, bottom = 80.dp))
        Text("✦", color = SparkleViolet.copy(alpha = 0.5f), fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 44.dp, bottom = 40.dp))
        Text("✦", color = SparkleWhite, fontSize = 16.sp,
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 24.dp, bottom = 40.dp))

        // ── Content ───────────────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.height(52.dp))


            Text(
                text = "Create\nAccount",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Violet,
                modifier = Modifier.padding(start = 28.dp),
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Start your personalized skincare journey",
                color = SubtitleGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 28.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Glass Card ───────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.88f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    // ── Email Field ──────────────────────────────────────────
                    FieldLabel(icon = { EmailIcon() }, label = "Email")

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text("Enter your email", color = Color(0xFFBBA8E0), fontSize = 13.sp)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = skinAiTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Username Field ───────────────────────────────────────
                    FieldLabel(icon = { PersonIcon() }, label = "Username")

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = {
                            Text("Choose a username", color = Color(0xFFBBA8E0), fontSize = 13.sp)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = skinAiTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Password Field ───────────────────────────────────────
                    FieldLabel(icon = { LockIcon() }, label = "Password")

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = {
                            Text("Create a password", color = Color(0xFFBBA8E0), fontSize = 13.sp)
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFFB0A0D8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = skinAiTextFieldColors(),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )

                    // ── Sign Up Button ───────────────────────────────────────
                    Button(
                        onClick = { viewModel.register(email, password, username, 0) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        enabled = authState !is AuthState.Loading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFB58FFF), Color(0xFF6A40E8))
                                    ),
                                    RoundedCornerShape(50.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "SIGN UP",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 20.dp)
                                        .size(22.dp)
                                )
                            }
                        }
                    }

                    // ── Error ────────────────────────────────────────────────
                    if (authState is AuthState.Error) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    // ── Sign In link ─────────────────────────────────────────
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Already have an account?",
                            color = SubtitleGray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = " Sign in",
                            color = VioletSoft,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.Login.route)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ── Shared helpers ──────────────────────────────────────────────────────────

@Composable
private fun FieldLabel(icon: @Composable () -> Unit, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 7.dp)
    ) {
        icon()
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = Color(0xFF3D1F8C),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmailIcon() = Icon(
    imageVector = Icons.Default.Email,
    contentDescription = null,
    tint = Color(0xFF7B57E8),
    modifier = Modifier.size(16.dp)
)

@Composable
private fun LockIcon() = Icon(
    imageVector = Icons.Default.Lock,
    contentDescription = null,
    tint = Color(0xFF7B57E8),
    modifier = Modifier.size(16.dp)
)

@Composable
private fun PersonIcon() = Icon(
    imageVector = Icons.Default.Person,
    contentDescription = null,
    tint = Color(0xFF7B57E8),
    modifier = Modifier.size(16.dp)
)

@Composable
private fun skinAiTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF7B57E8),
    unfocusedBorderColor = Color(0xFFE0D4FF),
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color(0xFFF7F3FF),
    cursorColor = Color(0xFF7B57E8),
    focusedTextColor = Color(0xFF3D1F8C),
    unfocusedTextColor = Color(0xFF3D1F8C)
)