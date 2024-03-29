package com.codelab.android.datastore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * These [Colors] are used as the `colors` of our [DataStoreTheme] custom [MaterialTheme] when the
 * system is considered to be in 'dark theme' (ie. it is NOT considered to be in 'light theme').
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
// It is a Compose constant of sorts
private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

/**
 * These [Colors] are used as the `colors` of our [DataStoreTheme] custom [MaterialTheme] when the
 * system is NOT considered to be in 'dark theme' (ie. it is considered to be in 'light theme').
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
 * This is the custom [MaterialTheme] used by this app. It just calls [MaterialTheme] with the
 * arguments:
 *  - `colors`: If [darkTheme] is `true` we pass `DarkColorPalette`, otherwise we pass
 *  `LightColorPalette`. This represents a complete definition of the Material Color theme
 *  for this hierarchy.
 *  - `typography`: We pass it our [Typography] custom [Typography] as the set of text styles
 *  to be used as this hierarchy's typography system.
 *  - `shapes`: We pass it our [Shapes] custom [Shapes] as the set of shapes to be used by
 *  the components in this hierarchy.
 *  - `content`: We pass it our [content] Composable lambda block, ie. the Composable hierarchy that
 *  will be governed by our [DataStoreTheme] custom [MaterialTheme].
 *
 * @param darkTheme `true` is the system considered to be in 'dark theme', defaults to the value
 * returned by [isSystemInDarkTheme].
 * @param content The Composable lambda block that we are wrapping.
 */
@Composable
fun DataStoreTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
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