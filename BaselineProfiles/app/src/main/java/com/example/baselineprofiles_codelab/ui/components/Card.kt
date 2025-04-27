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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * A composable function that creates a styled card surface.
 *
 * This function wraps the [JetsnackSurface] composable to provide a convenient way to create cards
 * with predefined styling, such as shape, color, content color, border, and elevation.
 *
 * The arguments of the [JetsnackSurface] are:
 *   - `modifier`: our [Modifier] parameter [modifier].
 *   - `shape`: our [Shape] parameter [shape].
 *   - `color`: our [Color] parameter [color].
 *   - `contentColor`: our [Color] parameter [contentColor].
 *   - `elevation`: our [Dp] parameter [elevation].
 *   - `border`: our [BorderStroke] parameter [border].
 *   - `content`: our lambda parameter [content].
 *
 * @param modifier Modifier to be applied to the card.
 * @param shape The shape of the card. Defaults to [Shapes.medium] of our custom [MaterialTheme.shapes].
 * @param color The background color of the card. Defaults to the [JetsnackColors.uiBackground] of
 * our custom [JetsnackTheme.colors].
 * @param contentColor The preferred color for content inside the card. Defaults to the
 * [JetsnackColors.textPrimary] of our custom [JetsnackTheme.colors].
 * @param border The border to draw around the card. Pass `null` for no border. Defaults to `null`.
 * @param elevation The elevation of the card. Defaults to `4.dp`.
 * @param content The content to be displayed inside the card.
 */
@Composable
fun JetsnackCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = JetsnackTheme.colors.uiBackground,
    contentColor: Color = JetsnackTheme.colors.textPrimary,
    border: BorderStroke? = null,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    JetsnackSurface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        elevation = elevation,
        border = border,
        content = content
    )
}

/**
 * Three previews of the [JetsnackCard] composable:
 *  - "default" preview with light theme
 *  - "dark theme" preview with dark theme
 *  - "large font" preview with font scale set to 2.0
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
private fun CardPreview() {
    JetsnackTheme {
        JetsnackCard {
            Text(text = "Demo", modifier = Modifier.padding(all = 16.dp))
        }
    }
}
