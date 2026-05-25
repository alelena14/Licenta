package com.example.frontend.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.frontend.presentation.Screen

// ── Culori ───────────────────────────────────────────────────────────────────
private val BgVioletTop    = Color(0xFFB399E8)   // violet intens sus
private val BgVioletMid    = Color(0xFFCEB8F5)   // liliac mid
private val BgWhiteBottom  = Color(0xFFF4EEFF)   // aproape alb jos
private val Violet         = Color(0xFF3D1F8C)
private val VioletSoft     = Color(0xFF7B57E8)
private val VioletLabel    = Color(0xFF6B4FC8)
private val SubtitleGray   = Color(0xFF7C7497)
private val BubbleColor    = Color(0xCCEEE8FF)   // bule semi-transparente
private val SparkleWhite   = Color(0xFFFFFFFF)
private val SparkleViolet  = Color(0xFF9B7BFF)


@Composable
fun WelcomeScreen(
    navController: NavHostController
) {
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

        // ── Mare cerc blur central (glass blob) ──────────────────────────────
        Box(
            modifier = Modifier
                .size(420.dp)
                .align(Alignment.TopCenter)
                .offset(y = 80.dp)
                .blur(30.dp)
                .background(
                    Color.White.copy(alpha = 0.38f),
                    CircleShape
                )
        )

        // ── Bule sticloase ───────────────────────────────────────────────────

        // Bula mare dreapta sus
        Box(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-28).dp, y = 90.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.75f),
                            Color(0xFFD8C8FF).copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Bula mica stanga mijloc
        Box(
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.CenterStart)
                .offset(x = 14.dp, y = 80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.65f),
                            Color(0xFFCBB8FF).copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Bula mica dreapta jos
        Box(
            modifier = Modifier
                .size(58.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-30).dp, y = (-160).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = 0.6f),
                            Color(0xFFD0BEFF).copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        // ── Sparkles ─────────────────────────────────────────────────────────
        Text("✦", color = SparkleWhite, fontSize = 22.sp,
            modifier = Modifier.align(Alignment.TopStart).padding(start = 32.dp, top = 120.dp))
        Text("✦", color = SparkleWhite, fontSize = 14.sp,
            modifier = Modifier.align(Alignment.TopStart).padding(start = 68.dp, top = 170.dp))
        Text("✦", color = SparkleWhite, fontSize = 18.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 60.dp, top = 100.dp))
        Text("✦", color = SparkleViolet, fontSize = 32.sp,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 28.dp, bottom = 60.dp))
        Text("✦", color = SparkleViolet.copy(alpha = 0.6f), fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 48.dp, bottom = 20.dp))

        // ── Valuri jos ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 10.dp)
                .blur(20.dp)
                .background(
                    Color(0xFFDDD0FF).copy(alpha = 0.55f),
                    RoundedCornerShape(topStart = 100.dp, topEnd = 150.dp)
                )
        )

        // ── Conținut principal ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(150.dp))

            // Logo cerc violet cu icon alb
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF9B7BFF), Color(0xFF6A3FB5))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SkinAI",
                color = VioletLabel,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.height(20.dp))


            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.width(50.dp).height(1.dp).background(Color(0xFFCCBBFF)))
                Text("✦", color = VioletSoft, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 10.dp))
                Box(modifier = Modifier.width(50.dp).height(1.dp).background(Color(0xFFCCBBFF)))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Welcome Back",
                color = Violet,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your personal AI skincare companion",
                color = SubtitleGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(56.dp))

            // ── SIGN IN ──────────────────────────────────────────────────────
            OutlinedButton(
                onClick = { navController.navigate(Screen.Login.route) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(50.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.horizontalGradient(listOf(Color(0xFFCDB4FF), Color(0xFF9A73FF)))
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.55f)
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✦",
                        color = Color(0xFFB08CFF),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 18.dp)
                    )

                    Text(
                        text = "SIGN IN",
                        color = VioletSoft,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ── SIGN UP ──────────────────────────────────────────────────────
            Button(
                onClick = { navController.navigate(Screen.Register.route) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFB58FFF), Color(0xFF6A40E8))
                            ),
                            shape = RoundedCornerShape(50.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "SIGN UP",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 24.dp)
                            .size(28.dp)
                    )

                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Bottom ───────────────────────────────────────────────────────
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.85f),
                shadowElevation = 6.dp
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = VioletSoft,
                    modifier = Modifier.padding(6.dp).size(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Healthy skin, powered by AI",
                color = SubtitleGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}