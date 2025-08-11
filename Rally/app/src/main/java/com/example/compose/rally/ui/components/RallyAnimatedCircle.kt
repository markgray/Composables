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

package com.example.compose.rally.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Used at the start and end of each arc to ensure that there is a gap between arcs
 */
private const val DividerLengthInDegrees = 1.8f

/**
 * A donut chart that animates when loaded.
 *
 * We start by initializing and remembering our [MutableTransitionState] variable `val currentState`
 * to a new instance whodse `initialState` is [AnimatedCircleProgress.START] to which we use the
 * [MutableTransitionState.apply] method to set its `targetState` to [AnimatedCircleProgress.END].
 * We initialize our [Stroke] variable `val stroke` to a new instance `with` the current [LocalDensity]
 * whose `width` argument is the pixel value of `5.dp`. We initialize and remember our [Transition]
 * of [AnimatedCircleProgress] variable `val transition` to the value returned by [rememberTransition]
 * for the `transitionState` argument of our [MutableTransitionState] of [AnimatedCircleProgress]
 * vqriable `currentState` and the `label` argument "CircleTransition".
 *
 * We initialize our animated [Float] variable `val angleOffset` to the value returned by the
 * [Transition.animateFloat] method of [Transition] variable `transition` whose `transitionSpec`
 * argument is a [tween] whose `delayMillis` argument is `500`, whose `durationMillis` argument is
 * `900`, and whose `easing` argument is [LinearOutSlowInEasing]. In its `targetValueByState`
 * composable lambda argument we accept the [AnimatedCircleProgress] passed the lambda in variable
 * `progress` and if that [AnimatedCircleProgress] is [AnimatedCircleProgress.START] we return `0f`,
 * otherwise we return `360f`.
 *
 * We initialize our animated [Float] variable `val shift` to the value returned by the
 * [Transition.animateFloat] method of [Transition] variable `transition` whose `transitionSpec`
 * argument is a [tween] whose `delayMillis` argument is `500`, whose `durationMillis` argument is
 * `900`, and whose `easing` argument is [CubicBezierEasing] whose `a` argument is `0f`, whose `b`
 * argument is `0.75f`, whose `c` argument is `0.35f`, and whose `d` argument is `0.85f`. In its
 * `targetValueByState` composable lambda argument we accept the [AnimatedCircleProgress] passed
 * the lambda in variable `progress` and if that [AnimatedCircleProgress] is
 * [AnimatedCircleProgress.START] we return `0f`, otherwise we return `30f`.
 *
 * Our root composable is a [Canvas] whose `modifier` argument is our [Modifier] parameter [modifier].
 * In ite [DrawScope] `onDraw` composable lambda argument we initialize our [Float] variable
 * `val innerRadius` to the value returned by the [Size.minDimension] of the [DrawScope.size] of
 * the [Canvas] minus the [Stroke.width] of our [Stroke] variable `stroke` the quantity dividied by
 * `2`. We initialize our [Size] variable `val halfSize` to the value returned by the [DrawScope.size]
 * of the [Canvas] divided by `2.0f`. We initialize our [Offset] variable `val topLeft` to a new
 * instance whose `x` argument is the value returned by the [Size.width] of `halfSize` minus
 * `innerRadius`, and whose `y` argument is the value returned by the [Size.height] of `halfSize`
 * minus `innerRadius`. We initialize our [Size] variable `val size` to a new instance whose `width`
 * argument is `innerRadius` times `2` and whose `height` argument is `innerRadius` times `2`.
 * We initialize our [Float] variable `var startAngle` to `shift` minus `90f`.
 *
 * Then we use the [Iterable.forEachIndexed] method of our [List] of [Float] parameter [proportions]
 * to loop over its contents capturing the [Float] passed the lambda in variable `proportion` and its
 * index in variable `index`. We initialize our [Float] variable `val sweep` to `proportion` times
 * `angleOffset`. We use the [DrawScope.drawArc] method of the [Canvas] to draw an arc whose
 * arguments are:
 *  - `color`: is the [Color] returned by the [List.get] method of our [List] of [Color] parameter
 *  [colors] whose `index` argument is `index`.
 *  - `startAngle`: is `startAngle` plus [DividerLengthInDegrees] divided by `2`.
 *  - `sweepAngle`: is `sweep` minus [DividerLengthInDegrees].
 *  - `topLeft`: is our [Offset] variable `topLeft`.
 *  - `size`: is our [Size] variable `size`.
 *  - `useCenter`: is `false`.
 *  - `style`: is our [Stroke] variable `stroke`.
 *
 * After drawing the arc we increment `startAngle` by `sweep` and loop around for the next [Float]
 * in the [List].
 *
 * @param proportions A list of [Float]s that represent the proportions of the chart.
 * @param colors A list of [Color]s to be used for the chart segments.
 * @param modifier A [Modifier] to be applied to the chart.
 */
@Composable
fun AnimatedCircle(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val currentState: MutableTransitionState<AnimatedCircleProgress> = remember {
        MutableTransitionState(initialState = AnimatedCircleProgress.START)
            .apply { targetState = AnimatedCircleProgress.END }
    }
    val stroke: Stroke = with(LocalDensity.current) { Stroke(width = 5.dp.toPx()) }
    val transition: Transition<AnimatedCircleProgress> = rememberTransition(
        transitionState = currentState,
        label = "CircleTransition"
    )
    val angleOffset: Float by transition.animateFloat(
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        }
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
        }
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
            val sweep: Float = proportion * angleOffset
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
 * Enum class representing the state of the animated circle progress.
 * It can be either [START] or [END].
 */
private enum class AnimatedCircleProgress { START, END }
