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

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android toggle button with icon and checked icon content slots. Wraps Material 3
 * [IconButton].
 *
 * Our root composable is a [FilledIconToggleButton] whose arguments are:
 *  - `checked`: is our [Boolean] parameter [checked].
 *  - `onCheckedChange`: is our [onCheckedChange] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `colors`: is a [IconButtonDefaults.iconToggleButtonColors] whose `checkedContainerColor`
 *  argument is the [ColorScheme.primaryContainer] of our custom [MaterialTheme.colorScheme],
 *  whose `checkedContentColor` argument is the [ColorScheme.onPrimaryContainer] of our custom
 *  [MaterialTheme.colorScheme], whose `disabledContainerColor` argument is a copy of the
 *  [ColorScheme.onBackground] of our custom [MaterialTheme.colorScheme] with its
 *  `alpha` value [NiaIconButtonDefaults.DISABLED_ICON_BUTTON_CONTAINER_ALPHA] is our [Boolean]
 *  parameter [checked] is `true` or [Color.Transparent] if it is `false`.
 *
 * In the `content` composable lambda argument of the [FilledIconToggleButton] we compose our
 * [checkedIcon] composable lambda parameter if our [Boolean] parameter [checked] is `true` or
 * our [icon] composable lambda parameter if it is `false`.
 *
 * @param checked Whether the toggle button is currently checked.
 * @param onCheckedChange Called when the user clicks the toggle button and toggles checked.
 * @param modifier Modifier to be applied to the toggle button.
 * @param enabled Controls the enabled state of the toggle button. When `false`, this toggle button
 * will not be clickable and will appear disabled to accessibility services.
 * @param icon The icon content to show when unchecked.
 * @param checkedIcon The icon content to show when checked.
 */
@Composable
fun NiaIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
    checkedIcon: @Composable () -> Unit = icon,
) {
    // TODO: File bug
    // Can't use regular IconToggleButton as it doesn't include a shape (appears square)
    FilledIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = if (checked) {
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = NiaIconButtonDefaults.DISABLED_ICON_BUTTON_CONTAINER_ALPHA,
                )
            } else {
                Color.Transparent
            },
        ),
    ) {
        if (checked) checkedIcon() else icon()
    }
}

/**
 * Two previews ("Light theme" and "Dark theme") of a [NiaIconToggleButton] Composable wrapped in
 * our [NiaTheme] custom [MaterialTheme]. The arguments to the [NiaIconToggleButton] are:
 *  - `checked`: true
 *  - `onCheckedChange`: a do nothing lambda
 *  - `icon`: an [Icon] whose `imageVector` argument is [NiaIcons.BookmarkBorder] (a stylised
 *  outlined bookmark), and whose `contentDescription` is `null`.
 *  - `checkedIcon`: an [Icon] whose `imageVector` argument is [NiaIcons.Bookmark] (a stylised
 *  filled bookmark), and whose `contentDescription` is `null`.
 */
@ThemePreviews
@Composable
fun IconButtonPreview() {
    NiaTheme {
        NiaIconToggleButton(
            checked = true,
            onCheckedChange = { },
            icon = {
                Icon(
                    imageVector = NiaIcons.BookmarkBorder,
                    contentDescription = null,
                )
            },
            checkedIcon = {
                Icon(
                    imageVector = NiaIcons.Bookmark,
                    contentDescription = null,
                )
            },
        )
    }
}

/**
 * Two previews ("Light theme" and "Dark theme") of an unchecked [NiaIconToggleButton]. We display
 * it wrapped in our [NiaTheme] custom [MaterialTheme]. The arguments to the [NiaIconToggleButton]
 * are:
 *  - `checked` is `false`.
 *  - `onCheckedChange` is an empty lambda.
 *  - `icon` is an [Icon] whose `imageVector` argument is [NiaIcons.BookmarkBorder] (a stylized open
 *  book with a bookmark hanging out the bottom).
 *  - `checkedIcon` is an [Icon] whose `imageVector` argument is [NiaIcons.Bookmark] (a stylized
 *  closed book with a bookmark hanging out the bottom).
 */
@ThemePreviews
@Composable
fun IconButtonPreviewUnchecked() {
    NiaTheme {
        NiaIconToggleButton(
            checked = false,
            onCheckedChange = { },
            icon = {
                Icon(
                    imageVector = NiaIcons.BookmarkBorder,
                    contentDescription = null,
                )
            },
            checkedIcon = {
                Icon(
                    imageVector = NiaIcons.Bookmark,
                    contentDescription = null,
                )
            },
        )
    }
}

/**
 * Now in Android icon button default values.
 */
object NiaIconButtonDefaults {
    // TODO: File bug
    // IconToggleButton disabled container alpha not exposed by IconButtonDefaults
    const val DISABLED_ICON_BUTTON_CONTAINER_ALPHA: Float = 0.12f
}
