package com.halam.gallerity.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AppleBlue,
    onPrimary = TextWhite,
    primaryContainer = AppleBlue,
    onPrimaryContainer = TextWhite,
    
    background = ApplePureBlack,
    onBackground = TextWhite,
    
    surface = DarkSurface1,
    onSurface = TextWhite,
    surfaceVariant = DarkSurface2,
    onSurfaceVariant = TextLightTertiary, // Muted text for dark theme

    outline = DarkSurface3,
    outlineVariant = DarkSurface1
)

private val LightColorScheme = lightColorScheme(
    primary = AppleBlue,
    onPrimary = TextWhite,
    primaryContainer = AppleBlue,
    onPrimaryContainer = TextWhite,

    background = AppleLightGray,
    onBackground = AppleNearBlack,

    surface = TextWhite, // Standard floating card inside LightGray background
    onSurface = AppleNearBlack,
    surfaceVariant = AppleLightButton,
    onSurfaceVariant = TextLightTertiary, // Muted text for light theme

    outline = AppleLightBorder,
    outlineVariant = AppleLightBorder
)

@Composable
fun GallerityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We explicitly ignore dynamicColor to enforce the Apple Design System rules
    @Suppress("UNUSED_PARAMETER") dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
