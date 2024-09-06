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

package com.codelab.layouts

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.codelab.layouts.ui.LayoutsCodelabTheme

/**
 * This creates a [Modifier] which produces a `padding` which is measured from the [FirstBaseline]
 * of the Composable it is applied to, instead of padding from the `top` of the Composable. We use
 * the [Modifier.then] function on `this` [Modifier] to concatenate it with a [LayoutModifier]
 * created by the [Modifier.layout] function. In the lambda argument to that function we set
 * our [Placeable] variable `val placeable` to the instance returned when we call the method
 * [Measurable.measure] of the `measurable` argument of the lambda when using its [Constraints]
 * argument `constraints` as the argument of [Measurable.measure] (which measures the layout with
 * constraints, returning a [Placeable] layout that has its new size). We use the [check] method
 * to make sure that the [FirstBaseline] property of `placeable` is not [AlignmentLine.Unspecified],
 * throwing [IllegalStateException] if it is. Otherwise we set our [Int] variable `val firstBaseline`
 * to the value of the [FirstBaseline] property of `placeable`. We use the `Dp.roundToPx` extension
 * function to convert our [Dp] parameter [firstBaselineToTop] to pixels by rounding it and subtract
 * `firstBaseline` from it to initialize our variable `val placeableY`. We initialize our variable
 * `val height` by adding `placeableY` to the `height` of `placeable`. Finally we call the [layout]
 * method specifying a `width` of the `width` property of `placeable` and a `height` of our variable
 * `height`. In the lambda argument of [layout] we use the `Placeable.placeRelative` method of
 * `placeable` to place it at `x` = 0, and `y` = `placeableY`.
 *
 * @param firstBaselineToTop the amount of padding in [Dp] to add between the first baseline and
 * the top of the composable.
 */
fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
): Modifier = this.layout { measurable: Measurable, constraints: Constraints ->
        val placeable: Placeable = measurable.measure(constraints)

        // Check the composable has a first baseline
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline: Int = placeable[FirstBaseline]

        // Height of the composable with padding - first baseline
        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY
        layout(width = placeable.width, height = height) {
            // Where the composable gets placed
            placeable.placeRelative(x = 0, y = placeableY)
        }
    }

/**
 * Preview of a [Text] wrapped in our [LayoutsCodelabTheme] custom [MaterialTheme] which uses
 * a [Modifier.firstBaselineToTop] of 32.dp as its `modifier` argument to add padding of 32.dp
 * measured from the first baseline to the top of the [Text].
 */
@Preview
@Composable
fun TextWithPaddingToBaselinePreview() {
    LayoutsCodelabTheme {
        Text(text = "Hi there!", modifier = Modifier.firstBaselineToTop(32.dp))
    }
}

/**
 * Preview of a [Text] wrapped in our [LayoutsCodelabTheme] custom [MaterialTheme] which uses
 * a [Modifier.padding] of 32.dp as its `modifier` argument to add padding of 32.dp at the `top`
 * of the [Text].
 */
@Preview
@Composable
fun TextWithNormalPaddingPreview() {
    LayoutsCodelabTheme {
        Text(text = "Hi there!", modifier = Modifier.padding(top = 32.dp))
    }
}
