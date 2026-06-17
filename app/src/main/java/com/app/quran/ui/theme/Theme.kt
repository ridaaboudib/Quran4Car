package com.app.quran.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Theme Types
enum class ThemeType {
    LIGHT,      // Default Cream
    DARK,       // Dark mode
    ISLAMIC_GREEN,
    ROYAL_GOLD
}

// Extended Color definitions
object ExtendedColors {
    // Light Theme
    val LightPrimary = Color(0xFF1B4D3E)
    val LightOnPrimary = Color(0xFFFFFFFF)
    val LightPrimaryContainer = Color(0xFF2A7A5F)
    val LightOnPrimaryContainer = Color(0xFFFFFFFF)
    val LightSecondary = Color(0xFFD4AF37)
    val LightOnSecondary = Color(0xFF2C3E50)
    val LightSecondaryContainer = Color(0xFFE6C555)
    val LightOnSecondaryContainer = Color(0xFF2C3E50)
    val LightTertiary = Color(0xFF1ABC9C)
    val LightOnTertiary = Color(0xFFFFFFFF)
    val LightBackground = Color(0xFFF7F5F0)
    val LightOnBackground = Color(0xFF2C3E50)
    val LightSurface = Color(0xFFFFFFFF)
    val LightOnSurface = Color(0xFF2C3E50)
    val LightSurfaceVariant = Color(0xFFF0EDE5)
    val LightOnSurfaceVariant = Color(0xFF7F8C8D)
    val LightError = Color(0xFFE74C3C)
    val LightOnError = Color(0xFFFFFFFF)

    // Dark Theme
    val DarkPrimary = Color(0xFF2A7A5F)
    val DarkOnPrimary = Color(0xFFFFFFFF)
    val DarkPrimaryContainer = Color(0xFF1B4D3E)
    val DarkOnPrimaryContainer = Color(0xFFFFFFFF)
    val DarkSecondary = Color(0xFFE6C555)
    val DarkOnSecondary = Color(0xFF2C3E50)
    val DarkSecondaryContainer = Color(0xFFD4AF37)
    val DarkOnSecondaryContainer = Color(0xFF2C3E50)
    val DarkTertiary = Color(0xFF48D6AD)
    val DarkOnTertiary = Color(0xFFFFFFFF)
    val DarkBackground = Color(0xFF1A1A1A)
    val DarkOnBackground = Color(0xFFFFFFFF)
    val DarkSurface = Color(0xFF2D2D2D)
    val DarkOnSurface = Color(0xFFFFFFFF)
    val DarkSurfaceVariant = Color(0xFF3D3D3D)
    val DarkOnSurfaceVariant = Color(0xFFB0B0B0)
    val DarkError = Color(0xFFE74C3C)
    val DarkOnError = Color(0xFFFFFFFF)

    // Islamic Green Theme
    val GreenPrimary = Color(0xFF00A86B)
    val GreenOnPrimary = Color(0xFFFFFFFF)
    val GreenPrimaryContainer = Color(0xFF00695C)
    val GreenOnPrimaryContainer = Color(0xFFE8F5E9)
    val GreenSecondary = Color(0xFFFFD700)
    val GreenOnSecondary = Color(0xFF2C3E50)
    val GreenSecondaryContainer = Color(0xFFFFE082)
    val GreenOnSecondaryContainer = Color(0xFF2C3E50)
    val GreenTertiary = Color(0xFF69F0AE)
    val GreenOnTertiary = Color(0xFF2C3E50)
    val GreenBackground = Color(0xFF004D40)
    val GreenOnBackground = Color(0xFFE8F5E9)
    val GreenSurface = Color(0xFF00695C)
    val GreenOnSurface = Color(0xFFE8F5E9)
    val GreenSurfaceVariant = Color(0xFF00796B)
    val GreenOnSurfaceVariant = Color(0xFF80CBC4)
    val GreenError = Color(0xFFFF5252)
    val GreenOnError = Color(0xFFFFFFFF)

    // Royal Gold Theme
    val GoldPrimary = Color(0xFFB8860B)
    val GoldOnPrimary = Color(0xFFFFFFFF)
    val GoldPrimaryContainer = Color(0xFFD4AF37)
    val GoldOnPrimaryContainer = Color(0xFFFFF8E1)
    val GoldSecondary = Color(0xFFFFD700)
    val GoldOnSecondary = Color(0xFF2C3E50)
    val GoldSecondaryContainer = Color(0xFFFFE082)
    val GoldOnSecondaryContainer = Color(0xFF2C3E50)
    val GoldTertiary = Color(0xFFFFC107)
    val GoldOnTertiary = Color(0xFF2C3E50)
    val GoldBackground = Color(0xFF2C2416)
    val GoldOnBackground = Color(0xFFFFF8E1)
    val GoldSurface = Color(0xFF3D3522)
    val GoldOnSurface = Color(0xFFFFF8E1)
    val GoldSurfaceVariant = Color(0xFF4D4532)
    val GoldOnSurfaceVariant = Color(0xFFD4AF37)
    val GoldError = Color(0xFFFF5252)
    val GoldOnError = Color(0xFFFFFFFF)
}

private fun getLightColorScheme(): ColorScheme {
    return ColorScheme(
        primary = ExtendedColors.LightPrimary,
        onPrimary = ExtendedColors.LightOnPrimary,
        primaryContainer = ExtendedColors.LightPrimaryContainer,
        onPrimaryContainer = ExtendedColors.LightOnPrimaryContainer,
        secondary = ExtendedColors.LightSecondary,
        onSecondary = ExtendedColors.LightOnSecondary,
        secondaryContainer = ExtendedColors.LightSecondaryContainer,
        onSecondaryContainer = ExtendedColors.LightOnSecondaryContainer,
        tertiary = ExtendedColors.LightTertiary,
        onTertiary = ExtendedColors.LightOnTertiary,
        background = ExtendedColors.LightBackground,
        onBackground = ExtendedColors.LightOnBackground,
        surface = ExtendedColors.LightSurface,
        onSurface = ExtendedColors.LightOnSurface,
        surfaceVariant = ExtendedColors.LightSurfaceVariant,
        onSurfaceVariant = ExtendedColors.LightOnSurfaceVariant,
        error = ExtendedColors.LightError,
        onError = ExtendedColors.LightOnError
    )
}

private fun getDarkColorScheme(): ColorScheme {
    return ColorScheme(
        primary = ExtendedColors.DarkPrimary,
        onPrimary = ExtendedColors.DarkOnPrimary,
        primaryContainer = ExtendedColors.DarkPrimaryContainer,
        onPrimaryContainer = ExtendedColors.DarkOnPrimaryContainer,
        secondary = ExtendedColors.DarkSecondary,
        onSecondary = ExtendedColors.DarkOnSecondary,
        secondaryContainer = ExtendedColors.DarkSecondaryContainer,
        onSecondaryContainer = ExtendedColors.DarkOnSecondaryContainer,
        tertiary = ExtendedColors.DarkTertiary,
        onTertiary = ExtendedColors.DarkOnTertiary,
        background = ExtendedColors.DarkBackground,
        onBackground = ExtendedColors.DarkOnBackground,
        surface = ExtendedColors.DarkSurface,
        onSurface = ExtendedColors.DarkOnSurface,
        surfaceVariant = ExtendedColors.DarkSurfaceVariant,
        onSurfaceVariant = ExtendedColors.DarkOnSurfaceVariant,
        error = ExtendedColors.DarkError,
        onError = ExtendedColors.DarkOnError
    )
}

private fun getIslamicGreenColorScheme(): ColorScheme {
    return ColorScheme(
        primary = ExtendedColors.GreenPrimary,
        onPrimary = ExtendedColors.GreenOnPrimary,
        primaryContainer = ExtendedColors.GreenPrimaryContainer,
        onPrimaryContainer = ExtendedColors.GreenOnPrimaryContainer,
        secondary = ExtendedColors.GreenSecondary,
        onSecondary = ExtendedColors.GreenOnSecondary,
        secondaryContainer = ExtendedColors.GreenSecondaryContainer,
        onSecondaryContainer = ExtendedColors.GreenOnSecondaryContainer,
        tertiary = ExtendedColors.GreenTertiary,
        onTertiary = ExtendedColors.GreenOnTertiary,
        background = ExtendedColors.GreenBackground,
        onBackground = ExtendedColors.GreenOnBackground,
        surface = ExtendedColors.GreenSurface,
        onSurface = ExtendedColors.GreenOnSurface,
        surfaceVariant = ExtendedColors.GreenSurfaceVariant,
        onSurfaceVariant = ExtendedColors.GreenOnSurfaceVariant,
        error = ExtendedColors.GreenError,
        onError = ExtendedColors.GreenOnError
    )
}

private fun getRoyalGoldColorScheme(): ColorScheme {
    return ColorScheme(
        primary = ExtendedColors.GoldPrimary,
        onPrimary = ExtendedColors.GoldOnPrimary,
        primaryContainer = ExtendedColors.GoldPrimaryContainer,
        onPrimaryContainer = ExtendedColors.GoldOnPrimaryContainer,
        secondary = ExtendedColors.GoldSecondary,
        onSecondary = ExtendedColors.GoldOnSecondary,
        secondaryContainer = ExtendedColors.GoldSecondaryContainer,
        onSecondaryContainer = ExtendedColors.GoldOnSecondaryContainer,
        tertiary = ExtendedColors.GoldTertiary,
        onTertiary = ExtendedColors.GoldOnTertiary,
        background = ExtendedColors.GoldBackground,
        onBackground = ExtendedColors.GoldOnBackground,
        surface = ExtendedColors.GoldSurface,
        onSurface = ExtendedColors.GoldOnSurface,
        surfaceVariant = ExtendedColors.GoldSurfaceVariant,
        onSurfaceVariant = ExtendedColors.GoldOnSurfaceVariant,
        error = ExtendedColors.GoldError,
        onError = ExtendedColors.GoldOnError
    )
}

@Composable
fun QuranBluetoothTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeType: ThemeType = if (darkTheme) ThemeType.DARK else ThemeType.LIGHT,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeType == ThemeType.DARK -> getDarkColorScheme()
        themeType == ThemeType.ISLAMIC_GREEN -> getIslamicGreenColorScheme()
        themeType == ThemeType.ROYAL_GOLD -> getRoyalGoldColorScheme()
        else -> getLightColorScheme()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = themeType != ThemeType.DARK
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}