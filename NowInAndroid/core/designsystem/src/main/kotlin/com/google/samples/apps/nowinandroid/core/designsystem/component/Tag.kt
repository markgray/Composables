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

package com.google.samples.apps.nowinandroid.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android topic tag Composable. Wraps Material 3 [TextButton].
 *
 * Our root composable is a [Box] whose `modifier` argument is our [Modifier] parameter [modifier].
 * In the [BoxScope] `content` Composable lambda argument of the [Box] we initialize our [Color]
 * variable `containerColor` to [ColorScheme.primaryContainer] of our custom
 * [MaterialTheme.colorScheme] if our [Boolean] parameter [followed] is `true`, or to a copy of
 * [ColorScheme.surfaceVariant] whose `alpha` is [NiaTagDefaults.UNFOLLOWED_TOPIC_TAG_CONTAINER_ALPHA]
 * if it is `false`. Next we compose a [TextButton] whose arguments are:
 *  - `onClick`: is our lambda parameter [onClick].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `colors`: is a [ButtonDefaults.textButtonColors] whose `containerColor` argument is our [Color]
 *  variable `containerColor`, whose `contentColor` argument is the [contentColorFor] of
 *  `containerColor`, and whose `disabledContainerColor` argument is a copy of
 *  [ColorScheme.onSurface] whose `alpha` is [NiaTagDefaults.DISABLED_TOPIC_TAG_CONTAINER_ALPHA].
 *
 * In the [RowScope] `content` Composable lambda argument of the [TextButton] we use [ProvideTextStyle]
 * to set the current value of [LocalTextStyle] to [Typography.labelSmall] of our custom
 * [MaterialTheme.typography] and in its `content` Composable lambda argument we compose our
 * Composable lambda parameter [text].
 *
 * @param modifier Modifier to be applied to the layout of the tag.
 * @param followed Whether the tag is followed yet.
 * @param onClick Called when the tag is clicked.
 * @param enabled Controls the enabled state of the tag. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param text The text label content.
 */
@Composable
fun NiaTopicTag(
    modifier: Modifier = Modifier,
    followed: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        val containerColor: Color = if (followed) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = NiaTagDefaults.UNFOLLOWED_TOPIC_TAG_CONTAINER_ALPHA,
            )
        }
        TextButton(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.textButtonColors(
                containerColor = containerColor,
                contentColor = contentColorFor(backgroundColor = containerColor),
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = NiaTagDefaults.DISABLED_TOPIC_TAG_CONTAINER_ALPHA,
                ),
            ),
        ) {
            ProvideTextStyle(value = MaterialTheme.typography.labelSmall) {
                text()
            }
        }
    }
}

/**
 * Two previews ("Light theme" and "Dark theme") of the [NiaTopicTag] composable. Wrapped in our
 * [NiaTheme] custom [MaterialTheme] we compose a [NiaTopicTag] whose `followed` argument is `true`,
 * whose `onClick` argument is a do nothing lambda, and whose `text` Composable lambda argument is a
 * [Text] displaying the uppercased [String] "Topic".
 */
@ThemePreviews
@Composable
fun TagPreview() {
    NiaTheme {
        NiaTopicTag(followed = true, onClick = {}) {
            Text(text = "Topic".uppercase())
        }
    }
}

/**
 * Now in Android tag default values.
 */
object NiaTagDefaults {
    /**
     * The alpha value used for the container color of unfollowed topic tags.
     */
    const val UNFOLLOWED_TOPIC_TAG_CONTAINER_ALPHA: Float = 0.5f

    /**
     * The alpha value used for the container color of a disabled [NiaTopicTag].
     * This is defined here because the value is not exposed by [ButtonDefaults].
     * TODO: File bug Button disabled container alpha value not exposed by ButtonDefaults
     */
    const val DISABLED_TOPIC_TAG_CONTAINER_ALPHA: Float = 0.12f
}
