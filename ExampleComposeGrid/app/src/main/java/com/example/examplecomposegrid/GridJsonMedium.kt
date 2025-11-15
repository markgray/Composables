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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*

/**
 * Creates a Keypad layout using [ConstraintLayout] and a JSON-based [ConstraintSet].
 *
 * This demonstrates how to build a grid-based UI, like a phone keypad, by defining the grid
 * properties (rows, columns, gaps, spans, etc.) within a JSON string.
 *
 * The layout consists of:
 *  - A 5x3 grid.
 *  - A display `Box` at the top, spanning all 3 columns of the first row.
 *  - Ten `Button`s for numbers 0-9.
 *  - One empty cell is skipped to position the '0' button correctly.
 *  - The display `Box` has a `rowWeight` of 2, making it twice as tall as the other rows.
 */
@Preview(group = "keypad")
@Composable
fun GridJsonKeypad() {
    ConstraintLayout(
        ConstraintSet(
            jsonContent = """
                    {
                        grid: { 
                            height: "parent",
                            width: "parent",
                            type: "grid",
                            vGap: 10,
                            hGap: 10,
                            rows: 5,
                            columns: 3,
                            spans: "0:1x3",
                            skips: "12:1x1",
                            rowWeights: "2,1,1,1,1",
                            contains: ["box", "btn1", "btn2", "btn3",
                              "btn4", "btn5", "btn6", "btn7", "btn8", "btn9","btn0"],
                            top: ["parent", "top", 20],
                            start: ["parent", "start", 20],
                            end: ["parent", "end", 20],
                            bottom: ["parent", "bottom", 20],
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
                         btn6: {
                          height: "spread",
                          width: "spread",
                         },
                         btn7: {
                          height: "spread",
                          width: "spread",
                         },
                         btn8: {
                          height: "spread",
                          width: "spread",
                         },
                         btn9: {
                          height: "spread",
                          width: "spread",
                         },
                         btn0: {
                          height: "spread",
                          width: "spread",
                         },
                         box: {
                          height: "spread",
                          width: "spread",
                         }
                    }
                    """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 45.sp)
            }
        }
        Box(
            modifier = Modifier
                .background(color = Color.Gray)
                .layoutId(layoutId = "box"),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(text = "100", fontSize = 80.sp)
        }
    }
}

/**
 * Creates a Calculator layout using [ConstraintLayout] and a JSON-based [ConstraintSet].
 *
 * This demonstrates how to build a complex grid-based UI by defining the grid
 * properties (rows, columns, gaps, spans, etc.) within a JSON string.
 *
 * The layout consists of:
 *  - A 7x4 grid.
 *  - A display `Box` at the top, spanning 4 columns across the first 2 rows (`spans: "0:2x4"`).
 *  - Various `Button`s for numbers, operators, and functions.
 *  - The "0" button spans 2 columns (`spans: "24:1x2"`), which is the first cell of the last row.
 *  - All components are defined to spread across their assigned grid cells.
 */
@Suppress("JsonDuplicatePropertyKeys") // TODO: figure this warning out
@Preview(group = "calculator")
@Composable
fun GridJsonMediumCalculator() {
    ConstraintLayout(
        ConstraintSet(
            jsonContent = """
                    {
                        grid: { 
                            height: "parent",
                            width: "parent",
                            type: "grid",
                            vGap: 10,
                            hGap: 10,
                            rows: 7,
                            columns: 4,
                            spans: "0:2x4,24:1x2",
                            contains: ["box", "btn_0","btn_clear","btn_neg","btn_percent","btn_div","btn_7",
                            "btn_8","btn_9","btn_mult","btn_4","btn_5","btn_6","btn_sub","btn_1","btn_2","btn_3",
                            "btn_plus","btn_dot","btn_equal"],
                            top: ["parent", "top", 20],
                            start: ["parent", "start", 20],
                            end: ["parent", "end", 20],
                            bottom: ["parent", "bottom", 20],
                          },
                         btn_0: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_1: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_2: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_3: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_4: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_5: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_6: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_7: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_8: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_9: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_clear: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_neg: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_percent: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_div: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_mult: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_sub: {
                          height: "spread",
                          width: "spread",
                         },
                         box: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_plus: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_dot: {
                          height: "spread",
                          width: "spread",
                         },
                         btn_equal: {
                          height: "spread",
                          width: "spread",
                         },
                         box: {
                          height: "spread",
                          width: "spread",
                         }
                    }
                    """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf(
            "0", "clear", "neg", "percent", "div", "7", "8", "9",
            "mult", "4", "5", "6", "sub", "1", "2", "3", "plus", "dot", "equal"
        )
        val symbolMap: Map<String, String> = mapOf(
            "clear" to "C", "neg" to "+/-", "percent" to "%", "div" to "/",
            "mult" to "*", "sub" to "-", "plus" to "+", "dot" to ".", "equal" to "="
        )
        var text: String
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn_%s", num)),
                onClick = {},
            ) {
                text = if (symbolMap.containsKey(num)) symbolMap[num].toString() else num
                Text(text = text, fontSize = 30.sp)
            }
        }
        Box(
            modifier = Modifier
                .background(color = Color.Gray)
                .layoutId(layoutId = "box"),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(text = "100", fontSize = 80.sp)
        }
    }
}

/**
 * Creates a simple horizontal row of five [Button]'s using [ConstraintLayout] and a JSON-based
 * [ConstraintSet].
 *
 * This demonstrates the `row` helper in a JSON [ConstraintSet]. The `row` is configured to fill
 * its parent's size and arranges its contained elements (`btn0` to `btn4`) horizontally with a
 * horizontal gap (`hGap`) of 10.
 */
@Preview(group = "row")
@Composable
fun GridJsonRow() {
    ConstraintLayout(
        ConstraintSet(
            jsonContent = """
                    {
                        row: { 
                            height: "parent",
                            width: "parent",
                            type: "row",
                            hGap: 10,
                            contains: ["btn0", "btn1", "btn2", "btn3", "btn4"],
                          }
                    }
                    """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("0", "1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 30.sp)
            }
        }
    }
}

/**
 * Creates a simple vertical column of five [Button]s using [ConstraintLayout] and a JSON-based
 * [ConstraintSet].
 *
 * This demonstrates the `column` helper in a JSON [ConstraintSet]. The `column` is configured to
 * fill its parent's size and arranges its contained elements (`btn0` to `btn4`) vertically with a
 * vertical gap (`vGap`) of 10.
 */
@Preview(group = "column")
@Composable
fun GridJsonColumn() {
    ConstraintLayout(
        ConstraintSet(
            jsonContent = """
                    {
                        column: { 
                            height: "parent",
                            width: "parent",
                            type: "column",
                            vGap: 10,
                            contains: ["btn0", "btn1", "btn2", "btn3", "btn4"],
                          }
                    }
                    """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("0", "1", "2", "3", "4")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 30.sp)
            }
        }
    }
}

/**
 * Demonstrates a nested grid layout using [ConstraintLayout] and a JSON-based [ConstraintSet].
 *
 * This example shows how one grid can be placed inside another.
 *
 * The layout consists of:
 *  - A main 3x3 `grid` that fills the parent with some padding.
 *      - It contains `grid2`, `btn1`, `btn2`, `btn3`, and `btn4`.
 *      - It skips three cells (`skips: "1:1x1,4:1x1,6:1x1"`) to create empty spaces.
 *  - A nested 3x3 `grid2` which is placed in the first cell of the main `grid`.
 *      - `grid2` is set to spread its size within its allocated cell.
 *      - It contains `btn5`, `btn6`, `btn7`, and `btn8`.
 *      - It also has its own skipped cells (`skips: "0:1x2,4:1x1,6:1x1"`) to position its buttons.
 *
 * This illustrates the power of composing complex layouts by nesting grid helpers within each other.
 */
@Preview(group = "nested")
@Composable
fun GridJsonNested() {
    ConstraintLayout(
        ConstraintSet(
            jsonContent = """
                    {
                        grid: { 
                            height: "parent",
                            width: "parent",
                            type: "grid",
                            skips: "1:1x1,4:1x1,6:1x1",
                            rows: 3,
                            columns: 3,
                            contains: ["grid2", "btn1", "btn2", "btn3", "btn4"],
                            top: ["parent", "top", 20],
                            start: ["parent", "start", 20],
                            end: ["parent", "end", 20],
                            bottom: ["parent", "bottom", 20],
                          },
                          grid2: { 
                            height: "spread",
                            width: "spread",
                            type: "grid",
                            skips: "0:1x2,4:1x1,6:1x1",
                            rows: 3,
                            columns: 3,
                            contains: ["btn5", "btn6", "btn7", "btn8"],     
                          },
                    }
                    """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
        for (num in numArray) {
            Button(
                modifier = Modifier
                    .layoutId(layoutId = String.format("btn%s", num))
                    .size(size = 50.dp),
                onClick = {},
            ) {
                Text(text = num, fontSize = 25.sp)
            }
        }
    }
}

/**
 * Demonstrates nesting a `column` helper within a `row` helper using [ConstraintLayout] and a
 * JSON-based [ConstraintSet].
 *
 * This example showcases how different layout helpers can be composed to create more complex
 * arrangements.
 *
 * The layout consists of:
 *  - A main `row` that fills the parent and arranges its children horizontally with a gap of 10.
 *      - The row contains `btn0`, a nested `column`, `btn1`, `btn2`, and `btn3`.
 *  - A nested `column` which is treated as a single item within the `row`.
 *      - The `column` is configured to spread its height and width within the space allocated by the row.
 *      - It arranges its own children (`btn4`, `btn5`, `btn6`) vertically with a gap of 10.
 *
 * All buttons are defined to spread their height and width within their respective containers (either
 * the `row` or the nested `column`).
 */
@Preview(group = "cinr")
@Composable
fun GridJsonColumnInRow() {
    ConstraintLayout(
        ConstraintSet(
            jsonContent = """
                    {
                        row: { 
                            height: "parent",
                            width: "parent",
                            type: "row",
                            hGap: 10,
                            contains: ["btn0","column", "btn1", "btn2", "btn3"],
                          },
                          column: { 
                            height: "spread",
                            width: "spread",
                            type: "column",
                            vGap: 10,
                            contains: ["btn4", "btn5", "btn6"],     
                          },
                          btn0: {
                          height: "spread",
                          width: "spread",
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
                         btn6: {
                          height: "spread",
                          width: "spread",
                         }
                    }
                    """.trimIndent()
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("0", "1", "2", "3", "4", "5", "6")
        for (num in numArray) {
            Button(
                modifier = Modifier
                    .layoutId(layoutId = String.format("btn%s", num))
                    .width(width = 50.dp),
                onClick = {},
            ) {
                Text(text = num, fontSize = 20.sp)
            }
        }
    }
}