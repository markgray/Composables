/*
 * Copyright 2022 Google LLC
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

package com.example.reply.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.window.layout.FoldingFeature
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.ui.theme.replyDarkInverseOnSurface
import com.example.reply.ui.theme.replyDarkPrimary
import com.example.reply.ui.theme.replyLightInverseOnSurface
import com.example.reply.ui.theme.replyLightPrimary
import com.example.reply.ui.utils.DevicePosture
import com.example.reply.ui.utils.ReplyContentType
import com.example.reply.ui.utils.ReplyNavigationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This is the root Composable of our app. Its responsibilities are to determine the [ReplyNavigationType]
 * and [ReplyContentType] based on its [WindowWidthSizeClass] parameter [windowSize] and [DevicePosture]
 * parameter [foldingDevicePosture]. It then calls the Composable [ReplyNavigationWrapperUI] with the
 * values it decides on as well as its [ReplyHomeUIState] parameter [replyHomeUIState] to supply the
 * [List] of [Email] object that its children will display.
 *
 * We start by declaring our [ReplyNavigationType] variable `val navigationType` and our [ReplyContentType]
 * variable `val contentType`. Then we branch on our [WindowWidthSizeClass] parameter [windowSize]:
 *  - [WindowWidthSizeClass.Compact] - (represents the majority of phones in portrait) we set our
 *  variable `navigationType`to [ReplyNavigationType.BOTTOM_NAVIGATION] (uses [ReplyBottomNavigationBar]
 *  which uses uses the Material Design bottom navigation bar [NavigationBar] to navigate between
 *  screens), and set our `contentType` variable to [ReplyContentType.LIST_ONLY] (uses [ReplyListOnlyContent]
 *  to display [Email] objects with only 2 lines of the [Email.body] of the [Email]).
 *  - [WindowWidthSizeClass.Medium] - (Represents the majority of tablets in portrait and large unfolded
 *  inner displays in portrait) we set our variable `navigationType`to [ReplyNavigationType.NAVIGATION_RAIL]
 *  (uses [ReplyNavigationRail] which uses the Material Design bottom navigation rail [NavigationRail]
 *  to navigate between screens), then if our [DevicePosture] parameter [foldingDevicePosture] is
 *  [DevicePosture.BookPosture] or [DevicePosture.Separating] we set our `contentType` variable to
 *  [ReplyContentType.LIST_AND_DETAIL] (uses [ReplyListAndDetailContent] to display [Email] objects
 *  with only 2 lines of the [Email.body] of the [Email], along with a separate [LazyColumn] which
 *  displays all the [Email.threads] associated with the selected [Email]), or else we set it to
 *  [ReplyContentType.LIST_ONLY] if it is neither (uses [ReplyListOnlyContent] to display [Email]
 *  objects with only 2 lines of the [Email.body] of the [Email]).
 *  - [WindowWidthSizeClass.Expanded] (Represents the majority of tablets in landscape and large
 *  unfolded inner displays in landscape) if our [DevicePosture] parameter [foldingDevicePosture] is
 *  [DevicePosture.BookPosture] we set our variable `navigationType`to [ReplyNavigationType.NAVIGATION_RAIL]
 *  (uses [ReplyNavigationRail] which uses the Material Design bottom navigation rail [NavigationRail]
 *  to navigate between screens), and if not we set it to [ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER]
 *  (uses [PermanentNavigationDrawer], the Material Design navigation permanent drawer, which is always
 *  visible and usually used for frequently switching destinations). In either case we set our
 *  `contentType` variable to [ReplyContentType.LIST_AND_DETAIL] (uses [ReplyListAndDetailContent]
 *  to display [Email] objects with only 2 lines of the [Email.body] of the [Email], along with a
 *  separate [LazyColumn] which displays all the [Email.threads] associated with the selected [Email])
 *  - Unknown [WindowWidthSizeClass] - we set our variable `navigationType`to
 *  [ReplyNavigationType.BOTTOM_NAVIGATION] (uses [ReplyBottomNavigationBar] which uses uses the
 *  Material Design bottom navigation bar [NavigationBar] to navigate between screens), and set our
 *  `contentType` variable to [ReplyContentType.LIST_ONLY] (uses [ReplyListOnlyContent] to display
 *  [Email] objects with only 2 lines of the [Email.body] of the [Email]).
 *
 * Having determined the [ReplyNavigationType] and [ReplyContentType] that should be used for the
 * device we call the [ReplyNavigationWrapperUI] Composable with the arguments:
 *  - `navigationType` our [ReplyNavigationType] variable `navigationType`,
 *  - `contentType` our [ReplyContentType] variable `contentType`,
 *  - `replyHomeUIState` our [ReplyHomeUIState] parameter [replyHomeUIState]
 */
@Composable
fun ReplyApp(
    replyHomeUIState: ReplyHomeUIState,
    windowSize: WindowWidthSizeClass,
    foldingDevicePosture: DevicePosture,
) {
    // You will add navigation info here
    /**
     * This will help us select type of navigation and content type depending on window size and
     * fold state of the device.
     */
    val navigationType: ReplyNavigationType
    val contentType: ReplyContentType

    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            navigationType = ReplyNavigationType.BOTTOM_NAVIGATION
            contentType = ReplyContentType.LIST_ONLY
        }

        WindowWidthSizeClass.Medium -> {
            navigationType = ReplyNavigationType.NAVIGATION_RAIL
            contentType = if (foldingDevicePosture is DevicePosture.BookPosture
                || foldingDevicePosture is DevicePosture.Separating) {
                ReplyContentType.LIST_AND_DETAIL
            } else {
                ReplyContentType.LIST_ONLY
            }
        }

        WindowWidthSizeClass.Expanded -> {
            navigationType = if (foldingDevicePosture is DevicePosture.BookPosture) {
                ReplyNavigationType.NAVIGATION_RAIL
            } else {
                ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER
            }
            contentType = ReplyContentType.LIST_AND_DETAIL
        }

        else -> {
            navigationType = ReplyNavigationType.BOTTOM_NAVIGATION
            contentType = ReplyContentType.LIST_ONLY
        }
    }

    ReplyNavigationWrapperUI(
        navigationType = navigationType,
        contentType = contentType,
        replyHomeUIState = replyHomeUIState
    )
}

/**
 * This Composable is responsible for calling the Composables needed to implement the type of
 * [ReplyNavigationType] navigation that is sepecified by our [navigationType] parameter. We
 * initialize and remember our [DrawerState] variable `val drawerState` with the initial value
 * [DrawerValue.Closed]. We use the [rememberCoroutineScope] method to initialize our [CoroutineScope]
 * variable `val scope` to a [CoroutineScope] bound to this point in the composition, the same
 * [CoroutineScope] instance will be returned across recompositions, and the scope will be cancelled
 * when this call leaves the composition. We initialize our [String] variable ``val selectedDestination`
 * to [ReplyDestinations.INBOX] which is the [String] "Inbox" (although we have not got around to
 * actually implementing any form of navigation, [ReplyDestinations] is a good candidate for the
 * "routes" in a navigation graph).
 *
 * Having initialized the variables we need we branch on the value of our [ReplyNavigationType]
 * parameter [navigationType]:
 *
 *  - [ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER]
 *
 * We call the Composable [PermanentNavigationDrawer] with its `drawerContent` argument a
 * [PermanentDrawerSheet] wrapped [NavigationDrawerContent] Composable whose `selectedDestination`
 * is our [String] variable `selectedDestination`. The `content` of the [PermanentNavigationDrawer]
 * is a [ReplyAppContent] Composable whose `navigationType` argument is our [navigationType]
 * parameter, whose `contentType` argument is our [contentType] parameter, and whose
 * `replyHomeUIState` argument is our [replyHomeUIState] parameter.
 *
 *  - [ReplyNavigationType.BOTTOM_NAVIGATION] or [ReplyNavigationType.NAVIGATION_RAIL]
 *
 * We call the Composable [ModalNavigationDrawer] with its `drawerState` argument our [DrawerState]
 * variable `drawerState`, and with its `drawerContent` argument a [ModalDrawerSheet] wrapped
 * [NavigationDrawerContent] Composable whose `selectedDestination` is our [String] variable
 * `selectedDestination`, and whose `onDrawerClicked` argument is a lambda which calls the
 * [CoroutineScope.launch] method of our `scope` variable to launch a new coroutine which calls the
 * [DrawerState.close] method of our `drawerState` variable. The `content` of the [ModalNavigationDrawer]
 * is a [ReplyAppContent] Composable whose `navigationType` argument is our [navigationType] parameter,
 * whose `contentType` argument is our [contentType] parameter, whose `replyHomeUIState` argument is
 * our [replyHomeUIState] parameter, and whose `onDrawerClicked` argument is a lambda which calls
 * the [CoroutineScope.launch] method of our `scope` variable to launch a new coroutine which calls
 * the [DrawerState.open] method of our `drawerState` variable.
 *
 * @param navigationType the [ReplyNavigationType] that will be used for navigation (someday), one of:
 *  - [ReplyNavigationType.BOTTOM_NAVIGATION] chosen if the [WindowWidthSizeClass] of the device is
 *  [WindowWidthSizeClass.Compact] (Represents the majority of phones in portrait) or is not a known
 *  [WindowWidthSizeClass]
 *  - [ReplyNavigationType.NAVIGATION_RAIL] chosen if [WindowWidthSizeClass] of the device is
 *  [WindowWidthSizeClass.Medium] (Represents the majority of tablets in portrait and large unfolded
 *  inner displays in portrait) or [WindowWidthSizeClass.Expanded] (Represents the majority of tablets
 *  in landscape and large unfolded inner displays in landscape) and the [DevicePosture] of the device
 *  is [DevicePosture.BookPosture] (The foldable device's hinge is in an intermediate position between
 *  opened and closed state, there is a non-flat angle between parts of the flexible screen or between
 *  physical screen panels, and the height of its [FoldingFeature] is greater than or equal to the width).
 *  - [ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER] chosen if [WindowWidthSizeClass] of the device is
 *  [WindowWidthSizeClass.Expanded] (Represents the majority of tablets in landscape and large unfolded
 *  inner displays in landscape) but the [DevicePosture] of the device is NOT [DevicePosture.BookPosture].
 *@param contentType the [ReplyContentType] that will be used to determine the amount of information to
 * be displayed:
 *  - [ReplyContentType.LIST_ONLY] displays [Email] objects with only 2 lines of the [Email.body] of
 *  the [Email], chosen if the [WindowWidthSizeClass] of the device is [WindowWidthSizeClass.Compact]
 *  (Represents the majority of phones in portrait) or [WindowWidthSizeClass.Medium] (Represents the
 *  majority of tablets in portrait and large unfolded inner displays in portrait) and the [DevicePosture]
 *  is neither [DevicePosture.BookPosture] nor [DevicePosture.Separating], or is not a known
 *  [WindowWidthSizeClass].
 *  - [ReplyContentType.LIST_AND_DETAIL] displays [Email] objects with only 2 lines of the [Email.body]
 *  of the [Email], along with a separate [LazyColumn] which displays all the [Email.threads] associated
 *  with the selected [Email], chosen if the [WindowWidthSizeClass] of the device is [WindowWidthSizeClass.Medium]
 *  and the [DevicePosture] of the device is either [DevicePosture.BookPosture] or [DevicePosture.Separating],
 *  or the [WindowWidthSizeClass] of the device is [WindowWidthSizeClass.Expanded].
 * @param replyHomeUIState the [ReplyHomeUIState] which our children Composables will use to access the
 * fake [Email] objects they display.
 */
@Composable
private fun ReplyNavigationWrapperUI(
    navigationType: ReplyNavigationType,
    contentType: ReplyContentType,
    replyHomeUIState: ReplyHomeUIState
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedDestination = ReplyDestinations.INBOX

    if (navigationType == ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet {
                    NavigationDrawerContent(selectedDestination)
                }
            }
        ) {
            ReplyAppContent(
                navigationType = navigationType,
                contentType = contentType,
                replyHomeUIState = replyHomeUIState
            )
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    NavigationDrawerContent(
                        selectedDestination,
                        onDrawerClicked = {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            },
        ) {
            ReplyAppContent(
                navigationType = navigationType,
                contentType = contentType,
                replyHomeUIState = replyHomeUIState,
                onDrawerClicked = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            )
        }
    }
}

/**
 * This Composable is responsible for loading the Composables that are needed for the [ReplyNavigationType]
 * and [ReplyContentType] that our caller has determined to be appropriate for the size and features of
 * the device we are running on. Our root Composable is a [Row] whose `modifier` argument is a
 * [Modifier.fillMaxSize] to have it fill the [Constraints.maxWidth] and [Constraints.maxHeight] of
 * the incoming measurement constraints. The `content` of the [Row] is:
 *  - an [AnimatedVisibility] wrapped [ReplyNavigationRail] whose `onDrawerClicked` argument is our
 *  [onDrawerClicked] parameter. The `visible` argument of the [AnimatedVisibility] is `true` if our
 *  [ReplyNavigationType] parameter [navigationType] is equal to [ReplyNavigationType.NAVIGATION_RAIL]
 *  and the [ReplyNavigationRail] `content` is only visible when this is so, which is when the
 *  [WindowWidthSizeClass] of the device is [WindowWidthSizeClass.Medium] (Represents the majority
 *  of tablets in portrait and large unfolded inner displays in portrait) or [WindowWidthSizeClass.Expanded]
 *  (Represents the majority of tablets in landscape and large unfolded inner displays in landscape)
 *  and the [DevicePosture] of the device is [DevicePosture.BookPosture] (The foldable device's hinge
 *  is in an intermediate position between opened and closed state, there is a non-flat angle between
 *  parts of the flexible screen or between physical screen panels, and the height of its [FoldingFeature]
 *  is greater than or equal to the width).
 *  - The next Composable in the [Row] is a [Column] whose `modifier` argument is a [Modifier.fillMaxSize]
 *  which has a [Modifier.background] that sets the background `color` to the [ColorScheme.inverseOnSurface]
 *  [Color] of [MaterialTheme.colorScheme] which is [replyDarkInverseOnSurface] (a shade of black) for
 *  our [darkColorScheme] and [replyLightInverseOnSurface] (a shade of white) for our [lightColorScheme].
 *
 * The `content` of the [Column] depends on the value of our [ReplyContentType] parameter [contentType]:
 *  - [ReplyContentType.LIST_AND_DETAIL] we call our [ReplyListAndDetailContent] Composable with its
 *  `replyHomeUIState` argument our [ReplyHomeUIState] parameter [replyHomeUIState] and its `modifier`
 *  argument a `ColumnScope` `Modifier.weight` of 1f which will cause it to occupy all the space left
 *  in the [Column] once its sibling is measured and placed.
 *  - For any other value of [ReplyContentType] we call our [ReplyListOnlyContent] Composable with its
 *  `replyHomeUIState` argument our [ReplyHomeUIState] parameter [replyHomeUIState] and its `modifier`
 *  argument a `ColumnScope` `Modifier.weight` of 1f which will cause it to occupy all the space left
 *  in the [Column] once its sibling is measured and placed.
 *  - At the bottom of the [Column] is an [AnimatedVisibility] wrapped [ReplyBottomNavigationBar].
 *  The `visible` argument of the [AnimatedVisibility] is `true` if our [ReplyNavigationType] parameter
 *  [navigationType] is equal to [ReplyNavigationType.BOTTOM_NAVIGATION] and the [ReplyBottomNavigationBar]
 *  `content` is only visible when this is so, which is when the [WindowWidthSizeClass] of the device
 *  is [WindowWidthSizeClass.Compact] (Represents the majority of phones in portrait) or is not a known
 *  [WindowWidthSizeClass].
 *
 * @param navigationType the [ReplyNavigationType] that should be used for navigation on this device.
 * One of [ReplyNavigationType.BOTTOM_NAVIGATION], [ReplyNavigationType.NAVIGATION_RAIL] or
 * [ReplyNavigationType.PERMANENT_NAVIGATION_DRAWER].
 * @param contentType the [ReplyContentType] that should be displayed on this device. One of
 * [ReplyContentType.LIST_ONLY] or [ReplyContentType.LIST_AND_DETAIL].
 * @param replyHomeUIState the [ReplyHomeUIState] that can be used to retrieve the [Email] objects
 * that are to be displayed.
 * @param onDrawerClicked a lambda that can be called by [ReplyNavigationRail] to open the
 * "Navigation Drawer".
 */
@Composable
fun ReplyAppContent(
    navigationType: ReplyNavigationType,
    contentType: ReplyContentType,
    replyHomeUIState: ReplyHomeUIState,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == ReplyNavigationType.NAVIGATION_RAIL) {
            ReplyNavigationRail(
                onDrawerClicked = onDrawerClicked
            )
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            if (contentType == ReplyContentType.LIST_AND_DETAIL) {
                ReplyListAndDetailContent(
                    replyHomeUIState = replyHomeUIState,
                    modifier = Modifier.weight(1f),
                )
            } else {
                ReplyListOnlyContent(replyHomeUIState = replyHomeUIState, modifier = Modifier.weight(1f))
            }

            AnimatedVisibility(visible = navigationType == ReplyNavigationType.BOTTOM_NAVIGATION) {
                ReplyBottomNavigationBar()
            }
        }
    }
}

/**
 * This Composable is both a skeletal implementation of the [NavigationRail] that the [ReplyAppContent]
 * Composable uses to navigate with when the [ReplyNavigationType] is [ReplyNavigationType.NAVIGATION_RAIL]
 * and a preview of the same. The root Composable is a [NavigationRail] (Material Design navigation rail)
 * whose `modifier` argument is a [Modifier.fillMaxHeight] that causes it to occupy the entire incoming
 * measurement height constraints. It has five [NavigationRailItem] children:
 *  - "Navigation Drawer" its `onClick` argument is our [onDrawerClicked] lambda parameter which traces
 *  back to a suspend lambda that [ReplyNavigationWrapperUI] passes to our [ReplyAppContent] caller
 *  which opens the [ModalNavigationDrawer]. Its `selected` argument is `false` and its `icon` argument
 *  displays the `Menu` `imageVector` of [Icons.Default].
 *  - "Inbox" its `selected` argument is `true` and its `icon` argument displays the `Inbox` `imageVector`
 *  of [Icons.Default]. Its `onClick` argument is a lambda which does nothing, but in the future would be
 *  used to navigate to an "Inbox" screen (which is either the screen [ReplyListAndDetailContent]
 *  of the screen [ReplyListOnlyContent] depending on the size of the device apparently).
 *  - "Articles" its `selected` argument is `false` and its `icon` argument displays the `Article`
 *  `imageVector` of [Icons.Default]. Its `onClick` argument is a lambda which does nothing, but in
 *  the future would be used to navigate to an "Article" screen which is yet to be written.
 *  - "Direct Messages" its `selected` argument is `false` and its `icon` argument displays the `Chat`
 *  `imageVector` of [Icons.Default]. Its `onClick` argument is a lambda which does nothing, but in
 *  the future would be used to navigate to an "Direct Messages" screen which is yet to be written.
 *  - "Groups" its `selected` argument is `false` and its `icon` argument displays the `People`
 *  `imageVector` of [Icons.Default]. Its `onClick` argument is a lambda which does nothing, but in
 *  the future would be used to navigate to an "Groups" screen which is yet to be written.
 *
 * @param onDrawerClicked a lambda which will be passed to the "Navigation Drawer" [NavigationRailItem]
 * as its `onClick` argument. In our case it is a suspend lambda that [ReplyNavigationWrapperUI] passes
 * to our [ReplyAppContent] caller which opens the [ModalNavigationDrawer].
 */
@Composable
@Preview
fun ReplyNavigationRail(
    onDrawerClicked: () -> Unit = {},
) {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        NavigationRailItem(
            selected = false,
            onClick = onDrawerClicked,
            icon = { Icon(imageVector = Icons.Default.Menu, contentDescription = stringResource(id = R.string.navigation_drawer)) }
        )
        NavigationRailItem(
            selected = true,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Default.Inbox, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationRailItem(
            selected = false,
            onClick = {/*TODO*/ },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Article, stringResource(id = R.string.tab_article)) }
        )
        NavigationRailItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.AutoMirrored.Outlined.Chat, stringResource(id = R.string.tab_dm)) }
        )
        NavigationRailItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.People, stringResource(id = R.string.tab_groups)) }
        )
    }
}

/**
 * This Composable is both a skeletal implementation of the [NavigationBar] that the [ReplyAppContent]
 * Composable uses to navigate with when the [ReplyNavigationType] is [ReplyNavigationType.BOTTOM_NAVIGATION]
 * and a preview of the same. The root Composable is a [NavigationBar] (Material Design bottom navigation
 * bar) whose `modifier` argument is a [Modifier.fillMaxWidth] that causes it to occupy the entire
 * incoming measurement width constraints. It has four do nothing [NavigationBarItem] children the
 * `selected` argument of the first item is `true` and the `selected` argument of the rest is `false`.
 * The only other difference between the [NavigationBarItem]'s is the `icon` argument:
 *  1. the `Inbox` `imageVector` of [Icons.Default].
 *  2. the `Article` `imageVector` of [Icons.Default].
 *  3. the `Chat` `imageVector` of [Icons.Outlined].
 *  4. the `Videocam` `imageVector` of [Icons.Outlined].
 */
@Composable
@Preview
fun ReplyBottomNavigationBar() {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        NavigationBarItem(
            selected = true,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Default.Inbox, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Article, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.AutoMirrored.Outlined.Chat, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.Videocam, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
    }
}

/**
 * This Composable is used as the `drawerContent` parameter of both the [PermanentNavigationDrawer]
 * Material Design navigation permanent drawer and the [ModalNavigationDrawer] Material Design modal
 * navigation drawer which are used by [ReplyNavigationWrapperUI] depending on the value of
 * [ReplyNavigationType] required for the size of the device we are running. Both of these drawers
 * execute [NavigationDrawerContent] in a `ColumnScope`, and call it with `selectedDestination` set
 * to [ReplyDestinations.INBOX], but the [ModalNavigationDrawer] also passes a lambda that launches
 * a coroutine that calls the [DrawerState.close] method that will close the drawer as the value or
 * our `onDrawerClicked` argument. Our root Composable is a [Column] whose `modifier` argument is a
 * [Modifier.wrapContentWidth] which allows the `content` to measure at its desired width, to which
 * is chained a [Modifier.fillMaxHeight] to have the content fill the [Constraints.maxHeight] of the
 * incoming measurement constraints, and this is followed by a [Modifier.background] that sets the
 * background [Color] to the `inverseOnSurface` [Color] of [MaterialTheme.colorScheme] which is
 * [replyDarkInverseOnSurface] for our [darkColorScheme] (Color(0xFF1F1B16), a shade of Black) or
 * [replyLightInverseOnSurface] for our [lightColorScheme] (Color(0xFFF9EFE6), a shade of White).
 * To all of this is added a [Modifier.padding] that adds 24.dp padding to `all` sides.
 *
 * The `content` of the [Column] starts with a [Row] whose `modifier` argument is a [Modifier.fillMaxWidth]
 * that causes the [Row] to occupy the entire incoming width constraint, to which a [Modifier.padding]
 * is added which sets the padding on all sides of the [Row] to 16.dp. The `horizontalArrangement`
 * argument of the [Row] is [Arrangement.SpaceBetween] that cause the [Row] to place children such
 * that they are spaced evenly across the main axis, without free space before the first child or
 * after the last child, and the `verticalAlignment` argument of the [Row] is [Alignment.CenterVertically]
 * causing the children to be centered vertically. The `content` of the [Row] is a [Text] displaying
 * the `text` "REPY" using as its `style` the `titleMedium` [TextStyle] of [MaterialTheme.typography]
 * (`fontWeight` is [FontWeight.SemiBold], `fontSize` is 16.sp, `lineHeight` is 24.sp, and `letterSpacing`
 * is 0.15.sp), and the `color` of the text is the `primary` [Color] of [MaterialTheme.colorScheme]
 * (which is [replyDarkPrimary] for our [darkColorScheme] (Color(0xFFFFB945), a shade of Orange) and
 * [replyLightPrimary] for our [lightColorScheme] (Color(0xFF825500), a shade of Brown). Next to the
 * [Text] in the [Row] is an [IconButton] whose `onClick` argument calls our [onDrawerClicked] lambda
 * parameter, and whose `content` is an [Icon] displaying the `MenuOpen` `imageVector` of [Icons.Default]
 * (which is three horizontal lines followed by a `<` symbol).
 *
 * Below the [Row] in the [Column] are four [NavigationDrawerItem] Composables (A [NavigationDrawerItem]
 * represents a destination within drawers for [ModalNavigationDrawer], or [PermanentNavigationDrawer]).
 * Their `selected` argument is `true` when our [String] parameter [selectedDestination] is equal to
 * the [ReplyDestinations] that they represent:
 *  - [ReplyDestinations.INBOX] uses a [Text] displaying the text "Inbox" as its `label` argument,
 *  and an [Icon] drawing the `Inbox` [ImageVector] of [Icons.Default] as its `icon` argument.
 *  - [ReplyDestinations.ARTICLES] uses a [Text] displaying the text "Articles" as its `label` argument,
 *  and an [Icon] drawing the `Article` [ImageVector] of [Icons.Default] as its `icon` argument.
 *  - [ReplyDestinations.DM] uses a [Text] displaying the text "Direct Messages" as its `label` argument,
 *  and an [Icon] drawing the `Chat` [ImageVector] of [Icons.Default] as its `icon` argument.
 *  - [ReplyDestinations.GROUPS] uses a [Text] displaying the text "Groups" as its `label` argument,
 *  and an [Icon] drawing the `Article` [ImageVector] of [Icons.Default] as its `icon` argument.
 *
 * All four use a [NavigationDrawerItemDefaults.colors] with its `unselectedContainerColor` [Color]
 * specified to be [Color.Transparent] as its `colors` argument, and all four use a "do nothing"
 * lambda as their `onClick` arguments.
 *
 * @param selectedDestination which of the four [NavigationDrawerItem]'s should be considered to be
 * selected. The `selected` argument of a [NavigationDrawerItem] is `true` if [selectedDestination]
 * is equal to the [ReplyDestinations] const [String] that is assigned to that [NavigationDrawerItem].
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our callers do not specify one so the empty, default, or starter [Modifier] that contains
 * no elements is used.
 * @param onDrawerClicked a lambda that our "Navigation Drawer" [IconButton] should use as its `onClick`
 * argument. None is passed when we are used in the [PermanentNavigationDrawer] of [ReplyNavigationWrapperUI],
 * and a lambda which launches a coroutine which closes the [ModalNavigationDrawer] is passed when we
 * are used in the [ModalNavigationDrawer] of [ReplyNavigationWrapperUI].
 */
@Composable
fun NavigationDrawerContent(
    selectedDestination: String,
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .background(color = MaterialTheme.colorScheme.inverseOnSurface)
            .padding(all = 24.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onDrawerClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                    contentDescription = stringResource(id = R.string.navigation_drawer)
                )
            }
        }

        NavigationDrawerItem(
            selected = selectedDestination == ReplyDestinations.INBOX,
            label = { Text(text = stringResource(id = R.string.tab_inbox), modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.Default.Inbox, contentDescription = stringResource(id = R.string.tab_inbox)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            selected = selectedDestination == ReplyDestinations.ARTICLES,
            label = { Text(text = stringResource(id = R.string.tab_article), modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Article, contentDescription = stringResource(id = R.string.tab_article)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            selected = selectedDestination == ReplyDestinations.DM,
            label = { Text(text = stringResource(id = R.string.tab_dm), modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = stringResource(id = R.string.tab_dm)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            selected = selectedDestination == ReplyDestinations.GROUPS,
            label = { Text(text = stringResource(id = R.string.tab_groups), modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.Article, contentDescription = stringResource(id = R.string.tab_groups)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
    }
}