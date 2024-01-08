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

package com.example.compose.jetchat.components

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

/**
 * Applied to a [Text], it sets the distance between the top and the first baseline. It
 * also makes the bottom of the element coincide with the last baseline of the text.
 *
 *     _______________
 *     |             |   ↑
 *     |             |   |  heightFromBaseline
 *     |Hello, World!|   ↓
 *     ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 *
 * This modifier can be used to distribute multiple text elements using a certain distance between
 * baselines.
 *
 * @param heightFromBaseline the distance between the top and the first baseline.
 */
data class BaselineHeightModifier(
    val heightFromBaseline: Dp
) : LayoutModifier {

    /**
     * The function used to measure the modifier. The measurable corresponds to the wrapped content,
     * and it can be measured with the desired constraints according to the logic of the [LayoutModifier].
     * The modifier needs to choose its own size, which can depend on the size chosen by the wrapped
     * content (the obtained [Placeable]), if the wrapped content was measured. The size needs to be
     * returned as part of a [MeasureResult], alongside the placement logic of the [Placeable], which
     * defines how the wrapped content should be positioned inside the [LayoutModifier]. A convenient
     * way to create the [MeasureResult] is to use the [MeasureScope.layout] factory function.
     *
     * A [LayoutModifier] uses the same measurement and layout concepts and principles as a [Layout],
     * the only difference is that they apply to exactly one child. For a more detailed explanation
     * of measurement and layout, see [MeasurePolicy].
     *
     * We start by initializing our [Placeable] variable `val textPlaceable` to the [Placeable] that
     * is returned by the [Measurable.measure] method of our [Measurable] parameter [measurable],
     * when called with our [Constraints] parameter [constraints]. We initialize our [Int] variable
     * `val firstBaseline` to the [AlignmentLine] defined by the baseline of a first line of the
     * [Text] of [Placeable] `textPlaceable`, and our [Int] variable `val lastBaseline` to the
     * [AlignmentLine] defined by the baseline of a last line of the [Text] of [Placeable]
     * `textPlaceable`. Then we initialize our [Int] variable `val height` to our [Dp] field
     * [heightFromBaseline] rounded to Px, plus `lastBaseline` minus `firstBaseline`. Finally we
     * return a [layout] whose `width` argument is the [Constraints.maxWidth] of our [constraints]
     * parameter, whose `height` argument is our `height` variable and whose `placementBlock` is
     * a lambda that initializes its [Int] variable `val topY` to our [Dp] field [heightFromBaseline]
     * rounded to Px minus `firstBaseline`, then returns the result of calling the `Placeable.place`
     * method of `textPlaceable` to place it at the coordinate `x` = 0, and `y` = `topY`.
     *
     * @param measurable the [Text] whose `modifier` argument is using us.
     * @param constraints the incoming [Constraints] of the [Text].
     */
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {

        val textPlaceable: Placeable = measurable.measure(constraints = constraints)
        val firstBaseline: Int = textPlaceable[FirstBaseline]
        val lastBaseline: Int = textPlaceable[LastBaseline]

        val height: Int = heightFromBaseline.roundToPx() + lastBaseline - firstBaseline
        return layout(width = constraints.maxWidth, height = height) {
            val topY: Int = heightFromBaseline.roundToPx() - firstBaseline
            textPlaceable.place(x = 0, y = topY)
        }
    }
}

/**
 * This extension function turns our [BaselineHeightModifier] custom [LayoutModifier] into a [Modifier]
 * that can be chained.
 *
 * @param heightFromBaseline the distance between the top and the first baseline.
 */
fun Modifier.baselineHeight(heightFromBaseline: Dp): Modifier =
    this.then(BaselineHeightModifier(heightFromBaseline = heightFromBaseline))
