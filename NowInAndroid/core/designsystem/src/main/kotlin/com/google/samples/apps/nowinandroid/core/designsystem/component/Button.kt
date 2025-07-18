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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android filled button with generic content slot. Wraps Material 3 [Button].
 *
 * Our root composable is a [Button] whose arguments are:
 *  - `onClick`: is is our [onClick] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `colors`: is a [ButtonDefaults.buttonColors] whose [ButtonColors.containerColor] is the
 *  [ColorScheme.onBackground] or our custom [MaterialTheme.colorScheme]
 *  - `contentPadding`: is our [PaddingValues] parameter [contentPadding].
 *  - `content`: is our [content] composable lambda parameter.
 *
 * @param onClick Will be called when the user clicks the button.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 * @param contentPadding The spacing values to apply internally between the container and the
 * content.
 * @param content The button content.
 */
@Composable
fun NiaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
        ),
        contentPadding = contentPadding,
        content = content,
    )
}

/**
 * Now in Android filled button with text and icon content slots.
 *
 * Our root composable is our [NiaButton] overload whose arguments are:
 *  - `onClick`: is is our [onClick] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `contentPadding`: is [ButtonDefaults.ButtonWithIconContentPadding] is our [leadingIcon]
 *  composable lambda parameter is not `null`, or [ButtonDefaults.ContentPadding] it is `null`.
 *
 * In the [RowScope] `content` composable lambda argument we compose a [NiaButtonContent] whose
 * `text` argument is our [text] composable lambda parameter and `leadingIcon` argument is our
 * [leadingIcon] composable lambda parameter.
 *
 * @param onClick Will be called when the user clicks the button.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 * @param text The button text label content.
 * @param leadingIcon The button leading icon content. Pass `null` here for no leading icon.
 */
@Composable
fun NiaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    NiaButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        },
    ) {
        NiaButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Now in Android outlined button with generic content slot. Wraps Material 3 [OutlinedButton].
 *
 * Our root composable is a [OutlinedButton] whose arguments are:
 *  - `onClick`: is is our [onClick] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `colors`: is a [ButtonDefaults.outlinedButtonColors] whose [ButtonColors.contentColor] is
 *  the [ColorScheme.onBackground] or our custom [MaterialTheme.colorScheme].
 *  - `border`: is a [BorderStroke] whose `width` argument is
 *  [NiaButtonDefaults.OutlinedButtonBorderWidth], whose `color` argument is [ColorScheme.outline]
 *  if our [Boolean] parameter [enabled] is `true`, or a copy of [ColorScheme.onSurface] with its
 *  `alpha` value [NiaButtonDefaults.DISABLED_OUTLINED_BUTTON_BORDER_ALPHA] if it is `false`.
 *  - `contentPadding`: is our [PaddingValues] parameter [contentPadding].
 *  - `content`: is our [content] composable lambda parameter.
 *
 * @param onClick Will be called when the user clicks the button.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 * @param contentPadding The spacing values to apply internally between the container and the
 * content.
 * @param content The button content.
 */
@Composable
fun NiaOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        border = BorderStroke(
            width = NiaButtonDefaults.OutlinedButtonBorderWidth,
            color = if (enabled) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = NiaButtonDefaults.DISABLED_OUTLINED_BUTTON_BORDER_ALPHA,
                )
            },
        ),
        contentPadding = contentPadding,
        content = content,
    )
}

/**
 * Now in Android outlined button with text and icon content slots.
 *
 * Our root composable is our [NiaOutlinedButton] overload whose arguments are:
 *  - `onClick`: is is our [onClick] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `contentPadding`: is [ButtonDefaults.ButtonWithIconContentPadding] is our [leadingIcon]
 *  composable lambda parameter is not `null`, or [ButtonDefaults.ContentPadding] it is `null`.
 *
 * In its [RowScope] `content` composable lambda argument we compose a [NiaButtonContent] whose
 * `text` argument is our [text] composable lambda parameter and `leadingIcon` argument is our
 * [leadingIcon] composable lambda parameter.
 *
 * @param onClick Will be called when the user clicks the button.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 * @param text The button text label content.
 * @param leadingIcon The button leading icon content. Pass `null` here for no leading icon.
 */
@Composable
fun NiaOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    NiaOutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding
        } else {
            ButtonDefaults.ContentPadding
        },
    ) {
        NiaButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Now in Android text button with generic content slot. Wraps Material 3 [TextButton].
 *
 * Our root composable is a [TextButton] whose arguments are:
 *  - `onClick`: is is our [onClick] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `colors`: is a [ButtonDefaults.textButtonColors] whose [ButtonColors.contentColor] is
 *  the [ColorScheme.onBackground] or our custom [MaterialTheme.colorScheme].
 *  - `content`: is our [content] composable lambda parameter.
 *
 * @param onClick Will be called when the user clicks the button.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 * @param content The button content.
 */
@Composable
fun NiaTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        content = content,
    )
}

/**
 * Now in Android text button with text and icon content slots.
 *
 * Our root composable is our [NiaTextButton] overload whose arguments are:
 *  - `onClick`: is is our [onClick] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *
 * In its [RowScope] `content` composable lambda argument we compose a [NiaButtonContent] whose
 * `text` argument is our [text] composable lambda parameter and `leadingIcon` argument is our
 * [leadingIcon] composable lambda parameter.
 *
 * @param onClick Will be called when the user clicks the button.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, this button will not be
 * clickable and will appear disabled to accessibility services.
 * @param text The button text label content.
 * @param leadingIcon The button leading icon content. Pass `null` here for no leading icon.
 */
@Composable
fun NiaTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    NiaTextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        NiaButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Internal Now in Android button content layout for arranging the text label and leading icon.
 *
 * If our [leadingIcon] composable lambda parameter is not `null`, we compose a [Box] whose `modifier`
 * argument is [Modifier.sizeIn] whose `maxHeight` argument is [ButtonDefaults.IconSize], and in its
 * [BoxScope] `content` composable lambda argument we compose our [leadingIcon] composable lambda
 * parameter.
 *
 * Next we compose a [Box] whose `modifier` argument is [Modifier.padding] whose `start` argument
 * is [ButtonDefaults.IconSpacing] if our [leadingIcon] composable lambda parameter is not `null`,
 * or [0.dp] it is `null`, and in its [BoxScope] `content` composable lambda argument we compose
 * our [text] composable lambda parameter.
 *
 * @param text The button text label content.
 * @param leadingIcon The button leading icon content. Default is `null` for no leading icon.Ï
 */
@Composable
private fun NiaButtonContent(
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    if (leadingIcon != null) {
        Box(modifier = Modifier.sizeIn(maxHeight = ButtonDefaults.IconSize)) {
            leadingIcon()
        }
    }
    Box(
        modifier = Modifier
            .padding(
                start = if (leadingIcon != null) {
                    ButtonDefaults.IconSpacing
                } else {
                    0.dp
                },
            ),
    ) {
        text()
    }
}

/**
 * This is two previews of a [NiaButton] ("Light theme" and "Dark theme"). We apply our [NiaTheme]
 * custom [MaterialTheme] and then in a [NiaBackground] whose `modifier` argument is a [Modifier.size]
 * that sets its size to 150.dp by 50.dp we compose a [NiaButton]. The `onClick` argument of the
 * [NiaButton] is a do nothing lambda, and its `text` argument is a lambda which composes a [Text]
 * displaying the label "Test button".
 */
@ThemePreviews
@Composable
fun NiaButtonPreview() {
    NiaTheme {
        NiaBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            NiaButton(onClick = {}, text = { Text("Test button") })
        }
    }
}

/**
 * This is two previews of our [NiaOutlinedButton] Composable ("Light theme" and "Dark theme"). We
 * wrap our [NiaTheme] custom [MaterialTheme] around a [NiaBackground] whose `modifier` argument is
 * a [Modifier.size] of 150.dp by 50.dp, and whose `content` is a [NiaOutlinedButton]. The `onClick`
 * lambda argument of the [NiaOutlinedButton] is a do nothing lambda, and its `text` lambda argument
 * is a [Text] displaying the string "Test button".
 */
@ThemePreviews
@Composable
fun NiaOutlinedButtonPreview() {
    NiaTheme {
        NiaBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            NiaOutlinedButton(onClick = {}, text = { Text("Test button") })
        }
    }
}

/**
 * This is two previews of our [NiaButton] Composable using its `leadingIcon` argument ("Light theme"
 * and "Dark theme"). We wrap our [NiaTheme] custom [MaterialTheme] around a [NiaBackground] whose
 * `modifier` argument is a [Modifier.size] of 150.dp by 50.dp, and whose `content` is a [NiaButton].
 * The `onClick` lambda argument of the [NiaButton] is a do nothing lambda, its `text` lambda argument
 * is a [Text] displaying the string "Test button", and its `leadingIcon` lambda argument is an [Icon]
 * whose `imageVector` argument is our [NiaIcons.Add] "Add" icon, and whose `contentDescription`
 * argument is `null`.
 */
@ThemePreviews
@Composable
fun NiaButtonLeadingIconPreview() {
    NiaTheme {
        NiaBackground(modifier = Modifier.size(width = 150.dp, height = 50.dp)) {
            NiaButton(
                onClick = {},
                text = { Text(text = "Test button") },
                leadingIcon = { Icon(imageVector = NiaIcons.Add, contentDescription = null) },
            )
        }
    }
}

/**
 * Now in Android button default values.
 */
object NiaButtonDefaults {
    // TODO: File bug
    // OutlinedButton border color doesn't respect disabled state by default
    const val DISABLED_OUTLINED_BUTTON_BORDER_ALPHA: Float = 0.12f

    // TODO: File bug
    // OutlinedButton default border width isn't exposed via ButtonDefaults
    val OutlinedButtonBorderWidth: Dp = 1.dp
}
