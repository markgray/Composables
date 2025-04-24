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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

/**
 * Applies a horizontal gradient background to the composable with an adjustable offset.
 *
 * This modifier creates a horizontal gradient using the provided list of colors and applies it
 * as a background. The gradient can be shifted horizontally using the `offset` parameter,
 * creating a scrolling or moving effect. The gradient is mirrored to create a seamless
 * repeating effect.
 *
 * @param colors The list of [Color]s to use for the gradient. The gradient will transition
 * linearly between these colors.
 * @param width The total width of the gradient in DP. This determines how much space the
 * gradient spans before repeating.
 * @param offset The horizontal offset of the gradient in DP. A positive value shifts the
 * gradient to the right, while a negative value shifts it to the left. Defaults to 0f (no offset).
 * @return A [Modifier] with the specified horizontal gradient background applied.
 */
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

/**
 * Applies a diagonal gradient border to the composable.
 *
 * This modifier draws a border around the composable with a linear gradient
 * that transitions through the provided colors. The gradient direction is
 * implicitly diagonal from the top-left to bottom-right.
 *
 * @param colors The list of colors to use in the gradient. The gradient will transition
 * between these colors in the order they are provided.
 * @param borderSize The thickness of the border. Defaults to 2.dp.
 * @param shape The shape of the border. This can be any [Shape] such as
 * [RoundedCornerShape] or [CircleShape]. The border will conform to the provided shape.
 * @return A [Modifier] that applies the diagonal gradient border to the composable.
 */
fun Modifier.diagonalGradientBorder(
    colors: List<Color>,
    borderSize: Dp = 2.dp,
    shape: Shape
): Modifier = this.border(
    width = borderSize,
    brush = Brush.linearGradient(colors = colors),
    shape = shape
)

/**
 * Adds a diagonally animated gradient border to the composable.
 *
 * This modifier applies a gradient border that fades in or out based on the [showBorder] parameter.
 * When [showBorder] is true, the border is fully visible, and when it's false, the border fades to
 * transparent.
 *
 * The animation is applied to the colors of the gradient, creating a smooth transition between the
 * visible and invisible states.
 *
 * Our root composable is a [Modifier.composed] modifier, and in its [Modifier] `factory` composable
 * lambda argument we initalize our animated [State] wrapped [List] of [Color] variable
 * `animatedColors` with a [MutableList] whose size is equal to the size of our [List] of [Color]
 * parameter [colors] and in the `init` lambda argument we call the [animateColorAsState] composable
 * for each of the colors in our [List] of [Color] parameter [colors] appending the `i`th
 * [Color] in [colors] if our [Boolean] parameter [showBorder] is true or appending a copy of
 * that [Color] with an alpha of 0f if our [Boolean] parameter [showBorder] is false.
 *
 * Then we return a [Modifier.diagonalGradientBorder] whose `colors` argument is our animated [State]
 * wrapped [List] of [Color] variable `animatedColors`, whose `borderSize` argument is our [Dp]
 * parameter [borderSize], and whose `shape` argument is our [Shape] parameter [shape].
 *
 * @param showBorder [Boolean] indicating whether the border should be visible (true) or
 * transparent (false).
 * @param colors List of [Color] values defining the gradient's color stops.
 * Must contain at least two colors.
 * @param borderSize The thickness of the border in [Dp]. Defaults to 2.dp.
 * @param shape The shape of the border.
 * @return A [Modifier] that applies the animated diagonal gradient border.
 */
fun Modifier.fadeInDiagonalGradientBorder(
    showBorder: Boolean,
    colors: List<Color>,
    borderSize: Dp = 2.dp,
    shape: Shape
): Modifier = composed {
    val animatedColors: List<Color> = List(size = colors.size) { i: Int ->
        animateColorAsState(if (showBorder) colors[i] else colors[i].copy(alpha = 0f)).value
    }
    diagonalGradientBorder(
        colors = animatedColors,
        borderSize = borderSize,
        shape = shape
    )
}
