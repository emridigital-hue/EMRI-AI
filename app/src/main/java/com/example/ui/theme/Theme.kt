package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = YtRed,
    onPrimary = Color.White,
    primaryContainer = YtRedDark,
    onPrimaryContainer = Color.White,
    secondary = VoiceWaveBlue,
    onSecondary = Color.White,
    tertiary = VoiceWaveCyan,
    background = StudioDarkBg,
    onBackground = Color(0xFFF1F1F3),
    surface = StudioDarkSurface,
    onSurface = Color(0xFFF1F1F3),
    surfaceVariant = StudioDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC7C7CF),
    outline = StudioDarkBorder,
    outlineVariant = Color(0xFF23232C),
    error = Color(0xFFFF5252),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = YtRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE5EA),
    onPrimaryContainer = YtRedDark,
    secondary = VoiceWaveBlue,
    onSecondary = Color.White,
    tertiary = VoiceWaveCyan,
    background = StudioLightBg,
    onBackground = Color(0xFF141416),
    surface = StudioLightSurface,
    onSurface = Color(0xFF141416),
    surfaceVariant = StudioLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF4B4B58),
    outline = StudioLightBorder,
    outlineVariant = Color(0xFFD4D6DD),
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

