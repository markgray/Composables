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

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * A custom `IconButton` with a gradient tint and border, designed for the Jetsnack app theme.
 *
 * This composable creates an icon button with a visually appealing gradient tint that changes
 * dynamically based on the button's pressed state. It also includes a diagonal gradient border
 * for added visual flair.
 *
 * First we initalize and remember our [MutableInteractionSource] variable `interactionSource` to
 * a new [MutableInteractionSource] instance. Then we initialize our [Modifier] variable `border`
 * to a custom [Modifier.fadeInDiagonalGradientBorder] that will be used to apply a border using
 * the [JetsnackColors.interactiveSecondary] of our custom [JetsnackTheme.colors] as its gradient
 * colors and use [CircleShape] as its shape.
 *
 * We initialize our [State] wrapped [Boolean] variable `pressed` by subscribing to our
 * [MutableInteractionSource] variable `interactionSource` using its
 * [InteractionSource.collectIsPressedAsState] method.
 *
 * We initialize our [Modifier] variable `background` to either a [Modifier.offsetGradientBackground]
 * whose `colors` argument is our [List] of [Color]'s parameter [colors] if `pressed` is true, or
 * or a [Modifier.background] whose `color` argument is the [JetsnackColors.uiBackground] of our
 * custom [JetsnackTheme.colors] if `pressed` is false.
 *
 * We initialize our [BlendMode] variable `blendMode` to either a [BlendMode.Darken] if we are in
 * dark theme, or a [BlendMode.Plus] if we are in light theme.
 *
 * We initialize our [Modifier] variable `modifierColor` to either a [Modifier.diagonalGradientTint]
 * that uses two copies of the [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors]
 * as its `colors` argument if `pressed` is `true`, or a [Modifier.diagonalGradientTint] that uses
 * our [List] of [Color]'s parameter [colors] if `pressed` is `false`.
 *
 * Then our root composable is a [Surface] whose arguments are:
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.clickable] whose
 *  `onClick` argument is our [onClick] lambda parameter, whose `interactionSource` argument is
 *  our [MutableInteractionSource] variable `interactionSource`, and whose `indication` argument
 *  is `null`. This is followed by a chain to a [Modifier.clip] whose `shape` argument is
 *  [CircleShape], then there is a chain to our [Modifier] variable `border` and a chain to our
 *  [Modifier] variable `background`.
 *  - `color`: background color is [Color.Transparent]
 *
 * In the `content` composable lambda argument of our [Surface] we compose an [Icon] whose
 * arguments are:
 *  - `imageVector`: our [ImageVector] parameter [imageVector]
 *  - `contentDescription`: our [String] parameter [contentDescription]
 *  - `modifier`: our [Modifier] variable `modifierColor`
 *
 * @param imageVector The [ImageVector] to be displayed within the icon button.
 * @param onClick The callback to be invoked when the button is clicked.
 * @param contentDescription The content description of the icon, used for accessibility.
 * @param modifier Modifier to be applied to the button.
 * @param colors A [List] of [Color] values used to create the gradient tint. Defaults to
 * the [JetsnackColors.interactiveSecondary] of our custom [JetsnackTheme.colors].
 */
@Composable
fun JetsnackGradientTintedIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    colors: List<Color> = JetsnackTheme.colors.interactiveSecondary
) {
    /**
     * The remembered [MutableInteractionSource] is used to track the interaction state of the
     * [Modifier.clickable] that is applied to this button to make it clickable.
     */
    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    // This should use a layer + srcIn but needs investigation
    /**
     * The [Modifier.fadeInDiagonalGradientBorder] is a custom [Modifier] that applies an animated
     * diagonal border to this button. The arguments are:
     *  - `showBorder`: is `true` indicating that the border should be visible.
     *  - `colors`: [List] of [Color] values defining the gradient's color stops
     *  - shape`: The shape of the border is [CircleShape], a Circular [Shape] with all the corners
     *  sized as the 50 percent of the shape size
     */
    val border: Modifier = Modifier.fadeInDiagonalGradientBorder(
        showBorder = true,
        colors = JetsnackTheme.colors.interactiveSecondary,
        shape = CircleShape
    )

    /**
     * Subscribes to our [MutableInteractionSource] variable `interactionSource` and returns a
     * [State] wrapped [Boolean] representing whether the button is currently pressed or not.
     */
    val pressed: Boolean by interactionSource.collectIsPressedAsState()

    /**
     * The [Modifier.offsetGradientBackground] is a custom [Modifier] that applies an offset
     * gradient background to this button if our button is currently pressed, otherwise it applies
     * a solid [Modifier.background] using the [JetsnackColors.uiBackground] of our custom
     * [JetsnackTheme.colors].
     */
    @Suppress("RedundantValueArgument")
    val background: Modifier = if (pressed) {
        Modifier.offsetGradientBackground(colors = colors, width = 200f, offset = 0f)
    } else {
        Modifier.background(color = JetsnackTheme.colors.uiBackground)
    }

    /**
     * The [BlendMode] to use for our [Modifier.diagonalGradientTint] variable `modifierColor` is a
     * [BlendMode.Darken] if we are in dark theme, otherwise it is a [BlendMode.Plus].
     */
    val blendMode: BlendMode = if (JetsnackTheme.colors.isDark) BlendMode.Darken else BlendMode.Plus

    /**
     * This [Modifier.diagonalGradientTint] is a custom [Modifier] that applies a diagonal gradient
     * tint to this button using two copies of [JetsnackColors.textSecondary] as its `colors`
     * argument if `pressed` is true, otherwise it applies a [Modifier.diagonalGradientTint] that
     * uses the our [List] of [Color]'s `colors` variable as its `colors` argument.
     */
    val modifierColor: Modifier = if (pressed) {
        Modifier.diagonalGradientTint(
            colors = listOf(
                JetsnackTheme.colors.textSecondary,
                JetsnackTheme.colors.textSecondary
            ),
            blendMode = blendMode
        )
    } else {
        Modifier.diagonalGradientTint(
            colors = colors,
            blendMode = blendMode
        )
    }
    Surface(
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
            .clip(shape = CircleShape)
            .then(other = border)
            .then(other = background),
        color = Color.Transparent
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifierColor
        )
    }
}

/**
 * Two previews of the [JetsnackGradientTintedIconButton] composable for different configurations.
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GradientTintedIconButtonPreview() {
    JetsnackTheme {
        JetsnackGradientTintedIconButton(
            imageVector = Icons.Default.Add,
            onClick = {},
            contentDescription = "Demo",
            modifier = Modifier.padding(all = 4.dp)
        )
    }
}
