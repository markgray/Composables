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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
 * Now in Android component catalog.
 * TODO: Continue here.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NiaCatalog() {
    NiaTheme {
        Surface {
            val contentPadding = WindowInsets
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
