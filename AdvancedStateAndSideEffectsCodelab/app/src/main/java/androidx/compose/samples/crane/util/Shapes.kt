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

package androidx.compose.samples.crane.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection

/**
 * This is unused, but I leave it in just to provide an example use of Compose [Canvas].
 *
 * @param color The color or fill to be applied to the circle
 */
@Suppress("unused") // Unused but instructional
@Composable
fun Circle(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(color = color)
    }
}

/**
 * This is unused, but I leave it in just to provide an example use of Compose [Canvas].
 *
 * @param color The color to be applied to the rectangle
 * @param lookingLeft if `true` the SemiRect should face left
 */
@Suppress("unused") // Unused but instructional
@Composable
fun SemiRect(color: Color, lookingLeft: Boolean = true) {
    val layoutDirection: LayoutDirection = LocalLayoutDirection.current
    Canvas(modifier = Modifier.fillMaxSize()) {
        // The SemiRect should face left EITHER the lookingLeft param is true
        // OR the layoutDirection is Rtl
        val offset: Offset = if (lookingLeft xor (layoutDirection == LayoutDirection.Rtl)) {
            Offset(x = 0f, y = 0f)
        } else {
            Offset(x = size.width / 2, y = 0f)
        }
        val size = Size(width = size.width / 2, height = size.height)

        drawRect(size = size, topLeft = offset, color = color)
    }
}

/**
 * Preview of our [Circle] Composable with the `color` argument [Color.Magenta].
 */
@Preview
@Composable
fun CirclePreview() {
    Circle(color = Color.Magenta)
}

/**
 * Preview of our [SemiRect] Composable with the `color` argument [Color.Cyan].
 */
@Preview
@Composable
fun SemiRectPreview() {
    SemiRect(color = Color.Cyan)
}