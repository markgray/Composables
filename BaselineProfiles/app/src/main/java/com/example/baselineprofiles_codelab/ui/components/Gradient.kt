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

package com.example.baselineprofiles_codelab.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a diagonal gradient tint to the content of the composable.
 *
 * This modifier draws a linear gradient diagonally across the composable's content
 * and blends it with the content using the specified [blendMode]. The gradient starts
 * from the top-left corner and ends at the bottom-right corner.
 *
 * We use the [drawWithContent] modifier and in its [ContentDrawScope] `onDraw` composable lambda
 * argument we first call the [ContentDrawScope.drawContent] method to draw the content
 * of the composable we are decorating. Then we call the [ContentDrawScope.drawRect] method to
 * draw a  rectangle over that content using as its `brush` argument a [Brush.linearGradient] whose
 * `colors` argument is our [List] of [Color]`s. parameter `colors` and whose `blendMode` argument
 * is our [BlendMode] parameter [blendMode].
 *
 * @param colors A list of [Color] values that define the gradient. The gradient will
 * transition smoothly between these colors. Must contain at least two colors.
 * @param blendMode The [BlendMode] used to combine the gradient with the composable's
 * existing content. Common options include [BlendMode.SrcAtop], [BlendMode.Multiply],
 * and [BlendMode.Overlay].
 *
 * @return A [Modifier] that applies the diagonal gradient tint.
 */
fun Modifier.diagonalGradientTint(
    colors: List<Color>,
    blendMode: BlendMode
): Modifier = drawWithContent {
    drawContent()
    drawRect(
        brush = Brush.linearGradient(colors = colors),
        blendMode = blendMode
    )
}

fun Modifier.offsetGradientBackground(
    colors: List<Color>,
    width: Float,
    offset: Float = 0f
): Modifier = background(
    Brush.horizontalGradient(
        colors = colors,
        startX = -offset,
        endX = width - offset,
        tileMode = TileMode.Mirror
    )
)

fun Modifier.diagonalGradientBorder(
    colors: List<Color>,
    borderSize: Dp = 2.dp,
    shape: Shape
): Modifier = this.border(
    width = borderSize,
    brush = Brush.linearGradient(colors = colors),
    shape = shape
)

fun Modifier.fadeInDiagonalGradientBorder(
    showBorder: Boolean,
    colors: List<Color>,
    borderSize: Dp = 2.dp,
    shape: Shape
): Modifier = composed {
    val animatedColors: List<Color> = List(colors.size) { i: Int ->
        animateColorAsState(if (showBorder) colors[i] else colors[i].copy(alpha = 0f)).value
    }
    diagonalGradientBorder(
        colors = animatedColors,
        borderSize = borderSize,
        shape = shape
    )
}
