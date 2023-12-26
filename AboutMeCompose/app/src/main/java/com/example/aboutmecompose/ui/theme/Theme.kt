package com.example.aboutmecompose.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * The [ColorScheme] that is used when the device is considered to be in Light Mode, either
 * [AboutMeComposeTheme] is called with its `useDarkTheme` parmeter `false` or if no value for
 * `useDarkTheme` is passed then [isSystemInDarkTheme] returns `false`.
 */
private val LightColors = lightColorScheme(
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
 * [AboutMeComposeTheme] is called with its `useDarkTheme` parmeter `true` or if no value for
 * `useDarkTheme` is passed then [isSystemInDarkTheme] returns `true`.
 */
private val DarkColors = darkColorScheme(
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
 * the widgets it wraps for [ColorScheme], [Typography], and [Shapes]. We start by initializing our
 * [Context] variable `val context` to the [Context] returned by `LocalContext.current`. Then we
 * initialize [ColorScheme] variable `val colors` to [dynamicDarkColorScheme] the device is newer
 * than [Build.VERSION_CODES.S] and [useDarkTheme] is `true` or to [dynamicLightColorScheme] if it
 * is `false`. When the device is older than [Build.VERSION_CODES.S] our custom [darkColorScheme]
 * field [DarkColors] is used if [useDarkTheme] is `true` or else our custom [lightColorScheme]
 * field [LightColors] is used. We initialized our [View] variable `val view` to the current Compose
 * [View] then if the [View.isInEditMode] method of `view` returns `true` (a [View] is usually in
 * edit mode when displayed within a developer tool) we lauch a [SideEffect] that schedules its
 * `effect` lambda to run when the current composition completes successfully and applies changes.
 * The lambda initializes its [Window] variable `val window` to the current [Window] for the activity,
 * then it calls the [Window.setStatusBarColor] method (kotlin `statusBarColor` property) to set the
 * the color of the status bar to the [ColorScheme.primary] color of `colors` (after converting it
 * to to an ARGB color [Int]), and use the [WindowCompat.getInsetsController] to fetch the single
 * [WindowInsetsControllerCompat] of the window this `view` is attached to call its
 * [WindowInsetsControllerCompat.setAppearanceLightStatusBars] method (kotlin
 * `isAppearanceLightStatusBars` property) to the value of [useDarkTheme].
 *
 * Finally we call the [MaterialTheme] Composable with its [MaterialTheme.colorScheme] argument our
 * [ColorScheme] `colors` variable, its [MaterialTheme.typography] argument our custom [Typography]
 * class [typography], its [MaterialTheme.shapes] argument our custom [Shapes] class [shapes] and
 * its `content` lambda our Composable [content] parameter.
 *
 * @param useDarkTheme if `true` a dark [ColorScheme] is used, either [dynamicDarkColorScheme] if
 * the device is newer than [Build.VERSION_CODES.S], or else our custom [darkColorScheme] field
 * [DarkColors] is used if it is older, and if [useDarkTheme] is `false` a light [ColorScheme] is
 * used, either [dynamicLightColorScheme] if the device is newer than [Build.VERSION_CODES.S], or
 * else our custom [lightColorScheme] field [LightColors] is used if it is older. Our callers do not
 * pass a value so the result of calling [isSystemInDarkTheme] is used instead.
 * @param content the Composable hierarchy that we are wrapping in order to supply default values.
 *
 */
@Composable
fun AboutMeComposeTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context: Context = LocalContext.current
    val colors: ColorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        useDarkTheme -> DarkColors
        else -> LightColors
    }
    // Add primary status bar color from chosen color scheme.
    val view: View = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window: Window = (view.context as Activity).window
            window.statusBarColor = colors.primary.toArgb()
            WindowCompat
                .getInsetsController(window, view)
                .isAppearanceLightStatusBars = useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
