package com.halam.gallerity.presentation.theme

import androidx.compose.ui.graphics.Color

// Apple Design System Colors
val AppleBlue = Color(0xFF0071e3)
val AppleBlueDarkBg = Color(0xFF2997ff) // Slightly brighter on dark bg
val AppleLinkBlue = Color(0xFF0066cc)

// Light Theme Neutrals
val AppleLightGray = Color(0xFFf5f5f7)
val AppleNearBlack = Color(0xFF1d1d1f)
val AppleLightButton = Color(0xFFfafafc)
val AppleLightBorder = Color(0x0A000000) // rgba(0, 0, 0, 0.04)

// Dark Theme Neutrals
val ApplePureBlack = Color(0xFF000000)
val DarkSurface1 = Color(0xFF272729)
val DarkSurface2 = Color(0xFF262628)
val DarkSurface3 = Color(0xFF28282a)

// Text Colors
val TextWhite = Color(0xFFffffff)
val TextLightSecondary = Color(0xCC000000) // rgba(0, 0, 0, 0.8)
val TextLightTertiary = Color(0x7A000000) // rgba(0, 0, 0, 0.48)
val OverlayScrim = Color(0xA3D2D2D7) // rgba(210, 210, 215, 0.64)

// Legacy compatibility (repurposed to Apple colors to prevent compilation errors before we fully swap)
val Purple80 = AppleBlueDarkBg
val PurpleGrey80 = DarkSurface1
val Pink80 = ApplePureBlack
val DarkBackground = ApplePureBlack

val Purple40 = AppleBlue
val PurpleGrey40 = AppleLightGray
val Pink40 = AppleLightGray
val LightBackground = AppleLightGray
