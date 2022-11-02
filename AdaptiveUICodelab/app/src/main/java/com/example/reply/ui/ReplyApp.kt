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
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.data.Email
import com.example.reply.ui.utils.DevicePosture
import com.example.reply.ui.utils.ReplyContentType
import com.example.reply.ui.utils.ReplyNavigationType
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
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
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
        PermanentNavigationDrawer(drawerContent = { NavigationDrawerContent(selectedDestination) }) {
            ReplyAppContent(navigationType, contentType, replyHomeUIState)
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                NavigationDrawerContent(
                    selectedDestination,
                    onDrawerClicked = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            },
            drawerState = drawerState
        ) {
            ReplyAppContent(
                navigationType, contentType, replyHomeUIState,
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
 *
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
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
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
 *
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
            icon = { Icon(imageVector = Icons.Default.Article, stringResource(id = R.string.tab_article)) }
        )
        NavigationRailItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.Chat, stringResource(id = R.string.tab_dm)) }
        )
        NavigationRailItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.People, stringResource(id = R.string.tab_groups)) }
        )
    }
}

/**
 *
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
            icon = { Icon(imageVector = Icons.Default.Article, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.Chat, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(imageVector = Icons.Outlined.Videocam, contentDescription = stringResource(id = R.string.tab_inbox)) }
        )
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerContent(
    selectedDestination: String,
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {}
) {
    Column(
        modifier
            .wrapContentWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(24.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    imageVector = Icons.Default.MenuOpen,
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
            icon = { Icon(imageVector = Icons.Default.Article, contentDescription = stringResource(id = R.string.tab_article)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            selected = selectedDestination == ReplyDestinations.DM,
            label = { Text(text = stringResource(id = R.string.tab_dm), modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.Default.Chat, contentDescription = stringResource(id = R.string.tab_dm)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
        NavigationDrawerItem(
            selected = selectedDestination == ReplyDestinations.GROUPS,
            label = { Text(text = stringResource(id = R.string.tab_groups), modifier = Modifier.padding(horizontal = 16.dp)) },
            icon = { Icon(imageVector = Icons.Default.Article, contentDescription = stringResource(id = R.string.tab_groups)) },
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            onClick = { /*TODO*/ }
        )
    }
}