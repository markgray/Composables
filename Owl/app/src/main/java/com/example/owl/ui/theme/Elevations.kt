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

package com.example.owl.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Elevation values that can be themed.
 * Represents a set of elevation values that can be applied to different UI elements.
 * This class is designed to be immutable, ensuring that elevation values remain consistent
 * throughout the application's theme.
 *
 * @property card The elevation value to be used for card-like surfaces.
 * Defaults to `0.dp`, indicating no elevation.
 */
@Immutable
data class Elevations(val card: Dp = 0.dp)

/**
 * CompositionLocal that provides access to the current [Elevations]
 * for the composition. This allows different parts of the UI to
 * use the themed elevation values consistently.
 *
 * It defaults to a new [Elevations] instance with default values.
 */
internal val LocalElevations = staticCompositionLocalOf { Elevations() }
