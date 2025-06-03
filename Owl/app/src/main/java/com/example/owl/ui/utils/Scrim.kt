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

package com.example.owl.ui.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope

/**
 * A [Modifier] which draws a vertical gradient scrim in the foreground.
 *
 * We call the [drawWithContent] method and in its [ContentDrawScope] `onDraw` lambda argument we
 * first call the [ContentDrawScope.drawContent] to draw the background, then we use the
 * [ContentDrawScope.drawRect] method to draw a vertical gradient scrim using a
 * [Brush.verticalGradient] whose `colors` argument is our [List] of [Color] parameter [colors].
 *
 * @param colors The colors to be used for the gradient, specify at least two. The first color
 * is the top of the gradient, the last color is the bottom of the gradient.
 */
fun Modifier.scrim(colors: List<Color>): Modifier = drawWithContent {
    drawContent()
    drawRect(brush = Brush.verticalGradient(colors = colors))
}
