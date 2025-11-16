/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.example.examplecomposegrid

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*

/**
 * Demonstrates the `row` grid helper in a JSON-based [ConstraintSet].
 *
 * This Composable creates a [ConstraintLayout] where five [Button]s are arranged horizontally
 * using a `row` helper. The buttons are spread evenly across the width of the parent,
 * with a horizontal gap of 10dp between them.
 */
@Preview(group = "row")
@Composable
fun RowJsonDemo() {
    ConstraintLayout(
        ConstraintSet(
            jsonContent = """
                    {
                        grid: { 
                            height: "parent",
                            width: "parent",
                            type: "row",
                            hGap: 10,
                            contains: ["btn1", "btn2", "btn3", "btn4", "btn5"],
                          },
                          btn1: {
                          height: "spread",
                          width: "spread",
                         },
                         btn2: {
                          height: "spread",
                          width: "spread",
                         },
                         btn3: {
                          height: "spread",
                          width: "spread",
                         },
                         btn4: {
                          height: "spread",
                          width: "spread",
                         },
                         btn5: {
                          height: "spread",
                          width: "spread",
                         },
                    }
                    """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 35.sp)
            }
        }
    }
}

/**
 * Demonstrates the `row` grid helper with `columnWeights` in a JSON-based [ConstraintSet].
 *
 * This Composable creates a [ConstraintLayout] where five [Button]s are arranged horizontally.
 * The `columnWeights` property is used to distribute the available width among the buttons
 * according to the specified weights (3, 2, 2, 1, 1). A horizontal gap of 10dp is maintained
 * between them.
 */
@Preview(group = "row")
@Composable
fun RowWeightsJsonDemo() {
    ConstraintLayout(
        ConstraintSet(
            jsonContent = """
                    {
                        grid: { 
                            height: "parent",
                            width: "parent",
                            type: "row",
                            hGap: 10,
                            columnWeights: "3,2,2,1,1",
                            contains: ["btn1", "btn2", "btn3", "btn4", "btn5"],
                          },
                          btn1: {
                          height: "spread",
                          width: "spread",
                         },
                         btn2: {
                          height: "spread",
                          width: "spread",
                         },
                         btn3: {
                          height: "spread",
                          width: "spread",
                         },
                         btn4: {
                          height: "spread",
                          width: "spread",
                         },
                         btn5: {
                          height: "spread",
                          width: "spread",
                         },
                    }
                    """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 35.sp)
            }
        }
    }
}

/**
 * Demonstrates the `column` grid helper in a JSON-based [ConstraintSet].
 *
 * This Composable creates a [ConstraintLayout] where five [Button]s are arranged vertically
 * using a `column` helper. The buttons are spread evenly down the height of the parent,
 * with a vertical gap of 10dp between them.
 */
@Preview(group = "column")
@Composable
fun ColumnJsonDemo() {
    ConstraintLayout(
        ConstraintSet(
            jsonContent = """
                    {
                        grid: { 
                            height: "parent",
                            width: "parent",
                            type: "column",
                            vGap: 10,
                            contains: ["btn1", "btn2", "btn3", "btn4", "btn5"],
                          },
                          btn1: {
                          height: "spread",
                          width: "spread",
                         },
                         btn2: {
                          height: "spread",
                          width: "spread",
                         },
                         btn3: {
                          height: "spread",
                          width: "spread",
                         },
                         btn4: {
                          height: "spread",
                          width: "spread",
                         },
                         btn5: {
                          height: "spread",
                          width: "spread",
                         },
                    }
                    """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 35.sp)
            }
        }
    }
}

/**
 * Demonstrates the `column` grid helper with `rowWeights` in a JSON-based [ConstraintSet].
 *
 * This Composable creates a [ConstraintLayout] where five [Button]s are arranged vertically.
 * The `rowWeights` property is used to distribute the available height among the buttons
 * according to the specified weights (3, 2, 2, 1, 1). A vertical gap of 10dp is maintained
 * between them.
 */
@Preview(group = "column")
@Composable
fun ColumnWeightsJsonDemo() {
    ConstraintLayout(
        ConstraintSet(
            jsonContent = """
                    {
                        grid: { 
                            height: "parent",
                            width: "parent",
                            type: "column",
                            vGap: 10,
                            rowWeights: "3,2,2,1,1",
                            contains: ["btn1", "btn2", "btn3", "btn4", "btn5"],
                          },
                          btn1: {
                          height: "spread",
                          width: "spread",
                         },
                         btn2: {
                          height: "spread",
                          width: "spread",
                         },
                         btn3: {
                          height: "spread",
                          width: "spread",
                         },
                         btn4: {
                          height: "spread",
                          width: "spread",
                         },
                         btn5: {
                          height: "spread",
                          width: "spread",
                         },
                    }
                    """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 35.sp)
            }
        }
    }
}