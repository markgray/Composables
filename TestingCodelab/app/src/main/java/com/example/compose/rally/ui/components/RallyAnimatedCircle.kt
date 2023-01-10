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

package com.example.compose.rally.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * The number of degrees that are left blank between the arcs drawn.
 */
private const val DividerLengthInDegrees = 1.8f

/**
 * A donut chart that animates when loaded.
 *
 * @param proportions a [List] of [Float] with each entry representing the fraction of the circle we
 * are to draw to represent the contribution of the entry to the whole circle.
 * @param colors the [List] of [Color]'s we are to use to draw each segment of the arc corresponding
 * to each entry in our [proportions] parameter.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [StatementBody] passes us a [Modifier.height] that sets our height to be 300.dp, to
 * which is chained a [BoxScope] `Modifier.align` with its `alignment` argument [Alignment.Center]
 * to align us with the center of the [Box] we are in, and a `Modifier.fillMaxWidth` to have us fill
 * the entire incoming width constraint.
 */
@Composable
fun AnimatedCircle(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val currentState: MutableTransitionState<AnimatedCircleProgress> = remember {
        MutableTransitionState(AnimatedCircleProgress.START)
            .apply { targetState = AnimatedCircleProgress.END }
    }
    val stroke: Stroke = with(LocalDensity.current) { Stroke(width = 5.dp.toPx()) }
    val transition: Transition<AnimatedCircleProgress> =
        updateTransition(transitionState = currentState, label = "currentState")
    val angleOffset: Float by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        },
        label = "angleOffset"
    ) { progress: AnimatedCircleProgress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            360f
        }
    }
    val shift: Float by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = CubicBezierEasing(a = 0f, b = 0.75f, c = 0.35f, d = 0.85f)
            )
        },
        label = "shift"
    ) { progress: AnimatedCircleProgress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            30f
        }
    }

    Canvas(modifier = modifier) {
        val innerRadius: Float = (size.minDimension - stroke.width) / 2
        val halfSize: Size = size / 2.0f
        val topLeft = Offset(
            x = halfSize.width - innerRadius,
            y = halfSize.height - innerRadius
        )
        val size = Size(width = innerRadius * 2, height = innerRadius * 2)
        var startAngle: Float = shift - 90f
        proportions.forEachIndexed { index: Int, proportion: Float ->
            val sweep = proportion * angleOffset
            drawArc(
                color = colors[index],
                startAngle = startAngle + DividerLengthInDegrees / 2,
                sweepAngle = sweep - DividerLengthInDegrees,
                topLeft = topLeft,
                size = size,
                useCenter = false,
                style = stroke
            )
            startAngle += sweep
        }
    }
}

/**
 * The enum representing the progress of our animations from [START] to [END].
 */
private enum class AnimatedCircleProgress { START, END }
