package com.example.ui.theme

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
    primary = AmberPrimaryDark,
    onPrimary = Color(0xFF422200),
    primaryContainer = Color(0xFF5E3516),
    onPrimaryContainer = Color(0xFFFFDCBE),
    secondary = AmberSecondaryDark,
    onSecondary = Color(0xFF3B2E2A),
    secondaryContainer = Color(0xFF524440),
    onSecondaryContainer = Color(0xFFEDE0DC),
    tertiary = AmberTertiaryDark,
    onTertiary = Color(0xFF00390E),
    background = DarkBackground,
    onBackground = Color(0xFFEBE0D8),
    surface = DarkSurface,
    onSurface = Color(0xFFEBE0D8),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD4C4B7)
)

private val LightColorScheme = lightColorScheme(
    primary = AmberPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDCC1),
    onPrimaryContainer = Color(0xFF331500),
    secondary = AmberSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3ECE4),
    onSecondaryContainer = Color(0xFF261A16),
    tertiary = AmberTertiaryLight,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = Color(0xFF201A17),
    surface = LightSurface,
    onSurface = Color(0xFF201A17),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF50453F)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep warm literary theme distinct
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
