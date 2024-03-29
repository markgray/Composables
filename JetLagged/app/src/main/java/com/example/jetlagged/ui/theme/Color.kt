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

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.jetlagged.JetLaggedHeaderTabs
import com.example.jetlagged.SleepDayData
import com.example.jetlagged.SleepGraphData
import com.example.jetlagged.SleepPeriod
import com.example.jetlagged.SleepType
import com.example.jetlagged.sleepData

/**
 * Unused but pretty
 */
@Suppress("unused")
val Lilac: Color = Color(color = 0xFFCCB6DC)

/**
 * Used as the [ColorScheme.primary] and [ColorScheme.secondaryContainer] colors of the custom
 * [MaterialTheme.colorScheme] used by our [JetLaggedTheme] custom [MaterialTheme] as well as in
 * several other places as a [Color] in its own right.
 */
val Yellow: Color = Color(color = 0xFFFFCB66)

/**
 * Used in two places as one of the colors in gradients used in [Brush]'s
 */
val YellowVariant: Color = Color(color = 0xFFFFDE9F)

/**
 * Used as the [ColorScheme.tertiary] color of the custom [MaterialTheme.colorScheme] used by our
 * [JetLaggedTheme] custom [MaterialTheme]
 */
val Coral: Color = Color(color = 0xFFF3A397)

/**
 * Used as the [ColorScheme.surface] color of the custom [MaterialTheme.colorScheme] used by our
 * [JetLaggedTheme] custom [MaterialTheme], as well as one of the colors in gradients used in [Brush],
 * and as the `containerColor` of the [ScrollableTabRow] used by [JetLaggedHeaderTabs].
 */
val White: Color = Color(color = 0xFFFFFFFF)

/**
 * Used as the [ColorScheme.secondary] color of the custom [MaterialTheme.colorScheme] used by our
 * [JetLaggedTheme] custom [MaterialTheme].
 */
val MintGreen: Color = Color(color = 0xFFACD6B8)

/**
 * Unused but pretty
 */
@Suppress("unused")
val DarkGray: Color = Color(color = 0xFF2B2B2D)

/**
 * Unused but pretty
 */
@Suppress("unused")
val DarkCoral: Color = Color(color = 0xFFF7A374)

/**
 * Unused but pretty
 */
@Suppress("unused")
val DarkYellow: Color = Color(color = 0xFFFFCE6F)

/**
 * Used as the [SleepType.Awake.color] of the [SleepType.Awake] enum that is used as a `type` of
 * [SleepPeriod] in the [List] of [SleepDayData] that is used in the faked [SleepGraphData] dataset
 * [sleepData].
 */
val Yellow_Awake: Color = Color(color = 0xFFffeac1)

/**
 * Used as the [SleepType.REM.color] of the [SleepType.REM] enum that is used as a `type` of
 * [SleepPeriod] in the [List] of [SleepDayData] that is used in the faked [SleepGraphData] dataset
 * [sleepData].
 */
val Yellow_Rem: Color = Color(color = 0xFFffdd9a)

/**
 * Used as the [SleepType.Light.color] of the [SleepType.Light] enum that is used as a `type` of
 * [SleepPeriod] in the [List] of [SleepDayData] that is used in the faked [SleepGraphData] dataset
 * [sleepData].
 */
val Yellow_Light: Color = Color(color = 0xFFffcb66)

/**
 * Used as the [SleepType.Deep.color] of the [SleepType.Deep] enum that is used as a `type` of
 * [SleepPeriod] in the [List] of [SleepDayData] that is used in the faked [SleepGraphData] dataset
 * [sleepData].
 */
val Yellow_Deep: Color = Color(color = 0xFFff973c)
