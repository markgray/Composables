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

package com.example.owl.ui.onboarding

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Shapes
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.owl.R
import com.example.owl.model.Topic
import com.example.owl.model.topics
import com.example.owl.ui.theme.Elevations
import com.example.owl.ui.theme.Images
import com.example.owl.ui.theme.LocalElevations
import com.example.owl.ui.theme.OwlTheme
import com.example.owl.ui.theme.YellowTheme
import com.example.owl.ui.theme.pink500
import com.example.owl.ui.utils.NetworkImage
import kotlin.math.max

/**
 * The Onboarding screen.
 *
 * Our root composable is a [Scaffold] (wrapped in our [YellowTheme] custom [MaterialTheme]) whose
 * arguments are:
 *  - `topBar`: is an [AppBar] composable.
 *  - `backgroundColor`: is the [Colors.primarySurface] of our custom [MaterialTheme.colors].
 *  - `floatingActionButton`: is a lambda that composes a [FloatingActionButton] whose `onClick`
 *  argument is our lambda parameter [onboardingComplete], and whose `modifier` argument is
 *  a new instance of [Modifier]. In its `content` composable lambda argument we compose an
 *  [Icon] whose `imageVector` argument is the [ImageVector] drawn by [Icons.Rounded.Explore], and
 *  whose `contentDescription` argument is the [String] with resource ID
 *  `R.string.label_continue_to_courses` ("Continue to courses").
 *
 * In the `content` composable lambda argument of the [Scaffold] we accept the [PaddingValues] passed
 * the lambda in variable `innerPadding` and compose a [Column] whose `modifier` argument is a
 * [Modifier.padding] whose `paddingValues` argument is `innerPadding`. In the [ColumnScope] `content`
 * composable lambda argument of the [Column] we:
 *
 * **First**: We compose a [Text] whose arguments are:
 *  - `text`: is the [String] with resource ID `R.string.choose_topics_that_interest_you` ("Choose
 *  topics that interest you").
 *  - `style`: is the [Typography.h4] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.End].
 *  - `modifier`: is a [Modifier.padding] that adds `16.dp` to the `horizontal` sides, and `32.dp`
 *  to the `vertical` sides.
 *
 * **Second**: We compose a [TopicsGrid] whose `modifier` argument is a [ColumnScope.weight] whose
 * `weight` argument is `1f` chained to a [Modifier.wrapContentHeight].
 *
 * **Third**: We compose a [Spacer] whose `modifier` argument is a [Modifier.height] whose `height`
 * argument is `56.dp`.
 *
 * @param onboardingComplete A callback to be invoked when the user has completed onboarding.
 */
@Composable
fun Onboarding(onboardingComplete: () -> Unit) {
    YellowTheme {
        Scaffold(
            topBar = { AppBar() },
            backgroundColor = MaterialTheme.colors.primarySurface,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onboardingComplete,
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Explore,
                        contentDescription = stringResource(id = R.string.label_continue_to_courses)
                    )
                }
            }
        ) { innerPadding: PaddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues = innerPadding)
            ) {
                Text(
                    text = stringResource(id = R.string.choose_topics_that_interest_you),
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 32.dp
                    )
                )
                TopicsGrid(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .wrapContentHeight()
                )
                Spacer(modifier = Modifier.height(height = 56.dp)) // center grid accounting for FAB
            }
        }
    }
}

/**
 * Top bar for the onboarding screen.
 *
 * Our root composable is a [Row] whose arguments are:
 *  - `horizontalArrangement`: is [Arrangement.SpaceBetween] to space the children evenly between
 *  the `start` and `end` sides of the row.
 *  - `verticalAlignment`: is [Alignment.CenterVertically] to center the children vertically.
 *  - `modifier`: is a [Modifier.fillMaxWidth] to fill the maximum width its parent allows, chained
 *  to a [Modifier.statusBarsPadding] to add padding to accommodate the status bars insets.
 *
 * In the [RowScope] `content` composable lambda argument of the [Row] we:
 *
 * **First**: We compose an [Image] whose arguments are:
 *  - `painter`: is the [Painter] returned by [painterResource] for the drawable whose resource ID
 *  is the resource ID returned for [Images.lockupLogo] of [OwlTheme.images] (this can vary by theme)
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.padding] that adds `16.dp` to all sides.
 *
 * **Second**: We compose an [IconButton] whose `modifier` argument is a [Modifier.padding] that
 * adds `16.dp` to all sides, and whose `onClick` argument is a do-nothing lambda. In the `content`
 * composable lambda argument we compose an [Icon] whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.Filled.Settings].
 *  - `contentDescription`: is the [String] with resource ID `R.string.label_settings` ("Settings").
 */
@Composable
private fun AppBar() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Image(
            painter = painterResource(id = OwlTheme.images.lockupLogo),
            contentDescription = null,
            modifier = Modifier.padding(all = 16.dp)
        )
        IconButton(
            modifier = Modifier.padding(all = 16.dp),
            onClick = { /* todo */ }
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(id = R.string.label_settings)
            )
        }
    }
}

/**
 * Displays a list of [topics] as a horizontally scrollable [StaggeredGrid].
 *
 * Our root composable is a [StaggeredGrid] whose `modifier` argument chains to our [Modifier]
 * parameter [modifier] a [Modifier.horizontalScroll] whose `state` argument is the remembered
 * [ScrollState] returned by [rememberScrollState], chained to a [Modifier.padding] that adds `8.dp`
 * to the `horizontal` sides. In the [StaggeredGrid] `content` composable lambda argument we use the
 * [Iterable.forEach] method of the global [List] of [Topic]s property [topics] to loop through
 * each [Topic] capturing the current [Topic] in variable `topic` and compose a [TopicChip] whose
 * `topic` argument is the current [Topic] passed the `action` lambda in variable `topic`.
 *
 * @param modifier a [Modifier] that our caller can use to modify our appearance or behavior. Our
 * caller passes us a [ColumnScope.weight] whose `weight` argument is `1f` chained to a
 * [Modifier.wrapContentHeight].
 */
@Composable
private fun TopicsGrid(modifier: Modifier = Modifier) {
    StaggeredGrid(
        modifier = modifier
            .horizontalScroll(state = rememberScrollState())
            .padding(horizontal = 8.dp)
    ) {
        topics.forEach { topic: Topic ->
            TopicChip(topic = topic)
        }
    }
}

/**
 * Represents the two states of a topic chip: selected and unselected.
 */
private enum class SelectionState { Unselected, Selected }

/**
 * Class holding animating values when transitioning topic chip states.
 */
private class TopicChipTransition(
    cornerRadius: State<Dp>,
    selectedAlpha: State<Float>,
    checkScale: State<Float>
) {
    val cornerRadius by cornerRadius
    val selectedAlpha by selectedAlpha
    val checkScale by checkScale
}

/**
 * Transition defining the animation for the corner radius, selected alpha, and check scale
 * of a topic chip. This is used to animate the chip when it is selected or unselected.
 *
 * We initialize our [Transition] of [SelectionState] variable `val transition` by calling
 * [updateTransition] with the `targetState` argument either [SelectionState.Selected] if our
 * [Boolean] parameter [topicSelected] is `true` or [SelectionState.Unselected] if it is `false`.
 *
 * We initialize our [State] wrapped animated [Dp] variable `val cornerRadius` by having `transition`
 * [Transition.animateDp] between 0.dp if the [SelectionState] is [SelectionState.Unselected] or
 * 28.dp if it is [SelectionState.Selected].
 *
 * We initialize our [State] wrapped animated [Float] variable `val selectedAlpha` by having
 * `transition` [Transition.animateFloat] between 0f if the [SelectionState] is
 * [SelectionState.Unselected] or 0.8f if it is [SelectionState.Selected].
 *
 * We initialize our [State] wrapped animated [Float] variable `val checkScale` by having `transition`
 * [Transition.animateFloat] between 0.6f if the [SelectionState] is [SelectionState.Unselected] or
 * `1f` if it is [SelectionState.Selected].
 *
 * Finally we return a [TopicChipTransition] that is `remember`ed with `key1` of `transition`
 * (so that a new [TopicChipTransition] is created whenever `transition` changes) whose arguments are:
 *  - `cornerRadius` our `cornerRadius` [State] of [Dp] variable.
 *  - `selectedAlpha` our `selectedAlpha` [State] of [Float] variable.
 *  - `checkScale` our `checkScale` [State] of [Float] variable.
 *
 * @param topicSelected whether the chip is currently selected or not.
 * @return a [TopicChipTransition] whose properties provide the animated values for corner radius,
 * selected alpha, and check scale based on the [topicSelected] state.
 */
@Composable
private fun topicChipTransition(topicSelected: Boolean): TopicChipTransition {
    val transition: Transition<SelectionState> = updateTransition(
        targetState = if (topicSelected) SelectionState.Selected else SelectionState.Unselected
    )
    val cornerRadius: State<Dp> = transition.animateDp { state: SelectionState ->
        when (state) {
            SelectionState.Unselected -> 0.dp
            SelectionState.Selected -> 28.dp
        }
    }
    val selectedAlpha: State<Float> = transition.animateFloat { state: SelectionState ->
        when (state) {
            SelectionState.Unselected -> 0f
            SelectionState.Selected -> 0.8f
        }
    }
    val checkScale: State<Float> = transition.animateFloat { state: SelectionState ->
        when (state) {
            SelectionState.Unselected -> 0.6f
            SelectionState.Selected -> 1f
        }
    }
    return remember(key1 = transition) {
        TopicChipTransition(
            cornerRadius = cornerRadius,
            selectedAlpha = selectedAlpha,
            checkScale = checkScale
        )
    }
}

/**
 * Display a chip for a [Topic].
 *
 * We start by using destructuring to initialize and remember our [MutableState] wrapped [Boolean]
 * variable `selected` and its setter lambda taking [Boolean] variable `onSelected` to an initial
 * value of `false`. We initialize our [TopicChipTransition] variable `topicChipTransitionState`
 * to the value returned by our function [topicChipTransition] whose `topicSelected` argument is
 * [MutableState] wrapped [Boolean] variable `selected`.
 *
 * Our root composable is a [Surface] whose `modifier` argument is a [Modifier.padding] that adds
 * `4.dp` to all sides, whose `elevation` argument is the [Elevations.card] of [OwlTheme.elevations]
 * (this property returns the `current` [LocalElevations]), and whose `shape` argument is a copy of
 * the [Shapes.medium] of our custom [MaterialTheme.shapes] with its `topStart` corner radius a
 * [CornerSize] whose `cornerRadius` argument is the [State] wrapped amimated [Dp] property of the
 * [TopicChipTransition] variable `topicChipTransitionState`. In the [Surface] `content` composable
 * lambda argument we compose a [Row] whose `modifier` argument is a [Modifier.toggleable] whose
 * `value` argument is [MutableState] wrapped [Boolean] variable `selected` and whose `onValueChange`
 * argument is our lambda variable `onSelected`. In the [RowScope] `content` composable lambda
 * argument of the [Row] we first compose a [Box] in whose [BoxScope] `content` composable lambda
 * argument we:
 *
 * **First**: We compose a [NetworkImage] whose arguments are:
 *  - `url`: is the [Topic.imageUrl] of [Topic] parameter [topic].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.size] whose `width` argument is `72.dp`, and whose `height` argument
 *  is `72.dp`, chained to a [Modifier.aspectRatio] whose `ratio` argument is `1f`.
 *
 * **Second**: if the [TopicChipTransition.selectedAlpha] of [TopicChipTransition] variable
 * `topicChipTransitionState` is greater than `0f` we compose a [Surface] whose `color` argument
 * is a copy of [pink500] with its `alpha` argument set to the [State] wrapped animated [Float]
 * property [TopicChipTransition.selectedAlpha] of [TopicChipTransition] variable
 * `topicChipTransitionState`, and whose `modifier` argument is a [BoxScope.matchParentSize].
 * In the [Surface] `content` composable lambda argument we compose an [Icon] whose arguments are:
 *  - `imageVector`: is the [ImageVector] drawn by [Icons.Filled.Done].
 *  - `contentDescription`: is `null`.
 *  - `tint`: is a copy of the [Colors.onPrimary] of our custom [MaterialTheme.colors] with its
 *  `alpha` set to the [State] wrapped animated [Float] property [TopicChipTransition.selectedAlpha]
 *  of [TopicChipTransition] variable `topicChipTransitionState`.
 *  - `modifier`: is a [Modifier.wrapContentSize] chained to a [Modifier.scale] whose `scale`
 *  argument is the [State] wrapped animated [Float] property [TopicChipTransition.checkScale] of
 *  [TopicChipTransition] variable `topicChipTransitionState`.
 *
 * Next in the [RowScope] `content` composable lambda argument of the [Row] we compose a [Column] in
 * whose [ColumnScope] `content` composable lambda argument we:
 *
 * **First**: We compose a [Text] whose arguments are:
 *  - `text`: is the [Topic.name] of [Topic] parameter [topic].
 *  - `style`: is the [Typography.body1] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] that adds `16.dp` to the `start`, `16.dp` to the `top`,
 *  `16.dp` to the `end`, and `8.dp` to the `bottom`.
 *
 * **Second**: We compose a [Row] whose `verticalAlignment` argument is [Alignment.CenterVertically].
 * In the [RowScope] `content` composable lambda argument of the [Row] we compose a
 * [CompositionLocalProvider] that provides [ContentAlpha.medium] as the [LocalContentAlpha] to its
 * `content` composable lambda argument which composes an [Icon] whose arguments are:
 *  - `painter`: is the [Painter] returned by [painterResource] for the drawable whose resource ID
 *  is `R.drawable.ic_grain` (which is an icon with 8 shaded circles).
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.padding] that adds `16.dp` to the `start` side, chained to a
 *  [Modifier.size] whose `size` argument is `12.dp`.
 *
 * Next in the [RowScope] `content` composable lambda argument of the [Row] we compose a [Text]
 * whose arguments are:
 *  - `text`: is the [Topic.courses] of [Topic] parameter [topic] converted to a [String].
 *  - `style`: is the [Typography.caption] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] that adds `8.dp` to the `start` side.
 *
 * @param topic the [Topic] to display.
 */
@Composable
private fun TopicChip(topic: Topic) {
    val (selected: Boolean, onSelected: (Boolean) -> Unit) = remember { mutableStateOf(false) }
    val topicChipTransitionState: TopicChipTransition = topicChipTransition(topicSelected = selected)

    Surface(
        modifier = Modifier.padding(all = 4.dp),
        elevation = OwlTheme.elevations.card,
        shape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(
                size = topicChipTransitionState.cornerRadius
            )
        )
    ) {
        Row(modifier = Modifier.toggleable(value = selected, onValueChange = onSelected)) {
            Box {
                NetworkImage(
                    url = topic.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 72.dp, height = 72.dp)
                        .aspectRatio(ratio = 1f)
                )
                if (topicChipTransitionState.selectedAlpha > 0f) {
                    Surface(
                        color = pink500.copy(alpha = topicChipTransitionState.selectedAlpha),
                        modifier = Modifier.matchParentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onPrimary.copy(
                                alpha = topicChipTransitionState.selectedAlpha
                            ),
                            modifier = Modifier
                                .wrapContentSize()
                                .scale(scale = topicChipTransitionState.checkScale)
                        )
                    }
                }
            }
            Column {
                Text(
                    text = topic.name,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_grain),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(size = 12.dp)
                        )
                        Text(
                            text = topic.courses.toString(),
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * A composable that places its children in a staggered grid.
 *
 * Our root composable is a [Layout] whose `content` argument is our lambda parameter [content],
 * and whose `modifier` argument is our [Modifier] parameter [modifier]. In its [MeasureScope]
 * `measurePolicy` lambda argument we accept the [List] of [Measurable] passed the lambda in
 * variable `measurables` and the [Constraints] passed the lambda in variable `constraints`.
 *
 * We initialize our [IntArray] variable `rowWidths` to an [IntArray] of size [rows] (we will use
 * this to keep track of the width of each row), and our [IntArray] variable `rowHeights` to an
 * [IntArray] of size [rows] (we will use this to keep track of the height of each row).
 *
 * We use the [Iterable.mapIndexed] method of our [List] of [Measurable] variable `measurables` to
 * loop through each [Measurable] capturing the current [Measurable] in variable `measurable` and
 * the current [Int] index in variable `index`, then initialize our [Placeable] variable `placeable`
 * to the value returned by calling the [Measurable.measure] method of the current [Measurable] in
 * `measurble` with the [Constraints] passed the lambda in variable `constraints` as the `constraints`
 * argument. We initialize our [Int] variable `row` to the current [Int] value of `index` modulo
 * our [Int] parameter [rows], then increment the [Int] value of `rowWidths` at the [Int] value of
 * `row` by the [Placeable.width] of `placeable`, and set the [Int] value of `rowHeights` at
 * the [Int] value of `row` to the [max] of its current value and the [Placeable.height] of `placeable`.
 * Finally we return the [Placeable] variable `placeable` to be added to our [List] of [Placeable]
 * variable `placeables` by the [Iterable.mapIndexed] method.
 *
 * We initialize our [Int] variable `width` to the [IntArray.maxOrNull] of our [IntArray] variable
 * `rowWidths` coercing its value to be between the [Constraints.minWidth] and [Constraints.maxWidth]
 * of `constraints` defaulting to [Constraints.minWidth] if it is `null`.
 *
 * We initialize our [Int] variable `height` to the [IntArray.sum] of our [IntArray] variable
 * `rowHeights` coercing its value to be between the [Constraints.minHeight] and
 * [Constraints.maxHeight] of `constraints` defaulting to [Constraints.minHeight] if it is `null`.
 *
 * We initalize our [IntArray] variable `rowY` to an [IntArray] of size [rows] (we will use this as
 * the y-coordinates of each row).
 *
 * We loop over `i` from `1` until [Int] parameter [rows] and set the [Int] value of `rowY` at
 * `i` to the [Int] value of `rowY` at `i - 1` plus the [Int] value of `rowHeights` at `i - 1`.
 *
 * Finally we use the [MeasureScope.layout] method of our [Layout] with its `width` argument our
 * [Int] variable `width` and its `height` argument our [Int] variable `height`. In its
 * [Placeable.PlacementScope] `placementBlock` lambda argument we initialize our [IntArray] variable
 * `rowX` to an [IntArray] of size [rows] (we will use this as the x-coordinate we have placed up to
 * per row). We use the [Iterable.forEachIndexed] method of our [List] of [Placeable] variable
 * `placeables` to loop through each [Placeable] capturing the current [Placeable] in variable
 * `placeable` and the current [Int] index in variable `index`. We initialize our [Int] variable
 * `row` to the current [Int] value of `index` modulo our [Int] parameter [rows]. We use the
 * [Placeable.PlacementScope.place] method of `placeable` to place its `x` at the [Int] value of
 * `rowX` at the [Int] value of `row` and its `y` at the [Int] value of `rowY` at the [Int] value
 * of `row`. We increment the [Int] value of `rowX` at the [Int] value of `row` by the
 * [Placeable.width] of `placeable`, and loop around for the next [Placeable].
 *
 * @param modifier the [Modifier] to apply to this layout.
 * @param rows the number of rows for the grid.
 * @param content a function which draws each child being placed in a slot in the grid.
 */
@Composable
private fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables: List<Measurable>, constraints: Constraints ->
        val rowWidths = IntArray(rows) // Keep track of the width of each row
        val rowHeights = IntArray(rows) // Keep track of the height of each row

        // Don't constrain child views further, measure them with given constraints
        val placeables: List<Placeable> = measurables.mapIndexed { index: Int, measurable: Measurable ->
            val placeable: Placeable = measurable.measure(constraints = constraints)

            // Track the width and max height of each row
            val row: Int = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        val width: Int = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth, constraints.maxWidth)
            ?: constraints.minWidth
        // Grid's height is the sum of each row
        val height: Int = rowHeights.sum().coerceIn(constraints.minHeight, constraints.maxHeight)

        // y co-ord of each row
        val rowY = IntArray(rows)
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }
        layout(width = width, height = height) {
            // x co-ord we have placed up to, per row
            val rowX = IntArray(rows)
            placeables.forEachIndexed { index: Int, placeable: Placeable ->
                val row: Int = index % rows
                placeable.place(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

/**
 * Preview of [Onboarding].
 */
@Preview(name = "Onboarding")
@Composable
private fun OnboardingPreview() {
    Onboarding(onboardingComplete = { })
}

/**
 * Preview of [TopicChip].
 */
@Preview("Topic Chip")
@Composable
private fun TopicChipPreview() {
    YellowTheme {
        TopicChip(topics.first())
    }
}
