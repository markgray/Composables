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

package com.google.samples.apps.nowinandroid.core.designsystem.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.KeyframesSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Now in Android loading wheel.
 *
 * We start by initializing and remembering our [InfiniteTransition] variable `infiniteTransition`
 * to the instance returned by [rememberInfiniteTransition] called with its `label` argument the
 * string "wheel transition". We initialize our [Float] variable `startValue` to `0F` is the current
 * [LocalInspectionMode] is `true` or `1F` if it is `false`. We initialize and remember our [List]
 * of [Animatable] of [Float] variable `floatAnimValues` using the [Iterable.map] method of the
 * range of `0` until [NUM_OF_LINES] with its `transform` lambda argument returning a remembered
 * [Animatable] whose `initialValue` is `startValue`.
 *
 * Then we compose a [LaunchedEffect] whose `key1` argument is `floatAnimValues` and in its
 * [CoroutineScope] `block` suspend lambda argument we use the [Iterable.map] method of the
 * range of `0` until [NUM_OF_LINES] with its `transform` lambda argument capturing the [Int]
 * passed the lambda in variable `index` the calls the [launch] method to launch a coroutine which
 * calls the [Animatable.animateTo] method of the [Animatable] at index `index` in the [List] of
 * [Animatable] of [Float] variable `floatAnimValues` with its `targetValue` argument set to `0F`,
 * its `animationSpec` argument set to [tween] with its `durationMillis` argument set to
 * `100`, its `easing` argument set to [FastOutSlowInEasing], and its `delayMillis` argument set
 * to `40 * index`.
 *
 * Having launched our [LaunchedEffect] we continue by initializing our animated [Float] variable
 * `rotationAnim` to the value returned by the [InfiniteTransition.animateFloat] method of the
 * [InfiniteTransition] variable `infiniteTransition` with its `initialValue` argument set to `0F`,
 * its `targetValue` argument set to `360F`, its `animationSpec` argument set to an
 * [infiniteRepeatable] with its `animation` argument set to [tween] with its `durationMillis`
 * argument set to [ROTATION_TIME], and its `easing` argument set to [LinearEasing]. The `label`
 * of this animated value is "wheel rotation animation".
 *
 * We initialize our [Color] variable `baseLineColor` to the [ColorScheme.onBackground] of our
 * custom [MaterialTheme.colorScheme], and our [Color] variable `progressLineColor` to the
 * [ColorScheme.inversePrimary] of our custom [MaterialTheme.colorScheme].
 *
 * We initialize our animated [List] of [State] of [Color] variable `colorAnimValues` using the
 * [Iterable.map] method of the range `0` until [NUM_OF_LINES] in whose `transform` lambda argument
 * we capture the [Int] passed the lambda in variable `index` and return the value returned by the
 * [InfiniteTransition.animateColor] method of [InfiniteTransition] variable `infiniteTransition`
 * when called with its `initialValue` argument set to `baseLineColor`, its `targetValue` argument
 * set to `baseLineColor`, its `animationSpec` argument set to an [infiniteRepeatable] with its
 * `animation` argument set to the [KeyframesSpec] returned by [keyframes] when called with its
 * `durationMillis` argument set to `ROTATION_TIME / 2`, `progressLineColor` at [ROTATION_TIME] /
 * [NUM_OF_LINES] / 2 using [LinearEasing], and `baseLineColor` at [ROTATION_TIME] / [NUM_OF_LINES]
 * using [LinearEasing]. The `repeatMode` argument of the [infiniteRepeatable] is set to
 * [RepeatMode.Restart], and the `initialStartOffset` argument is set to [StartOffset] with its
 * `offsetMillis` argument set to [ROTATION_TIME] / [NUM_OF_LINES] / 2 * index. The `label` of each
 * animated [Color] is "wheel color animation".
 *
 * Finally our root composable is a [Canvas] whose `modifier` argument chains to our [Modifier]
 * parameter [modifier] a [Modifier.size] whose `size` argument is `48.dp`, chained to a
 * [Modifier.padding] that adds `8.dp` to `all` sides, chained to a [Modifier.graphicsLayer] in whose
 * [GraphicsLayerScope] `block` lambda argument we set the [GraphicsLayerScope.rotationZ] to our
 * animated [Float] variable `rotationAnim`, chained to a [Modifier.semantics] in whose
 * [SemanticsPropertyReceiver] `properties` argument we set the
 * [SemanticsPropertyReceiver.contentDescription] to our [String] parameter [contentDesc].
 *
 * In the [DrawScope] `onDraw` lambda argument we use [repeat] to loop [NUM_OF_LINES] times and in
 * its `action` lambda argument we capture the [Int] passed the lambda in variable `index` then
 * call [DrawScope.rotate] with its `degrees` argument set to `index * 30f`, and in its [DrawScope]
 * `block` lambda argument we call [DrawScope.drawLine] with its `color` argument set to the [Color]
 * at index `index` of our animated [List] of [State] of [Color] variable `colorAnimValues`, its
 * `alpha` argument set to `1f` if the [Float] at index `index` of our animated [List] of [Animatable]
 * of [Float] variable `floatAnimValues` is less than `1f` or `0f` if it is greater than or equal to
 * `1f`, its `strokeWidth` argument set to `4F`, its `cap` argument set to [StrokeCap.Round], its
 * `start` argument set to an [Offset] with its `x` argument set to `size.width / 2`, and its `y`
 * argument set to `size.height / 4`. The `end` argument of the [DrawScope.drawLine] is set to an
 * [Offset] with its `x` argument set to `size.width / 2`, and its `y` argument set to the [Float] at
 * index `index` of our animated [List] of [Animatable] of [Float] variable `floatAnimValues` times
 * `size.height / 4`.
 *
 * @param contentDesc The content description for the loading wheel.
 * @param modifier The modifier to be applied to the loading wheel.
 */
@Composable
fun NiaLoadingWheel(
    contentDesc: String,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition: InfiniteTransition =
        rememberInfiniteTransition(label = "wheel transition")

    // Specifies the float animation for slowly drawing out the lines on entering
    val startValue: Float = if (LocalInspectionMode.current) 0F else 1F
    val floatAnimValues: List<Animatable<Float, AnimationVector1D>> =
        (0 until NUM_OF_LINES).map { remember { Animatable(initialValue = startValue) } }
    LaunchedEffect(key1 = floatAnimValues) {
        (0 until NUM_OF_LINES).map { index: Int ->
            launch {
                floatAnimValues[index].animateTo(
                    targetValue = 0F,
                    animationSpec = tween(
                        durationMillis = 100,
                        easing = FastOutSlowInEasing,
                        delayMillis = 40 * index,
                    ),
                )
            }
        }
    }

    // Specifies the rotation animation of the entire Canvas composable
    val rotationAnim: Float by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ROTATION_TIME, easing = LinearEasing),
        ),
        label = "wheel rotation animation",
    )

    // Specifies the color animation for the base-to-progress line color change
    val baseLineColor: Color = MaterialTheme.colorScheme.onBackground
    val progressLineColor: Color = MaterialTheme.colorScheme.inversePrimary

    val colorAnimValues: List<State<Color>> = (0 until NUM_OF_LINES).map { index: Int ->
        infiniteTransition.animateColor(
            initialValue = baseLineColor,
            targetValue = baseLineColor,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = ROTATION_TIME / 2
                    progressLineColor at ROTATION_TIME / NUM_OF_LINES / 2 using LinearEasing
                    baseLineColor at ROTATION_TIME / NUM_OF_LINES using LinearEasing
                },
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset(offsetMillis = ROTATION_TIME / NUM_OF_LINES / 2 * index),
            ),
            label = "wheel color animation",
        )
    }

    // Draws out the LoadingWheel Canvas composable and sets the animations
    Canvas(
        modifier = modifier
            .size(size = 48.dp)
            .padding(all = 8.dp)
            .graphicsLayer { rotationZ = rotationAnim }
            .semantics { contentDescription = contentDesc }
            .testTag(tag = "loadingWheel"),
    ) {
        repeat(times = NUM_OF_LINES) { index: Int ->
            rotate(degrees = index * 30f) {
                drawLine(
                    color = colorAnimValues[index].value,
                    // Animates the initially drawn 1 pixel alpha from 0 to 1
                    alpha = if (floatAnimValues[index].value < 1f) 1f else 0f,
                    strokeWidth = 4F,
                    cap = StrokeCap.Round,
                    start = Offset(x = size.width / 2, y = size.height / 4),
                    end = Offset(
                        x = size.width / 2,
                        y = floatAnimValues[index].value * size.height / 4,
                    ),
                )
            }
        }
    }
}

/**
 * Now in Android overlay loading wheel with scrim.
 *
 * This function displays a loading wheel overlaid on content. It consists of a [Surface]
 * with a rounded corner shape, shadow elevation, and a semi-transparent background color.
 * Inside the [Surface], the [NiaLoadingWheel] is displayed.
 *
 * Our root composable is a [Surface] whose `shape` argument is a [RoundedCornerShape] with its
 * `size` argument set to `60.dp`, whose `shadowElevation` argument is `8.dp`, its `color` argument
 * is a copy of the [ColorScheme.surface] of our custom [MaterialTheme.colorScheme] with its
 * `alpha` argument set to `0.83f`, and its `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.size] whose `size` argument is `60.dp`.
 *
 * In the `content` composable lambda argument of the [Surface] we compose a [NiaLoadingWheel] whose
 * `contentDesc` argument is our [String] parameter [contentDesc].
 *
 * @param contentDesc The content description for the loading wheel.
 * @param modifier The modifier to be applied to the loading wheel.
 */
@Composable
fun NiaOverlayLoadingWheel(
    contentDesc: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(size = 60.dp),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.83f),
        modifier = modifier
            .size(size = 60.dp),
    ) {
        NiaLoadingWheel(
            contentDesc = contentDesc,
        )
    }
}

/**
 * Two previews ("Light theme" and "Dark theme") of the [NiaLoadingWheel] composable. Wrapped in our
 * [NiaTheme] custom [MaterialTheme] we compose a [Surface] which composes a [NiaLoadingWheel] in its
 * `content` composable lambda argument. The `contentDesc` argument of the [NiaLoadingWheel] is
 * "LoadingWheel".
 */
@ThemePreviews
@Composable
fun NiaLoadingWheelPreview() {
    NiaTheme {
        Surface {
            NiaLoadingWheel(contentDesc = "LoadingWheel")
        }
    }
}

/**
 * Two previews ("Light theme" and "Dark theme") of the [NiaOverlayLoadingWheel].
 *
 * This preview displays the [NiaOverlayLoadingWheel] within a [NiaTheme] wrapped [Surface].
 * The `contentDesc` argument of the [NiaOverlayLoadingWheel] is "LoadingWheel".
 */
@ThemePreviews
@Composable
fun NiaOverlayLoadingWheelPreview() {
    NiaTheme {
        Surface {
            NiaOverlayLoadingWheel(contentDesc = "LoadingWheel")
        }
    }
}

/**
 * Duration of one complete rotation of the loading wheel.
 */
private const val ROTATION_TIME = 12000

/**
 * The number of lines in the loading wheel.
 */
private const val NUM_OF_LINES = 12
