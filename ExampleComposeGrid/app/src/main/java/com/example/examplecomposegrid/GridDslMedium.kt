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
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension

/**
 * A Composable function that demonstrates the use of the `createGrid` DSL helper within
 * `ConstraintLayout` to build a keypad layout.
 *
 * This example showcases:
 *  - A 5x3 grid containing a display `Box` and number `Button`s (0-9).
 *  - **Spanning**: The display `Box` at the top spans across all 3 columns (`spans = "0:1x3"`).
 *  - **Skipping**: An empty cell is created in the grid to position the '0' button correctly
 *  (`skips = "12:1x1"`).
 *  - **Weighting**: The rows are weighted, making the first row (the display) twice as tall as
 *  the other rows (`rowWeights = weights`).
 *  - Gaps between grid cells, both vertically and horizontally.
 */
@Preview(group = "keypad")
@Composable
fun GridDslKeypad() {
    ConstraintLayout(
        ConstraintSet {
            val btn1: ConstrainedLayoutReference = createRefFor(id = "btn1")
            val btn2: ConstrainedLayoutReference = createRefFor(id = "btn2")
            val btn3: ConstrainedLayoutReference = createRefFor(id = "btn3")
            val btn4: ConstrainedLayoutReference = createRefFor(id = "btn4")
            val btn5: ConstrainedLayoutReference = createRefFor(id = "btn5")
            val btn6: ConstrainedLayoutReference = createRefFor(id = "btn6")
            val btn7: ConstrainedLayoutReference = createRefFor(id = "btn7")
            val btn8: ConstrainedLayoutReference = createRefFor(id = "btn8")
            val btn9: ConstrainedLayoutReference = createRefFor(id = "btn9")
            val btn0: ConstrainedLayoutReference = createRefFor(id = "btn0")
            val box: ConstrainedLayoutReference = createRefFor(id = "box")

            val weights: IntArray = intArrayOf(2, 1, 1, 1, 1)

            val g1: ConstrainedLayoutReference = createGrid(
                box, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0,
                rows = 5,
                columns = 3,
                verticalGap = 25.dp,
                horizontalGap = 25.dp,
                spans = "0:1x3",
                skips = "12:1x1",
                rowWeights = weights,
            )

            constrain(ref = g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(anchor = parent.top, margin = 20.dp)
                bottom.linkTo(anchor = parent.bottom, margin = 20.dp)
                start.linkTo(anchor = parent.start, margin = 20.dp)
                end.linkTo(anchor = parent.end, margin = 20.dp)
            }
            constrain(ref = btn1) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn2) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn3) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn4) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn5) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn6) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn7) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn8) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn9) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn0) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = box) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },

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
 * A Composable function that demonstrates a more complex grid layout to create a calculator UI
 * using the `createGrid` DSL helper within `ConstraintLayout`.
 *
 * This example showcases:
 *  - A 7x4 grid containing a display `Box` and various calculator `Button`s.
 *  - **Spanning**:
 *      - The display `Box` at the top spans two rows and four columns (`"0:2x4"`).
 *      - The "equal" button at the bottom right spans one row and two columns (`"24:1x2"`).
 *  - The creation of `ConstrainedLayoutReference`s programmatically in a loop.
 *  - Mapping of internal string identifiers (e.g., "clear", "plus") to user-facing symbols
 *  (e.g., "C", "+").
 */
@Preview(group = "calculator")
@Composable
fun GridDslMediumCalculator() {
    val numArray: Array<String> = arrayOf(
        "0", "clear", "neg", "percent", "div", "7", "8", "9",
        "mult", "4", "5", "6", "sub", "1", "2", "3", "plus", "dot", "equal"
    )
    ConstraintLayout(
        ConstraintSet {
            val elem: Array<ConstrainedLayoutReference> =
                Array(size = numArray.size + 1) { i: Int ->
                    createRefFor(id = i)
                }
            elem[0] = createRefFor(id = "box")
            for (i in numArray.indices) {
                elem[i + 1] = createRefFor(id = String.format("btn_%s", numArray[i]))
            }
            val g1: ConstrainedLayoutReference = createGrid(
                elements = elem,
                rows = 7,
                columns = 4,
                verticalGap = 10.dp,
                horizontalGap = 10.dp,
                spans = "0:2x4,24:1x2",
            )

            constrain(ref = g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(anchor = parent.top, margin = 20.dp)
                bottom.linkTo(anchor = parent.bottom, margin = 20.dp)
                start.linkTo(anchor = parent.start, margin = 20.dp)
                end.linkTo(anchor = parent.end, margin = 20.dp)
            }
            for (e in elem) {
                constrain(ref = e) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val symbolMap: Map<String, String> = mapOf(
            "clear" to "C", "neg" to "+/-", "percent" to "%",
            "div" to "/", "mult" to "*", "sub" to "-", "plus" to "+", "dot" to ".", "equal" to "="
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
 * A Composable function that demonstrates the use of the `createRow` DSL helper within
 * `ConstraintLayout`.
 *
 * This example arranges a series of `Button`s horizontally in a single row, with a specified
 * gap between each button. The row itself is constrained to fill the parent layout.
 */
@Preview(group = "row")
@Composable
fun GridDslMediumRow() {
    val numArray: Array<String> = arrayOf("0", "1", "2", "3", "4")
    ConstraintLayout(
        ConstraintSet {
            val elem: Array<ConstrainedLayoutReference> =
                Array(numArray.size) { i: Int ->
                    createRefFor(id = i)
                }
            for (i in numArray.indices) {
                elem[i] = createRefFor(id = String.format("btn_%s", numArray[i]))
            }
            val g1: ConstrainedLayoutReference = createRow(
                elements = elem,
                horizontalGap = 10.dp,
            )

            constrain(ref = g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(anchor = parent.top, margin = 20.dp)
                bottom.linkTo(anchor = parent.bottom, margin = 20.dp)
                start.linkTo(anchor = parent.start, margin = 20.dp)
                end.linkTo(anchor = parent.end, margin = 20.dp)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn_%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 30.sp)
            }
        }
    }
}

/**
 * A Composable function that demonstrates the use of the `createColumn` DSL helper within
 * `ConstraintLayout`.
 *
 * This example arranges a series of `Button`s vertically in a single column, with a specified
 * gap between each button. The column itself is constrained to fill the parent layout.
 * TODO: Continue here.
 */
@Preview(group = "column")
@Composable
fun GridDslMediumColumn() {
    val numArray: Array<String> = arrayOf("0", "1", "2", "3", "4")
    ConstraintLayout(
        ConstraintSet {
            val elem: Array<ConstrainedLayoutReference> = Array(numArray.size) { i: Int ->
                createRefFor(id = i)
            }
            for (i in numArray.indices) {
                elem[i] = createRefFor(id = String.format("btn_%s", numArray[i]))
            }
            val g1: ConstrainedLayoutReference = createColumn(
                elements = elem,
                verticalGap = 10.dp,
            )

            constrain(ref = g1) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(anchor = parent.top, margin = 20.dp)
                bottom.linkTo(anchor = parent.bottom, margin = 20.dp)
                start.linkTo(anchor = parent.start, margin = 20.dp)
                end.linkTo(anchor = parent.end, margin = 20.dp)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn_%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 30.sp)
            }
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(group = "nested")
@Composable
fun GridDslMediumNested() {
    ConstraintLayout(
        ConstraintSet {
            val btn1: ConstrainedLayoutReference = createRefFor(id = "btn1")
            val btn2: ConstrainedLayoutReference = createRefFor(id = "btn2")
            val btn3: ConstrainedLayoutReference = createRefFor(id = "btn3")
            val btn4: ConstrainedLayoutReference = createRefFor(id = "btn4")
            val btn5: ConstrainedLayoutReference = createRefFor(id = "btn5")
            val btn6: ConstrainedLayoutReference = createRefFor(id = "btn6")
            val btn7: ConstrainedLayoutReference = createRefFor(id = "btn7")
            val btn8: ConstrainedLayoutReference = createRefFor(id = "btn8")
            val g1: ConstrainedLayoutReference = createGrid(
                btn5, btn6, btn7, btn8,
                rows = 3,
                columns = 3,
                skips = "0:1x2,4:1x1,6:1x1",
            )
            val g2: ConstrainedLayoutReference = createGrid(
                g1, btn1, btn2, btn3, btn4,
                rows = 3,
                columns = 3,
                skips = "1:1x1,4:1x1,6:1x1",
            )

            constrain(ref = g1) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = g2) {
                width = Dimension.matchParent
                height = Dimension.matchParent
                top.linkTo(anchor = parent.top, margin = 20.dp)
                bottom.linkTo(anchor = parent.bottom, margin = 20.dp)
                start.linkTo(anchor = parent.start, margin = 20.dp)
                end.linkTo(anchor = parent.end, margin = 20.dp)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
        for (num in numArray) {
            Button(
                modifier = Modifier
                    .layoutId(layoutId = String.format("btn%s", num))
                    .size(50.dp),
                onClick = {},
            ) {
                Text(text = num, fontSize = 25.sp)
            }
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(group = "cinr")
@Composable
fun GridDslColumnInRow() {
    ConstraintLayout(
        ConstraintSet {
            val btn0: ConstrainedLayoutReference = createRefFor(id = "btn0")
            val btn1: ConstrainedLayoutReference = createRefFor(id = "btn1")
            val btn2: ConstrainedLayoutReference = createRefFor(id = "btn2")
            val btn3: ConstrainedLayoutReference = createRefFor(id = "btn3")
            val btn4: ConstrainedLayoutReference = createRefFor(id = "btn4")
            val btn5: ConstrainedLayoutReference = createRefFor(id = "btn5")
            val btn6: ConstrainedLayoutReference = createRefFor(id = "btn6")
            val column: ConstrainedLayoutReference = createColumn(
                btn4, btn5, btn6,
                verticalGap = 10.dp
            )
            val row: ConstrainedLayoutReference = createRow(
                btn0, column, btn1, btn2, btn3,
                horizontalGap = 10.dp,
            )

            constrain(ref = column) {
                width = Dimension.fillToConstraints
                height = Dimension.matchParent
            }
            constrain(ref = row) {
                width = Dimension.matchParent
                height = Dimension.matchParent
            }
            constrain(ref = btn0) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn1) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn2) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn3) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn4) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn5) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(ref = btn6) {
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        val numArray: Array<String> = arrayOf("0", "1", "2", "3", "4", "5", "6")
        for (num in numArray) {
            Button(
                modifier = Modifier.layoutId(layoutId = String.format("btn%s", num)),
                onClick = {},
            ) {
                Text(text = num, fontSize = 40.sp)
            }
        }
    }
}
