package com.app.quran.ui.theme

import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1B4D3E),
    secondary = Color(0xFFD4AF37),
    background = Color(0xFFF7F5F0),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF2C3E50),
    tertiary = Color(0xFF1ABC9C)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2A7A5F),
    secondary = Color(0xFFE6C555),
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF2D2D2D),
    onBackground = Color(0xFFFFFFFF),
    tertiary = Color(0xFF48D6AD)
)

@Composable
fun QuranBluetoothPlayerTheme(
    darkTheme: Boolean = isSystemInDarkMode(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
