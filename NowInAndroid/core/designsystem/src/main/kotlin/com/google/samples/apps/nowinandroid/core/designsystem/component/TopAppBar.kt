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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.google.samples.apps.nowinandroid.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android top app bar with content slots. Wraps Material 3 [CenterAlignedTopAppBar].
 * Our root composable is a [CenterAlignedTopAppBar] whose arguments are:
 *  - `title`: is a lambda that composes a [Text] whose `text` argument is the string resource
 *  whose ID is our [Int] parameter [titleRes].
 *  - `navigationIcon`: is a lambda that composes a [IconButton] whose `onClick` argument is our
 *  lambda parameter [onNavigationClick], and in its `content` Composable lambda argument we compose
 *  an [Icon] whose `imageVector` argument is our [ImageVector] parameter [navigationIcon], whose
 *  `contentDescription` argument is our [String] parameter [navigationIconContentDescription], and
 *  whose `tint` argument is the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme].
 *  - `actions`: is a [RowScope] lambda that composes an [IconButton] whose `onClick` argument is
 *  our lambda parameter [onActionClick], and in its `content` Composable lambda argument we compose
 *  an [Icon] whose `imageVector` argument is our [ImageVector] parameter [actionIcon], whose
 *  `contentDescription` argument is our [String] parameter [actionIconContentDescription], and
 *  whose `tint` argument is the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme].
 *  - `colors`: is our [TopAppBarColors] parameter [colors].
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.testTag] whose `tag`
 *  argument is "niaTopAppBar".
 *
 * @param titleRes The title to be displayed in the top app bar.
 * @param navigationIcon The navigation icon to be displayed at the start of the top app bar.
 * @param navigationIconContentDescription The content description for the navigation icon.
 * @param actionIcon The action icon to be displayed at the end of the top app bar.
 * @param actionIconContentDescription The content description for the action icon.
 * @param modifier The modifier to be applied to the top app bar.
 * @param colors The colors to be used for the top app bar.
 * @param onNavigationClick The callback to be invoked when the navigation icon is clicked.
 * @param onActionClick The callback to be invoked when the action icon is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NiaTopAppBar(
    @StringRes titleRes: Int,
    navigationIcon: ImageVector,
    navigationIconContentDescription: String,
    actionIcon: ImageVector,
    actionIconContentDescription: String,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = navigationIconContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionIconContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        colors = colors,
        modifier = modifier.testTag(tag = "niaTopAppBar"),
    )
}

/**
 * This is a Preview of our [NiaTopAppBar] Composable wrapped in our [NiaTheme] custom [MaterialTheme]
 * Composable. We use [android.R.string.untitled] as the `titleRes` argument, [NiaIcons.Search]
 * as the `navigationIcon` argument, "Navigation icon" as the `navigationIconContentDescription`
 * argument, [NiaIcons.MoreVert] as the `actionIcon` argument, and "Action icon" as the
 * `actionIconContentDescription` argument.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Top App Bar")
@Composable
private fun NiaTopAppBarPreview() {
    NiaTheme {
        NiaTopAppBar(
            titleRes = android.R.string.untitled,
            navigationIcon = NiaIcons.Search,
            navigationIconContentDescription = "Navigation icon",
            actionIcon = NiaIcons.MoreVert,
            actionIconContentDescription = "Action icon",
        )
    }
}
