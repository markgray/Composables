package com.example.examplecomposegrid.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


/**
 * A dark theme color palette for the application.
 *
 * This [Colors] object defines the specific color scheme to be used when the app is in dark mode.
 * It is created using the [darkColors] function from Material Design, which provides sensible
 * defaults for a dark theme, with specific overrides for primary and secondary colors.
 *
 *  - `primary`: The primary brand color, used for prominent elements like app bars and FABs.
 *  - `primaryVariant`: A darker variant of the primary color, used for status bars, for example.
 *  - `secondary`: The secondary brand color, used for accents and selection controls like sliders
 *  and switches.
 */
private val DarkColorPalette: Colors = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

/**
 * A light theme color palette for the application.
 *
 * This [Colors] object defines the specific color scheme to be used when the app is in light mode.
 * It is created using the [lightColors] function from Material Design, which provides sensible
 * defaults for a light theme. This palette overrides the default primary and secondary colors
 * to match the application's brand identity.
 *
 *  - `primary`: The primary brand color, used for prominent elements like app bars and FABs.
 *  - `primaryVariant`: A darker variant of the primary color, used for status bars, for example.
 *  - `secondary`: The secondary brand color, used for accents and selection controls like sliders
 *  and switches.
 */
private val LightColorPalette: Colors = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

/**
 * The main theme for the ExampleComposeGrid application.
 *
 * This Composable function wraps the content of the application and applies a consistent
 * look and feel based on Material Design principles. It determines whether to use a
 * light or dark color palette, and applies the defined typography and shapes.
 *
 * @param darkTheme A boolean flag to determine if the dark theme should be used. By default,
 * it follows the system's dark theme setting via [isSystemInDarkTheme].
 * @param content The Composable content to which this theme will be applied. This is typically
 * the root of your application's UI hierarchy.
 */
@Composable
fun ExampleComposeGridTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}