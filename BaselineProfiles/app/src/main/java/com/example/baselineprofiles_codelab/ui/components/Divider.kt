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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * A custom divider for Jetsnack, based on Material Design's [Divider].
 *
 * This composable provides a horizontal line that separates content. It's a visual element
 * used to create clear divisions between sections within a layout. It uses a default color
 * and thickness consistent with the Jetsnack theme. The arguments to the [Divider] are:
 *  - `modifier`: our [Modifier] parameter [modifier].
 *  - `color`: our [Color] parameter [color].
 *  - `thickness`: our [Dp] parameter [thickness].
 *  - `startIndent`: our [Dp] parameter [startIndent].
 *
 * @param modifier The [Modifier] to be applied to this divider.
 * @param color The color of the divider. Defaults to a semi-transparent copy of the
 * [JetsnackColors.uiBorder] of our custom [JetsnackTheme.colors] with its `alpha` set to
 * [DividerAlpha] (0.12f).
 * @param thickness The thickness of the divider. Defaults to 1.dp.
 * @param startIndent The amount of space to leave at the start of the divider. Defaults to 0.dp.
 */
@Composable
fun JetsnackDivider(
    modifier: Modifier = Modifier,
    color: Color = JetsnackTheme.colors.uiBorder.copy(alpha = DividerAlpha),
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp
) {
    Divider(
        modifier = modifier,
        color = color,
        thickness = thickness,
        startIndent = startIndent
    )
}

/**
 * The alpha value to use for dividers in the UI. This provides a subtle visual
 * separation between elements without being too intrusive.
 *
 * The value is set to 0.12f, which represents 12% opacity.
 */
private const val DividerAlpha = 0.12f

/**
 * Two previews of the [JetsnackDivider] composable.
 */
@Preview("default", showBackground = true)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun DividerPreview() {
    JetsnackTheme {
        Box(Modifier.size(height = 10.dp, width = 100.dp)) {
            JetsnackDivider(modifier = Modifier.align(alignment = Alignment.Center))
        }
    }
}
