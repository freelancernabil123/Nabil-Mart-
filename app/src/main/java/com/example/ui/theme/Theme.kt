package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    secondary = Gold80,
    tertiary = GreenGrey80,
    background = Color(0xFF111412),
    surface = Color(0xFF1B201D),
    onPrimary = Color(0xFF003822),
    onSecondary = Color(0xFF492F00),
    onBackground = Color(0xFFE1E3E0),
    onSurface = Color(0xFFE1E3E0)
)

private val LightColorScheme = lightColorScheme(
    primary = NabilGreenPrimary,
    secondary = NabilGoldSecondary,
    tertiary = NabilGreenDark,
    background = NabilBackground,
    surface = NabilSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = NabilTextDark,
    onSurface = NabilTextDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false by default for e-commerce apps to preserve the specific, organic green branding aesthetic!
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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
