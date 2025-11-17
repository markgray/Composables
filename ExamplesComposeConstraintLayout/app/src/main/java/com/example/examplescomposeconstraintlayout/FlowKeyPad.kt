@file:Suppress("UnusedImport")

package com.example.examplescomposeconstraintlayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Wrap

/**
 * A Composable function that demonstrates the use of [ConstraintSetScope.createFlow] in a
 * [ConstraintLayout] to create a phone-style keypad.
 *
 * [ConstraintSetScope.createFlow] is a virtual helper that allows for positioning of Composables in
 * a chain-like manner, automatically wrapping them to the next line or column when they run out of
 * space.
 *
 * In this example:
 *  - A [ConstraintLayout] is used to arrange a series of `Button`s.
 *  - An array of `String`s (`"1"`, `"2"`, `"3"`, etc.) is used to create references for each button.
 *  - [ConstraintSetScope.createFlow] is used to arrange these button references.
 *      - `maxElement = 3` specifies that there should be at most 3 elements in a row.
 *      - `wrapMode = Wrap.Aligned` ensures that wrapped elements are aligned with the elements in the
 *      previous row/column.
 *      - `verticalGap` and `horizontalGap` define the spacing between the buttons.
 *  - The entire `flow` is then centered within its parent [ConstraintLayout].
 *  - The buttons themselves are created by an [Array.map] and associated with their layout IDs.
 */
@Preview(group = "flow1")
@Composable
fun FlowPad() {
    val names: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        ConstraintLayout(
            constraintSet = ConstraintSet {
                val keys: Array<ConstrainedLayoutReference> = names.map { number: String ->
                    createRefFor(id = number)
                }.toTypedArray()
                val flow: ConstrainedLayoutReference = createFlow(
                    elements = keys,
                    maxElement = 3,
                    wrapMode = Wrap.Aligned,
                    verticalGap = 8.dp,
                    horizontalGap = 8.dp
                )
                constrain(ref = flow) {
                    centerTo(other = parent)
                }
            },
            modifier = Modifier
                .background(color = Color(color = 0xFFDAB539))
                .padding(all = 8.dp)
        ) {
            names.map { number: String ->
                Button(
                    modifier = Modifier.layoutId(layoutId = number),
                    onClick = {},
                ) {
                    Text(text = number)
                }
            }
        }
    }
}