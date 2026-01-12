package com.carryon.companion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CarryOnRed,
    secondary = CarryOnAccent,
    background = CarryOnDark,
    surface = CarryOnSurface,
    surfaceVariant = CarryOnSurfaceVariant,
    onPrimary = CarryOnTextPrimary,
    onSecondary = CarryOnDark,
    onBackground = CarryOnTextPrimary,
    onSurface = CarryOnTextPrimary,
    onSurfaceVariant = CarryOnTextSecondary,
)

@Composable
fun CarryOnCompanionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content,
    )
}
