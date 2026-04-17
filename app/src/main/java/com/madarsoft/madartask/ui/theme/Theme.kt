package com.madarsoft.madartask.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = MadarOrange,
    onPrimary = Color.White,
    primaryContainer = MadarOrangeContainer,
    onPrimaryContainer = MadarOnOrangeContainer,
    secondary = MadarGreen,
    onSecondary = Color.White,
    secondaryContainer = MadarGreenContainer,
    onSecondaryContainer = MadarOnGreenContainer,
    surface = MadarSurface,
    onSurface = MadarOnSurface,
    surfaceVariant = MadarSurfaceVariant,
    onSurfaceVariant = MadarOnSurfaceVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = MadarOrangeDark,
    onPrimary = MadarOnOrangeContainer,
    primaryContainer = MadarOnOrangeContainer,
    onPrimaryContainer = MadarOrangeContainer,
    secondary = MadarGreenDark,
    onSecondary = MadarDark,
    secondaryContainer = MadarOnGreenContainer,
    onSecondaryContainer = MadarGreenContainer,
    surface = MadarDarkSurface,
    onSurface = Color(0xFFE2E3DC),
    surfaceVariant = MadarDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC4C8BB),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun MadarTaskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
