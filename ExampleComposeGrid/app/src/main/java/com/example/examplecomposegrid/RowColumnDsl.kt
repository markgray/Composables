/*
 * Copyright (C) 2021 The Android Open Source Project
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

@file:Suppress("UnusedImport")

package com.example.examplecomposegrid

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension

/**
 * Demonstrates the [ConstraintSetScope.createRow] DSL to arrange multiple widgets horizontally.
 * The widgets are spread across the available width, separated by a `horizontalGap`.
 * In this example, five buttons are placed in a row with a gap of `10.dp` between them.
 */
@Preview(group = "row")
@Composable
fun RowDslDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a: ConstrainedLayoutReference = createRefFor(id = "1")
            val b: ConstrainedLayoutReference = createRefFor(id = "2")
            val c: ConstrainedLayoutReference = createRefFor(id = "3")
            val d: ConstrainedLayoutReference = createRefFor(id = "4")
            val e: ConstrainedLayoutReference = createRefFor(id = "5")
            val g1: ConstrainedLayoutReference = createRow(
                a, b, c, d, e,
                horizontalGap = 10.dp,
            )

            constrain(ref = g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
            constrain(ref = a) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = b) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = c) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = d) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = e) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier
                    .layoutId(layoutId = num)
                    .width(width = 120.dp),
                onClick = {},
            ) {
                Text(text = String.format("btn%s", num))
            }
        }
    }
}

/**
 * Demonstrates the [ConstraintSetScope.createRow] DSL to arrange multiple widgets horizontally,
 * distributing the available width according to the provided `columnWeights`.
 * In this example, five buttons are placed in a row with a `horizontalGap` of `10.dp`.
 * The widths are weighted with values of `[3, 3, 2, 2, 1]`, meaning the first two buttons
 * will each take up 3 parts of the available space, the next two will take up 2 parts each,
 * and the last button will take up 1 part.
 */
@Preview(group = "row")
@Composable
fun RowWeightsDslDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a: ConstrainedLayoutReference = createRefFor(id = "1")
            val b: ConstrainedLayoutReference = createRefFor(id = "2")
            val c: ConstrainedLayoutReference = createRefFor(id = "3")
            val d: ConstrainedLayoutReference = createRefFor(id = "4")
            val e: ConstrainedLayoutReference = createRefFor(id = "5")
            val weights: IntArray = intArrayOf(3, 3, 2, 2, 1)
            val g1: ConstrainedLayoutReference = createRow(
                a, b, c, d, e,
                horizontalGap = 10.dp,
                columnWeights = weights,
            )

            constrain(ref = g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
            constrain(ref = a) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = b) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = c) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = d) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = e) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier
                    .layoutId(layoutId = num)
                    .width(120.dp),
                onClick = {},
            ) {
                Text(text = String.format("btn%s", num))
            }
        }
    }
}

/**
 * Demonstrates the [ConstraintSetScope.createColumn] DSL to arrange multiple widgets vertically.
 * The widgets are spread across the available height, separated by a `verticalGap`.
 * In this example, five buttons are placed in a column with a gap of `10.dp` between them.
 */
@Preview(group = "column")
@Composable
fun ColumnDslDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a: ConstrainedLayoutReference = createRefFor(id = "1")
            val b: ConstrainedLayoutReference = createRefFor(id = "2")
            val c: ConstrainedLayoutReference = createRefFor(id = "3")
            val d: ConstrainedLayoutReference = createRefFor(id = "4")
            val e: ConstrainedLayoutReference = createRefFor(id = "5")
            val g1: ConstrainedLayoutReference = createColumn(
                a, b, c, d, e,
                verticalGap = 10.dp,
            )

            constrain(ref = g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
            constrain(ref = a) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = b) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = c) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = d) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = e) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier
                    .layoutId(layoutId = num)
                    .width(width = 120.dp),
                onClick = {},
            ) {
                Text(text = String.format("btn%s", num))
            }
        }
    }
}

/**
 * Demonstrates the [ConstraintSetScope.createColumn] DSL to arrange multiple widgets vertically,
 * distributing the available height according to the provided `rowWeights`.
 * In this example, five buttons are placed in a column with a `verticalGap` of `10.dp`.
 * The heights are weighted with values of `[3, 3, 2, 2, 1]`, meaning the first two buttons
 * will each take up 3 parts of the available space, the next two will take up 2 parts each,
 * and the last button will take up 1 part.
 */
@Preview(group = "column")
@Composable
fun ColumnWeightsDslDemo() {
    ConstraintLayout(
        ConstraintSet {
            val a: ConstrainedLayoutReference = createRefFor(id = "1")
            val b: ConstrainedLayoutReference = createRefFor(id = "2")
            val c: ConstrainedLayoutReference = createRefFor(id = "3")
            val d: ConstrainedLayoutReference = createRefFor(id = "4")
            val e: ConstrainedLayoutReference = createRefFor(id = "5")
            val weights: IntArray = intArrayOf(3, 3, 2, 2, 1)
            val g1: ConstrainedLayoutReference = createColumn(
                a, b, c, d, e,
                verticalGap = 10.dp,
                rowWeights = weights,
            )

            constrain(ref = g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
            constrain(ref = a) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = b) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = c) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = d) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = e) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier
                    .layoutId(layoutId = num)
                    .width(width = 120.dp),
                onClick = {},
            ) {
                Text(text = String.format("btn%s", num))
            }
        }
    }
}