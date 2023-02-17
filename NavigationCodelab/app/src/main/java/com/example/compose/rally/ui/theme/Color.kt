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

package com.example.compose.rally.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

/**
 * The [Color] used for [Colors.primary] of our [ColorPalette] custom [Colors].
 */
val Green500: Color = Color(0xFF1EB980)

/**
 * The [Color] used for [Colors.surface] and [Colors.background] of our [ColorPalette] custom [Colors].
 */
val DarkBlue900: Color = Color(0xFF26282F)

/**
 * Rally is always dark themed.
 */
val ColorPalette: Colors = darkColors(
    primary = Green500,
    surface = DarkBlue900,
    onSurface = Color.White,
    background = DarkBlue900,
    onBackground = Color.White
)
