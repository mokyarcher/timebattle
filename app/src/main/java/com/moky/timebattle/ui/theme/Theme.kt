package com.moky.timebattle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val DarkColorScheme = darkColorScheme(
    primary = LifeRed,
    onPrimary = WarmWhite,
    primaryContainer = DarkRed,
    onPrimaryContainer = WarmWhite,
    secondary = MutedWhite,
    onSecondary = WarmWhite,
    background = AbyssBlack,
    onBackground = WarmWhite,
    surface = CarbonGrey,
    onSurface = WarmWhite,
    surfaceVariant = DeepGrey,
    onSurfaceVariant = MutedWhite,
    outline = StrokeLight,
    outlineVariant = StrokeRed,
    error = LifeRedGlow,
    onError = WarmWhite
)

@Composable
fun TimebattleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // 强制暗色主题，无视系统主题
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
