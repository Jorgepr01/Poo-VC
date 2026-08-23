package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TacticalColorScheme = darkColorScheme(
    primary = AntiqueBronze,
    onPrimary = DeepSlate,
    primaryContainer = PineGreen,
    onPrimaryContainer = WarmCream,
    secondary = SageOlive,
    onSecondary = DeepSlate,
    secondaryContainer = DarkPineGreen,
    onSecondaryContainer = WarmCream,
    tertiary = WarmCream,
    onTertiary = DeepSlate,
    background = DeepSlate,
    onBackground = WarmCream,
    surface = PineGreen,
    onSurface = WarmCream,
    surfaceVariant = DarkPineGreen,
    onSurfaceVariant = SageOlive,
    outline = SageOlive,
    outlineVariant = DarkSageOlive
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TacticalColorScheme,
        typography = Typography,
        content = content
    )
}
