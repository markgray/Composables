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

@file:Suppress("UnusedImport")

package com.example.compose.jetchat.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.lerp
import com.example.compose.jetchat.R
import com.example.compose.jetchat.profile.ProfileFab
import com.example.compose.jetchat.profile.ProfileScreen
import kotlin.math.roundToInt

/**
 * A layout that shows an icon and a text element used as the content for a FAB that extends with
 * an animation. We start by initializing our [ExpandableFabStates] variable `val currentState` to
 * [ExpandableFabStates.Extended] if our [Boolean] parameter [extended] is `true` or to
 * [ExpandableFabStates.Collapsed] if it is `false`. We initialize our [Transition] of
 * [ExpandableFabStates] variable `val transition` by using the [updateTransition] method to set up
 * a [Transition], with `currentState` the `targetState` that it uses to be updated. When
 * `currentState` changes, [Transition] will run all of its child animations towards their target
 * values specified for the new `targetState`. We initialize our [Float] variable `val textOpacity`
 * by using the [Transition.animateFloat] method of `transition` to Create a Float animation as a
 * part of `transition` (This means the states of this animation will be managed by the [Transition]).
 * Its `transitionSpec` argument is a lambda that uses different [tween] instances depending on
 * whether the `targetState` of `transition` is [ExpandableFabStates.Collapsed] with different
 * `delayMillis` but the same `durationMillis`. The `targetValueByState` lambda argument of the
 * [Transition.animateFloat] uses 0f as its value for the `targetState` [ExpandableFabStates.Collapsed]
 * and 1f as its value for the `targetState` [ExpandableFabStates.Extended].
 *
 * We initialize our [Float] variable `val fabWidthFactor` by using the [Transition.animateFloat]
 * method of `transition` to Create a Float animation as a part of `transition`. Its `transitionSpec`
 * argument is a lambda that uses identical [tween] instances whether the `targetState` of `transition`
 * is [ExpandableFabStates.Collapsed] or [ExpandableFabStates.Extended]. The `targetValueByState`
 * lambda argument of the [Transition.animateFloat] uses 0f as its value for the `targetState`
 * [ExpandableFabStates.Collapsed] and 1f as its value for the `targetState` [ExpandableFabStates.Extended].
 *
 * Finally our root Composable is an [IconAndTextRow] whose `icon` argument is our [icon] parameter,
 * whose `text` argument is our [text] parameter, whose `opacityProgress` argument is a lambda whose
 * value is our [Transition] animated variable `textOpacity`, whose `widthProgress` argument is a
 * lambda whose value is our [Transition] animated variable `fabWidthFactor`, and whose `modifier`
 * argument is our [modifier] parameter.
 *
 * @param icon we use this as the `icon` argument of our [IconAndTextRow]. Our caller [ProfileFab]
 * calls us with an [Icon] whose `imageVector` is either [Icons.Outlined.Create] if the "user is me"
 * or [Icons.Outlined.Chat] if it is not.
 * @param text we use this as the `text` argument of our [IconAndTextRow]. Our caller [ProfileFab]
 * calls us with a [Text] that displays the [String] with resource ID [R.string.edit_profile]
 * ("Edit Profile") if the "user is me" or the [String] with resource ID [R.string.message]
 * ("Message") if it is not.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [ProfileFab] does not pass one so is the empty, default, or starter
 * [Modifier] that contains no elements is used instead.
 * @param extended a [Boolean] flag that indicates whether our [ExpandableFabStates] `currentState`
 * is [ExpandableFabStates.Extended] (`true`) or [ExpandableFabStates.Collapsed] (`false`). We use
 * this to animate the `fabWidthFactor` that we pass in a lambda as the `widthProgress` argument of
 * our [IconAndTextRow]. It uses it to linearly interpret between the `initialWidth`, and the
 * `expandedWidth` to determine the `width` it uses when using [layout] to place the [Icon] and
 * [Text] it renders. Our caller [ProfileFab] passes us its `extended` parameter, which its caller
 * [ProfileScreen] passes it, which is a [derivedStateOf] the [ScrollState] it uses to make its
 * [Column] able to `verticalScroll`, with a [ScrollState.value] of 0 as `true`.
 */
@Composable
fun AnimatingFabContent(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    extended: Boolean = true
) {
    val currentState = if (extended) ExpandableFabStates.Extended else ExpandableFabStates.Collapsed
    val transition: Transition<ExpandableFabStates> = updateTransition(
        targetState = currentState,
        label = "fab_transition"
    )

    val textOpacity: Float by transition.animateFloat(
        transitionSpec = {
            if (targetState == ExpandableFabStates.Collapsed) {
                tween(
                    easing = LinearEasing,
                    durationMillis = (transitionDuration / 12f * 5).roundToInt() // 5 / 12 frames
                )
            } else {
                tween(
                    easing = LinearEasing,
                    delayMillis = (transitionDuration / 3f).roundToInt(), // 4 / 12 frames
                    durationMillis = (transitionDuration / 12f * 5).roundToInt() // 5 / 12 frames
                )
            }
        },
        label = "fab_text_opacity"
    ) { state: ExpandableFabStates ->
        if (state == ExpandableFabStates.Collapsed) {
            0f
        } else {
            1f
        }
    }
    val fabWidthFactor: Float by transition.animateFloat(
        transitionSpec = {
            if (targetState == ExpandableFabStates.Collapsed) {
                tween(
                    easing = FastOutSlowInEasing,
                    durationMillis = transitionDuration
                )
            } else {
                tween(
                    easing = FastOutSlowInEasing,
                    durationMillis = transitionDuration
                )
            }
        },
        label = "fab_width_factor"
    ) { state ->
        if (state == ExpandableFabStates.Collapsed) {
            0f
        } else {
            1f
        }
    }
    // Deferring reads using lambdas instead of Floats here can improve performance,
    // preventing recompositions.
    IconAndTextRow(
        icon = icon,
        text = text,
        opacityProgress = { textOpacity },
        widthProgress = { fabWidthFactor },
        modifier = modifier
    )
}

/**
 * This is the animated Composable content that [AnimatingFabContent] animates for [ProfileFab] to
 * supply to [ProfileScreen] (the buck stops here). Our root Composable is a [Layout] which it used
 * to measure and position one [layout] child. Its `modifier` argument is our [Modifier] parameter
 * [modifier], and its `content` argument (chidren to be composed) are our Composable lambda parameter
 * [icon], and a [Box] whose `modifier` argument is a [Modifier.graphicsLayer] that uses the [Float]
 * value returned by our [opacityProgress] lambda parameter to animate the `alpha` of its `content`
 * which is our [text] Composable lambda. In the [MeasureScope] block we use the entry at index 0 in
 * the `measurables` [List] of [Measurable]'s passed the block to call its [Measurable.measure]
 * method with the `constraints` [Constraints] passed the block to initialize our [Placeable]
 * variable `val iconPlaceable` (the first child of the `content` argument of the [Layout]), and we
 * use the entry at index 1 in the `measurables` [List] of [Measurable]'s passed the block to call
 * its [Measurable.measure] method with the `constraints` [Constraints] passed the block to initialize
 * our [Placeable] variable `val textPlaceable` (the second child of the `content` argument of the
 * [Layout]). We initialize our [Int] variable `val height` to the [Constraints.maxHeight] property
 * of the `constraints` passed the block, and our [Float] variable `val initialWidth` to the [Float]
 * version of `height`. We initialize our [Float] variable `val iconPadding` to one half the quantity
 * `initialWidth` minus the [Placeable.width] property of `iconPlaceable`. We initialize our [Float]
 * variable `val expandedWidth` to the [Placeable.width] property of of `iconPlaceable` plus the
 * [Placeable.width] property of `textPlaceable` plus 3 times `iconPadding` (this is the full width
 * when the [IconAndTextRow] is expanded). We initialize our [Float] variable `val width` using the
 * [lerp] method to linearly interpret between `initialWidth` and `expandedWidth` with the fraction
 * the value returned by our lambda parameter [widthProgress] between them.
 *
 * Finally we call [layout] with its `width` argument our `width` variable rounded to [Int], and its
 * `height` argument our `height` variable, and in its `placementBlock` we call the `place` method
 * of `iconPlaceable` to place it at `x` argument `iconPlaceable` rounded to [Int] and at `y`
 * argument one half the [Constraints.maxHeight] property of `constraints` plus one half the
 * [Placeable.height] property of `iconPlaceable`, then we call the `place` method of `textPlaceable`
 * to place it at `x` argument the [Placeable.width] property of `iconPlaceable` plus 2 times
 * `iconPadding` with the quantity rounded to [Int] and at `y` argument one half the
 * [Constraints.maxHeight] property of `constraints` plus one half the [Placeable.height] property
 * of `textPlaceable`.
 *
 * @param icon a Composable lambda that displays the [Icon] that [ProfileFab] wants to be displayed
 * in its [FloatingActionButton].
 * @param text a Composable lambda that displays a [Text] that [ProfileFab] wants to be displayed in
 * its [FloatingActionButton].
 * @param opacityProgress a lambda that produces an animated [Float] value that we should use as the
 * `alpha` value for the [Modifier.graphicsLayer] that we use as the `modifier` of the [Box] holding
 * our [text] Composable lambda parameter. [AnimatingFabContent] uses a [Transition] of the current
 * [ExpandableFabStates] to animate this between 0f for [ExpandableFabStates.Collapsed] and 1f for
 * [ExpandableFabStates.Extended]. The value is passed using a lambda to prevent recompositions.
 * @param widthProgress a lambda that produces an animated [Float] value that we should use when
 * calculating the `width` argument we use in the call to [layout] which places our [Icon] and
 * [Text]. [AnimatingFabContent] uses a [Transition] of the current [ExpandableFabStates] to animate
 * this between 0f for [ExpandableFabStates.Collapsed] and 1f for [ExpandableFabStates.Extended].
 * The value is passed using a lambda to prevent recompositions.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [AnimatingFabContent] calls us with its `modifier` parameter, but since its
 * caller [ProfileFab] does not pass it one the empty, default, or starter [Modifier] that contains
 * no elements is used instead.
 */
@Composable
private fun IconAndTextRow(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    opacityProgress: () -> Float, // Lambdas instead of Floats, to defer read
    widthProgress: () -> Float,
    modifier: Modifier
) {
    Layout(
        modifier = modifier,
        content = {
            icon()
            Box(
                modifier = Modifier.graphicsLayer { alpha = opacityProgress() }
            ) {
                text()
            }
        }
    ) { measurables: List<Measurable>, constraints: Constraints ->

        val iconPlaceable: Placeable = measurables[0].measure(constraints = constraints)
        val textPlaceable: Placeable = measurables[1].measure(constraints = constraints)

        val height: Int = constraints.maxHeight

        // FAB has an aspect ratio of 1 so the initial width is the height
        val initialWidth: Float = height.toFloat()

        // Use it to get the padding
        val iconPadding: Float = (initialWidth - iconPlaceable.width) / 2f

        // The full width will be : padding + icon + padding + text + padding
        val expandedWidth: Float = iconPlaceable.width + textPlaceable.width + iconPadding * 3

        // Apply the animation factor to go from initialWidth to fullWidth
        val width: Float = lerp(start = initialWidth, stop = expandedWidth, fraction = widthProgress())

        layout(width = width.roundToInt(), height = height) {
            iconPlaceable.place(
                x = iconPadding.roundToInt(),
                y = constraints.maxHeight / 2 - iconPlaceable.height / 2
            )
            textPlaceable.place(
                x = (iconPlaceable.width + iconPadding * 2).roundToInt(),
                y = constraints.maxHeight / 2 - textPlaceable.height / 2
            )
        }
    }
}

/**
 * enum which specifies whether the [IconAndTextRow] is Collapsed, oe Extended
 */
private enum class ExpandableFabStates { Collapsed, Extended }

/**
 * The duration in milliseconds of the `transitionSpec` used when animating the [Transition] between
 * the two [ExpandableFabStates].
 */
private const val transitionDuration = 200
