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

@file:Suppress("unused")

package com.example.owl.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.owl.R

/**
 * Light theme color palette for Yellow theme.
 */
private val YellowThemeLight = lightColors(
    primary = yellow500,
    primaryVariant = yellow400,
    onPrimary = Color.Black,
    secondary = blue700,
    secondaryVariant = blue800,
    onSecondary = Color.White
)

/**
 * Dark theme color palette for Yellow theme.
 */
private val YellowThemeDark = darkColors(
    primary = yellow200,
    secondary = blue200,
    onSecondary = Color.Black,
    surface = yellowDarkPrimary
)

/**
 * Theme that uses yellow as the primary color.
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 * @param content The composable content of the theme.
 */
@Composable
fun YellowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        YellowThemeDark
    } else {
        YellowThemeLight
    }
    OwlTheme(darkTheme = darkTheme, colors = colors, content = content)
}

/**
 * Light theme color palette for Blue theme.
 */
private val BlueThemeLight = lightColors(
    primary = blue700,
    onPrimary = Color.White,
    primaryVariant = blue800,
    secondary = yellow500
)

/**
 * Dark theme color palette for Blue theme.
 */
private val BlueThemeDark = darkColors(
    primary = blue200,
    secondary = yellow200,
    surface = blueDarkPrimary
)

/**
 * Theme that uses blue as the primary color.
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 * @param content The composable content of the theme.
 */
@Composable
fun BlueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        BlueThemeDark
    } else {
        BlueThemeLight
    }
    OwlTheme(darkTheme = darkTheme, colors = colors, content = content)
}

/**
 * Light theme color palette for Pink theme.
 */
private val PinkThemeLight = lightColors(
    primary = pink500,
    secondary = pink500,
    primaryVariant = pink600,
    onPrimary = Color.Black,
    onSecondary = Color.Black
)

/**
 * Dark theme color palette for Pink theme.
 */
private val PinkThemeDark = darkColors(
    primary = pink200,
    secondary = pink200,
    surface = pinkDarkPrimary
)

/**
 * Theme that uses pink as the primary color.
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 * @param content The composable content of the theme.
 */
@Composable
fun PinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        PinkThemeDark
    } else {
        PinkThemeLight
    }
    OwlTheme(darkTheme = darkTheme, colors = colors, content = content)
}

/**
 * Default [Elevations] for light theme.
 */
private val LightElevation = Elevations()

/**
 * Default [Elevations] for dark themes.
 */
private val DarkElevation = Elevations(card = 1.dp)

/**
 * Default [Images] for light theme.
 */
private val LightImages = Images(lockupLogo = R.drawable.ic_lockup_blue)

/**
 * Default [Images] for for dark themes.
 */
private val DarkImages = Images(lockupLogo = R.drawable.ic_lockup_white)

/**
 * Base theme for Owl.
 *
 * This component wraps MaterialTheme, providing baseline values for the color, typography,
 * and shapes systems. It also provides values for custom theme systems like elevations and images
 * through [CompositionLocalProvider].
 *
 * @param darkTheme Whether the theme should use a dark color scheme.
 * @param colors The colors to be used in the theme.
 * @param content The composable content of the theme.
 */
@Composable
private fun OwlTheme(
    darkTheme: Boolean,
    colors: Colors,
    content: @Composable () -> Unit
) {
    val elevation: Elevations = if (darkTheme) DarkElevation else LightElevation
    val images: Images = if (darkTheme) DarkImages else LightImages
    CompositionLocalProvider(
        LocalElevations provides elevation,
        LocalImages provides images
    ) {
        MaterialTheme(
            colors = colors,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

/**
 * Alternate to [MaterialTheme] allowing us to add our own theme systems (e.g. [Elevations]) or to
 * extend [MaterialTheme]'s types e.g. return our own [Colors] extension
 */
object OwlTheme {

    /**
     * Proxy to [MaterialTheme]
     */
    val colors: Colors
        @Composable
        get() = MaterialTheme.colors

    /**
     * Proxy to [MaterialTheme]
     */
    val typography: Typography
        @Composable
        get() = MaterialTheme.typography

    /**
     * Proxy to [MaterialTheme]
     */
    val shapes: Shapes
        @Composable
        get() = MaterialTheme.shapes

    /**
     * Retrieves the current [Elevations] at the call site's position in the hierarchy.
     */
    val elevations: Elevations
        @Composable
        get() = LocalElevations.current

    /**
     * Retrieves the current [Images] at the call site's position in the hierarchy.
     */
    val images: Images
        @Composable
        get() = LocalImages.current
}
