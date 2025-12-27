package com.example.technews.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
        darkColorScheme(
                primary = PrimaryDark,
                onPrimary = OnPrimaryDark,
                primaryContainer = PrimaryVariantDark,
                onPrimaryContainer = OnSurfaceDark,
                secondary = SecondaryDark,
                onSecondary = OnSecondaryDark,
                secondaryContainer = SecondaryVariantDark,
                onSecondaryContainer = OnSurfaceDark,
                tertiary = GradientMiddleDark,
                onTertiary = OnPrimaryDark,
                background = BackgroundDark,
                onBackground = OnBackgroundDark,
                surface = SurfaceDark,
                onSurface = OnSurfaceDark,
                surfaceVariant = SurfaceVariantDark,
                onSurfaceVariant = OnSurfaceVariantDark,
                error = ErrorDark,
                onError = OnErrorDark,
                outline = CardBorderDark
        )

private val LightColorScheme =
        lightColorScheme(
                primary = Primary,
                onPrimary = OnPrimary,
                primaryContainer = PrimaryVariant,
                onPrimaryContainer = OnSurface,
                secondary = Secondary,
                onSecondary = OnSecondary,
                secondaryContainer = SecondaryVariant,
                onSecondaryContainer = OnSurface,
                tertiary = GradientMiddle,
                onTertiary = OnPrimary,
                background = Background,
                onBackground = OnBackground,
                surface = Surface,
                onSurface = OnSurface,
                surfaceVariant = SurfaceVariant,
                onSurfaceVariant = OnSurfaceVariant,
                error = Error,
                onError = OnError,
                outline = CardBorder
        )

@Composable
fun TechNewsTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
        val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

        val view = LocalView.current
        if (!view.isInEditMode) {
                SideEffect {
                        val window = (view.context as Activity).window
                        // Make status bar match the background for a seamless look
                        window.statusBarColor =
                                if (darkTheme) {
                                        BackgroundDark.toArgb()
                                } else {
                                        Primary.toArgb()
                                }
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                                !darkTheme
                }
        }

        MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
