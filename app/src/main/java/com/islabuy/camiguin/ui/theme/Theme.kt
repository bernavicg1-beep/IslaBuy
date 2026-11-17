package com.islabuy.camiguin.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Preserved Camiguin Market color palette
val GreenPrimary = androidx.compose.ui.graphics.Color(0xFF2E7D32)
val GreenSecondary = androidx.compose.ui.graphics.Color(0xFF4CAF50)
val GreenTertiary = androidx.compose.ui.graphics.Color(0xFF81C784)
val EarthBrown = androidx.compose.ui.graphics.Color(0xFF795548)
val WarmOrange = androidx.compose.ui.graphics.Color(0xFFFF8F00)
val LightBackground = androidx.compose.ui.graphics.Color(0xFFF1F8E9)
val DarkBackground = androidx.compose.ui.graphics.Color(0xFF1B1B1B)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = GreenTertiary,
    secondary = GreenSecondary,
    tertiary = EarthBrown,
    background = LightBackground,
    surface = androidx.compose.ui.graphics.Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenSecondary,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = GreenPrimary,
    secondary = GreenTertiary,
    tertiary = WarmOrange,
    background = DarkBackground,
    surface = androidx.compose.ui.graphics.Color(0xFF2C2C2C)
)

@Composable
fun IslaBuyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
