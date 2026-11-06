package com.example.moviesapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CinematicDarkColorScheme = darkColorScheme(
    primary = CinemaGold,
    onPrimary = CinemaBackground,
    primaryContainer = CinemaSurfaceVariant,
    onPrimaryContainer = CinemaGold,
    secondary = CinemaCyan,
    onSecondary = CinemaBackground,
    secondaryContainer = CinemaSurfaceVariant,
    onSecondaryContainer = CinemaCyan,
    tertiary = CinemaRed,
    onTertiary = TextPrimary,
    background = CinemaBackground,
    onBackground = TextPrimary,
    surface = CinemaSurface,
    onSurface = TextPrimary,
    surfaceVariant = CinemaSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = CinemaRed,
    onError = TextPrimary
)

@Composable
fun MoviesAppTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = CinematicDarkColorScheme,
        typography = Typography,
        content = content
    )
}