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

package com.codelab.theming.ui.finish.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import com.codelab.theming.ui.start.theme.JetnewsTheme

/**
 * Used as the [Colors.error] color of the [darkColors] `DarkThemeColors` of our [JetnewsTheme]
 * custom [MaterialTheme]
 */
val Red200: Color = Color(0xfff297a2)

/**
 * Used as the [Colors.primary] and [Colors.secondary] colors of the [darkColors] `DarkThemeColors`
 * of our [JetnewsTheme] custom [MaterialTheme]
 */
val Red300: Color = Color(0xffea6d7e)

/**
 * Used as the [Colors.primaryVariant] color of the [darkColors] `DarkThemeColors` of our [JetnewsTheme]
 * custom [MaterialTheme], and as the [Colors.primary] and [Colors.secondary] colors of its [lightColors]
 * `LightThemeColors`
 */
val Red700: Color = Color(0xffdd0d3c)

/**
 * Used as the [Colors.error] color of the [lightColors] `LightThemeColors` of our [JetnewsTheme]
 * custom [MaterialTheme]
 */
val Red800: Color = Color(0xffd00036)

/**
 * Used as the [Colors.primaryVariant] and [Colors.secondaryVariant] colors of the [lightColors]
 * `LightThemeColors` of our [JetnewsTheme] custom [MaterialTheme]
 */
val Red900: Color = Color(0xffc20029)
