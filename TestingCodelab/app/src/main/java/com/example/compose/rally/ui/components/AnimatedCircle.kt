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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * The number of degrees that are left blank between the arcs drawn.
 */
// It is a constant of sorts
private const val DividerLengthInDegrees = 1.8f

/**
 * A donut chart that animates when loaded. We start by initializing our variable `val currentState`
 * to a [MutableTransitionState] that can be mutated using [updateTransition] between the enum values
 * [AnimatedCircleProgress.START] and [AnimatedCircleProgress.END]. We initialize our [Stroke] variable
 * `val stroke` to a new instance of 5.dp wide [Stroke] using the method [with] to use the `current`
 * [LocalDensity] to transform between [dp] and pixels when calling the constructor. We initialize
 * our variable `val transition` to a [Transition] of [AnimatedCircleProgress] using the method
 * [updateTransition] with its `transitionState` argument our `currentState` variable (Creates a
 * [Transition] and puts it in the currentState of that [MutableTransitionState]. Whenever the
 * `targetState` of the [MutableTransitionState] changes, the [Transition] will animate to the new
 * target state). The `label` argument to [updateTransition] ("currentState") is used to differentiate
 * different transitions in Android Studio.
 *
 * We initialize our animated [Float] variable `val angleOffset` "by" the [Transition.animateFloat]
 * method with its `transitionSpec` argument a [tween] whose `durationMillis` (duration in milliseconds)
 * is 900ms, whose `delayMillis` (amount of time in milliseconds that animation waits before starting)
 * is 500ms, and whose `easing` is [LinearOutSlowInEasing] (incoming elements are animated using
 * deceleration easing, which starts a transition at peak velocity and ends at rest). The
 * `targetValueByState` lambda argument of [Transition.animateFloat] returns 0f if the value of the
 * [AnimatedCircleProgress] passed it is [AnimatedCircleProgress.START] or 360f if it is not.
 *
 * We initialize our animated [Float] variable `val shift` "by" the [Transition.animateFloat] method
 * with its `transitionSpec` argument a [tween] whose `durationMillis` (duration in milliseconds)
 * is 900ms, whose `delayMillis` (amount of time in milliseconds that animation waits before starting)
 * is 500ms, and whose `easing` is a [CubicBezierEasing] with the arguments `a` = 0f, `b` = 0.75f,
 * `c` = 0.35f, and `d` = 0.85f
 *
 * Having set up our animation variables, we move on to the [Canvas] on which we will draw our circle.
 * The `modifier` argument of the [Canvas] is our [modifier] parameter. The `onDraw` lambda runs in
 * [DrawScope] and contains code which:
 *  - initializes its [Float] variable `val innerRadius` to half of the lesser of the magnitudes of
 *  the width and the height of [DrawScope.size] (the dimensions of the current drawing environment)
 *  minus the [Stroke.width] of our `stroke` variable.
 *  - initializes its [Size] variable `val halfSize` to half of [DrawScope.size].
 *  - initializes its [Offset] variable `val topLeft` to an instance whose `x` coordinate is the
 *  [Size.width] of `halfSize` minus `innerRadius` and whose 'y' coordinate is the [Size.height] of
 *  `halfSize` minus `innerRadius`.
 *  - initializes its [Size] variable `val size` to an instance whose `width` is `innerRadius` times
 *  2, and whose `height` is `innerRadius` times 2.
 *  - initializes its [Float] variable `var startAngle` to `shift` minus 90.
 *
 * It then uses the [forEachIndexed] extension function on our [List] of [Float] parameter `proportions`
 * to initialize `index` to the index of each item in the [List] in turn and to initialize `proportion`
 * to the [Float] value of each item in the [List]. It proceeds then to initialize its [Float] variable
 * `val sweep` to `proportion` times `angleOffset` and calls [DrawScope.drawArc] with the arguments:
 *  - `color` is the `index` entry in our [List] of [Color] parameter [colors] (Color to be applied
 *  to the arc)
 *  - `startAngle` is our `startAngle` variable plus half of [DividerLengthInDegrees] (Starting angle
 *  in degrees. 0 represents 3 o'clock)
 *  - `sweepAngle` is our `sweep` variable minus [DividerLengthInDegrees] (Size of the arc in degrees
 *  that is drawn clockwise relative to `startAngle` argument).
 *  - `topLeft` is our `topLeft` variable (Offset from the local origin of 0, 0 relative to the
 *  current translation)
 *  - `size` is our [Size] variable `size` (Dimensions of the arc to draw)
 *  - `useCenter` is `false` (if `true` the arc is to close the center of the bounds, if `false` the
 *  center of the circle is not drawn).
 *  - `style` is our [Stroke] variable `stroke` (Whether or not the arc is stroked or filled in - we
 *  just stroke the arc).
 *
 * Then it adds `sweep` to `startAngle` and loops back to handle the next entry in our [List] of
 * [Float] parameter [proportions].
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
 * The enum representing the progress of our animations from [START] to [END].
 */
private enum class AnimatedCircleProgress { START, END }
