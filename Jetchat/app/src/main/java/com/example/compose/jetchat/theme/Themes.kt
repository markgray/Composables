/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetchat.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * The [ColorScheme] used when the device is considered to be in "Dark Theme". The [Color]'s use are:
 *
 *  - `primary` The primary color is the color displayed most frequently across your app’s screens
 *  and components, we use [Blue80] for our [darkColorScheme]
 *  - `onPrimary` Color used for text and icons displayed on top of the primary color, we use [Blue20]
 *  for our [darkColorScheme]
 *  - `primaryContainer` The preferred tonal color of containers, we use [Blue30] for our [darkColorScheme]
 *  - `onPrimaryContainer` The color (and state variants) that should be used for content on top of
 *  `primaryContainer`, we use [Blue90] for our [darkColorScheme].
 *  - `inversePrimary` Color to be used as a "primary" color in places where the inverse color scheme
 *  is needed, such as the button on a SnackBar, we use [Blue40]
 *  - `secondary` The secondary color provides more ways to accent and distinguish your product.
 *  Secondary colors are best for: Floating action buttons, Selection controls, like checkboxes and
 *  radio buttons, Highlighting selected text, Links and headlines, we use [DarkBlue80]
 *  - `onSecondary` Color used for text and icons displayed on top of the secondary color, we use [DarkBlue20].
 *  - `secondaryContainer` A tonal color to be used in containers, we use [DarkBlue30]
 *  - `onSecondaryContainer` The color (and state variants) that should be used for content on top
 *  of secondaryContainer, we use [DarkBlue90]
 *  - `tertiary` The tertiary color that can be used to balance primary and secondary colors, or
 *  bring heightened attention to an element such as an input field, we use [Yellow80]
 *  - `onTertiary` Color used for text and icons displayed on top of the tertiary color, we use [Yellow20]
 *  - `tertiaryContainer` A tonal color to be used in containers, we use [Yellow30]
 *  - `onTertiaryContainer` The color (and state variants) that should be used for content on top of
 *  tertiaryContainer, we use [Yellow90]
 *  - `error` The error color is used to indicate errors in components, such as invalid text in a text
 *  field, we use [Red80]
 *  - `onError` Color used for text and icons displayed on top of the error color, we use [Red20]
 *  - `errorContainer` The preferred tonal color of error containers, we use [Red30]
 *  - `onErrorContainer` The color (and state variants) that should be used for content on top of
 *  errorContainer, we use [Red90]
 *  - `background` The background color that appears behind scrollable content, we use [Grey10]
 *  - `onBackground` Color used for text and icons displayed on top of the background color, we use [Grey90]
 *  - `surface` The surface color that affect surfaces of components, such as cards, sheets, and menus,
 *  we use [Grey10]
 *  - `onSurface`  used for text and icons displayed on top of the surface color, we use [Grey80]
 *  - `inverseSurface` A color that contrasts sharply with surface. Useful for surfaces that sit on
 *  top of other surfaces with surface color, we use [Grey90]
 *  - `inverseOnSurface` A color that contrasts well with inverseSurface. Useful for content that
 *  sits on top of containers that are inverseSurface, we use [Grey20]
 *  - `surfaceVariant` Another option for a color with similar uses of surface, we use [BlueGrey30]
 *  - `onSurfaceVariant` The color (and state variants) that can be used for content on top of surface,
 *  we use [BlueGrey80].
 *  - `outline` Subtle color used for boundaries. Outline color role adds contrast for accessibility
 *  purposes, we use [BlueGrey60].
 */
private val JetchatDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    inversePrimary = Blue40,
    secondary = DarkBlue80,
    onSecondary = DarkBlue20,
    secondaryContainer = DarkBlue30,
    onSecondaryContainer = DarkBlue90,
    tertiary = Yellow80,
    onTertiary = Yellow20,
    tertiaryContainer = Yellow30,
    onTertiaryContainer = Yellow90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Grey10,
    onBackground = Grey90,
    surface = Grey10,
    onSurface = Grey80,
    inverseSurface = Grey90,
    inverseOnSurface = Grey20,
    surfaceVariant = BlueGrey30,
    onSurfaceVariant = BlueGrey80,
    outline = BlueGrey60
)

/**
 * The [ColorScheme] used when the device is considered to be in "Light Theme". The [Color]'s use are:
 */
private val JetchatLightColorScheme: ColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    inversePrimary = Blue80,
    secondary = DarkBlue40,
    onSecondary = Color.White,
    secondaryContainer = DarkBlue90,
    onSecondaryContainer = DarkBlue10,
    tertiary = Yellow40,
    onTertiary = Color.White,
    tertiaryContainer = Yellow90,
    onTertiaryContainer = Yellow10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Grey99,
    onBackground = Grey10,
    surface = Grey99,
    onSurface = Grey10,
    inverseSurface = Grey20,
    inverseOnSurface = Grey95,
    surfaceVariant = BlueGrey90,
    onSurfaceVariant = BlueGrey30,
    outline = BlueGrey50
)

/**
 *
 */
@SuppressLint("NewApi")
@Composable
fun JetchatTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val myColorScheme: ColorScheme = when {
        dynamicColor && isDarkTheme -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        dynamicColor && !isDarkTheme -> {
            dynamicLightColorScheme(LocalContext.current)
        }
        isDarkTheme -> JetchatDarkColorScheme
        else -> JetchatLightColorScheme
    }

    MaterialTheme(
        colorScheme = myColorScheme,
        typography = JetchatTypography,
        content = content
    )
}
