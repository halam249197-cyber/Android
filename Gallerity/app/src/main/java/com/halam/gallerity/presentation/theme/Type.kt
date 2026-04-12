package com.halam.gallerity.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Apple Typography System (Simulating SF Pro optical sizing via Roboto)
// Key rules applied:
// - Tight negative letter-spacing for standard text
// - Extremely tight line-heights for Display sizes (1.07-1.14)
// - Open line-heights for body sizes (1.47)

val Typography = Typography(
    // Display Hero (Matches SF Pro Display 56px)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 56.sp,
        lineHeight = 60.sp, // ~1.07
        letterSpacing = (-0.28).sp
    ),
    // Section Heading (Matches SF Pro Display 40px)
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp,
        lineHeight = 44.sp, // ~1.10
        letterSpacing = 0.sp
    ),
    // Large Nav / Tile Heading
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 50.sp, // 1.47
        letterSpacing = (-0.37).sp
    ),
    // Tile Heading (Matches SF Pro Display 28px)
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 32.sp, // 1.14
        letterSpacing = 0.2.sp
    ),
    // Card Title (Matches SF Pro Display 21px)
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 25.sp, // 1.19
        letterSpacing = 0.23.sp
    ),
    // Nav / standard headings
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.26).sp
    ),
    // Standard Card / Component titles
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.37).sp
    ),
    // Body (Matches SF Pro Text 17px)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 25.sp, // 1.47
        letterSpacing = (-0.37).sp
    ),
    // Body Small / Links (Matches SF Pro Text 14px)
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp, // 1.43
        letterSpacing = (-0.22).sp
    ),
    // Micro text
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp, // 1.33
        letterSpacing = (-0.12).sp
    ),
    // Standard Button (SF Pro Text 17px)
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 40.sp, // relaxed 2.41
        letterSpacing = 0.sp
    ),
    // Labels / secondary buttons
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.22).sp
    ),
    // Captions / Tags
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
)
