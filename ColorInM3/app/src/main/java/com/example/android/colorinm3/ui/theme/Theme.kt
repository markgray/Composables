package com.example.android.colorinm3.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * The [ColorScheme] that is used when the device is considered to be in Light Mode, either
 * [ColorInM3Theme] is called with its `darkTheme` parmeter `false` or if no value for
 * `darkTheme` is passed then [isSystemInDarkTheme] returns `false`.
 */
val LightColorScheme: ColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

/**
 * The [ColorScheme] that is used when the device is considered to be in Dark Mode, either
 * [ColorInM3Theme] is called with its `darkTheme` parmeter `true` or if no value for
 * `darkTheme` is passed then [isSystemInDarkTheme] returns `true`.
 */
val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)


/**
 * This is the custom [MaterialTheme] used to wrap our UI and supply Material 3 default values to
 * the widgets it wraps for [ColorScheme], [Typography], and [Shapes] (we do not specify a custom
 * [Shapes] so the default shapes are used). We start by initializing our [Context] variable
 * `val context` to the [Context] returned by `LocalContext.current`. If [dynamicColor] is `true`
 * and the device is newer than [Build.VERSION_CODES.S] we initialize [ColorScheme] variable
 * `val colors` to [dynamicDarkColorScheme] if [darkTheme] is `true` or to [dynamicLightColorScheme]
 * if it is `false`. When [dynamicColor] is `false` or the device is older than [Build.VERSION_CODES.S]
 * our custom [darkColorScheme] field [DarkColorScheme] is used if [darkTheme] is `true` or else our
 * custom [lightColorScheme] field [LightColorScheme] is used.
 *
 * Finally we call the [MaterialTheme] Composable with its [MaterialTheme.colorScheme] argument our
 * [ColorScheme] `colors` variable, its [MaterialTheme.typography] argument our custom [Typography]
 * class [Typography], and its `content` lambda our Composable [content] parameter.
 *
 * @param darkTheme if `true` a dark [ColorScheme] is used, either [dynamicDarkColorScheme] if
 * the device is newer than [Build.VERSION_CODES.S], or else our custom [darkColorScheme] field
 * [DarkColorScheme] is used if it is older, and if [darkTheme] is `false` a light [ColorScheme] is
 * used, either [dynamicLightColorScheme] if the device is newer than [Build.VERSION_CODES.S], or
 * else our custom [lightColorScheme] field [LightColorScheme] is used if it is older.
 * @param dynamicColor Use a dynamic color scheme if the device is newer than [Build.VERSION_CODES.S]
 * if this is `true`, otherwise use one of our static [ColorScheme]'s.
 * @param content the Composable hierarchy that we are wrapping in order to supply default values.
 *
 */
@Composable
fun ColorInM3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}