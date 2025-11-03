/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.composemail.ui.compositionlocal

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal that provides the current [WindowHeightSizeClass]. This can be used to customize
 * layouts for different window heights. For example, you might show a different UI for a
 * [WindowHeightSizeClass.Compact] screen than for a [WindowHeightSizeClass.Expanded] screen.
 *
 * It defaults to [WindowHeightSizeClass.Compact].
 */
val LocalHeightSizeClass: ProvidableCompositionLocal<WindowHeightSizeClass> =
    compositionLocalOf { WindowHeightSizeClass.Compact }

/**
 * CompositionLocal that provides the current [WindowWidthSizeClass]. This can be used to customize
 * layouts for different window widths. For example, you might show a different UI for a
 * [WindowWidthSizeClass.Compact] screen than for a [WindowWidthSizeClass.Expanded] screen.
 *
 * It defaults to [WindowWidthSizeClass.Compact].
 */
val LocalWidthSizeClass: ProvidableCompositionLocal<WindowWidthSizeClass> =
    compositionLocalOf { WindowWidthSizeClass.Compact }