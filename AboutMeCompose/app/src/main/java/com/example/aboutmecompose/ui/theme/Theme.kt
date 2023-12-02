package com.example.aboutmecompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * These [Colors] are used as the `colors` of our [AboutMeComposeTheme] custom [MaterialTheme] when
 * the system is considered to be in 'dark theme' (ie. it is NOT considered to be in 'light theme').
 *
 *  - `primary` = [Purple200], The primary color is the color displayed most frequently across your
 *  app’s screens and components.
 *  - `primaryVariant` = [Purple700], The primary variant color is used to distinguish two elements
 *  of the app using the primary color, such as the top app bar and the system bar.
 *  - `secondary` = [Teal200], The secondary color provides more ways to accent and distinguish your
 *  product. Secondary colors are best for Floating action buttons, Selection controls, like
 *  checkboxes and radio buttons, Highlighting selected text, Links and headlines.
 *
 * The other colors are left as the default produced by [darkColors]:
 *  - `secondaryVariant` is same as `secondary`, The secondary variant color is used to distinguish
 *  two elements of the app using the secondary color.
 *  - `background` = Color(0xFF121212) almost black, The background color appears behind scrollable
 *  content.
 *  - `surface` = Color(0xFF121212) almost black, The surface color is used on surfaces of components,
 *  such as cards, sheets and menus.
 *  - `error` = Color(0xFFCF6679) The error color is used to indicate error within components, such
 *  as text fields.
 *  - `onPrimary` = [Color.Black] Color used for text and icons displayed on top of the primary color
 *  - `onSecondary` =  [Color.Black] Color used for text and icons displayed on top of the secondary
 *  color.
 *  - `onBackground` = [Color.White], Color used for text and icons displayed on top of the background
 *  color.
 *  - `onSurface` = [Color.White], Color used for text and icons displayed on top of the surface color.
 *  - `onError` = [Color.Black], Color used for text and icons displayed on top of the error color.
 */
private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

/**
 * These [Colors] are used as the `colors` of our [AboutMeComposeTheme] custom [MaterialTheme] when
 * the system is NOT considered to be in 'dark theme' (ie. it is considered to be in 'light theme').
 *
 *  - `primary` = [Purple200], The primary color is the color displayed most frequently across your app’s
 *  screens and components.
 *  - `primaryVariant` = [Purple700], The primary variant color is used to distinguish two elements of the
 *  app using the primary color, such as the top app bar and the system bar.
 *  - `secondary` = [Teal200], The secondary color provides more ways to accent and distinguish your
 *  product. Secondary colors are best for Floating action buttons, Selection controls, like checkboxes
 *  and radio buttons, Highlighting selected text, Links and headlines.
 *
 * The other colors are left as the default produced by [lightColors]:
 * - `secondaryVariant` = `Color(0xFF018786)` The secondary variant color is used to distinguish two
 * elements of the app using the secondary color.
 *  - `background` = [Color.White], The background color appears behind scrollable content.
 *  - `surface` = [Color.White], The surface color is used on surfaces of components, such as cards,
 *  sheets and menus.
 *  - `error` = `Color(0xFFB00020)` The error color is used to indicate error within components,
 *  such as text fields.
 *  - `onPrimary` = [Color.White] Color used for text and icons displayed on top of the primary color.
 *  - `onSecondary` = [Color.Black] Color used for text and icons displayed on top of the secondary
 *  color.
 *  - `onBackground` = [Color.Black], Color used for text and icons displayed on top of the
 *  background color.
 *  - `onSurface` = [Color.Black], Color used for text and icons displayed on top of the surface color.
 *  - `onError` = [Color.White], Color used for text and icons displayed on top of the error color.
 */
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
 * This is the custom [MaterialTheme] we use to wrap the composables used in our UI.
 *
 * @param darkTheme if `true` we should use our [DarkColorPalette] for the `colors` of our theme,
 * and if `false` we should use our [LightColorPalette]
 * @param content the `Composable` we are wrapping in our custom [MaterialTheme]
 */
@Composable
fun AboutMeComposeTheme(
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