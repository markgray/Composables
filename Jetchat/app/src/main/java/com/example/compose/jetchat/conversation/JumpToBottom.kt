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

package com.example.compose.jetchat.conversation

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.jetchat.R

/**
 * This enum is used to animate the offset of our [ExtendedFloatingActionButton] depending on the
 * value of the [Boolean] parameter `enabled` of [JumpToBottom]. When `enabled` is `true` the
 * `targetState` of the [Transition] of [Visibility] is [Visibility.VISIBLE], and when `enabled` is
 * `false` the `targetState` is [Visibility.GONE].
 */
private enum class Visibility {
    VISIBLE,
    GONE
}

/**
 * Shows a button that lets the user scroll to the bottom. We start by initializing our [Transition]
 * of [Visibility] variable `val transition` to the [Transition] returned by the [updateTransition]
 * method when passed a `targetState` argument which is [Visibility.VISIBLE] when our [Boolean]
 * parameter [enabled] is `true` or [Visibility.GONE] when it is `false`. We initialize our [Dp]
 * variable `val bottomOffset` to the animated [Dp] produced by the [Transition.animateDp] method of
 * `transition` when it animates between -32.dp for [Visibility.GONE] and 32.dp for [Visibility.VISIBLE].
 * Then only is `bottomOffset` is greater than 0 do we compose an instance of [ExtendedFloatingActionButton]
 * into the UI. The `icon` argument of that [ExtendedFloatingActionButton] is an [Icon] drawing the
 * [ImageVector] defined in [Icons.Filled.ArrowDownward], the `text` argument is the [String] with
 * resource ID [R.string.jumpBottom] ("Jump to bottom"), its `onClick` argument is our [onClicked]
 * parameter, its `containerColor` argument (the color used for the background of the FAB) is the
 * [ColorScheme.surface] of [MaterialTheme.colorScheme], its `contentColor` (preferred color for
 * content inside the FAB) is the [ColorScheme.primary] of [MaterialTheme.colorScheme]. The `modifier`
 * argument chains a [Modifier.offset] to our [modifier] parameter which offsets it by 0.dp in the
 * `x` direction, and by minus our animated [Dp] variable `bottomOffset` in the `y` direction,, and
 * that is followed by a chain to a [Modifier.height] that sets the `height` of the FAB to 36.dp.
 *
 * @param enabled if `true` our [ExtendedFloatingActionButton] has its composition animated into the
 * UI, and if `false` it has its composition animated out. This is accomplished by creating a
 * [Transition] of the [Visibility] enum, then using it to animate a [Dp] value which animates the
 * `offset` of the [ExtendedFloatingActionButton] until it is greater than 0 when an `if` statement
 * causes the [ExtendedFloatingActionButton] to be composed into the UI, and the reverse happens when
 * [enabled] transitions to `false`.
 * @param onClicked a lambda to be called when the [ExtendedFloatingActionButton] is clicked.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. [Messages] calls us with a [BoxScope.align] whose `alignment` argument is
 * [Alignment.BottomCenter] causing us to be aligned to the bottom center of the [Box] we are in.
 */
@Composable
fun JumpToBottom(
    enabled: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Show Jump to Bottom button
    val transition: Transition<Visibility> = updateTransition(
        targetState = if (enabled) Visibility.VISIBLE else Visibility.GONE,
        label = "JumpToBottom visibility animation"
    )
    val bottomOffset: Dp by transition.animateDp(label = "JumpToBottom offset animation") {
        if (it == Visibility.GONE) {
            (-32).dp
        } else {
            32.dp
        }
    }
    if (bottomOffset > 0.dp) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDownward,
                    modifier = Modifier.height(height = 18.dp),
                    contentDescription = null
                )
            },
            text = {
                Text(text = stringResource(id = R.string.jumpBottom))
            },
            onClick = onClicked,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .offset(x = 0.dp, y = -bottomOffset)
                .height(height = 36.dp)
        )
    }
}

/**
 * Preview of our [JumpToBottom] custom [ExtendedFloatingActionButton]
 */
@Preview
@Composable
fun JumpToBottomPreview() {
    JumpToBottom(enabled = true, onClicked = {})
}
