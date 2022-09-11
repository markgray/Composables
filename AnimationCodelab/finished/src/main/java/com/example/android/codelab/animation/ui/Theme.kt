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

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

/**
 * This is the custom [MaterialTheme] used to wrap the Composables in our app. It only modifies some
 * of the `colors` of the default [MaterialTheme].
 *
 * @param content the Composable fun we are wrapping in order to provide it with default values for
 * the `colors`: [Colors], `typography`: [Typography], and `shapes`: [Shapes] it can use.
 */
@Composable
fun AnimationCodelabTheme(content: @Composable () -> Unit) {
    val colors = lightColors(
        primary = Purple500,
        primaryVariant = Purple700,
        secondary = Teal200
    )
    MaterialTheme(
        colors = colors,
        content = content
    )
}
