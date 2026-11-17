package com.islabuy.camiguin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val IslaBuyColors = lightColorScheme(
    primary = NavyBlue,
    onPrimary = PureWhite,
    secondary = OceanBlue,
    onSecondary = PureWhite,
    tertiary = IslaOrange,
    background = ScreenBackground,
    onBackground = PrimaryText,
    surface = CardBackground,
    onSurface = PrimaryText,
    error = ErrorRed
)

@Composable
fun IslaBuyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IslaBuyColors,
        typography = Typography,
        content = content
    )
}