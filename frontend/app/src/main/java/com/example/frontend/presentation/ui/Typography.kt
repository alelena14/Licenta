package com.example.frontend.presentation.ui

import com.example.frontend.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/* ---------- FONT FAMILY ---------- */

val Lato = FontFamily(
    Font(R.font.lato_light, FontWeight.Light),
    Font(R.font.lato_regular, FontWeight.Normal),
    Font(R.font.lato_bold, FontWeight.Bold),
    Font(R.font.lato_italic, FontWeight.Normal)
)

val Serif4 = FontFamily(
    Font(R.font.source_serif4, FontWeight.Normal),
    Font(R.font.source_serif4_italic, FontWeight.Normal)
)

/* ---------- TYPOGRAPHY ---------- */

val Typography = Typography(

    /*
    Folosit pentru titlurile principale ale unui ecran
    */
    headlineLarge = TextStyle(
        fontFamily = Serif4,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),

    /*
    Titluri de secțiuni mari
    Exemplu: "Recent Activity", "Your Courses"
    */
    headlineMedium = TextStyle(
        fontFamily = Serif4,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),

    /*
    Titlu pentru carduri sau componente importante
    Exemplu: titlu card, titlu list item
    */
    titleLarge = TextStyle(
        fontFamily = Lato,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),

    /*
    Titlu pentru elemente UI mai mici
    */
    titleMedium = TextStyle(
        fontFamily = Lato,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),

    /*
    Text principal din aplicație
    */
    bodyLarge = TextStyle(
        fontFamily = Lato,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    /*
    Text secundar
    Exemplu: informații adiționale, explicații
    */
    bodyMedium = TextStyle(
        fontFamily = Lato,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),

    /*
    Pentru butoane
    Exemplu: text din Button
    */
    labelLarge = TextStyle(
        fontFamily = Lato,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),

    /*
    Pentru elemente mici UI
    Exemplu: bottom navigation, chips
    */
    labelMedium = TextStyle(
        fontFamily = Lato,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)