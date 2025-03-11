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

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.codelab.layouts.ui.LayoutsCodelabTheme

/**
 * This demonstrates how to use a [ConstraintLayout] Composable to position widgets inside it.
 */
@Composable
fun ConstraintLayoutContent() {
    /**
     * Layout that positions its children according to the constraints between them.
     */
    ConstraintLayout {
        /**
         * This creates `ConstrainedLayoutReference`'s for the variables `button` and `text` which
         * we can assign to layouts within this ConstraintLayout by creating modifiers using
         * `Modifier.constrainAs`.
         */
        val (button: ConstrainedLayoutReference, text: ConstrainedLayoutReference) = createRefs()

        Button(
            onClick = { /* Do something */ },
            modifier = Modifier.constrainAs(button) {
                top.linkTo(parent.top, margin = 16.dp)
            }
        ) {
            Text("Button")
        }

        Text("Text", Modifier.constrainAs(text) {
            /**
             * Creates a link from the `top` of this [Text] to the `bottom` of the Composable
             * which has the [ConstrainedLayoutReference] of `button` assigned to it.
             */
            top.linkTo(button.bottom, margin = 16.dp)
        })

    }
}

/**
 * This example decouples the constaints from the layout they apply to, with the [ConstraintLayout]
 * being passed the [ConstraintSet] as its `constraintSet` parameter instead of specifying them
 * inline with a modifier in the composable they're applied to. Then the widgets identify themselves
 * for the constraints in the [ConstraintSet] using [Modifier.layoutId]. If the `maxWidth` of our
 * [BoxWithConstraints] is less than its `maxHeight` we set our variable `val constraints` to the
 * [ConstraintSet] returned by our [decoupledConstraints] method for a `margin` of 16.dp (Portrait
 * constraints) otherwise we set it to the [ConstraintSet] returned by our [decoupledConstraints]
 * method for a `margin` of 32.dp (Landscape constraints). We then create a [ConstraintLayout] whose
 * `constraintSet` argument is `constraints` which contains a [Button] labeled "Button" that uses
 * [Modifier.layoutId] as its `modifier` argument to have it constrained by its [ConstraintLayout]
 * as the "button" [ConstrainedLayoutReference] of `constraints`, and the [ConstraintLayout] also
 * contains a [Text] displaying the `text` "Text" that uses [Modifier.layoutId] as its `modifier`
 * argument to have it constrained by its [ConstraintLayout] as the "text" [ConstrainedLayoutReference]
 * of `constraints`.
 */
@Composable
fun DecoupledConstraintLayout() {
    BoxWithConstraints {
        val constraints: ConstraintSet = if (this.maxWidth < this.maxHeight) {
            decoupledConstraints(margin = 16.dp) // Portrait constraints
        } else {
            decoupledConstraints(margin = 32.dp) // Landscape constraints
        }

        ConstraintLayout(constraintSet = constraints) {
            Button(
                onClick = { /* Do something */ },
                modifier = Modifier.layoutId("button")
            ) {
                Text("Button")
            }

            Text("Text", Modifier.layoutId("text"))
        }
    }
}

/**
 * This method creates a [ConstraintSet] that uses its [margin] parameter as the `margin` parameter
 * of the `linkTo`'s it creates for the [ConstrainedLayoutReference] id's "button" and "text". In
 * the [ConstraintLayout] those two widgets will then use [Modifier.layoutId] to identify themselves.
 *
 * @param margin the value we should use as the `margin` parameter of our `linkTo` calls.
 */
private fun decoupledConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val button: ConstrainedLayoutReference = createRefFor("button")
        val text: ConstrainedLayoutReference = createRefFor("text")

        constrain(button) {
            top.linkTo(anchor = parent.top, margin = margin)
        }
        constrain(text) {
            top.linkTo(anchor = button.bottom, margin = margin)
        }
    }
}

/**
 * This Composable uses [ConstraintLayout] to position two [Button] widgets and a [Text] widget.
 * First it uses [ConstraintLayoutScope.createRefs] to create three [ConstrainedLayoutReference]'s
 * `button1`, `button2`, and `text`. It then creates its three widgets and a guideline:
 *  - a [Button] labeled "Button 1" whose `modifier` argument uses `Modifier.constrainAs` to
 *  constrain it as the [ConstrainedLayoutReference] `button1` and to link its `top` to its parent's
 *  `top` with a `margin` of 16.dp
 *  - a [Text] with the `text` "Text" whose `modifier` argument uses `Modifier.constrainAs` to
 *  constrain it as the [ConstrainedLayoutReference] `text` to `link` its `top` to the `bottom`
 *  of `button1` with a margin of 16.dp and to use `centerAround` to add start and end links towards
 *  the vertical anchor represented by the `end` of `button1`
 *  - it uses `createEndBarrier` to create a [ConstraintLayoutBaseScope.VerticalAnchor] for the
 *  variable `val barrier` containing the elements `button1`, and `text` (this represents a vertical
 *  anchor or guideline that contains the ends of both `button1`, and `text` that layouts can link
 *  to.)
 *  - a [Button] labeled "Button 2" whose `modifier` argument uses `Modifier.constrainAs` to
 *  constrain it as the [ConstrainedLayoutReference] `button2`, linking its `top` to its parent's
 *  `top` with a `margin` of 16.dp, and linking its `start` to the guideline `barrier`.
 */
@Composable
fun ConstraintLayoutContentExample2() {
    ConstraintLayout {
        val (button1, button2, text) = createRefs()

        Button(
            onClick = { /* Do something */ },
            modifier = Modifier.constrainAs(button1) {
                top.linkTo(parent.top, margin = 16.dp)
            }
        ) {
            Text("Button 1")
        }

        Text(text = "Text", modifier = Modifier.constrainAs(text) {
            top.linkTo(button1.bottom, margin = 16.dp)
            centerAround(button1.end)
        })

        val barrier: ConstraintLayoutBaseScope.VerticalAnchor = createEndBarrier(button1, text)
        Button(
            onClick = { /* Do something */ },
            modifier = Modifier.constrainAs(button2) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(barrier)
            }
        ) {
            Text("Button 2")
        }
    }
}

/**
 * This Composable demonstrates how to use [ConstraintLayout] to modify the width of its children
 * if necessary. First it uses [ConstraintLayoutScope.createRef] to create a [ConstrainedLayoutReference]
 * for its variable `val text` and [ConstraintLayoutScope.createGuidelineFromStart] to create a vertical
 * guideline at the center of the [ConstraintLayout] for its variable `val guideline`. It then creates
 * a [Text] widget whose `text` is a very long string (which will require wrapping) and whose `modifier`
 * argument uses `Modifier.constrainAs` to constrain the widget using the `text` [ConstrainedLayoutReference]
 * with `linkTo` linking its `start` to `guideline` and its `end` to the `end` of parent, with the
 * width of the [Text] child of [ConstraintLayout] specified as [Dimension.preferredWrapContent]
 * (which is a A [Dimension] with "suggested" wrap content behavior).
 */
@Composable
fun LargeConstraintLayout() {
    ConstraintLayout {
        val text: ConstrainedLayoutReference = createRef()

        val guideline: ConstraintLayoutBaseScope.VerticalAnchor = createGuidelineFromStart(fraction = 0.5f)
        Text(
            text = "This is a very very very very very very very long text",
            modifier = Modifier.constrainAs(text) {
                linkTo(start = guideline, end = parent.end)
                width = Dimension.preferredWrapContent
            }
        )
    }
}

/**
 * This is the Preview of the [ConstraintLayoutContent] Composable wrapped in our [LayoutsCodelabTheme]
 * custom [MaterialTheme].
 */
@Preview
@Composable
fun ConstraintLayoutContentPreview() {
    LayoutsCodelabTheme {
        ConstraintLayoutContent()
    }
}

/**
 * This is the Preview of the [ConstraintLayoutContentExample2] Composable wrapped in our
 * [LayoutsCodelabTheme] custom [MaterialTheme].
 */
@Preview
@Composable
fun ConstraintLayoutContentExample2Preview() {
    LayoutsCodelabTheme {
        ConstraintLayoutContentExample2()
    }
}

/**
 * This is the Preview of the [LargeConstraintLayout] Composable wrapped in our [LayoutsCodelabTheme]
 * custom [MaterialTheme].
 */
@Preview
@Composable
fun LargeConstraintLayoutPreview() {
    LayoutsCodelabTheme {
        LargeConstraintLayout()
    }
}

/**
 * This is the Preview of the [DecoupledConstraintLayout] Composable wrapped in our
 * [LayoutsCodelabTheme] custom [MaterialTheme].
 */
@Preview
@Composable
fun DecoupledConstraintLayoutPreview() {
    LayoutsCodelabTheme {
        DecoupledConstraintLayout()
    }
}
