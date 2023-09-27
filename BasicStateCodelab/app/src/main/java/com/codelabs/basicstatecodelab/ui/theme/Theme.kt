package com.codelabs.basicstatecodelab.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Suppress("PrivatePropertyName") // It is a Compose constant of sorts
private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

@Suppress("PrivatePropertyName") // It is a Compose constant of sorts
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
 * This is the custom [MaterialTheme] used by our `BasicStateCodelab`. It wraps its `content` Composable
 * lambda, and the material widgets of the lambda will use the `colors`, [Typography], and [Shapes]
 * we suggest as their defaults.
 *
 * @param darkTheme if `true` the system is considered to be in 'dark theme' and we will use our
 * [DarkColorPalette] as the `colors` of our [MaterialTheme], and if `false` we will use our
 * [LightColorPalette] as the `colors` of our [MaterialTheme]
 * @param content the Composable lambda we are wrapping, it is passed as the `content` parameter
 * of [MaterialTheme].
 */
@Composable
fun BasicStateCodelabTheme(
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