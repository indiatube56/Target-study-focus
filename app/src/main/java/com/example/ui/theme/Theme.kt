package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CosmicDarkPrimary,
    secondary = CosmicDarkSecondary,
    tertiary = CosmicDarkTertiary,
    background = CosmicDarkBackground,
    surface = CosmicDarkSurface,
    onPrimary = Color(0xFF001E22),
    onSecondary = Color(0xFF002018),
    onTertiary = Color(0xFF261D00),
    onBackground = Color(0xFFE1E2E4),
    onSurface = Color(0xFFE1E2E4),
    surfaceVariant = Color(0xFF1E2837),
    onSurfaceVariant = Color(0xFFC1C7CE)
)

private val LightColorScheme = lightColorScheme(
    primary = CosmicLightPrimary,
    secondary = CosmicLightSecondary,
    tertiary = CosmicLightTertiary,
    background = CosmicLightBackground,
    surface = CosmicLightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF191C1E),
    onSurface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFFDFE2E6),
    onSurfaceVariant = Color(0xFF43474E)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorsSymbol = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorsSymbol,
        typography = Typography,
        content = content
    )
}
