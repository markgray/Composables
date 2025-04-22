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

package com.example.baselineprofiles_codelab.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * A styled button composable for the Jetsnack application.
 *
 * This button provides a gradient background, customizable shape, border, and content.
 * It handles enabled/disabled states, click actions, and content alignment.
 *
 * Our root Composable for this button is a [JetsnackSurface] whose arquments are:
 *  - shape: is our [Shape] parameter [shape]
 *  - color: is [Color.Transparent]
 *  - contentColor: is [contentColor] if our [Boolean] parameter [enabled] is true,
 *  otherwise [disabledContentColor]
 *  - border: is our [BorderStroke] parameter [border]
 *  - modifier: is our [Modifier] parameter [modifier] with a [Modifier.clip] whose `shape` is our
 *  [Shape] parameter [shape] chained to that, followed by a [Modifier.background] whose `brush` is
 *  a [Brush.horizontalGradient] whose `colors` are our [List] of [Color] parameter
 *  [backgroundGradient] if our [Boolean] parameter [enabled] is true, otherwise its our [List] of
 *  [Color] parameter [disabledBackgroundGradient], and at the end of the chain we have a
 *  [Modifier.clickable] whose `onClick` is our lambda [onClick], `enabled` is our [Boolean]
 *  parameter [enabled], `role` is [Role.Button], `interactionSource` is our
 *  [MutableInteractionSource] parameter [interactionSource] and `indication` is null.
 *
 * In the `content` composable lambda argument of the [JetsnackSurface] we use [ProvideTextStyle]
 * to provide the [Typography.button] of our custom [MaterialTheme.typography] as the local
 * [TextStyle] of a [Row] with the following arguments:
 *  - modifier: is a [Modifier.defaultMinSize] whose `minWidth` is the [ButtonDefaults.MinWidth]
 *  and `minHeight` is the [ButtonDefaults.MinHeight], with a [Modifier.indication] whose
 *  `interactionSource` is our [MutableInteractionSource] parameter [interactionSource] and
 *  `indication` is the [ripple] function, and at the end of the chain we have a
 *  [Modifier.padding] whose `paddingValues` is our [PaddingValues] parameter [contentPadding].
 *  - horizontalArrangement: is [Arrangement.Center]
 *  - verticalAlignment: is [Alignment.CenterVertically]
 *  - content: is our composabe lambda parameter [content].
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier The [Modifier] to be applied to the button.
 * @param enabled Controls the enabled state of the button. When `false`, the button will be grayed out and not clickable.
 * @param interactionSource The [MutableInteractionSource] representing the stream of [Interaction]s
 * for this button. You can create and pass in your own remembered [MutableInteractionSource] if yo
 * u want to observe the button's interactions.
 * @param shape The shape of the button's border and background. Defaults to [ButtonShape].
 * @param border The border of the button. Defaults to null.
 * @param backgroundGradient The [List] of [Color] gradient colors of the button's background.
 * Defaults to the [JetsnackColors.interactivePrimary] of our custom [JetsnackTheme.colors].
 * @param disabledBackgroundGradient The [List] of [Color] gradient colors of the button's
 * background when it's disabled.
 * Defaults to the [JetsnackColors.interactiveSecondary] of our custom [JetsnackTheme.colors].
 * @param contentColor The content color of the button's content. Defaults to the
 * [JetsnackColors.textInteractive] of our custom [JetsnackTheme.colors].
 * @param disabledContentColor The content color of the button's content when it's disabled.
 * Defaults to the [JetsnackColors.textHelp] of our custom [JetsnackTheme.colors].
 * @param contentPadding The padding of the button's content. Defaults to
 * [ButtonDefaults.ContentPadding].
 * @param content The content of the button. It should be a [RowScope] composable lambda.
 */
@Composable
fun JetsnackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = ButtonShape,
    border: BorderStroke? = null,
    backgroundGradient: List<Color> = JetsnackTheme.colors.interactivePrimary,
    disabledBackgroundGradient: List<Color> = JetsnackTheme.colors.interactiveSecondary,
    contentColor: Color = JetsnackTheme.colors.textInteractive,
    disabledContentColor: Color = JetsnackTheme.colors.textHelp,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    JetsnackSurface(
        shape = shape,
        color = Color.Transparent,
        contentColor = if (enabled) contentColor else disabledContentColor,
        border = border,
        modifier = modifier
            .clip(shape = shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (enabled) backgroundGradient else disabledBackgroundGradient
                )
            )
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null
            )
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.button
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = ButtonDefaults.MinWidth,
                        minHeight = ButtonDefaults.MinHeight
                    )
                    .indication(interactionSource = interactionSource, indication = ripple())
                    .padding(paddingValues = contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

/**
 * Defines the shape of the buttons used in the UI.
 *
 * This property uses a [RoundedCornerShape] with a 50% corner radius,
 * resulting in a pill-shaped or capsule-shaped button appearance.
 * This provides a visually consistent and modern look for the buttons.
 *
 * @see RoundedCornerShape
 */
private val ButtonShape = RoundedCornerShape(percent = 50)

/**
 * Three Previews of a rounded [JetsnackButton]
 */
@Preview("default", "round")
@Preview("dark theme", "round", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "round", fontScale = 2f)
@Composable
private fun ButtonPreview() {
    JetsnackTheme {
        JetsnackButton(onClick = {}) {
            Text(text = "Demo")
        }
    }
}

/**
 * Three Previews of a rectangle shaped [JetsnackButton]
 */
@Preview("default", "rectangle")
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "rectangle", fontScale = 2f)
@Composable
private fun RectangleButtonPreview() {
    JetsnackTheme {
        JetsnackButton(
            onClick = {}, shape = RectangleShape
        ) {
            Text(text = "Demo")
        }
    }
}
