package com.anantborad.act.core.ui.theme

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
    primary = BlueprintBlue,
    onPrimary = Color.White,
    secondary = MutedForeground,
    tertiary = EnglishRose,
    background = DarkPrimary,
    surface = DarkPrimary,
    onBackground = Color.White,
    onSurface = Color.White,
    outline = MutedForeground
)

private val LightColorScheme = lightColorScheme(
    primary = BlueprintBlue,
    onPrimary = Color.White,
    secondary = MutedForeground,
    tertiary = EnglishRose,
    background = BackgroundSlate,
    surface = Color.White,
    onBackground = DarkPrimary,
    onSurface = DarkPrimary,
    outline = BorderSlate,
    surfaceVariant = MutedSlate,
    onSurfaceVariant = MutedForeground
)

@Composable
fun ACTTheme(
    darkTheme: Boolean = false, // Force light mockup theme for consistent UI display
    dynamicColor: Boolean = false, // Disabled for exact mockup look
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
