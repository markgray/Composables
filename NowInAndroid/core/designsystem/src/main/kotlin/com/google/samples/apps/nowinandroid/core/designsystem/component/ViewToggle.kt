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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android view toggle button with included trailing icon as well as compact and expanded
 * text label content slots.
 *
 * Our root composable is a [TextButton] whose arguments are:
 *  - `onClick`: is a lambda that calls our lambda parameter [onExpandedChange] with the inverse of
 *  our boolean parameter [expanded].
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `colors`: is a [ButtonDefaults.textButtonColors] whose `contentColor` argument is the
 *  [ColorScheme.onBackground] of our custom [MaterialTheme.colorScheme].
 *  - `contentPadding`: is our [NiaViewToggleDefaults.ViewToggleButtonContentPadding] global
 *  [PaddingValues] constant.
 *
 * In the [RowScope] `content` Composable lambda argument of the [TextButton] we compose a
 * [NiaViewToggleButtonContent] whose `text` argument is our lambda parameter [expandedText] if
 * our [Boolean] parameter [expanded] is `true`, or our lambda parameter [compactText] if it is
 * `false`, and its `trailingIcon` argument is a lambda that composes an [Icon] whose `imageVector`
 * argument is [NiaIcons.ViewDay] if our [Boolean] parameter [expanded] is `true`, or
 * [NiaIcons.ShortText] if it is `false`, and its `contentDescription` argument is `null`.
 *
 * @param expanded Whether the view toggle is currently in expanded mode or compact mode.
 * @param onExpandedChange Called when the user clicks the button and toggles the mode.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 * @param compactText The text label content to show in expanded mode.
 * @param expandedText The text label content to show in compact mode.
 */
@Composable
fun NiaViewToggleButton(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    compactText: @Composable () -> Unit,
    expandedText: @Composable () -> Unit,
) {
    TextButton(
        onClick = { onExpandedChange(!expanded) },
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        contentPadding = NiaViewToggleDefaults.ViewToggleButtonContentPadding,
    ) {
        NiaViewToggleButtonContent(
            text = if (expanded) expandedText else compactText,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) NiaIcons.ViewDay else NiaIcons.ShortText,
                    contentDescription = null,
                )
            },
        )
    }
}

/**
 * Internal Now in Android view toggle button content layout for arranging the text label and
 * trailing icon.
 *
 * Our root Composable is a [Box] whose `modifier` argument is a [Modifier.padding] that adds
 * [ButtonDefaults.IconSpacing] to the `end` if our [Boolean] parameter [trailingIcon] is `true`
 * or `0.dp` if it is `false`. In ite [BoxScope] `content` Composable lambda argument we use
 * [ProvideTextStyle] to set the current value of [LocalTextStyle] to [Typography.labelSmall] of
 * our custom [MaterialTheme.typography] and in its `content` Composable lambda argument we compose
 * our Composable lambda parameter [text].
 *
 * Then if our Composable lambda parameter [trailingIcon] is not `null` we compose a [Box] whose
 * `modifier` argument is a [Modifier.sizeIn] whose `maxHeight` argument is [ButtonDefaults.IconSize],
 * and in its [BoxScope] `content` Composable lambda argument we compose our Composable lambda parameter
 * [trailingIcon].
 *
 * @param text The button text label content.
 * @param trailingIcon The button trailing icon content. Default is `null` for no trailing icon.
 */
@Composable
private fun NiaViewToggleButtonContent(
    text: @Composable () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .padding(
                end = if (trailingIcon != null) {
                    ButtonDefaults.IconSpacing
                } else {
                    0.dp
                },
            ),
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.labelSmall) {
            text()
        }
    }
    if (trailingIcon != null) {
        Box(modifier = Modifier.sizeIn(maxHeight = ButtonDefaults.IconSize)) {
            trailingIcon()
        }
    }
}

/**
 * Two previews ("Light theme" and "Dark theme") of the [NiaViewToggleButton] Composable when its
 * `expanded` parameter is `true`.
 *
 * Its root Composable is a [NiaTheme] which wraps a [Surface] whose `content` is a [NiaViewToggleButton].
 * The arguments of the [NiaViewToggleButton] are:
 *  - `expanded` is `true`.
 *  - `onExpandedChange` is an empty lambda.
 *  - `compactText` is a lambda which composes a [Text] displaying the string "Compact view".
 *  - `expandedText` is a lambda which composes a [Text] displaying the string "Expanded view".
 */
@ThemePreviews
@Composable
fun ViewTogglePreviewExpanded() {
    NiaTheme {
        Surface {
            NiaViewToggleButton(
                expanded = true,
                onExpandedChange = { },
                compactText = { Text(text = "Compact view") },
                expandedText = { Text(text = "Expanded view") },
            )
        }
    }
}

/**
 * This is a Preview of the [NiaViewToggleButton] Composable in its "Compact" state (its `expanded`
 * parameter is `false`). Wrapped in our [NiaTheme] custom [MaterialTheme] we compose a [Surface]
 * whose `content` Composable lambda argument is a [NiaViewToggleButton]. The `expanded` argument of
 * the [NiaViewToggleButton] is `false`, its `onExpandedChange` argument is an empty lambda, its
 * `compactText` argument is a lambda that composes a [Text] displaying the label "Compact view",
 * and its `expandedText` argument is a lambda that composes a [Text] displaying the label
 * "Expanded view".
 */
@Preview
@Composable
fun ViewTogglePreviewCompact() {
    NiaTheme {
        Surface {
            NiaViewToggleButton(
                expanded = false,
                onExpandedChange = { },
                compactText = { Text(text = "Compact view") },
                expandedText = { Text(text = "Expanded view") },
            )
        }
    }
}

/**
 * Now in Android view toggle default values.
 */
object NiaViewToggleDefaults {
    /**
     * The default content padding used by [NiaViewToggleButton]
     * TODO: File bug Various default button padding values aren't exposed via ButtonDefaults
     */
    val ViewToggleButtonContentPadding: PaddingValues =
        PaddingValues(
            start = 16.dp,
            top = 8.dp,
            end = 12.dp,
            bottom = 8.dp,
        )
}
