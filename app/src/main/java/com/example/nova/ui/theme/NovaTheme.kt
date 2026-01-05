package com.example.nova.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// Nova Enterprise Colors (Monotone)
val NovaLightBackground = Color(0xFFF5F5F7) // Light Grey Base
val NovaLightSurface = Color(0xFFFFFFFF)
val NovaEnterpriseBlue = Color(0xFF0052CC) // Enterprise Blue (Trust/Professional)
val NovaGraphite = Color(0xFF333333) // Dark Grey (Text/Icons)
val NovaSubtleGrey = Color(0xFF888888) // Secondary Text
val NovaReviewYellow = Color(0xFFFFD600) // Keep for warnings if needed

// Legacy / Utility Colors
val NovaBlack = Color(0xFF000000)
val NovaDarkGrey = Color(0xFF1E1E1E)

val NovaGlassWhite = Color(0xEEFFFFFF) // Higher opacity for enterprise (cleaner)
val NovaGlassBorder = Color(0x33000000) // Subtle dark border

private val LightColorScheme = lightColorScheme(
    primary = NovaEnterpriseBlue, // Blue accent
    secondary = NovaEnterpriseBlue,
    tertiary = NovaGraphite,
    background = NovaLightBackground,
    surface = NovaLightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = NovaGraphite,
    onSurface = NovaGraphite,
)

// Legacy Dark (Not primarily used in this reconstruction)
private val DarkColorScheme = darkColorScheme(
    primary = NovaEnterpriseBlue,
    background = Color(0xFF101010),
    surface = Color(0xFF1E1E1E),
)

@Composable
fun NovaTheme(
    darkTheme: Boolean = false, // Default to Light as per request
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic to enforce brand
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb() // Transparent for glass effect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

// Glassmorphism Modifier (Enterprise Refined)
fun Modifier.glass(
    cornerRadius: Dp = 12.dp, // Slightly sharper corners
    alpha: Float = 0.9f // Higher opacity for readability
): Modifier = this
    .shadow(4.dp, RoundedCornerShape(cornerRadius), ambientColor = Color.LightGray, spotColor = Color.Gray)
    .background(Color.White.copy(alpha = alpha), RoundedCornerShape(cornerRadius))
    .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(cornerRadius)) // Subtle border
    .clip(RoundedCornerShape(cornerRadius))
