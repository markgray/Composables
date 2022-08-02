package com.example.dessertclickercompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
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
 * Our custom [MaterialTheme].
 *
 * @param darkTheme flag which if `true` indicates that we are to use our [DarkColorPalette] palette
 * of [darkColors] as the `colors` of our [MaterialTheme], and if `false` indicates that we are to
 * use our [LightColorPalette] palette of [lightColors] for the complete definition of the Material
 * Color theme for this hierarchy.
 * @param content the Composable lambda we are to wrap in our theme.
 */
@Composable
fun DessertClickerComposeTheme(
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