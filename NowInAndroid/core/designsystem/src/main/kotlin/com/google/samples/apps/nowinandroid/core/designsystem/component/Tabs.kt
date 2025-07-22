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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android tab. Wraps Material 3 [Tab] and shifts text label down.
 *
 * Our root composable is a [Tab] whose arguments are:
 *  - `selected`: is our [Boolean] parameter [selected]
 *  - `onClick`: is our lambda parameter [onClick].
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `text`: is our Composable lambda parameter [text].
 *
 * @param selected Whether this tab is selected or not.
 * @param onClick The callback to be invoked when this tab is selected.
 * @param modifier Modifier to be applied to the tab.
 * @param enabled Controls the enabled state of the tab. When `false`, this tab will not be
 * clickable and will appear disabled to accessibility services.
 * @param text The text label content.
 */
@Composable
fun NiaTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        text = {
            val style: TextStyle =
                MaterialTheme.typography.labelLarge.copy(textAlign = TextAlign.Center)
            ProvideTextStyle(
                value = style,
                content = {
                    Box(modifier = Modifier.padding(top = NiaTabDefaults.TabTopPadding)) {
                        text()
                    }
                },
            )
        },
    )
}

/**
 * Now in Android tab row. Wraps Material 3 [TabRow].
 *
 * Our root composable is a [TabRow] whose arguments are:
 *  - `selectedTabIndex`: is our [Int] parameter [selectedTabIndex].
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `containerColor`: is [Color.Transparent].
 *  - `contentColor`: is the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme].
 *  - `indicator`: is a lambda that captures the [List] of [TabPosition] passed the lambda in variable
 *  `tabPositions` and returns a [TabRowDefaults.SecondaryIndicator] whose `modifier` argument is
 *  a [Modifier.tabIndicatorOffset] whose `currentTabPosition` argument is the [TabPosition] at index
 *  [selectedTabIndex] in `tabPositions`, whose `height` argument is 2.dp, whose `color` argument is
 *  the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme].
 *  - `tabs`: is our Composable lambda parameter [tabs].
 *
 * @param selectedTabIndex The index of the currently selected tab.
 * @param modifier Modifier to be applied to the tab row.
 * @param tabs The tabs inside this tab row. Typically this will be multiple [NiaTab]s. Each element
 * inside this lambda will be measured and placed evenly across the row, each taking up equal space.
 */
@Composable
fun NiaTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: @Composable () -> Unit,
) {
    @Suppress("DEPRECATION") // TODO: Replace with PrimaryTabRow or SecondaryTabRow.
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        indicator = { tabPositions: List<TabPosition> ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(currentTabPosition = tabPositions[selectedTabIndex]),
                height = 2.dp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        tabs = tabs,
    )
}

/**
 * Two previews ("Light theme" and "Dark theme") of the [NiaTabRow] and [NiaTab] components.
 * It displays a row of two tabs, "Topics" and "People", with the first tab selected.
 * The tabs are styled using the [NiaTheme].
 */
@ThemePreviews
@Composable
fun TabsPreview() {
    NiaTheme {
        val titles: List<String> = listOf("Topics", "People")
        NiaTabRow(selectedTabIndex = 0) {
            titles.forEachIndexed { index: Int, title: String ->
                NiaTab(
                    selected = index == 0,
                    onClick = { },
                    text = { Text(text = title) },
                )
            }
        }
    }
}

/**
 * Now in Android tab default values.
 */
object NiaTabDefaults {
    /**
     * The default padding from the top of the tab to the text label inside.
     */
    val TabTopPadding: Dp = 7.dp
}
