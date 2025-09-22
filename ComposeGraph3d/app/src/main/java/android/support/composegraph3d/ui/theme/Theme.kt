package android.support.composegraph3d.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

// It is a Compose constant of sorts
private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

// It is a Compose constant of sorts
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
 * This function sets up the [MaterialTheme] with the appropriate color palette (dark or light),
 * typography, and shapes defined for the `ComposeGraph3d` application.
 *
 * @param darkTheme Whether the dark theme should be used. Defaults to the system's dark
 * theme setting.
 * @param content The Composable content to which the theme will be applied.
 */
@Composable
fun ComposeGraph3dTheme(
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