/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.baselineprofiles_codelab.ui.home.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

/**
 * A composable that provides a swipe-to-dismiss functionality for its Composable lambda parameter
 * [content]. It wraps a [SwipeToDismiss] composable, holding its animation and the current state,
 * and passes the information needed by [SwipeToDismiss] to allow the user to dismiss the item.
 *
 * We start by initializing and remembering our [DismissState] variable `dismissState` to a new
 * instance. We initialize our [Boolean] variable `isDismissed` to the value of the
 * [DismissState.isDismissed] property of `dismissState` for the `direction`
 * [DismissDirection.EndToStart]. We initialize our [Dp] variable `offset` to the value of the
 * [DismissState.offset] property of `dismissState` converted to [Dp] using the
 * current [LocalDensity].
 *
 * Then our root composable is a [AnimatedVisibility] whose `modifier` argument is our [Modifier]
 * parameter [modifier], whose `visible` argument is the negation of `isDismissed`, whose `enter`
 * argument is our [EnterTransition] parameter [enter], and whose `exit` argument is our
 * [ExitTransition] parameter [exit]. In its [AnimatedVisibilityScope] `content` composable lambd
 * argument we compose a [SwipeToDismiss] whose arguments are:
 *  - `modifier` is our [Modifier] parameter [modifier]
 *  - `state` is our [DismissState] variable `dismissState`
 *  - `directions` is our [Set] of [DismissDirection] parameter [directions]
 *  - `background` is a a lambda that calls our lambda parameter [background] with the [Dp]
 *  variable `offset` as its argument.
 *  - `dismissContent` is a lambda that calls our lambda parameter [content] with the [Boolean]
 *  variable `isDismissed` as its argument.
 *
 * @param modifier [Modifier] to be applied to the [SwipeDismissItem].
 * @param directions A set of [DismissDirection]s in which the item can be swiped to be dismissed.
 * Defaults to only [DismissDirection.EndToStart] (swipe from right to left).
 * @param enter The [EnterTransition] to be used when the item is shown. Defaults to [expandVertically].
 * @param exit The [ExitTransition] to be used when the item is dismissed. Defaults to [shrinkVertically].
 * @param background A composable that will be shown in the background when the item is being swiped.
 * It receives the current swipe offset in [Dp] as its argument. This can be used to create a dynamic
 * background based on the swipe progress.
 * @param content A composable that represents the main content of the item. It receives a boolean
 * `isDismissed` indicating whether the item is currently dismissed. This can be used to change the
 * item state when dismissed.
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun SwipeDismissItem(
    modifier: Modifier = Modifier,
    directions: Set<DismissDirection> = setOf(DismissDirection.EndToStart),
    enter: EnterTransition = expandVertically(),
    exit: ExitTransition = shrinkVertically(),
    background: @Composable (offset: Dp) -> Unit,
    content: @Composable (isDismissed: Boolean) -> Unit,
) {
    /**
     * Hold the current state from the Swipe to Dismiss composable
     */
    val dismissState: DismissState = rememberDismissState()

    /**
     * Boolean value used for hiding the item if the current state is dismissed
     */
    val isDismissed: Boolean = dismissState.isDismissed(direction = DismissDirection.EndToStart)

    /**
     * Returns the swiped value in dp
     */
    val offset: Dp = with(LocalDensity.current) { dismissState.offset.value.toDp() }

    AnimatedVisibility(
        modifier = modifier,
        visible = !isDismissed,
        enter = enter,
        exit = exit
    ) {
        SwipeToDismiss(
            modifier = modifier,
            state = dismissState,
            directions = directions,
            background = { background(offset) },
            dismissContent = { content(isDismissed) }
        )
    }
}
