package com.example.frontend.presentation.ui


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(

    /* culoarea principală a aplicației */
    primary = PurpleMain,

    /* text sau icon pe primary */
    onPrimary = Color.White,

    /* secundar (accent soft pentru UI) */
    secondary = PurpleSoft,
    onSecondary = PurpleDark,

    /* fundal principal */
    background = PurpleLightest,
    onBackground = PurpleDark,

    /* carduri / surfaces */
    surface = PurpleLight,
    onSurface = PurpleDark,

    /* pentru erori */
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(

    /* accent principal */
    primary = DarkAccent,
    onPrimary = Color.Black,

    secondary = PurpleSoft,
    onSecondary = Color.Black,

    /* fundal aplicație */
    background = DarkBackground,
    onBackground = Color.White,

    /* carduri */
    surface = DarkSurface,
    onSurface = Color.White,

    error = Color(0xFFF2B8B5),
    onError = Color.Black
)


@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}