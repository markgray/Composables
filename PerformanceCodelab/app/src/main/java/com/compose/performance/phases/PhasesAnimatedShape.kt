/*
 * Copyright 2024 The Android Open Source Project
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
package com.compose.performance.phases

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.compose.performance.ui.theme.Purple80

/**
 * The small size of our animated shape. It is defined as 64 density-independent pixels (dp).
 */
private val smallSize = 64.dp

/**
 * The large size of our animated shape. It is defined as 200 density-independent pixels (dp).
 */
private val bigSize = 200.dp

/**
 * A Composable function that demonstrates an animated shape transition between two sizes.
 *
 * This function displays a shape (represented by [MyShape]) that smoothly changes its size
 * between a small size and a big size using Compose's animation capabilities.
 *
 * The shape's size is controlled by a state variable (`targetSize`) which can be toggled
 * by pressing a button. The actual size of the shape is animated using `animateDpAsState`,
 * which provides a smooth transition between the current size and the `targetSize`.
 *
 * The animation uses a spring-based animation with a high bouncy damping ratio and very low
 * stiffness, creating a visually appealing "bouncy" effect during the size change.
 *
 * @see androidx.compose.animation.core.animateDpAsState
 * @see androidx.compose.animation.core.spring
 * @see androidx.compose.runtime.mutableStateOf
 * @see androidx.compose.runtime.remember
 * @see androidx.compose.foundation.layout.Box
 * @see androidx.compose.material3.Button
 */
@Composable
fun PhasesAnimatedShape(): Unit = trace(sectionName = "PhasesAnimatedShape") {
    var targetSize: Dp by remember { mutableStateOf(value = smallSize) }
    val size: Dp by animateDpAsState(
        targetValue = targetSize,
        label = "box_size",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        MyShape(
            size = { size },
            modifier = Modifier.align(alignment = Alignment.Center)
        )
        Button(
            onClick = {
                targetSize = if (targetSize == smallSize) {
                    bigSize
                } else {
                    smallSize
                }
            },
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(text = "Toggle Size")
        }
    }
}

/**
 * A composable that draws a circular shape with a dynamic size.
 *
 * This composable creates a [Box] with a circular shape and a purple background.
 * The size of the circle is determined by the `size` parameter, which is a lambda
 * function that returns a [Dp] value. The size is constrained to be at least 0.
 * The `layout` modifier is used to precisely control the size and placement of the
 * circular `Box`.
 *
 * @param size A lambda function that returns the desired size (width and height) of the circle
 * in [Dp]. This allows for dynamic sizing based on external factors or state.
 * @param modifier An optional [Modifier] to be applied to the [Box]. This can be used to add
 * padding, margins, or other visual effects. Defaults to [Modifier].
 */
@Composable
fun MyShape(
    size: () -> Dp,
    modifier: Modifier = Modifier
): Unit = trace("MyShape") {
    Box(
        modifier = modifier
            .background(color = Purple80, shape = CircleShape)
            .layout { measurable: Measurable, _: Constraints ->
                val sizePx = size()
                    .roundToPx()
                    .coerceAtLeast(minimumValue = 0)

                val constraints = Constraints.fixed(
                    width = sizePx,
                    height = sizePx,
                )

                val placeable: Placeable = measurable.measure(constraints = constraints)
                layout(width = sizePx, height = sizePx) {
                    placeable.place(x = 0, y = 0)
                }
            }
    )
}
