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

package com.example.android.codelab.animation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Scaffold
import androidx.compose.ui.graphics.Color

/**
 * Used as `backgroundColor` of the `HomeTabBar` that is used by the [Scaffold] of `Home` when the
 * current `tabPage` is `TabPage.Home`
 */
val Purple100: Color = Color(0xFFE1BEE7)

/**
 * Used as the `primary` [Color] of [AnimationCodelabTheme]
 */
val Purple500: Color = Color(0xFF6200EE)

/**
 * Used as the `primaryVariant` [Color] of [AnimationCodelabTheme], and the "Border color" of the
 * `HomeTabIndicator` when the `TabPage.Home` is selected.
 */
val Purple700: Color = Color(0xFF3700B3)

/**
 * Used as the `secondary` [Color] of [AnimationCodelabTheme]
 */
val Teal200: Color = Color(0xFF03DAC5)

/**
 * Used as `backgroundColor` of the `HomeTabBar` that is used by the [Scaffold] of `Home` when the
 * current `tabPage` is NOT `TabPage.Home`
 */
val Green300: Color = Color(0xFF81C784)

/**
 * Used as the "Border color" of the `HomeTabIndicator` when the `TabPage.Home` is NOT selected.
 */
val Green800: Color = Color(0xFF2E7D32)

/**
 * Used as the `background` of the 48.dp `CircleShape` [Box] at the left side of `WeatherRow`
 */
val Amber600: Color = Color(0xFFFFB300)
