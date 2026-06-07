package com.example.frontend.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val BgTop       = Color(0xFFCEB8F5)
private val BgBottom    = Color(0xFFF4EEFF)
private val Violet      = Color(0xFF3D1F8C)
private val VioletMid   = Color(0xFF6A3FB5)
private val VioletSoft  = Color(0xFF7B57E8)
private val VioletPale  = Color(0xFFF3EEFF)
private val SubtitleGray = Color(0xFF8B70B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSkinAiScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgBottom)))
    ) {
        Text("✦", color = Color.White.copy(alpha = 0.3f), fontSize = 20.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 24.dp, top = 52.dp))
        Text("✦", color = Color(0xFF9B7BFF).copy(alpha = 0.2f), fontSize = 13.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 60.dp, top = 78.dp))

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF5B2FD4), Color(0xFF9B7BFF), Color(0xFFCEB8F5))
                        )
                    )
                    .padding(top = 48.dp, bottom = 28.dp, start = 8.dp, end = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                    Column {
                        Text("About skinAI", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text("How it works & what to expect", fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {

                // ── Hero card ─────────────────────────────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-16).dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    Brush.linearGradient(listOf(VioletSoft, Color(0xFF9B7BFF))),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✦", fontSize = 22.sp, color = Color.White)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("skinAI Analysis", fontSize = 17.sp,
                            fontWeight = FontWeight.Bold, color = Violet)
                        Spacer(Modifier.height(4.dp))
                        Surface(shape = RoundedCornerShape(50.dp), color = VioletPale) {
                            Text("Version 1.0 · Powered by EfficientNetV2",
                                fontSize = 11.sp, color = SubtitleGray,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "An AI-powered tool that detects skin concerns from photos and helps you build a smarter skincare routine.",
                            fontSize = 13.sp, color = VioletMid,
                            lineHeight = 20.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                SectionLabel("How it works")

                // ── How it works card ─────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        HowItWorksRow(1, "Upload a photo",
                            "Take or select a clear, well-lit photo of your face.")
                        HorizontalDivider(color = Color(0xFFF0EBF8), thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp))
                        HowItWorksRow(2, "AI analysis",
                            "Our model scans for 8 skin concerns using deep learning.")
                        HorizontalDivider(color = Color(0xFFF0EBF8), thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp))
                        HowItWorksRow(3, "Get results",
                            "See detected concerns with confidence scores and tailored product recommendations.")
                    }
                }

                Spacer(Modifier.height(12.dp))
                SectionLabel("What we detect")

                // ── Detected concerns card ────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Detected skin concerns", fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = Violet)
                        Spacer(Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Acne", "Wrinkles", "Pores", "Discoloration",
                                "Blackheads", "Eye bags", "Dry skin", "Whiteheads"
                            ).forEach { concern ->
                                Surface(shape = RoundedCornerShape(50.dp), color = VioletPale) {
                                    Text(concern, fontSize = 12.sp, color = VioletMid,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                SectionLabel("Medical disclaimer")

                // ── Disclaimer card ───────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F0)),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5C4B3))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚠", fontSize = 16.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("Not a medical tool", fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, color = Color(0xFF993C1D))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "skinAI is designed for cosmetic guidance only. Results are not a medical diagnosis and should not replace advice from a licensed dermatologist. Always consult a healthcare professional for skin conditions that concern you.",
                            fontSize = 12.sp, color = Color(0xFF712B13), lineHeight = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                SectionLabel("Privacy & data")

                // ── Privacy card ──────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        PrivacyRow("Photos not stored",
                            "Images are processed and discarded immediately.")
                        HorizontalDivider(color = Color(0xFFF0EBF8), thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp))
                        PrivacyRow("Secure transmission",
                            "All data is sent over encrypted HTTPS.")
                        HorizontalDivider(color = Color(0xFFF0EBF8), thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp))
                        PrivacyRow("No personal data shared",
                            "We never share your data with third parties.")
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = SubtitleGray,
        letterSpacing = 0.06.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 2.dp)
    )
}

@Composable
private fun HowItWorksRow(step: Int, title: String, subtitle: String) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(VioletPale, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(step.toString(), fontSize = 12.sp,
                fontWeight = FontWeight.Bold, color = VioletSoft)
        }
        Column {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Violet)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, fontSize = 12.sp, color = SubtitleGray, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun PrivacyRow(title: String, subtitle: String) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(VioletPale, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🔒", fontSize = 14.sp)
        }
        Column {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Violet)
            Text(subtitle, fontSize = 11.sp, color = SubtitleGray)
        }
    }
}