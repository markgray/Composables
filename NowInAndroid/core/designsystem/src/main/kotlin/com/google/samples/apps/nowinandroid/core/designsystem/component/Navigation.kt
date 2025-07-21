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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * Now in Android navigation bar item with icon and label content slots. Wraps Material 3
 * [NavigationBarItem].
 *
 * Our root composable is a [NiaNavigationSuiteScaffold] whose arguments are:
 *  - `selected`: is our [Boolean] parameter [selected]
 *  - `onClick`: is our lambda parameter [onClick].
 *  - `icon`: if our [Boolean] parameter [selected] is `true` it is our lambda parameter [selectedIcon],
 *  and if it is `false` it is our lambda parameter [icon].
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `label`: is our Composable lambda parameter [label].
 *  - `alwaysShowLabel`: is our [Boolean] parameter [alwaysShowLabel].
 *  - `colors`: is a [NavigationBarItemDefaults.colors] whose `selectedIconColor` is the [Color]
 *  returned by the [NiaNavigationDefaults.navigationSelectedItemColor] method, whose
 *  `unselectedIconColor` is the [Color] returned by the [NiaNavigationDefaults.navigationContentColor],
 *  method, whose `selectedTextColor` is the [Color] returned by the
 *  [NiaNavigationDefaults.navigationSelectedItemColor] method, whose `unselectedTextColor` is the
 *  [Color] returned by the [NiaNavigationDefaults.navigationContentColor] method, and whose
 *  `indicatorColor` is the [Color] returned by the [NiaNavigationDefaults.navigationIndicatorColor]
 *  method.
 *
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param modifier Modifier to be applied to this item.
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 * @param icon The item icon content when [selected] is `false`.
 * @param selectedIcon The item icon content when [selected] is `true`.
 * @param label The item text label content.
 */
@Composable
fun RowScope.NiaNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = NiaNavigationDefaults.navigationContentColor(),
            selectedTextColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = NiaNavigationDefaults.navigationContentColor(),
            indicatorColor = NiaNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * Now in Android navigation bar with content slot. Wraps Material 3 [NavigationBar].
 *
 * Our root composable is a [NavigationBar] whose arguments are:
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `contentColor`: is the [Color] returned by the [NiaNavigationDefaults.navigationContentColor]
 *  method.
 *  - `tonalElevation`: is `0.dp`.
 *  - `content`: is our Composable lambda parameter [content].
 *
 * @param modifier Modifier to be applied to the navigation bar.
 * @param content Destinations inside the navigation bar. This should contain multiple
 * [NavigationBarItem]s.
 */
@Composable
fun NiaNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = NiaNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}

/**
 * Now in Android navigation rail item with icon and label content slots. Wraps Material 3
 * [NavigationRailItem].
 *
 * Our root composable is a [NavigationRailItem] whose arguments are:
 *  - `selected`: is our [Boolean] parameter [selected]
 *  - `onClick`: is our lambda parameter [onClick].
 *  - `icon`: if our [Boolean] parameter [selected] is `true` it is our lambda parameter [selectedIcon],
 *  and if it is `false` it is our lambda parameter [icon].
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `enabled`: is our [Boolean] parameter [enabled].
 *  - `label`: is our Composable lambda parameter [label].
 *  - `alwaysShowLabel`: is our [Boolean] parameter [alwaysShowLabel].
 *  - `colors`: is a [NavigationRailItemDefaults.colors] whose `selectedIconColor` is the [Color]
 *  returned by the [NiaNavigationDefaults.navigationSelectedItemColor] method, whose
 *  `unselectedIconColor` is the [Color] returned by the [NiaNavigationDefaults.navigationContentColor],
 *  method, whose `selectedTextColor` is the [Color] returned by the
 *  [NiaNavigationDefaults.navigationSelectedItemColor] method, whose `unselectedTextColor` is the
 *  [Color] returned by the [NiaNavigationDefaults.navigationContentColor] method, and whose
 *  `indicatorColor` is the [Color] returned by the [NiaNavigationDefaults.navigationIndicatorColor]
 *  method.
 *
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param modifier Modifier to be applied to this item.
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 * @param icon The item icon content when [selected] is `false`.
 * @param selectedIcon The item icon content when [selected] is `true`.
 * @param label The item text label content.
 */
@Composable
fun NiaNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = NiaNavigationDefaults.navigationContentColor(),
            selectedTextColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = NiaNavigationDefaults.navigationContentColor(),
            indicatorColor = NiaNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * Now in Android navigation rail with header and content slots. Wraps Material 3 [NavigationRail].
 *
 * Our root composable is a [NavigationRail] whose arguments are:
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `containerColor`: is [Color.Transparent].
 *  - `contentColor`: is the [Color] returned by the [NiaNavigationDefaults.navigationContentColor]
 *  method.
 *  - `header`: is our Composable lambda parameter [header].
 *  - `content`: is our Composable lambda parameter [content].
 *
 * @param modifier Modifier to be applied to the navigation rail.
 * @param header Optional header that may hold a floating action button or a logo.
 * @param content Destinations inside the navigation rail. This should contain multiple
 * [NavigationRailItem]s.
 */
@Composable
fun NiaNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = NiaNavigationDefaults.navigationContentColor(),
        header = header,
        content = content,
    )
}

/**
 * Now in Android navigation suite scaffold with item and content slots.
 * Wraps Material 3 [NavigationSuiteScaffold].
 *
 * We start by initializing our [NavigationSuiteType] variable `layoutType` to the value returned
 * by the [NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo] method with its `adaptiveInfo`
 * argument set to our [WindowAdaptiveInfo] parameter [windowAdaptiveInfo].
 *
 * We initialize our [NavigationSuiteItemColors] variable `navigationSuiteItemColors` to an
 * instance of the [NavigationSuiteItemColors] class whose `navigationBarItemColors` argument is set
 * to a [NavigationBarItemDefaults.colors] whose `selectedIconColor` is the [Color] returned by the
 * [NiaNavigationDefaults.navigationSelectedItemColor] method, whose `unselectedIconColor` is the
 * [Color] returned by the [NiaNavigationDefaults.navigationContentColor] method, method,
 * whose `selectedTextColor` is the [Color] returned by the
 * [NiaNavigationDefaults.navigationSelectedItemColor] method, whose `unselectedTextColor` is the
 * [Color] returned by the [NiaNavigationDefaults.navigationContentColor] method, and whose
 * `indicatorColor` is the [Color] returned by the [NiaNavigationDefaults.navigationIndicatorColor]
 * method. The `navigationRailItemColors` argument is set to a [NavigationRailItemDefaults.colors]
 * whose `selectedIconColor` is the [Color] returned by the [NiaNavigationDefaults.navigationSelectedItemColor]
 * method, whose `unselectedIconColor` is the [Color] returned by the
 * [NiaNavigationDefaults.navigationContentColor] method, method, whose `selectedTextColor` is the
 * [Color] returned by the [NiaNavigationDefaults.navigationSelectedItemColor] method, whose
 * `unselectedTextColor` is the [Color] returned by the [NiaNavigationDefaults.navigationContentColor]
 * method, and whose `indicatorColor` is the [Color] returned by the
 * [NiaNavigationDefaults.navigationIndicatorColor] method. The `navigationDrawerItemColors`
 * argument is set to a [NavigationDrawerItemDefaults.colors] whose `selectedIconColor` is the
 * [Color] returned by the [NiaNavigationDefaults.navigationSelectedItemColor] method, whose
 * `unselectedIconColor` is the [Color] returned by the [NiaNavigationDefaults.navigationContentColor]
 * method, method, whose `selectedTextColor` is the [Color] returned by the
 * [NiaNavigationDefaults.navigationSelectedItemColor] method, whose `unselectedTextColor` is the
 * [Color] returned by the [NiaNavigationDefaults.navigationContentColor] method, and whose
 * `indicatorColor` is the [Color] returned by the [NiaNavigationDefaults.navigationIndicatorColor]
 * method.
 *
 * Our root composable is a [NavigationSuiteScaffold] whose arguments are:
 *  - `navigationSuiteItems`: is a [NiaNavigationSuiteScope] whose `navigationSuiteScope` argument
 *  is the [NavigationSuiteScope] of the lambda argument, and whose `navigationSuiteItemColors`
 *  argument is the [NavigationSuiteItemColors] variable `navigationSuiteItemColors`. We then call
 *  the [NiaNavigationSuiteScope.run] method of the [NiaNavigationSuiteScope] and in its `block`
 *  lambda argument we call the our [NiaNavigationSuiteScope] lambda parameter [navigationSuiteItems]
 *  to populate the navigation items to be displayed.
 *  - `layoutType`: is the [NavigationSuiteType] variable `layoutType`.
 *  - `containerColor`: is [Color.Transparent].
 *  - `navigationSuiteColors`: is a [NavigationSuiteDefaults.colors] whose `navigationBarContentColor`
 *  is the [Color] returned by the [NiaNavigationDefaults.navigationContentColor] method and whose
 *  `navigationRailContainerColor` is [Color.Transparent].
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `content`: is our Composable lambda parameter [content].
 *
 * @param navigationSuiteItems A slot to display multiple items via [NiaNavigationSuiteScope].
 * @param modifier Modifier to be applied to the navigation suite scaffold.
 * @param windowAdaptiveInfo The window adaptive info.
 * @param content The app content inside the scaffold.
 */
@Composable
fun NiaNavigationSuiteScaffold(
    navigationSuiteItems: NiaNavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    content: @Composable () -> Unit,
) {
    val layoutType: NavigationSuiteType = NavigationSuiteScaffoldDefaults
        .calculateFromAdaptiveInfo(adaptiveInfo = windowAdaptiveInfo)
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = NiaNavigationDefaults.navigationContentColor(),
            selectedTextColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = NiaNavigationDefaults.navigationContentColor(),
            indicatorColor = NiaNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = NiaNavigationDefaults.navigationContentColor(),
            selectedTextColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = NiaNavigationDefaults.navigationContentColor(),
            indicatorColor = NiaNavigationDefaults.navigationIndicatorColor(),
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = NiaNavigationDefaults.navigationContentColor(),
            selectedTextColor = NiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = NiaNavigationDefaults.navigationContentColor(),
        ),
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            NiaNavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors,
            ).run(block = navigationSuiteItems)
        },
        layoutType = layoutType,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContentColor = NiaNavigationDefaults.navigationContentColor(),
            navigationRailContainerColor = Color.Transparent,
        ),
        modifier = modifier,
    ) {
        content()
    }
}

/**
 * A wrapper around [NavigationSuiteScope] to declare navigation items.
 *
 * We just substitute the parameters of a call to [NiaNavigationSuiteScope.item] appropriately
 * as the arguments of a call to [NavigationSuiteScope.item].
 *
 * @param navigationSuiteScope The navigation suite scope.
 * @param navigationSuiteItemColors The navigation suite item colors.
 */
class NiaNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        selectedIcon: @Composable () -> Unit = icon,
        label: @Composable (() -> Unit)? = null,
    ): Unit = navigationSuiteScope.item(
        selected = selected,
        onClick = onClick,
        icon = {
            if (selected) {
                selectedIcon()
            } else {
                icon()
            }
        },
        label = label,
        colors = navigationSuiteItemColors,
        modifier = modifier,
    )
}

/**
 * Two previews ("Light theme" and "Dark theme") of the [NiaNavigationBar] Composable. We initialize
 * our [List] of [String] variable `val items` to ("For you", "Saved", "Interests"). We initialize
 * our [List] of [ImageVector] variable `val icons` to a list of the [NiaIcons.UpcomingBorder],
 * [NiaIcons.BookmarksBorder], and [NiaIcons.Grid3x3], icons. We initialize our [List] of [ImageVector]
 * variable `val selectedIcons` to a [NiaIcons.Upcoming], [NiaIcons.Bookmarks], and [NiaIcons.Grid3x3]
 * icons.
 *
 * Our root Composable is a [NiaTheme] whose `content` Composable lambda argument is a
 * [NiaNavigationBar]. For the `content` of the [NiaNavigationBar] we loop through the
 * [String]'s in `items` capturing the [Int] passed the lambda in variable `index` and the [String]
 * it variable `item` then compose a [NiaNavigationBarItem] whose arguments are:
 *  - `icon`: is an [Icon] whose `imageVector` is the [NiaIcons] at index `index` in `icons`
 *  and whose `contentDescription` is the [String] `item`.
 *  - `selectedIcon`: is an [Icon] whose `imageVector` is the [NiaIcons] at index `index` in
 *  `selectedIcons` and whose `contentDescription` is the [String] `item`.
 *  - `label`: is a [Text] whose `text` is the [String] `item`.
 *  - `selected`: is `true` if `index` is 0.
 *  - `onClick`: is an empty lambda.
 */
@ThemePreviews
@Composable
fun NiaNavigationBarPreview() {
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

    NiaTheme {
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
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

/**
 * Two previews ("Light theme" and "Dark theme") of the [NiaNavigationRail] Composable. We initialize
 * our [List] of [String] variable `val items` to ("For you", "Saved", "Interests"). We initialize
 * our [List] of [ImageVector] variable `val icons` to a list of the [NiaIcons.UpcomingBorder],
 * [NiaIcons.BookmarksBorder], and [NiaIcons.Grid3x3] icons. We initialize our [List] of [ImageVector]
 * variable `val selectedIcons` to a list of the [NiaIcons.Upcoming], [NiaIcons.Bookmarks], and
 * [NiaIcons.Grid3x3] icons.
 *
 * Our root Composable is a [NiaTheme] whose `content` Composable lambda argument is a
 * [NiaNavigationRail]. For the `content` of the [NiaNavigationRail] we loop through the
 * [String]'s in `items` capturing the [Int] passed the lambda in variable `index` and the [String]
 * it variable `item` then compose a [NiaNavigationRailItem] whose arguments are:
 *  - `icon`: is an [Icon] whose `imageVector` is the [NiaIcons] at index `index` in `icons`
 *  and whose `contentDescription` is the [String] `item`.
 *  - `selectedIcon`: is an [Icon] whose `imageVector` is the [NiaIcons] at index `index` in
 *  `selectedIcons` and whose `contentDescription` is the [String] `item`.
 *  - `label`: is a [Text] whose `text` is the [String] `item`.
 *  - `selected`: is `true` if `index` is 0.
 *  - `onClick`: is an empty lambda.
 */
@ThemePreviews
@Composable
fun NiaNavigationRailPreview() {
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

    NiaTheme {
        NiaNavigationRail {
            items.forEachIndexed { index: Int, item: String ->
                NiaNavigationRailItem(
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
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

/**
 * Now in Android navigation default values.
 */
object NiaNavigationDefaults {
    /**
     * Returns the [Color] to be used for the content of navigation items, which is the
     * [ColorScheme.onSurfaceVariant] of our custom [MaterialTheme.colorScheme].
     */
    @Composable
    fun navigationContentColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant

    /**
     * Returns the [Color] to be used for the selected navigation item, which is the
     * [ColorScheme.onPrimaryContainer] of our custom [MaterialTheme.colorScheme].
     */
    @Composable
    fun navigationSelectedItemColor(): Color = MaterialTheme.colorScheme.onPrimaryContainer

    /**
     * Returns the [Color] to be used for the indicator of the selected navigation item, which is
     * the [ColorScheme.primaryContainer] of our [MaterialTheme.colorScheme].
     */
    @Composable
    fun navigationIndicatorColor(): Color = MaterialTheme.colorScheme.primaryContainer
}
