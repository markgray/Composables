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

@file:Suppress("UnusedImport")

package com.example.examplecomposegrid

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

/**
 * A demonstration of using the [ConstraintSetScope.createGrid] helper in a [MotionScene] DSL to
 * animate a grid of [Button]s.
 *
 * The animation transitions between a 2x3 grid and a 3x2 grid. The transition is triggered by
 * the "Run" button, which toggles the animation state.
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "grid1")
@Composable
fun MotionGridDslDemo() {
    val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5", "6")
    var animateToEnd: Boolean by remember { mutableStateOf(value = false) }
    val progress: Animatable<Float, AnimationVector1D> = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(key1 = animateToEnd) {
        progress.animateTo(
            targetValue = if (animateToEnd) 1f else 0f,
            animationSpec = tween(durationMillis = 3000)
        )
    }

    Column(modifier = Modifier.background(color = Color.White)) {
        val scene1 = MotionScene {
            val elem: Array<ConstrainedLayoutReference> =
                Array(numArray.size) { i: Int -> createRefFor(id = i) }
            for (i in numArray.indices) {
                elem[i] = createRefFor(id = String.format("btn%s", numArray[i]))
            }
            // basic "default" transition
            defaultTransition(
                // specify the starting layout
                from = constraintSet { // this: ConstraintSetScope
                    val grid: ConstrainedLayoutReference = createGrid(
                        elements = elem,
                        rows = 2,
                        columns = 3,
                    )
                    constrain(ref = grid) {
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                    }
                },
                // specify the ending layout
                to = constraintSet { // this: ConstraintSetScope
                    val grid: ConstrainedLayoutReference = createGrid(
                        elements = elem,
                        rows = 3,
                        columns = 2,
                    )
                    constrain(ref = grid) {
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                    }
                }
            )
        }

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 400.dp),
            motionScene = scene1,
            progress = progress.value
        ) {
            for (num in numArray) {
                Button(
                    modifier = Modifier.layoutId(layoutId = String.format("btn%s", num)),
                    onClick = {},
                ) {
                    Text(text = num, fontSize = 35.sp)
                }
            }
        }
        Button(
            onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 3.dp)
        ) {
            Text(text = "Run")
        }
    }
}

/**
 * A demonstration of using the [ConstraintSetScope.createRow] and [ConstraintSetScope.createColumn]
 * helpers within a [MotionScene] DSL.
 *
 * This example showcases a complex layout animation where a group of [Button]s transitions
 * between two distinct arrangements.
 *
 * In the starting state, the layout is primarily a vertical column containing some buttons and a
 * horizontal row of other buttons. In the ending state, this is reversed: the layout becomes a
 * horizontal row containing some buttons and a vertical column of the others.
 *
 * The animation is driven by a "Run" button, which toggles the progress of the [MotionLayout]
 * between its start and end [ConstraintSet]'s.
 */
@OptIn(ExperimentalMotionApi::class)
@Preview(group = "grid2")
@Composable
fun MotionDslDemo2() {
    val numArray: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7")
    var animateToEnd: Boolean by remember { mutableStateOf(false) }
    val progress: Animatable<Float, AnimationVector1D> = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(key1 = animateToEnd) {
        progress.animateTo(
            targetValue = if (animateToEnd) 1f else 0f,
            animationSpec = tween(durationMillis = 3000)
        )
    }

    Column(modifier = Modifier.background(Color.White)) {
        val scene1 = MotionScene {
            val btn1: ConstrainedLayoutReference = createRefFor(id = "btn1")
            val btn2: ConstrainedLayoutReference = createRefFor(id = "btn2")
            val btn3: ConstrainedLayoutReference = createRefFor(id = "btn3")
            val btn4: ConstrainedLayoutReference = createRefFor(id = "btn4")
            val btn5: ConstrainedLayoutReference = createRefFor(id = "btn5")
            val btn6: ConstrainedLayoutReference = createRefFor(id = "btn6")
            val btn7: ConstrainedLayoutReference = createRefFor(id = "btn7")

            // basic "default" transition
            defaultTransition(
                // specify the starting layout
                from = constraintSet { // this: ConstraintSetScope
                    val row: ConstrainedLayoutReference = createRow(
                        btn5, btn6, btn7,
                        horizontalGap = 10.dp
                    )
                    val column: ConstrainedLayoutReference = createColumn(
                        btn1, btn2, row, btn3, btn4,
                        verticalGap = 10.dp
                    )
                    constrain(ref = row) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    constrain(ref = column) {
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                    }
                    constrain(ref = btn1) {
                        width = Dimension.fillToConstraints
                    }
                    constrain(ref = btn2) {
                        width = Dimension.fillToConstraints
                    }
                    constrain(ref = btn3) {
                        width = Dimension.fillToConstraints
                    }
                    constrain(ref = btn4) {
                        width = Dimension.fillToConstraints
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
                },
                // specify the ending layout
                to = constraintSet { // this: ConstraintSetScope
                    val column: ConstrainedLayoutReference = createColumn(
                        btn5, btn6, btn7,
                        verticalGap = 10.dp
                    )
                    val row: ConstrainedLayoutReference = createRow(
                        btn1, btn2, column, btn3, btn4,
                        horizontalGap = 10.dp
                    )
                    constrain(ref = row) {
                        width = Dimension.matchParent
                        height = Dimension.matchParent
                    }
                    constrain(ref = column) {
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
                        height = Dimension.fillToConstraints
                    }
                    constrain(ref = btn6) {
                        height = Dimension.fillToConstraints
                    }
                    constrain(ref = btn7) {
                        height = Dimension.fillToConstraints
                    }
                }
            )
        }

        MotionLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 400.dp),
            motionScene = scene1,
            progress = progress.value
        ) {
            for (num in numArray) {
                Button(
                    modifier = Modifier.layoutId(layoutId = String.format("btn%s", num)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Gray.copy(
                            alpha = 0.1F,
                        ),
                    ),
                    onClick = {},
                ) {
                    Text(text = num, fontSize = 35.sp)
                }
            }
        }

        Button(
            onClick = { animateToEnd = !animateToEnd },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 3.dp)
        ) {
            Text(text = "Run")
        }
    }
}