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

package com.google.samples.apps.niacatalog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaFilterChip
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaIconToggleButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaNavigationBar
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaNavigationBarItem
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaOutlinedButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTab
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTabRow
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTextButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTopicTag
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaViewToggleButton
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android component catalog that showcases all Now in Android UI components. Wrapped in our
 * [NiaTheme] custom [MaterialTheme], our root composable is a [Surface]. In its `content` composable
 * lambda argument we first initialize our [PaddingValues] variable `contentPadding` to the
 * [WindowInsets.Companion.systemBars], to which we chain a [WindowInsets.add] to add [WindowInsets]
 * whose `left` argument is `16.dp`, `top` argument is `16.dp`, `right` argument is `16.dp`, and
 * `bottom` argument is `16.dp`, to which we chain a [WindowInsets.asPaddingValues] to convert the
 * [WindowInsets] to [PaddingValues]. Then we compose a [LazyColumn] with its `modifier` argument
 * [Modifier.fillMaxSize()], `contentPadding` argument `contentPadding`, and `verticalArrangement`
 * argument [Arrangement.spacedBy] with its `space` argument `16.dp`. In its [LazyListScope] `content`
 * composable lambda argument we:
 *
 * **First**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * argument we compose a [Text] with its `text` argument "NiA Catalog", and its `style` argument
 * the [Typography.headlineSmall] of our custom [MaterialTheme.typography].
 *
 * **Second**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * argument we compose a [Text] with its `text` argument "Buttons", and its `modifier` argument
 * a [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Third**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * we compose a [FlowRow] with its `horizontalArrangement` argument [Arrangement.spacedBy] with its
 * `space` argument `16.dp`. In its [FlowRowScope] `content` composable lambda argument we compose:
 *  1. a [NiaButton] with its `onClick` argument a do-nothing lambda, and in its [RowScope] `content`
 *  composable lambda argument a [Text] with its `text` argument the [String] "Enabled".
 *  2. a [NiaOutlinedButton] with its `onClick` argument a do-nothing lambda, and in its [RowScope]
 *  `content` composable lambda argument a [Text] with its `text` argument the [String] "Enabled".
 *  3. a [NiaTextButton] with its `onClick` argument a do-nothing lambda, and in its [RowScope]
 *  `content` composable lambda argument a [Text] with its `text` argument the [String] "Enabled".
 *
 * **Fourth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * argument we compose a [Text] with its `text` argument "Disabled buttons", and its `modifier`
 * argument a [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Fifth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * we compose a [FlowRow] with its `horizontalArrangement` argument [Arrangement.spacedBy] with its
 * `space` argument `16.dp`. In its [FlowRowScope] `content` composable lambda argument we compose:
 *  1. a [NiaButton] with its `onClick` argument a do-nothing lambda, its `enabled` argument set
 *  to `false`, and in its [RowScope] `content` composable lambda argument a [Text] with its
 *  `text` argument the [String] "Disabled".
 *  2. a [NiaOutlinedButton] with its `onClick` argument a do-nothing lambda, its `enabled`
 *  argument set to `false`, and in its [RowScope] `content` composable lambda argument a [Text]
 *  with its `text` argument the [String] "Disabled".
 *  3. a [NiaTextButton] with its `onClick` argument a do-nothing lambda, its `enabled` argument
 *  set to `false`, and in its [RowScope] `content` composable lambda argument a [Text] with its
 *  `text` argument the [String] "Disabled".
 *
 * **Sixth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * argument we compose a [Text] with its `text` argument "Buttons with leading icons", and its
 * `modifier` argument a [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Seventh**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * we compose a [FlowRow] with its `horizontalArrangement` argument [Arrangement.spacedBy] with its
 * `space` argument `16.dp`. In its [FlowRowScope] `content` composable lambda argument we compose:
 *  1. a [NiaButton] with its `onClick` argument a do-nothing lambda, its `text` argument set to a
 *  [Text] with its `text` argument the [String] "Enabled", and its `leadingIcon` argument set to
 *  an [Icon] whose `imageVector` argument is the [ImageVector] drawn by [NiaIcons.Add], and whose
 *  `contentDescription` argument is `null`.
 *  2. a [NiaOutlinedButton] with its `onClick` argument a do-nothing lambda, its `text` argument
 *  set to a [Text] with its `text` argument the [String] "Enabled", and its `leadingIcon` argument
 *  set to an [Icon] whose `imageVector` argument is the [ImageVector] drawn by [NiaIcons.Add], and
 *  whose `contentDescription` argument is `null`.
 *  3. a [NiaTextButton] with its `onClick` argument a do-nothing lambda, its `text` argument set
 *  to a [Text] with its `text` argument the [String] "Enabled", and its `leadingIcon` argument set
 *  to an [Icon] whose `imageVector` argument is the [ImageVector] drawn by [NiaIcons.Add], and whose
 *  `contentDescription` argument is `null`.
 *
 * **Eighth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * argument we compose a [Text] with its `text` argument "Disabled buttons with leading icons", and
 * its `modifier` argument a [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Ninth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * we compose a [FlowRow] with its `horizontalArrangement` argument [Arrangement.spacedBy] with its
 * `space` argument `16.dp`. In its [FlowRowScope] `content` composable lambda argument we compose:
 *  1. a [NiaButton] with its `onClick` argument a do-nothing lambda, its `enabled` argument set
 *  to `false`, its `text` argument set to a [Text] with its `text` argument the [String] "Disabled",
 *  and its `leadingIcon` argument set to an [Icon] whose `imageVector` argument is the [ImageVector]
 *  drawn by [NiaIcons.Add], and whose `contentDescription` argument is `null`.
 *  2. a [NiaOutlinedButton] with its `onClick` argument a do-nothing lambda, its `enabled`
 *  argument set to `false`, its `text` argument set to a [Text] with its `text` argument the
 *  [String] "Disabled", and its `leadingIcon` argument set to an [Icon] whose `imageVector` argument
 *  is the [ImageVector] drawn by [NiaIcons.Add], and whose `contentDescription` argument is `null`.
 *  3. a [NiaTextButton] with its `onClick` argument a do-nothing lambda, its `enabled` argument
 *  set to `false`, its `text` argument set to a [Text] with its `text` argument the [String]
 *  "Disabled", and its `leadingIcon` argument set to an [Icon] whose `imageVector` argument is the
 *  [ImageVector] drawn by [NiaIcons.Add], and whose `contentDescription` argument is `null`.
 *
 * **Tenth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * argument we compose a [Text] with its `text` argument "Dropdown menus" and its `modifier` argument
 * a [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Eleventh**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * argument we compose a [Text] with its `text` argument "Chips" and its `modifier` argument a
 * [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Twelfth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable lambda
 * we compose a [FlowRow] with its `horizontalArrangement` argument [Arrangement.spacedBy] with its
 * `space` argument `16.dp`. In its [FlowRowScope] `content` composable lambda argument we first
 * initialize and [rememberSaveable] our [MutableState] wrapped [Boolean] variable `firstChecked` to
 * `false`, and then compose:
 *  1. a [NiaFilterChip] with its `selected` argument set to `firstChecked`, its `onSelectedChange`
 *  argument a lambda whose that sets `firstChecked` to the [Boolean] passed the lambda, and its
 *  `label` argument a [Text] whose `text` argument is the [String] "Enabled".
 *  2. we initialize and [rememberSaveable] our [MutableState] wrapped [Boolean] variable
 *  `secondChecked` to `true`, and then compose a [NiaFilterChip] with its `selected` argument is
 *  `secondChecked`, its `onSelectedChange` argument a lambda whose that sets `secondChecked` to
 *  the [Boolean] passed the lambda, and its `label` argument a [Text] whose `text` argument is the
 *  [String] "Enabled.
 *  3. a [NiaFilterChip] with its `selected` argument set to `false`, its `onSelectedChange` argument
 *  set to a do-nothing lambda, its `enabled` argument set to `false`, and its `label` argument a
 *  [Text] whose `text` argument is the [String] "Disabled".
 *  4. a [NiaFilterChip] with its `selected` argument set to `true`, its `onSelectedChange` argument
 *  set to a do-nothing lambda, its `enabled` argument set to `false`, and its `label` argument a
 *  [Text] whose `text` argument is the [String] "Disabled".
 *
 * **Thirteenth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable
 * lambda argument we compose a [Text] with its `text` argument "Icon buttons", and its `modifier`
 * argument a [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Fourteenth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable
 * we first initialize and [rememberSaveable] our [MutableState] wrapped [Boolean] variable
 * `firstChecked` to `false`, and then compose:
 *  1. a [NiaIconToggleButton] with its `checked` argument set to `firstChecked`, its `onCheckedChange`
 *  argument a lambda whose that sets `firstChecked` to the [Boolean] passed the lambda, its `icon`
 *  argument set to an [Icon] whose `imageVector` argument is the [ImageVector] drawn by
 *  [NiaIcons.BookmarkBorder], and whose `contentDescription` argument is `null`, The `checkedIcon`
 *  argument of the [NiaIconToggleButton] is set to an [Icon] whose `imageVector` argument is the
 *  [ImageVector] drawn by [NiaIcons.Bookmark], and whose `contentDescription` argument is `null`.
 *  2. we initialize and [rememberSaveable] our [MutableState] wrapped [Boolean] variable
 *  `secondChecked` to `true`, and then compose a [NiaIconToggleButton] with its `checked` argument
 *  set to `secondChecked`, its `onCheckedChange` argument a do-nothing lambda, its `icon` argument
 *  set to an [Icon] whose `imageVector` argument is the [ImageVector] drawn by
 *  [NiaIcons.BookmarkBorder], and whose `contentDescription` argument is `null`, The `checkedIcon`
 *  argument of the [NiaIconToggleButton] is set to an [Icon] whose `imageVector` argument is the
 *  [ImageVector] drawn by [NiaIcons.Bookmark], and whose `contentDescription` argument is `null`.
 *  The `enabled` argument of the [NiaIconToggleButton] is set to `false`.
 *  3. a [NiaIconToggleButton] with its `checked` argument set to `true`, its `onCheckedChange`
 *  argument set to a do-nothing lambda, its `icon` argument set to an [Icon] whose `imageVector`
 *  argument is the [ImageVector] drawn by [NiaIcons.BookmarkBorder], and whose `contentDescription`
 *  argument is `null`, The `checkedIcon` argument of the [NiaIconToggleButton] is set to an [Icon]
 *  whose `imageVector` argument is the [ImageVector] drawn by [NiaIcons.Bookmark], and whose
 *  `contentDescription` argument is `null`. The `enabled` argument of the [NiaIconToggleButton] is
 *  set to `false`.
 *
 * **Fifteenth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable
 * lambda argument we compose a [Text] with its `text` argument "View toggle", and its `modifier`
 * argument a [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Sixteenth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable
 * lambda argument we first initialize and [rememberSaveable] our [MutableState] wrapped [Boolean]
 * variable `firstSelected` to `false`, and then compose:
 *  1. a [NiaViewToggleButton] with its `expanded` argument set to `firstExpanded`, its
 *  `onExpandedChange` argument a lambda whose that sets `firstExpanded` to the [Boolean] passed
 *  the lambda, its `compactText` argument set to a [Text] whose `text` argument is the [String]
 *  "Compact view", and its `expandedText` argument set to a [Text] whose `text` argument is the
 *  [String] "Expanded view".
 *  2. we initialize and [rememberSaveable] our [MutableState] wrapped [Boolean] variable
 *  `secondExpanded` to `true`, and then compose a [NiaViewToggleButton] with its `expanded` argument
 *  set to `secondExpanded`, its `onExpandedChange` argument a lambda that sets `secondExpanded` to
 *  the [Boolean] passed the lambda, its `compactText` argument set to a [Text] whose `text` argument
 *  is the [String] "Compact view", and its `expandedText` argument set to a [Text] whose `text`
 *  argument is the [String] "Expanded view".
 *  3. a [NiaViewToggleButton] with its `expanded` argument set to `false`, its `onExpandedChange`
 *  argument set to a do-nothing lambda, its `compactText` argument set to a [Text] whose `text`
 *  argument is the [String] "Disabled", and its `expandedText` argument set to a [Text] whose
 *  `text` argument is the [String] "Disabled". The `enabled` argument of the [NiaViewToggleButton]
 *  is set to `false`.
 *
 * **Seventeenth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable
 * lambda argument we compose a [Text] with its `text` argument "Tags", and its `modifier` argument
 * a [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Eighteenth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable
 * lambda argument we compose a [FlowRow] with its `horizontalArrangement` argument is an
 * [Arrangement.spacedBy] whose `space` argument is `16.dp`. In its [FlowRowScope] `content` we
 * compose:
 *  1. a [NiaTopicTag] with its `followed` argument set to `true`, its `onClick` argument a
 *  do-nothing lambda, and its `text` argument set to a [Text] whose `text` argument is the [String]
 *  "Topic 1" converted to uppercase.
 *  2. a [NiaTopicTag] with its `followed` argument set to `false`, its `onClick` argument a
 *  do-nothing lambda, and its `text` argument set to a [Text] whose `text` argument is the [String]
 *  "Topic 2" converted to uppercase.
 *  3. a [NiaTopicTag] with its `followed` argument set to `false`, its `onClick` argument a
 *  do-nothing lambda, and its `text` argument set to a [Text] whose `text` argument is the [String]
 *  "Disabled" converted to uppercase, and the `enabled` argument of the [NiaTopicTag] is set to
 *  `false`.
 *
 * **Nineteenth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable
 * lambda argument we compose a [Text] with its `text` argument "Tabs", and its `modifier` argument
 * a [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Twentieth**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable
 * lambda argument we first initialize and [rememberSaveable] our [MutableState] wrapped [Int]
 * variable `selectedTab` to `0`, and initialize our [List] of [String] variable `tabTitles` to the
 * [List] ("Topics", "People"). Then compose a [NiaTabRow] with its `selectedTabIndex` argument set
 * to `selectedTab`, and in its `tabs` composable lambda argument we use the [Iterable.forEachIndexed]
 * method of [List] of [String] variable `tabTitles` to loop through all of the [String]s in
 * [List] of [String] variable `tabTitles` capturing the index in variable `index` and the [String]
 * in variable `title`. Then we compose a [NiaTab] whose `selected` argument is `true` if `selectedTab`
 * is equal to `index`, and whose `onClick` argument is a lambda whose that sets `selectedTab` to
 * `index`, and whose `text` argument is a [Text] whose `text` argument is the [String] in [String]
 * variable `title`.
 *
 * **Twenty first**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable
 * lambda argument we compose a [Text] with its `text` argument "Navigation", and its `modifier`
 * argument a [Modifier.padding] with its `top` argument `16.dp`.
 *
 * **Twenty second**: Compose an [LazyListScope.item] in whose [LazyItemScope] `content` composable
 * lambda argument we first initialize and [rememberSaveable] our [MutableState] wrapped [Int]
 * variable `selectedDestination` to `0`, and initialize our [List] of [String] variable `items`
 * to the [List] ("For you", "Saved", "Interests"). We initialize our [List] of [ImageVector]
 * variable `icons` to the [List] of [NiaIcons.UpcomingBorder], [NiaIcons.BookmarksBorder], and
 * [NiaIcons.Grid3x3]. We initialize our [List] of [ImageVector] variable `selectedIcons` to the
 * [List] of [NiaIcons.Upcoming], [NiaIcons.Bookmarks], and [NiaIcons.Grid3x3], and then compose a
 * [NiaNavigationBar] in whose [RowScope] `content` composable lambda argument we use the
 * [Iterable.forEachIndexed] method of [List] of [String] variable `items` to loop through all of
 * the [String]s in [List] of [String] variable `items` capturing the index in variable `index` and
 * the [String] in variable `item`. Then we compose a [NiaNavigationBarItem] whose `icon` argument
 * is an [Icon] whose `imageVector` argument is the [ImageVector] in [List] of [ImageVector] variable
 * `icons` at index `index`, and whose `contentDescription` argument is the [String] `item`. The
 * `selectedIcon` argument of the [NiaNavigationBarItem] is set to an [Icon] whose `imageVector`
 * argument is the [ImageVector] in [List] of [ImageVector] variable `selectedIcons` at index `index`,
 * and whose `contentDescription` argument is the [String] `item`. The `label` argument of the
 * [NiaNavigationBarItem] is set to a [Text] whose `text` argument is the [String] `item`. The
 * `selected` argument of the [NiaNavigationBarItem] is set to `true` if `selectedItem` is equal to
 * `index`, and the `onClick` argument of the [NiaNavigationBarItem] is a lambda whose that sets
 * `selectedItem` to `index`.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NiaCatalog() {
    NiaTheme {
        Surface {
            val contentPadding: PaddingValues = WindowInsets
                .systemBars
                .add(WindowInsets(left = 16.dp, top = 16.dp, right = 16.dp, bottom = 16.dp))
                .asPaddingValues()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            ) {
                item {
                    Text(
                        text = "NiA Catalog",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                item { Text(text = "Buttons", modifier = Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                        NiaButton(onClick = {}) {
                            Text(text = "Enabled")
                        }
                        NiaOutlinedButton(onClick = {}) {
                            Text(text = "Enabled")
                        }
                        NiaTextButton(onClick = {}) {
                            Text(text = "Enabled")
                        }
                    }
                }
                item { Text(text = "Disabled buttons", modifier = Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                        NiaButton(
                            onClick = {},
                            enabled = false,
                        ) {
                            Text(text = "Disabled")
                        }
                        NiaOutlinedButton(
                            onClick = {},
                            enabled = false,
                        ) {
                            Text(text = "Disabled")
                        }
                        NiaTextButton(
                            onClick = {},
                            enabled = false,
                        ) {
                            Text(text = "Disabled")
                        }
                    }
                }
                item {
                    Text(
                        text = "Buttons with leading icons",
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                        NiaButton(
                            onClick = {},
                            text = { Text(text = "Enabled") },
                            leadingIcon = {
                                Icon(imageVector = NiaIcons.Add, contentDescription = null)
                            },
                        )
                        NiaOutlinedButton(
                            onClick = {},
                            text = { Text(text = "Enabled") },
                            leadingIcon = {
                                Icon(imageVector = NiaIcons.Add, contentDescription = null)
                            },
                        )
                        NiaTextButton(
                            onClick = {},
                            text = { Text(text = "Enabled") },
                            leadingIcon = {
                                Icon(imageVector = NiaIcons.Add, contentDescription = null)
                            },
                        )
                    }
                }
                item {
                    Text(
                        text = "Disabled buttons with leading icons",
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                        NiaButton(
                            onClick = {},
                            enabled = false,
                            text = { Text(text = "Disabled") },
                            leadingIcon = {
                                Icon(imageVector = NiaIcons.Add, contentDescription = null)
                            },
                        )
                        NiaOutlinedButton(
                            onClick = {},
                            enabled = false,
                            text = { Text(text = "Disabled") },
                            leadingIcon = {
                                Icon(imageVector = NiaIcons.Add, contentDescription = null)
                            },
                        )
                        NiaTextButton(
                            onClick = {},
                            enabled = false,
                            text = { Text(text = "Disabled") },
                            leadingIcon = {
                                Icon(imageVector = NiaIcons.Add, contentDescription = null)
                            },
                        )
                    }
                }
                item { Text(text = "Dropdown menus", modifier = Modifier.padding(top = 16.dp)) }
                item { Text(text = "Chips", modifier = Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                        var firstChecked: Boolean by rememberSaveable { mutableStateOf(false) }
                        NiaFilterChip(
                            selected = firstChecked,
                            onSelectedChange = { checked: Boolean -> firstChecked = checked },
                            label = { Text(text = "Enabled") },
                        )
                        var secondChecked: Boolean by rememberSaveable { mutableStateOf(true) }
                        NiaFilterChip(
                            selected = secondChecked,
                            onSelectedChange = { checked: Boolean -> secondChecked = checked },
                            label = { Text(text = "Enabled") },
                        )
                        NiaFilterChip(
                            selected = false,
                            onSelectedChange = {},
                            enabled = false,
                            label = { Text(text = "Disabled") },
                        )
                        NiaFilterChip(
                            selected = true,
                            onSelectedChange = {},
                            enabled = false,
                            label = { Text(text = "Disabled") },
                        )
                    }
                }
                item { Text(text = "Icon buttons", modifier = Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                        var firstChecked: Boolean by rememberSaveable { mutableStateOf(false) }
                        NiaIconToggleButton(
                            checked = firstChecked,
                            onCheckedChange = { checked: Boolean -> firstChecked = checked },
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
                        var secondChecked: Boolean by rememberSaveable { mutableStateOf(true) }
                        NiaIconToggleButton(
                            checked = secondChecked,
                            onCheckedChange = { checked: Boolean -> secondChecked = checked },
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
                        NiaIconToggleButton(
                            checked = false,
                            onCheckedChange = {},
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
                            enabled = false,
                        )
                        NiaIconToggleButton(
                            checked = true,
                            onCheckedChange = {},
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
                            enabled = false,
                        )
                    }
                }
                item { Text(text = "View toggle", modifier = Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                        var firstExpanded: Boolean by rememberSaveable { mutableStateOf(false) }
                        NiaViewToggleButton(
                            expanded = firstExpanded,
                            onExpandedChange = { expanded: Boolean -> firstExpanded = expanded },
                            compactText = { Text(text = "Compact view") },
                            expandedText = { Text(text = "Expanded view") },
                        )
                        var secondExpanded: Boolean by rememberSaveable { mutableStateOf(true) }
                        NiaViewToggleButton(
                            expanded = secondExpanded,
                            onExpandedChange = { expanded: Boolean -> secondExpanded = expanded },
                            compactText = { Text(text = "Compact view") },
                            expandedText = { Text(text = "Expanded view") },
                        )
                        NiaViewToggleButton(
                            expanded = false,
                            onExpandedChange = {},
                            compactText = { Text(text = "Disabled") },
                            expandedText = { Text(text = "Disabled") },
                            enabled = false,
                        )
                    }
                }
                item { Text(text = "Tags", modifier = Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                        NiaTopicTag(
                            followed = true,
                            onClick = {},
                            text = { Text(text = "Topic 1".uppercase()) },
                        )
                        NiaTopicTag(
                            followed = false,
                            onClick = {},
                            text = { Text(text = "Topic 2".uppercase()) },
                        )
                        NiaTopicTag(
                            followed = false,
                            onClick = {},
                            text = { Text(text = "Disabled".uppercase()) },
                            enabled = false,
                        )
                    }
                }
                item { Text(text = "Tabs", modifier = Modifier.padding(top = 16.dp)) }
                item {
                    var selectedTabIndex: Int by rememberSaveable { mutableIntStateOf(0) }
                    val titles: List<String> = listOf("Topics", "People")
                    NiaTabRow(selectedTabIndex = selectedTabIndex) {
                        titles.forEachIndexed { index: Int, title: String ->
                            NiaTab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = title) },
                            )
                        }
                    }
                }
                item { Text(text = "Navigation", modifier = Modifier.padding(top = 16.dp)) }
                item {
                    var selectedItem: Int by rememberSaveable { mutableIntStateOf(0) }
                    val items: List<String> = listOf("For you", "Saved", "Interests")
                    val icons: List<ImageVector> = listOf(
                        NiaIcons.UpcomingBorder,
                        NiaIcons.BookmarksBorder,
                        NiaIcons.Grid3x3,
                    )
                    val selectedIcons: List<ImageVector> = listOf(
                        NiaIcons.Upcoming,
                        NiaIcons.Bookmarks,
                        NiaIcons.Grid3x3,
                    )
                    NiaNavigationBar {
                        items.forEachIndexed { index: Int, item: String ->
                            NiaNavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = item,
                                    )
                                },
                                selectedIcon = {
                                    Icon(
                                        imageVector = selectedIcons[index],
                                        contentDescription = item,
                                    )
                                },
                                label = { Text(text = item) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index },
                            )
                        }
                    }
                }
            }
        }
    }
}
