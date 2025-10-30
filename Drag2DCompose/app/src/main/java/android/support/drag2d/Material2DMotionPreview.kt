/*
 * Copyright (C) 2023 The Android Open Source Project
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

package android.support.drag2d

import android.support.drag2d.compose.materialVelocity2D
import android.support.drag2d.lib.MaterialEasing
import android.support.drag2d.lib.MaterialVelocity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * A Composable preview that demonstrates and allows interactive testing of the
 * `materialVelocity2D` animation spec.
 *
 * This preview consists of:
 *  1. A draggable `Box` that, upon release, animates back to its center origin using the
 *  `materialVelocity2D` animation. The path of the drag and subsequent animation is drawn
 *  on the screen.
 *  2. Sliders to control the `duration`, `maxVelocity`, and `maxAcceleration` parameters of the
 *  animation.
 *  3. A dropdown menu to select different `Easing` functions to apply to the animation.
 *
 * This allows for real-time visualization and tweaking of how different parameters affect the
 * Material Design-style fling animation in a 2D space.
 *
 * We start by initializing and remembering our [MutableState] of [Float] variable `duration` to an
 * initial value of `1200f`, initializing and remembering our [MutableState] of [Float] variable
 * `maxVelocity` to an initial value of `2000f`, initializing and remembering our [MutableState] of
 * [Float] variable `maxAcceleration` to an initial value of `2000f`, and initializing and
 * remembering our [MutableState] of [String] variable `currentEasing` to an initial value of
 * `"EaseOutBack"`. We then initialize and remember our [Map] of [String] to [MaterialVelocity.Easing]
 * variable `nameToEasing` to a [Map] which contains all of the [MaterialVelocity.Easing] choices
 * keyed by their names.
 *
 * Our root composable is a [Column] whose `modifier` argument is a [Modifier.fillMaxWidth]. In the
 * [ColumnScope] `content` composable lambda argument of the [Column] we initialize and remember our
 * [MutableIntState] variable `touchUpIndex` to the `value` [Integer.MAX_VALUE]. We initialize and
 * remember our [ArrayList] of [Offset] variable `accumulator` to a new instance. We initialize and
 * remember our [Animatable] of [Offset] variable `offset` to an initial value of [Offset.Zero] and
 * a type converter of [Offset.Companion.VectorConverter]. We initialize and remember our
 * [MutableState] of [Offset] variable `referenceOffset` to an initial value of [Offset.Zero].
 *
 * Next we compose a [Box] to contain the draggable item and touch input drawing, whose `modifier`
 * argument is a [Modifier.fillMaxWidth], chained to a [ColumnScope.weight] whose `weight` is `1f`
 * and whose `fill` is `true`, chained to a [Modifier.drawWithContent]. In the [ContentDrawScope]
 * `onDraw` lambda argument we:
 *  - call the [ContentDrawScope.drawContent] method to cause the content of the [Box] to be drawn.
 *  - reference the `value` of our [Animatable] of [Offset] variable `offset` to trigger
 *  recomposition when needed.
 *  - if our [ArrayList] of [Offset] variable `accumulator` is empty we return.
 *  - otherwise we iterate over the indices of our [ArrayList] of [Offset] variable `accumulator`
 *  and call the [ContentDrawScope.drawLine] method with its `color` argument [Color.Red] if
 *  our current index is greater than our [MutableIntState] variable `touchUpIndex`, or [Color.Blue]
 *  if it is less than it. The `start` argument is the previous [Offset] in our [ArrayList] of
 *  [Offset] variable `accumulator` if it exists, or [Offset.Zero] if it does not, and the `end`
 *  argument is the current [Offset] in our [ArrayList] of [Offset] variable `accumulator`. The
 *  `strokeWidth` argument is `2f`.
 *
 * The `contentAlignment` argument of the [Box] is a lambda which accepts the [IntSize] of the
 * [Box] and the [IntSize] of its parent, and returns an [Offset] whose `x` is the [IntSize.width]
 * of the parent divided by two minus the [IntSize.width] of the [Box] divided by two, and whose
 * `y` is the [IntSize.height] of the parent divided by two minus the [IntSize.height] of the
 * [Box] divided by two.
 *
 * In the [BoxScope] `content` composable lambda argument of the [Box] we initialize and remember
 * our [Color] variable `color` to the [Color] returned by [Color.hsv] for the `hue` which is
 * a random value between `0` and `360`, for the `saturation` of `0.5f`, and the `value` of `0.8f`.
 * We initialize and remember our [VelocityTracker] variable `velocityTracker` to a new instance.
 * We initialize and remember our [CoroutineScope] variable `scope` to a new instance.
 *
 * Next we compose another [Box] whose `modifer` argument is a [Modifier.dragToAnimate] whose
 * `offset` argument is our [Animatable] of [Offset] variable `offset`, whose `accumulator` argument
 * is our [ArrayList] of [Offset] variable `accumulator`, whose `touchUpIndex` argument is our
 * [MutableIntState] variable `touchUpIndex`, whose `referenceOffset` argument is our [MutableState]
 * wrapped [Offset] variable `referenceOffset`, whose `scope` argument is our [CoroutineScope]
 * variable `scope`, whose `velocityTracker` argument is our [VelocityTracker] variable
 * `velocityTracker`, whose `duration` argument is our [MutableState] of [Float] variable `duration`,
 * whose `maxVelocity` argument is our [MutableState] of [Float] variable `maxVelocity`, whose
 * `maxAcceleration` argument is our [MutableFloatState] variable `maxAcceleration`, and whose
 * `easing` argument is the [MaterialVelocity.Easing] in [Map] of [String] to [MaterialVelocity.Easing]
 * variable `nameToEasing` whose key is our [MutableState] of [String] variable `currentEasing` if
 * that is not `null` or the `first` entry in `nameToEasing` if it is. Chanined to that is a
 * [Modifier.size] whose `size` is `80.dp`, chained to a [Modifier.background] whose whose `color`
 * is our [Color] variable `color` and whose `shape` is [CardDefaults.shape].
 *
 * In the [BoxScope] `content` composable lambda argument of this [Box] we compose a [Text] whose
 * `text` argument is `"Drag Me"`.
 *
 * Having created the area for the draggable item and touch input drawing, we move on to widgets
 * to allow the modification of the parameters controlling the animation:
 *
 * **First** we compose a [Row] whose `verticalAlignment` is [Alignment.CenterVertically]. In the
 * [RowScope] `content` composable lambda argument of the [Row] we compose:
 *  - A [Text] whose `text` argument displays the value of [MutableState] of [Float] variable
 *  `duration` with the caption "Duration:".
 *  - A [Spacer] whose `modifier` argument is a [Modifier.width] whose `width` is `8.dp`
 *  - A [Slider] whose `value` argument is our [MutableState] of [Float] variable `duration`, whose
 *  `onValueChange` argument is a lambda which accepts a [Float] and assigns it to our [MutableState]
 *  of [Float] variable `duration`, whose `valueRange` argument is `100f..4000f`, and whose `steps`
 *  argument is `4000f` minus `100f` divided by `100f`, rounded to the nearest integer, and whose
 *  `modifier` argument is a [RowScope.weight] whose `weight` is `1f` and whose `fill` is `true`.
 *
 * **Second** we compose a [Row] whose `verticalAlignment` is [Alignment.CenterVertically]. In the
 * [RowScope] `content` composable lambda argument of the [Row] we compose:
 *  - A [Text] whose `text` argument displays the value of [MutableState] of [Float] variable
 *  `maxVelocity` with the caption "MaxVelocity:".
 *  - A [Spacer] whose `modifier` argument is a [Modifier.width] whose `width` is `8.dp`
 *  - A [Slider] whose `value` argument is our [MutableState] of [Float] variable `maxVelocity`,
 *  whose `onValueChange` argument is a lambda which accepts a [Float] and assigns it to our
 *  [MutableState] of [Float] variable `maxVelocity`, whose `valueRange` argument is `100f..4000f`,
 *  whose `steps` argument is `4000f` minus `100f` divided by `100f`, and whose `modifier` argument
 *  is a [RowScope.weight] whose `weight` is `1f` and whose `fill` is `true`.
 *
 * **Third** we compose a [Row] whose `verticalAlignment` is [Alignment.CenterVertically]. In the
 * [RowScope] `content` composable lambda argument of the [Row] we compose:
 *  - A [Text] whose `text` argument displays the value of [MutableState] of [Float] variable
 *  `maxAcceleration` with the caption "MaxAcceleration:".
 *  - A [Spacer] whose `modifier` argument is a [Modifier.width] whose `width` is `8.dp`
 *  - A [Slider] whose `value` argument is our [MutableFloatState] variable `maxAcceleration`,
 *  whose `onValueChange` argument is a lambda which accepts a [Float] and assigns it to our
 *  [MutableFloatState] variable `maxAcceleration`, whose `valueRange` argument is `100f..4000f`,
 *  whose `steps` argument is `4000f` minus `100f` divided by `100f`, and whose `modifier` argument
 *  is a [RowScope.weight] whose `weight` is `1f` and whose `fill` is `true`.
 *
 * **Fourth** we compose a [Row] whose `verticalAlignment` is [Alignment.CenterVertically]. In the
 * [RowScope] `content` composable lambda argument of the [Row] we initialize and remember our
 * [MutableState] of [Boolean] variable `isExpanded` to an initial value of `false`. Next:
 *  - We compose a [Button] whose `onClick` argument is a lambda which sets `isExpanded` to `true`.
 *  In the [RowScope] `content` Composable lambda argument we compose a [Text] whose `text` argument
 *  displays the current value of our [MutableState] of [String] variable `currentEasing` with the
 *  caption "Easing:".
 *  - We compose a [DropdownMenu] whose `expanded` argument is the current value of our [MutableState]
 *  of [Boolean] variable `isExpanded`, and whose `onDismissRequest` argument is a lambda which sets
 *  `isExpanded` to `false. In the [ColumnScope] `content` composable lambda argument of the
 *  [DropdownMenu] we use the [Iterable.forEach] method of the [Set] returned by the [Map.keys]
 *  property of the [Map] of [String] to [MaterialVelocity.Easing] variable `nameToEasing` to
 *  iterate over the keys and compose a [DropdownMenuItem] for each key whose `text` argument is
 *  a [Text] whose `text` argument is the current key, and whose `onClick` argument is a lambda
 *  which sets our [MutableState] of [String] variable `currentEasing` to the current key.
 */
@Preview
@Composable
fun Material2DMotionPreview() {
    val duration: MutableState<Float> = remember { mutableFloatStateOf(value = 1200f) }
    val maxVelocity: MutableState<Float> = remember { mutableFloatStateOf(value = 2000f) }
    val maxAcceleration: MutableFloatState = remember { mutableFloatStateOf(value = 2000f) }
    val currentEasing: MutableState<String> = remember { mutableStateOf(value = "EaseOutBack") }
    val nameToEasing: Map<String, MaterialVelocity.Easing> = remember {
        mapOf(
            "Decelerate" to MaterialEasing.DECELERATE,
            "Linear" to MaterialEasing.LINEAR,
            "Overshoot" to MaterialEasing.OVERSHOOT,
            "EaseOutSine" to MaterialEasing.EASE_OUT_SINE,
            "EaseOutCubic" to MaterialEasing.EASE_OUT_CUBIC,
            "EaseOutQuint" to MaterialEasing.EASE_OUT_QUINT,
            "EaseOutCirc" to MaterialEasing.EASE_OUT_CIRC,
            "EaseOutQuad" to MaterialEasing.EASE_OUT_QUAD,
            "EaseOutQuart" to MaterialEasing.EASE_OUT_QUART,
            "EaseOutExpo" to MaterialEasing.EASE_OUT_EXPO,
            "EaseOutBack" to MaterialEasing.EASE_OUT_BACK,
            "EaseOutElastic" to MaterialEasing.EASE_OUT_ELASTIC
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val touchUpIndex: MutableIntState =
            remember { mutableIntStateOf(value = Integer.MAX_VALUE) }
        val accumulator: ArrayList<Offset> = remember { arrayListOf() }
        val offset: Animatable<Offset, AnimationVector2D> = remember {
            Animatable(
                initialValue = Offset.Zero,
                typeConverter = Offset.VectorConverter
            )
        }
        val referenceOffset: MutableState<Offset> = remember {
            mutableStateOf(value = Offset.Zero)
        }

        // Box that includes the draggable item and touch input drawing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f, fill = true)
                .drawWithContent {
                    drawContent()
                    // Draw recorded touch input points to reflect the behavior
                    // TODO: Draw curves from Velocity2D
                    offset.value // Trigger recomposition
                    if (accumulator.isEmpty()) {
                        return@drawWithContent
                    }
                    for (i in 0 until accumulator.size) {
                        drawLine(
                            color = if (i > touchUpIndex.intValue) Color.Red else Color.Blue,
                            start = (accumulator.getOrNull(index = i - 1)
                                ?: Offset.Zero) + referenceOffset.value,
                            end = accumulator[i] + referenceOffset.value,
                            strokeWidth = 2f
                        )
                    }
                },
            contentAlignment = { size: IntSize, space: IntSize, _ ->
                Offset(
                    x = (space.width / 2f) - (size.width / 2f),
                    y = (space.height / 2f) - (size.height / 2f)
                ).round()
            }
        ) {
            val color: Color = remember {
                Color.hsv(
                    hue = IntRange(start = 0, endInclusive = 360).random().toFloat(),
                    saturation = 0.5f,
                    value = 0.8f
                )
            }
            val velocityTracker: VelocityTracker = remember { VelocityTracker() }
            val scope: CoroutineScope = rememberCoroutineScope()
            Box(
                modifier = Modifier
                    .dragToAnimate(
                        offset = offset,
                        accumulator = accumulator,
                        touchUpIndex = touchUpIndex,
                        referenceOffset = referenceOffset,
                        scope = scope,
                        velocityTracker = velocityTracker,
                        duration = duration.value,
                        maxVelocity = maxVelocity.value,
                        maxAcceleration = maxAcceleration.floatValue,
                        easing = nameToEasing[currentEasing.value] ?: nameToEasing.values.first()
                    )
                    .size(size = 80.dp)
                    .background(color = color, shape = CardDefaults.shape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Drag Me")
            }
        }
        // Sliders to control the animation parameters, and a DropdownMenu to choose MaterialEasing
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Duration: ${duration.value.roundToInt()}ms")
            Spacer(modifier = Modifier.width(width = 8.dp))
            Slider(
                value = duration.value,
                onValueChange = { duration.value = it },
                valueRange = 100f..4000f,
                steps = ((4000f - 100f) / 100f).roundToInt() - 1,
                modifier = Modifier.weight(weight = 1f, fill = true)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "MaxVelocity: ${maxVelocity.value.roundToInt()}")
            Spacer(modifier = Modifier.width(width = 8.dp))
            Slider(
                value = maxVelocity.value,
                onValueChange = { maxVelocity.value = it },
                valueRange = remember { 100f..4000f },
                steps = remember { ((4000f - 100f) / 100f).roundToInt() - 1 },
                modifier = Modifier.weight(1f, true)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "MaxAcceleration: ${maxAcceleration.floatValue.roundToInt()}")
            Spacer(modifier = Modifier.width(width = 8.dp))
            Slider(
                value = maxAcceleration.floatValue,
                onValueChange = { maxAcceleration.floatValue = it },
                valueRange = remember { 100f..4000f },
                steps = remember { ((4000f - 100f) / 100f).roundToInt() - 1 },
                modifier = Modifier.weight(weight = 1f, fill = true)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            val isExpanded: MutableState<Boolean> = remember { mutableStateOf(value = false) }
            Button(onClick = { isExpanded.value = true }) {
                Text(text = "Easing: ${currentEasing.value}")
            }
            DropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = { isExpanded.value = false }
            ) {
                nameToEasing.keys.forEach { name: String ->
                    DropdownMenuItem(
                        text = { Text(text = name) },
                        onClick = {
                            currentEasing.value = name
                        }
                    )
                }
            }
        }
    }
}


/**
 * A Modifier extension function that enables drag-and-animate behavior for a Composable.
 *
 * When applied, the Composable can be dragged around the screen. Upon release, it will animate
 * back to its original position (Offset.Zero) using a physics-based animation defined by
 * `materialVelocity2D`.
 *
 * This function handles touch input for dragging, tracks velocity to provide a natural fling
 * motion upon release, and launches the animation coroutine.
 *
 * We start by chaining a [Modifier.onPlaced] to our [Modifier] receiver (this will invoke onPlaced
 * after the parent layout has been placed and before we are placed. This allows us to adjust oue
 * placement based on where the parent is). In the `onPlaced` lambda argument we accept the
 * [LayoutCoordinates] passed the lambda in variable `layoutCoordinates`. We initialize our [IntSize]
 * variable `parentSize` to the [LayoutCoordinates.size] of the [LayoutCoordinates.parentCoordinates]
 * property of our variable `layoutCoordinates` if that is not `null` or to [IntSize.Zero] if it is.
 * Then we set the value of our [MutableState] of [Offset] parameter [referenceOffset] to an [Offset]
 * whose `x` is the [IntSize.width] of `parentSize` divided by two and whose `y` is the [IntSize.height]
 * of `parentSize` divided by two.
 *
 * Then we chain to the [Modifier.onPlaced] a [Modifier.offset] whose `offset` argument is the
 * value of our [Animatable] of [Offset] parameter [offset].
 *
 * At the end of the chain we chain a [Modifier.pointerInput] whose `key1` argument is [Unit] and
 * in the [PointerInputScope] `block` lambda argument we call the [PointerInputScope.detectDragGestures]
 * method with the arguments:
 *  - `onDragStart`: a lambda which sets the value of our [MutableIntState] parameter [touchUpIndex]
 *  to [Integer.MAX_VALUE] and clears the value of our [ArrayList] parameter [accumulator].
 *  - `onDragEnd`: a lambda which uses the [CoroutineScope.launch] method of [CoroutineScope]
 *  parameter [scope] to launch a coroutine in which we:
 *  - set the value of our [MutableIntState] parameter [touchUpIndex] to the size of our [ArrayList]
 *  parameter [accumulator]
 *  - initialize our [Velocity] variable `initialVelocity` to the value returned by the
 *  [VelocityTracker.calculateVelocity] method of our [VelocityTracker] parameter [velocityTracker].
 *  - animate the value of our [Animatable] of [Offset] parameter [offset] to the `targetValue`
 *  [Offset.Zero], with an `animationSpec` of [materialVelocity2D] with its `durationMs` argument
 *  set to the value of our [Float] parameter [duration], its `maxVelocity` argument set to the
 *  value of our [Float] parameter [maxVelocity], its `maxAcceleration` argument set to the value
 *  of our [Float] parameter [maxAcceleration], and its `easing` argument set to the value of our
 *  [MaterialVelocity.Easing] parameter [easing], and the `initialVelocity` argument of the animation
 *  an [Offset] constructed from the value of our [Offset] variable `initialVelocity`. An in the
 *  [Animatable] of [Offset] `block` lambda argument we add the value of the [Offset] to our
 *  [ArrayList] of [Offset] parameter [accumulator].
 *  - call the [VelocityTracker.resetTracking] method of our [VelocityTracker] parameter
 *  [velocityTracker] to clear the tracked positions added.
 *
 * In the `onDragEnd` lambda argument of the [PointerInputScope.detectDragGestures] we accept the
 * [PointerInputChange] passed the lambda in variable `change` and the [Offset] passed the lambda
 * in variable `dragAmount` then we:
 *  - call the [VelocityTracker.addPointerInputChange] method with its `event` argument set to
 *  the value of our [PointerInputChange] variable `change`.
 *  - initialize our [Offset] variable `position` to the value of our [Animatable] of [Offset]
 *  parameter [offset] plus our [Offset] variable `dragAmount`.
 *  - add the value of our [Offset] variable `position` to our [ArrayList] of [Offset] parameter
 *  [accumulator].
 *  - use the [CoroutineScope.launch] method of [CoroutineScope] parameter [scope] to launch a
 *  coroutine in which we call the [Animatable.snapTo] method of our [Animatable] of [Offset]
 *  parameter [offset] with its `targetValue` argument our [Offset] variable `position`.
 *
 * @param offset The animatable offset of the composable. This is what gets updated during
 * the drag and subsequent animation.
 * @param accumulator A list to store the path of the drag and animation for drawing purposes.
 * @param touchUpIndex The index in the accumulator where the drag gesture ended.
 * @param referenceOffset The center offset of the parent, used to calculate the drawing path correctly.
 * @param scope The coroutine scope to launch the animation.
 * @param velocityTracker The VelocityTracker to calculate the fling velocity on release.
 * @param duration The duration of the return animation in milliseconds.
 * @param maxVelocity The maximum velocity for the animation.
 * @param maxAcceleration The maximum acceleration for the animation.
 * @param easing The easing function to be used for the animation.
 */
fun Modifier.dragToAnimate(
    offset: Animatable<Offset, AnimationVector2D>,
    accumulator: ArrayList<Offset>,
    touchUpIndex: MutableIntState,
    referenceOffset: MutableState<Offset>,
    scope: CoroutineScope,
    velocityTracker: VelocityTracker,
    duration: Float,
    maxVelocity: Float,
    maxAcceleration: Float,
    easing: MaterialVelocity.Easing
): Modifier = this
    .onPlaced { layoutCoordinates: LayoutCoordinates ->
        val parentSize: IntSize =
            layoutCoordinates.parentCoordinates?.size ?: IntSize.Zero
        referenceOffset.value = Offset(
            x = parentSize.width / 2f,
            y = parentSize.height / 2f
        )
    }
    .offset { offset.value.round() }
    .pointerInput(key1 = Unit) {
        detectDragGestures(
            onDragStart = {
                touchUpIndex.intValue = Integer.MAX_VALUE
                accumulator.clear()
            },
            onDragEnd = {
                scope.launch {
                    touchUpIndex.intValue = accumulator.size - 1
                    val initialVelocity: Velocity =
                        velocityTracker.calculateVelocity()

                    offset.animateTo(
                        targetValue = Offset.Zero,
                        animationSpec = materialVelocity2D(
                            durationMs = duration.roundToInt(),
                            maxVelocity = maxVelocity,
                            maxAcceleration = maxAcceleration,
                            easing = easing
                        ),
                        initialVelocity = Offset(
                            x = initialVelocity.x,
                            y = initialVelocity.y
                        )
                    ) {
                        accumulator.add(this.value)
                    }
                    velocityTracker.resetTracking()
                }
            }
        ) { change: PointerInputChange, dragAmount: Offset ->
            velocityTracker.addPointerInputChange(event = change)
            val position: Offset = offset.value + dragAmount
            accumulator.add(position)
            scope.launch {
                offset.snapTo(targetValue = position)
            }
        }
    }
