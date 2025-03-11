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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.codelab.layouts.ui.LayoutsCodelabTheme

/**
 * This Composable demonstrates how to use the [Layout] method to create a custom layout that will
 * measure and position zero or more layout children. Our root Composable is a [Layout] whose
 * `modifier` argument is our [modifier] parameter, and whose `content` argument is our [content]
 * parameter. The arguments of the lambda argument of [Layout] are `measurables` (a [List] of
 * [Measurable]) and `constraints` (the [Constraints] imposed on our Composable by its parent).
 * We start by initializing our [List] of [Placeable] variable to the [List] created by using the
 * [Iterable.map] method of `measurables` to create a [Placeable] out of every [Measurable] in
 * `measurables` using its [Measurable.measure] method with the [Constraints] `constraints` as its
 * argument. When done creating the [List] of [Placeable]'s we initialize our [Int] variable
 * `var yPosition` to 0 (we will use this to track the `y` coordinate we have placed children up to).
 * Next we call the [layout] method with its `width` argument the [Constraints.maxWidth] property
 * of `constraints` and its `height` argument the [Constraints.maxHeight] property of `constraints`.
 * In the lambda argument of [layout] we loop over all or the [Placeable] entries in `placeables`
 * calling the `placeRelative` extension function of [Placeable] to position the item at `x` = 0,
 * and `y` = `yPosition` and then add the [Placeable.height] of the `placeable` to `yPosition` to
 * produce the `y` coordinate for the next [Placeable] to be positioned.
 *
 * @param modifier a [Modifier] that our caller can use to modify our appearance or behavior.
 * @param content zero or more Composables for us to measure and position in the space allotted us.
 */
@Composable
fun MyOwnColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables: List<Placeable> = measurables.map { measurable: Measurable ->
            // Measure each child
            measurable.measure(constraints)
        }

        // Track the y co-ord we have placed children up to
        var yPosition = 0

        // Set the size of the layout as big as it can
        layout(width = constraints.maxWidth, height = constraints.maxHeight) {
            // Place children in the parent layout
            placeables.forEach { placeable: Placeable ->
                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}

/**
 * The Preview of the [MyOwnColumn] Composable. We use our [LayoutsCodelabTheme] custom [MaterialTheme]
 * to wrap our content which consists of a [Surface] whose `modifier` argument uses a [Modifier.padding]
 * of 8.dp to add 8.dp padding around its content which consists of a [MyOwnColumn] Composable holding
 * four [Text] Composables displaying the texts "MyOwnColumn", "places items", "vertically.", and
 * "We've done it by hand!" respectively.
 */
@Preview
@Composable
fun MyOwnColumnPreview() {
    LayoutsCodelabTheme {
        Surface(modifier = Modifier.padding(8.dp)) {
            MyOwnColumn {
                Text(text = "MyOwnColumn")
                Text(text = "places items")
                Text(text = "vertically.")
                Text(text = "We've done it by hand!")
            }
        }
    }
}
