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

package com.codelab.basiclayouts.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color

/**
 * This is the [Color] used for the `onBackground` color of the `DarkColorPalette` and the `background`
 * color of the `LightColorPalette` that our [MySootheTheme] custom [MaterialTheme] uses for the
 * `colors` parameter of [MaterialTheme] (a gray-brown color)
 */
val taupe100: Color = Color(0xFFF0EAE2)

/**
 * This is the [Color] used for the `onBackground` color of the `LightColorPalette` that our
 * [MySootheTheme] custom [MaterialTheme] uses for the `colors` parameter of [MaterialTheme]
 * (a dark gray-brown color)
 */
val taupe800: Color = Color(0xFF655454)

/**
 * This is the [Color] used for the `secondary` color of the `DarkColorPalette` that our
 * [MySootheTheme] custom [MaterialTheme] uses for the `colors` parameter of [MaterialTheme]
 */
val rust300: Color = Color(0xFFE1AFAF)

/**
 * This is the [Color] used for the `secondary` color of the `LightColorPalette` that our
 * [MySootheTheme] custom [MaterialTheme] uses for the `colors` parameter of [MaterialTheme]
 */
val rust600: Color = Color(0xFF886363)

/**
 * This is the [Color] used for the `background`, `onPrimary` and `onSecondary` colors of the
 * `DarkColorPalette` and the `primary` color of the `LightColorPalette` that our [MySootheTheme]
 * custom [MaterialTheme] uses for the `colors` parameter of [MaterialTheme]. It is also used with
 * an `alpha` of 0.8f for the color [gray800], and the `onSurface` color of the `LightColorPalette`
 */
val gray900: Color = Color(0xFF333333)

/**
 * This is a variant of the [Color.White] whose `alpha` is changed to 0.15f - it is unused.
 */
@Suppress("unused") // Suggested change would make class less reusable
val white150: Color = Color.White.copy(alpha = 0.15f)

/**
 * This is a variant of the [Color.White] whose `alpha` is changed to 0.8f - it is unused.
 */
@Suppress("unused") // Suggested change would make class less reusable
val white800: Color = Color.White.copy(alpha = 0.8f)

/**
 * This is a variant of the [Color.White] whose `alpha` is changed to 0.85f - it is unused.
 */
@Suppress("unused") // Suggested change would make class less reusable
val white850: Color = Color.White.copy(alpha = 0.85f)

/**
 * This is a variant of the [gray900] color whose `alpha` is changed to 0.8f - it is unused.
 */
val gray800: Color = gray900.copy(alpha = 0.8f)
