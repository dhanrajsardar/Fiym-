package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BroOrangePrimary,
    onPrimary = Color.White,
    primaryContainer = BroContainerOrange,
    onPrimaryContainer = BroOrangeTertiary,
    secondary = BroOrangeSecondary,
    onSecondary = Color.White,
    tertiary = BroOrangeTertiary,
    background = BroDarkBackground,
    onBackground = BroTextPrimary,
    surface = BroDarkSurface,
    onSurface = BroTextPrimary,
    surfaceVariant = BroDarkSurfaceVariant,
    onSurfaceVariant = BroTextSecondary,
    outline = BroDarkBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

