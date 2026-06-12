package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ElegantDarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = OnPrimaryPurple,
    primaryContainer = PrimaryPurple,
    onPrimaryContainer = OnPrimaryPurple,
    secondaryContainer = NavIndicator,
    onSecondaryContainer = OnNavIndicator,
    background = BgDark,
    onBackground = OnBgDark,
    surface = BgDark,
    onSurface = OnBgDark,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = OnSurfaceSecondary,
    outline = Color(0xFF938F99)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
  MaterialTheme(colorScheme = ElegantDarkColorScheme, typography = Typography, content = content)
}
