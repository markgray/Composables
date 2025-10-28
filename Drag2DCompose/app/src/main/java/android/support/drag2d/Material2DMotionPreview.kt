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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.input.pointer.PointerInputChange
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
 * TODO: Continue here.
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
        Modifier
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
            Modifier
                .fillMaxWidth()
                .weight(weight = 1f, fill = true)
                .drawWithContent {
                    drawContent()
                    // Draw recorded touch input points to reflect the behavior
                    // TODO: Draw curves from Velocity2D
                    offset.value // Trigger recomposition

                    @Suppress(
                        "UsePropertyAccessSyntax",
                        "RedundantSuppression"
                    ) // TODO: Gradle considers `isEmpty` property an error
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
                                            durationMs = duration.value.roundToInt(),
                                            maxVelocity = maxVelocity.value,
                                            maxAcceleration = maxAcceleration.floatValue,
                                            easing = nameToEasing[currentEasing.value]
                                                ?: nameToEasing.values.first()
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
                    .size(size = 80.dp)
                    .background(color = color, shape = CardDefaults.shape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Drag Me")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Duration: ${duration.value.roundToInt()}ms")
            Spacer(Modifier.width(width = 8.dp))
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
            Button(onClick = {
                isExpanded.value = true
            }) {
                Text(text = "Easing: ${currentEasing.value}")
            }
            DropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = { isExpanded.value = false }
            ) {
                nameToEasing.keys.forEach { name ->
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