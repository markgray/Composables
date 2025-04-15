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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.trace
import com.compose.performance.R


/**
 * Displays the Compose logo centered within the available screen space.
 *
 * This composable function renders the Compose logo image, ensuring it's
 * centered within its parent container. It dynamically calculates the logo's
 * position based on the available size and the logo's intrinsic dimensions.
 *
 * The function utilizes `remember` and `mutableStateOf` to efficiently manage
 * the state of the container's size, triggering recomposition only when necessary.
 * It also leverages the `onPlaced` modifier to get the parent container size.
 * The `logoPosition` function calculates the correct offset for centering the logo.
 *
 * The [Image] composable displays the actual logo and is placed using the calculated offset.
 */
@Composable
fun PhasesComposeLogo(): Unit = trace(sectionName = "PhasesComposeLogo") {
    val logo: Painter = painterResource(id = R.drawable.compose_logo)
    var size: IntSize by remember { mutableStateOf(IntSize.Zero) }
    val logoPosition: IntOffset by logoPosition(size = size, logoSize = logo.intrinsicSize)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onPlaced { coord: LayoutCoordinates ->
                size = coord.size
            }
    ) {
        with(LocalDensity.current) {
            Image(
                painter = logo,
                contentDescription = "logo",
                modifier = Modifier.offset { IntOffset(logoPosition.x,  logoPosition.y) }
            )
        }
    }
}

/**
 * Calculates the animated position of a logo within a given area.
 *
 * This composable function uses `produceState` to create a state object that represents the
 * current position of a moving logo. The logo bounces off the edges of the defined area.
 * The movement is continuous and controlled by the `MOVE_SPEED` constant.
 *
 * @param size The size of the area within which the logo should move. If this is `IntSize.Zero`,
 * the logo will remain stationary at the origin (0, 0).
 * @param logoSize The size of the logo itself. This is used to ensure the logo bounces off the
 * edges correctly and does not go outside of the bounds.
 * @return A [State] of [IntOffset]` object that represents the current position of the logo. The
 * [IntOffset] values indicate the `x` and `y` coordinates of the top-left corner of the logo
 * within the bounds.
 */
@Composable
fun logoPosition(size: IntSize, logoSize: Size): State<IntOffset> =
    produceState(initialValue = IntOffset.Zero, size, logoSize) {
        if (size == IntSize.Zero) {
            this.value = IntOffset.Zero
            return@produceState
        }

        var xDirection = 1
        var yDirection = 1

        while (true) {
            withFrameMillis {
                value += IntOffset(x = MOVE_SPEED * xDirection, y = MOVE_SPEED * yDirection)

                if (value.x <= 0 || value.x >= size.width - logoSize.width) {
                    xDirection *= -1
                }

                if (value.y <= 0 || value.y >= size.height - logoSize.height) {
                    yDirection *= -1
                }
            }
        }
    }

/**
 * The speed at which our Compose logo moves, measured in units per frame. This constant determines
 * how many units the [IntOffset] returned by [logoPosition] will move in each update cycle.
 * A higher value means faster movement, while a lower value means slower movement.
 */
internal const val MOVE_SPEED = 10
