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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.codelab.layouts.ui.LayoutsCodelabTheme
import kotlin.math.max

val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)

@Composable
fun LayoutsCodelab() {
    /**
     * Scaffold implements the basic material design visual layout structure. This component provides
     * API to put together several material components to construct your screen, by ensuring proper
     * layout strategy for them and collecting necessary data so these components will work together
     * correctly.
     *
     * Parameters:
     *
     * topBar - top app bar of the screen, which is a  `Composable () -> Unit = {}`
     * We use a TopAppBar.
     *
     * content - content of your screen. The lambda receives an PaddingValues that should be applied
     * to the content root via Modifier.padding to properly offset top and bottom bars. If you're
     * using VerticalScroller, apply this modifier to the child of the scroller, and not on the
     * scroller itself. Our content is our [BodyContent] Composable which is called with a padding
     * [Modifier] created from the [PaddingValues] parameter of the content lambda.
     */
    Scaffold(
        topBar = {
            /**
             * Material Design top app bar. The top app bar displays information and actions relating
             * to the current screen. This TopAppBar has slots for a title, navigation icon, and
             * actions. Note that the title slot is inset from the start according to spec - for
             * custom use cases such as horizontally centering the title, use the other TopAppBar
             * overload for a generic TopAppBar with no restriction on content.
             *
             * Parameters:
             *
             * title - The title to be displayed in the center of the TopAppBar. A Composable fun,
             * we use a [Text] Composable displaying the `text` "LayoutsCodelab".
             *
             * actions - The actions displayed at the end of the TopAppBar. This should typically be
             * IconButtons. The default layout here is a Row, so icons inside will be placed
             * horizontally. We use a [IconButton] Composable which holds an [Icon] Composable that
             * displays the [Icons.Filled.Favorite] system [ImageVector].
             */
            TopAppBar(
                title = {
                    Text(text = "LayoutsCodelab")
                },
                actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(imageVector = Icons.Filled.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding: PaddingValues ->
        BodyContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    /**
     * A layout composable that places its children in a horizontal sequence. For a layout composable
     * that places its children in a vertical sequence, see [Column]. Note that by default items do
     * not scroll; see `Modifier.horizontalScroll` to add this behavior. For a horizontally
     * scrollable list that only composes and lays out the currently visible items see `LazyRow`.
     *
     * Parameters:
     *
     * modifier - The modifier to be applied to the Row, we take our padding [Modifier] parameter
     * [modifier], set its background color to [Color.LightGray], set its padding to 16dp, set its
     * size to 200dp, and modify it using `horizontalScroll` to allow to scroll horizontally with
     * the `state` of the scroll using [rememberScrollState] to create and automatically remember
     * that ScrollState with default parameters.
     *
     * content - The Composable lambda that we are to layout horizontally, in our case this is a
     * [StaggeredGrid] Composable which holds a [Chip] Composable for each of strings in our
     * [topics] list.
     */
    Row(
        modifier = modifier
            .background(color = Color.LightGray)
            .padding(16.dp)
            .size(200.dp)
            .horizontalScroll(state = rememberScrollState()),
        content = {
            StaggeredGrid {
                for (topic in topics) {
                    Chip(modifier = Modifier.padding(8.dp), text = topic)
                }
            }
        }
    )
}

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    /**
     * Layout is the main core component for layout. It can be used to measure and position zero or
     * more layout children. The measurement, layout and intrinsic measurement behaviours of this
     * layout will be defined by the measurePolicy instance. See MeasurePolicy for more details.
     *
     * modifier - Modifiers to be applied to the layout.
     *
     * content - The children composable to be laid out, in our case the `content` Composable that
     * [StaggeredGrid] is called with which is a bunch of `Chip` Composables.
     */
    Layout(
        modifier = modifier,
        content = content
    ) { measurables: List<Measurable>, constraints: Constraints ->

        // Keep track of the width of each row
        val rowWidths = IntArray(rows) { 0 }

        // Keep track of the max height of each row
        val rowHeights = IntArray(rows) { 0 }

        /**
         * This is where we have each of our children in our [List] of [Measurable] parameter measure
         * itself by calling its [Measurable.measure] method given our [Constraints] parameter
         * [constraints] returning a [Placeable] child layout that has its new size which we can
         * then position as its parent layout. The [mapIndexed] extension function places each of
         * these [Placeable] objects in a new [List] of [Placeable] objects.
         */
        val placeables: List<Placeable> = measurables.mapIndexed { index, measurable ->
            // Measure each child
            // Don't constrain child views further, measure them with given constraints
            // List of measured children
            val placeable: Placeable = measurable.measure(constraints)

            // Track the width and max height of each row
            val row: Int = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        val width = rowWidths.maxOrNull()
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth

        // Grid's height is the sum of the tallest element of each row
        // coerced to the height constraints
        val height = rowHeights.sumOf { it }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        // Y of each row, based on the height accumulation of previous rows
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }

        // Set the size of the parent layout
        layout(width, height) {
            // x co-ord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }

            /**
             * This is where we go through all of the [Placeable] objects in the [List] of [Placeable]
             * that we created from our [List] of [Measurable] parameter and place them a X and Y
             * positions relative to this parent coordinate system using the `Placeable.placeRelative`
             * method.
             */
            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
fun Chip(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp, 16.dp)
                    .background(color = MaterialTheme.colors.secondary)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text)
        }
    }
}

@Preview
@Composable
fun ChipPreview() {
    LayoutsCodelabTheme {
        Chip(text = "Hi there")
    }
}

@Preview
@Composable
fun LayoutsCodelabPreview() {
    LayoutsCodelabTheme {
        LayoutsCodelab()
    }
}
