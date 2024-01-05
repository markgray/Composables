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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
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
 * supply to [ProfileScreen] (the buck stops here).
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
            Box(modifier = Modifier.graphicsLayer { alpha = opacityProgress() }) {
                text()
            }
        }
    ) { measurables, constraints ->

        val iconPlaceable = measurables[0].measure(constraints)
        val textPlaceable = measurables[1].measure(constraints)

        val height = constraints.maxHeight

        // FAB has an aspect ratio of 1 so the initial width is the height
        val initialWidth = height.toFloat()

        // Use it to get the padding
        val iconPadding = (initialWidth - iconPlaceable.width) / 2f

        // The full width will be : padding + icon + padding + text + padding
        val expandedWidth = iconPlaceable.width + textPlaceable.width + iconPadding * 3

        // Apply the animation factor to go from initialWidth to fullWidth
        val width = lerp(initialWidth, expandedWidth, widthProgress())

        layout(width.roundToInt(), height) {
            iconPlaceable.place(
                iconPadding.roundToInt(),
                constraints.maxHeight / 2 - iconPlaceable.height / 2
            )
            textPlaceable.place(
                (iconPlaceable.width + iconPadding * 2).roundToInt(),
                constraints.maxHeight / 2 - textPlaceable.height / 2
            )
        }
    }
}

private enum class ExpandableFabStates { Collapsed, Extended }

private const val transitionDuration = 200
