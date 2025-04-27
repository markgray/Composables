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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme
import kotlin.math.ln

/**
 * An alternative to [androidx.compose.material.Surface] utilizing Jetsnack theming.
 *
 * JetsnackSurface is a composable that applies a standard background color, shape, border,
 * elevation, and content color to its content. It's a foundational component for building UI
 * elements with consistent styling within the Jetsnack application.
 *
 * Our root Composable is a [Box] whose arguments are:
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.shadow] whose `elevation`
 *  argument is our [Dp] parameter [elevation], whose `shape` argument is our [Shape] parameter
 *  [shape] and whose `clip` argument is set to `false`, followed by a chain to a [Modifier.zIndex]
 *  whose `zIndex` argument is the float [Dp.value] of our [Dp] parameter [elevation], then is
 *  our [BorderStroke] parameter [border] is not `null` a chain to a [Modifier.border] whose
 *  `border` argument is our [BorderStroke] parameter [border] and whose `shape` argument is our
 *  [Shape] parameter [shape] otherwise an empty [Modifier] is chained, and then we chain a
 *  [Modifier.background] whose `color` argument is the [Color] returned by the
 *  [getBackgroundColorForElevation] function for the `color` argument [color] and the
 *  `elevation` argument [elevation] and whose `shape` argument is our [Shape] parameter
 *  [shape], and at the end of the chain is a [Modifier.clip] whose `shape` argument is our [Shape].
 *
 * In the [BoxScope] `content` composable lambda argument of the [Box] we compose a
 * [CompositionLocalProvider] whose [ProvidedValue] `value` argument provides our [Color] parameter
 * [contentColor] as the [LocalContentColor] to its `content` composable lambda argument which is our
 * [content] composable lambda parameter.
 *
 * @param modifier Modifier to be applied to the Surface.
 * @param shape Defines the shape of the surface. Defaults to [RectangleShape].
 * @param color The background color of the surface. Defaults to the [JetsnackColors.uiBackground]
 * of our custom [JetsnackTheme.colors].
 * @param contentColor The preferred content color provided to the content. Defaults to the
 * [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors].
 * @param border Optional border to draw around the surface.
 * @param elevation The z-index based elevation of the surface, controlling the shadow
 * below it. Defaults to 0.dp.
 * @param content The content to be displayed inside the surface.
 */
@Composable
fun JetsnackSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = JetsnackTheme.colors.uiBackground,
    contentColor: Color = JetsnackTheme.colors.textSecondary,
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.shadow(elevation = elevation, shape = shape, clip = false)
            .zIndex(zIndex = elevation.value)
            .then(if (border != null) Modifier.border(border = border, shape = shape) else Modifier)
            .background(
                color = getBackgroundColorForElevation(color = color, elevation = elevation),
                shape = shape
            )
            .clip(shape = shape)
    ) {
        CompositionLocalProvider(value = LocalContentColor provides contentColor, content = content)
    }
}

/**
 * Calculates the background color for a given elevation.
 *
 * This function determines the background color based on the provided base color and elevation.
 * If the elevation is greater than 0.dp, it applies an elevation effect to the color using
 * [Color.withElevation], making the color slightly lighter based on the elevation. Otherwise,
 * it returns the original color unchanged.
 *
 * @param color The base background color.
 * @param elevation The elevation, which determines the intensity of the elevation effect.
 * @return The calculated background color, adjusted for elevation if applicable.
 */
@Composable
private fun getBackgroundColorForElevation(color: Color, elevation: Dp): Color {
    return if (elevation > 0.dp // && https://issuetracker.google.com/issues/161429530
        // JetsnackTheme.colors.isDark //&&
        // color == JetsnackTheme.colors.uiBackground
    ) {
        color.withElevation(elevation = elevation)
    } else {
        color
    }
}

/**
 * Applies a [Color.White] overlay to this color based on the [elevation]. This increases visibility
 * of elevation for surfaces in a dark theme.
 *
 * TODO: Remove when public https://issuetracker.google.com/155181601
 *
 * Applies an elevation effect to the color by compositing a calculated foreground color over the
 * base color.
 *
 * This function simulates the visual effect of an elevation by creating a semi-transparent overlay
 * based on the provided elevation value and then blending it with the base color. Higher elevations
 * result in a lighter overlay, creating the appearance of a shadow or highlight.
 *
 * @param elevation The elevation value, determining the intensity of the effect. Higher values
 * produce a lighter overlay.
 * @return A new [Color] representing the base color with the elevation effect applied.
 */
private fun Color.withElevation(elevation: Dp): Color {
    val foreground: Color = calculateForeground(elevation = elevation)
    return foreground.compositeOver(background = this)
}

/*
 * @return
 * the resultant color.
 */

/**
 * Calculates an alpha-modified [Color.White] to overlay on top of a color based on the provided
 * elevation.
 *
 * This function determines the alpha value of a foreground color ([Color.White])
 * based on the logarithmic relationship between the elevation and the desired opacity.
 * The higher the elevation, the more opaque the foreground color will be.
 *
 * The formula used to calculate the alpha value is:
 * `alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f`
 *
 * Where:
 *  - `elevation` is the elevation in Dp.
 *  - `ln` is the natural logarithm.
 *  - `elevation.value` converts the Dp to a float value
 *  - `alpha` is the calculated alpha value, ranging from 0.0f (fully transparent)
 *  to 1.0f (fully opaque).
 *
 * @param elevation The elevation value in Dp. This value influences the opacity of the foreground.
 * @return The calculated foreground color with an alpha value determined by the elevation.
 * The base color is always white.
 */
private fun calculateForeground(elevation: Dp): Color {
    val alpha: Float = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return Color.White.copy(alpha = alpha)
}
