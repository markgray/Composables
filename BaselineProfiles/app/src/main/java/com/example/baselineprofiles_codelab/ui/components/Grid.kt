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

package com.example.baselineprofiles_codelab.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import com.example.baselineprofiles_codelab.model.SearchCategoryCollection

/**
 * A simple grid which lays elements out vertically in evenly sized [columns]. This is used by the
 * `SearchCategoryCollection` composable to display its [SearchCategoryCollection] argument, and it
 * is used by [com.example.baselineprofiles_codelab.ui.home.search.SearchCategories] to display its
 * [List] of [SearchCategoryCollection] argument. Our root Composable is a [Layout] whose `content`
 * argument is our lambda parameter [content], and whose `modifier` argument is our [Modifier]
 * parameter [modifier].
 *
 * In the [MeasureScope] lambda block of the [Layout] we accept the [List] of [Measurable] passed the
 * lambda in our variable `measurables` and the [Constraints] passed it in our variable `constraints`.
 * We initialize our [Int] variable `val itemWidth` to the [Constraints.maxWidth] of `constraints`
 * divided by our [Int] parameter [columns]. We then initialize our [Constraints] variable
 * `val itemConstraints` to a copy of `constraints` with the overridden values `minWidth` =
 * `itemWidth` and `maxWidth` = `itemWidth`. We initialize our [List] of [Placeable] variable
 * `val placeables` by using the [Iterable.map] method of [List] of [Measurable] variable
 * `measurables` to apply [Measurable.measure] using `itemConstraints` as the `constraints` argument
 * to each of the [Measurable] in the [List]. We initialize our [Array] of [Int] variable
 * `val columnHeights` to an [Int] parameter [columns] sized array initialized to all zeros. We use
 * the [Iterable.forEachIndexed] method of `placeables` to loop over its contents accepting the index
 * in [Int] variable `index` and the [Placeable] in [Placeable] variable `placeable`. We initialize
 * our [Int] variable `val column` to `index` modulo our [Int] parameter [columns] and add the
 * [Placeable.height] of `placeable` to the `column` index of `columnHeights`. When done we initialize
 * our [Int] variable `val height` to the maximum value in `columnHeights` coerced to at most the
 * [Constraints.maxHeight] of `constraints`. Then we call the [MeasureScope.layout] method with its
 * `width` argument the [Constraints.maxWidth] of `constraints` and its `height` argument our [Int]
 * variable `height`. In the `placementBlock` lambda we initialize our [Array] of [Int] variable
 * `val columnY` to an [Array] to an [Int] parameter [columns] sized array initialized to all zeros.
 * Then we use the [Iterable.forEachIndexed] method of `placeables` to loop through all its entries
 * accepting the index in [Int] variable `index` and the [Placeable] in [Placeable] variable `placeable`.
 * Then we use the [Placeable.PlacementScope.placeRelative] method of `placeable` to place it at `x`
 * coordinate `column` times `itemWidth` and `y` coordinate of the `column` entry of array `columnY`.
 * Then we add the [Placeable.height] of `placeable` to the value of the `column` entry in `columnY`
 * and loop around for the next [Placeable].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller passes us a [Modifier.padding] that adds `16.dp` to each of our horizontal
 * sides.
 * @param columns the number of columns we should use. Our caller does not pass us a value so our
 * default of `2` is used.
 * @param content the Composable lambda containing multiple Composables which we should display in
 * our grid.
 */
@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables: List<Measurable>, constraints: Constraints ->
        val itemWidth: Int = constraints.maxWidth / columns
        // Keep given height constraints, but set an exact width
        val itemConstraints: Constraints = constraints.copy(
            minWidth = itemWidth,
            maxWidth = itemWidth
        )
        // Measure each item with these constraints
        val placeables: List<Placeable> = measurables.map { it.measure(constraints = itemConstraints) }
        // Track each columns height so we can calculate the overall height
        val columnHeights: Array<Int> = Array(size = columns) { 0 }
        placeables.forEachIndexed { index: Int, placeable: Placeable ->
            val column: Int = index % columns
            columnHeights[column] += placeable.height
        }
        val height: Int = (columnHeights.maxOrNull() ?: constraints.minHeight)
            .coerceAtMost(constraints.maxHeight)
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            // Track the Y co-ord per column we have placed up to
            val columnY: Array<Int> = Array(columns) { 0 }
            placeables.forEachIndexed { index: Int, placeable: Placeable ->
                val column = index % columns
                placeable.placeRelative(
                    x = column * itemWidth,
                    y = columnY[column]
                )
                columnY[column] += placeable.height
            }
        }
    }
}
