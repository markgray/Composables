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

package com.example.baselineprofiles_codelab.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.TopAppBar
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baselineprofiles_codelab.R
import com.example.baselineprofiles_codelab.ui.components.JetsnackDivider
import com.example.baselineprofiles_codelab.ui.theme.AlphaNearOpaque
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * Displays a bar at the top of the screen indicating the delivery destination.
 *
 * This composable shows the current delivery address and provides an expandable button
 * to potentially change the destination. It is designed to be used at the top of a screen
 * or a section related to delivery.
 *
 * Our root composable is a [Column] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.statusBarsPadding] to pad the top of the [Column] to avoid overlap with
 * the status bar. In its [ColumnScope] `content` composable lambda argument we compose a
 * [TopAppBar] and a [JetsnackDivider]. The [TopAppBar]'s `backgroundColor` is set to a copy
 * of the [JetsnackColors.uiBackground] of our custom [JetsnackTheme.colors] with an alpha of
 * [AlphaNearOpaque], its `contentColor` is set to our the [JetsnackColors.textSecondary] of
 * our custom [JetsnackTheme.colors], and its `elevation` is set to `0.dp`. In its [RowScope]
 * `content` composable lambda argument we compose:
 *
 * **First** A [Text] whose arguments are:
 *  - `text`: the current delivery address is the constant string "Delivery to 1600 Amphitheater Way"
 *  - `style`: the [TextStyle] is the [Typography.subtitle1] of our custom [MaterialTheme.typography]
 *  - `color`: the [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors]
 *  - `textAlign`: the [TextAlign] is [TextAlign.Center]
 *  - `maxLines`: the [Int] is 1
 *  - `overflow`: the [TextOverflow] is [TextOverflow.Ellipsis]
 *  - `modifier`: is a [RowScope.weight] whose `weight` is set to 1f with an [RowScope.align]
 *  chained to that whose `alignment` is set to [Alignment.CenterVertically]
 *
 * **Second** An [IconButton] whose `onClick` lambda argument is an empty lambda, and whose
 * `modifier` argument is a [RowScope.align] whose `alignment` is set to [Alignment.CenterVertically].
 * In its `content` composable lambda argument we compose an [Icon] whose arguments are:
 *  - `imageVector`: the [ImageVector] drawn by [Icons.Outlined.ExpandMore].
 *  - `tint`: the [JetsnackColors.brand] of our custom [JetsnackTheme.colors]
 *  - `contentDescription`: the [String] with the resource ID `R.string.label_select_delivery`
 *  ("Select delivery address")
 *
 * @param modifier [Modifier] to be applied to the root layout of the destination bar.
 * Defaults to an empty [Modifier].
 */
@Composable
fun DestinationBar(modifier: Modifier = Modifier) {
    Column(modifier = modifier.statusBarsPadding()) {
        TopAppBar(
            backgroundColor = JetsnackTheme.colors.uiBackground.copy(alpha = AlphaNearOpaque),
            contentColor = JetsnackTheme.colors.textSecondary,
            elevation = 0.dp
        ) {
            Text(
                text = "Delivery to 1600 Amphitheater Way",
                style = MaterialTheme.typography.subtitle1,
                color = JetsnackTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(weight = 1f)
                    .align(alignment = Alignment.CenterVertically)
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ExpandMore,
                    tint = JetsnackTheme.colors.brand,
                    contentDescription = stringResource(id = R.string.label_select_delivery)
                )
            }
        }
        JetsnackDivider()
    }
}

/**
 * Three previews of the [DestinationBar] with different configurations:
 *  - "default": light theme
 *  - "dark theme": dark theme
 *  - "large font": large font size
 */
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun PreviewDestinationBar() {
    JetsnackTheme {
        DestinationBar()
    }
}
