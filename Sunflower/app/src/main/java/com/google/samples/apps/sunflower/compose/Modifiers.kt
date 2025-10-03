/*
 * Copyright 2020 Google LLC
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

package com.google.samples.apps.sunflower.compose

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints

/**
 * Hides an element on the screen leaving its space occupied.
 * This should be replaced with the real visible modifier in the future:
 * https://issuetracker.google.com/issues/158837937
 *
 * @param isVisible is of type () -> Boolean because if the calling composable doesn't own the
 * state boolean of that Boolean, a read (recompose) will be avoided.
 */
fun Modifier.visible(isVisible: () -> Boolean): Modifier =
    this.then(other = VisibleModifier(isVisible = isVisible))

/**
 * A [LayoutModifier] that controls the visibility of a composable. When [isVisible] is false,
 * the composable is not drawn, but it still occupies space in the layout.
 *
 * This is a private implementation detail of the [Modifier.visible] extension function.
 *
 * @param isVisible A lambda that returns `true` if the composable should be visible, false otherwise.
 */
private data class VisibleModifier(
    private val isVisible: () -> Boolean
) : LayoutModifier {
    /**
     * The function used to measure the modifier. The [measurable] corresponds to the wrapped
     * content, and it can be measured with the desired constraints according to the logic of the
     * [LayoutModifier]. The modifier needs to choose its own size, which can depend on the size
     * chosen by the wrapped content (the obtained [Placeable]), if the wrapped content was
     * measured. The size needs to be returned as part of a [MeasureResult], alongside the placement
     * logic of the [Placeable], which defines how the wrapped content should be positioned inside
     * the [LayoutModifier]. A convenient way to create the [MeasureResult] is to use the
     * [MeasureScope.layout] factory function.
     *
     * A [LayoutModifier] uses the same measurement and layout concepts and principles as a
     * [Layout], the only difference is that they apply to exactly one child. For a more detailed
     * explanation of measurement and layout, see [MeasurePolicy].
     *
     * We initialize our [Placeable] variable `placeable` to the value returned by the
     * [Measurable.measure] method of our [Measurable] parameter [measurable] for the `constraints`
     * argument our [Constraints] parameter [constraints]. Then we return the [MeasureResult] that
     * the [layout] method of [MeasureScope] produces for the `width` and `height` arguments the
     * [Placeable.width] and [Placeable.height] of our [Placeable] variable `placeable`. In the
     * [Placeable.PlacementScope] `placementBlock` of the [layout] if the [isVisible] lambda
     * argument is `true`, we call the [Placeable.PlacementScope.place] method of our [Placeable]
     * variable `placeable` to place the [Placeable] in the layout, otherwise we do nothing.
     *
     * @param measurable The [Measurable] to measure and layout.
     * @param constraints The [Constraints] that the [Measurable] can be measured in.
     */
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable: Placeable = measurable.measure(constraints = constraints)
        return layout(width = placeable.width, height = placeable.height) {
            if (isVisible()) {
                placeable.place(x = 0, y = 0)
            }
        }
    }
}
