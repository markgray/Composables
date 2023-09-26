/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * These [Colors] are used as the `colors` of our [JetnewsTheme] custom [MaterialTheme] when the
 * system is NOT considered to be in 'dark theme' (ie. it is considered to be in 'light theme').
 *
 *  - `primary` = [Red700], The primary color is the color displayed most frequently across your app’s
 *  screens and components.
 *  - `primaryVariant` = [Red900], The primary variant color is used to distinguish two elements of the
 *  app using the primary color, such as the top app bar and the system bar.
 *  - `onPrimary` = [Color.White], Color used for text and icons displayed on top of the primary color.
 *  - `secondary` = [Red700], The secondary color provides more ways to accent and distinguish your
 *  product. Secondary colors are best for Floating action buttons, Selection controls, like checkboxes
 *  and radio buttons, Highlighting selected text, Links and headlines.
 *  - `secondaryVariant` = [Red900], The secondary variant color is used to distinguish two elements
 *  of the app using the secondary color.
 *  - `onSecondary` = [Color.White], Color used for text and icons displayed on top of the secondary
 *  color.
 *  - `error` = [Red800], The error color is used to indicate error within components, such as text
 *  fields.
 *
 * The other colors are left as the default produced by [lightColors]:
 *  - `background` = [Color.White], The background color appears behind scrollable content.
 *  - `surface` = [Color.White], The surface color is used on surfaces of components, such as cards,
 *  sheets and menus.
 *  - `onBackground` = [Color.Black], Color used for text and icons displayed on top of the
 *  background color.
 *  - `onSurface` = [Color.Black], Color used for text and icons displayed on top of the surface color.
 *  - `onError` = [Color.White], Color used for text and icons displayed on top of the error color.
 */
@Suppress("PrivatePropertyName") // It is a Compose constant of sorts
private val LightThemeColors: Colors = lightColors(
    primary = Red700,
    primaryVariant = Red900,
    onPrimary = Color.White,
    secondary = Red700,
    secondaryVariant = Red900,
    onSecondary = Color.White,
    error = Red800
)

/**
 * These [Colors] are used as the `colors` of our [JetnewsTheme] custom [MaterialTheme] when the
 * system is considered to be in 'dark theme' (ie. it is NOT considered to be in 'light theme').
 *
 *  - `primary` = [Red300], The primary color is the color displayed most frequently across your
 *  app’s screens and components.
 *  - `primaryVariant` = [Red700], The primary variant color is used to distinguish two elements of the
 * app using the primary color, such as the top app bar and the system bar.
 *  - `onPrimary` = [Color.Black], Color used for text and icons displayed on top of the primary color.
 *  - `secondary` = [Red300], The secondary color provides more ways to accent and distinguish your
 *  product. Secondary colors are best for Floating action buttons, Selection controls, like checkboxes
 *  and radio buttons, Highlighting selected text, Links and headlines.
 *  - `onSecondary` = [Color.Black], Color used for text and icons displayed on top of the secondary
 *  color.
 *  - `error` = [Red200], The error color is used to indicate error within components, such as text
 *  fields.
 *
 * The other colors are left as the default produced by [darkColors]:
 *  - `secondaryVariant` is same as `secondary`, The secondary variant color is used to distinguish
 *  two elements of the app using the secondary color.
 *  - `background` = Color(0xFF121212) almost black, The background color appears behind scrollable
 *  content.
 *  - `surface` = Color(0xFF121212) almost black, The surface color is used on surfaces of components,
 *  such as cards, sheets and menus.
 *  - `onBackground` = [Color.White], Color used for text and icons displayed on top of the background
 *  color.
 *  - `onSurface` = [Color.White], Color used for text and icons displayed on top of the surface color.
 *  - `onError` = [Color.Black], Color used for text and icons displayed on top of the error color.
 */
@Suppress("PrivatePropertyName") // It is a Compose constant of sorts
private val DarkThemeColors: Colors = darkColors(
    primary = Red300,
    primaryVariant = Red700,
    onPrimary = Color.Black,
    secondary = Red300,
    onSecondary = Color.Black,
    error = Red200
)

/**
 * This is the custom [MaterialTheme] used by this app. It just calls [MaterialTheme] with the
 * arguments:
 *  - `colors`: If [darkTheme] is `true` we pass [DarkThemeColors], otherwise we pass [LightThemeColors].
 *  This represents a complete definition of the Material Color theme for this hierarchy.
 *  - `typography`: We pass it our [JetnewsTypography] custom [Typography] as the set of text styles
 *  to be used as this hierarchy's typography system.
 *  - `shapes`: We pass it our [JetnewsShapes] custom [Shapes] as the set of shapes to be used by
 *  the components in this hierarchy.
 *  - `content`: We pass it our [content] Composable lambda block, ie. the Composable hierarchy that
 *  will be governed by our [JetnewsTheme] custom [MaterialTheme].
 *
 * @param darkTheme `true` is the system considered to be in 'dark theme', defaults to the value
 * returned by [isSystemInDarkTheme].
 * @param content The Composable lambda block that we are wrapping.
 */
@Composable
fun JetnewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        typography = JetnewsTypography,
        shapes = JetnewsShapes,
        content = content
    )
}
