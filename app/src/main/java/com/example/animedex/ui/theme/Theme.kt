package com.example.animedex.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AnimeDexColorScheme = darkColorScheme(
    primary = AnimeOrange,
    secondary = AnimeTeal,
    background = AnimeBackground,
    surface = AnimeSurface,
    surfaceVariant = AnimeSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color(0xFF06211E),
    onBackground = Color(0xFFE5E7EB),
    onSurface = Color(0xFFF9FAFB),
    onSurfaceVariant = Color(0xFFD1D5DB)
)

@Composable
fun AnimeDexTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AnimeDexColorScheme,
        typography = Typography(),
        content = content
    )
}
