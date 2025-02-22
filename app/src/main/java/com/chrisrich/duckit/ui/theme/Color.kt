package com.chrisrich.duckit.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val PrimaryGreen = Color(0xFF45C066)  // Main Green
val SecondaryGreen = Color(0xFF2A8B4F) // Darker Green for accents
val TertiaryGreen = Color(0xFF74D99E) // Lighter Green for highlights
val BackgroundColor = Color(0xFFF5FFF5) // Light Greenish Background
val SurfaceColor = Color(0xFFFFFFFF) // White for clean UI
val OnPrimaryColor = Color(0xFFFFFFFF) // White text on primary
val OnBackgroundColor = Color(0xFF1B5E20) // Dark green text

val DarkBackgroundColor = Color(0xFF1B5E20) // Dark Green
val DarkSurfaceColor = Color(0xFF2A8B4F) // Darker Surface
val OnDarkPrimaryColor = Color(0xFFFFFFFF) // White text on primary

val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGreen,
    tertiary = TertiaryGreen,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = OnPrimaryColor,
    onBackground = OnBackgroundColor
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGreen,
    tertiary = TertiaryGreen,
    background = DarkBackgroundColor,
    surface = DarkSurfaceColor,
    onPrimary = OnDarkPrimaryColor,
    onBackground = Color.White
)