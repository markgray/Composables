/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.jetlagged.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * The custom [ColorScheme] we use for our [JetLaggedTheme] custom [MaterialTheme]. We only override
 * the following:
 *
 *  - [ColorScheme.primary] The primary color is the color displayed most frequently across your app’s
 *  screens and components, we use [Yellow].
 *  - [ColorScheme.secondary] The secondary color provides more ways to accent and distinguish your
 *  product. Secondary colors are best for: Floating action buttons, Selection controls, like
 *  checkboxes and radio buttons, Highlighting selected text, Links and headlines, we use [MintGreen].
 *  - [ColorScheme.tertiary] The tertiary color that can be used to balance primary and secondary
 *  colors, or bring heightened attention to an element such as an input field, we use [Coral].
 *  - [ColorScheme.secondaryContainer] A tonal color to be used in containers, we use [Yellow].
 *  - [ColorScheme.surface] The surface color that affect surfaces of components, such as cards,
 *  sheets, and menus, we use [White].
 */
private val LightColorScheme: ColorScheme = lightColorScheme(
    primary = Yellow,
    secondary = MintGreen,
    tertiary = Coral,
    secondaryContainer = Yellow,
    surface = White
)

/**
 * The custom [Shapes] we use for our [JetLaggedTheme] custom [MaterialTheme]. We only override the
 * [Shapes.large] of the default [MaterialTheme.shapes].
 */
private val shapes: Shapes
    @Composable
    get() = MaterialTheme.shapes.copy(
        large = CircleShape
    )

/**
 * Our custom [MaterialTheme].
 *
 * @param content the Composable hierarachy that we will supply default values to.
 */
@Composable
fun JetLaggedTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = shapes,
        content = content
    )
}
