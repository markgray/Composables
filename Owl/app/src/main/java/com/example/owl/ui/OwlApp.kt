/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.owl.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.owl.ui.courses.CourseTabs
import com.example.owl.ui.theme.BlueTheme
import java.util.Locale

/**
 * This is the main Composable of our app. It uses our [BlueTheme] custom [MaterialTheme] to wrap
 * its children. It initializes and remembers its [Array] of [CourseTabs] variable `val tabs` to all
 * of the entries in the [CourseTabs] enum, and initializes and remembers its [NavHostController]
 * variable `val navController` to a new instance. Its root Composable is a [Scaffold] whose
 * `backgroundColor` argument is the [Colors.primarySurface] color of our custom
 * [MaterialTheme.colors], and whose `bottomBar` argument is an [OwlBottomBar] whose `navController`
 * argument is our `navController` variable and whose `tabs` argument is our `tabs` variable. In its
 * `content` composable lambda argument we accept the [PaddingValues] passed the lambda in variable
 * `innerPaddingModifier` then compose a [NavGraph] whose arguments are:
 *  - `finishActivity`: is our lambda parameter [finishActivity].
 *  - `navController`: is our [NavHostController] variable `navController`.
 *  - `modifier`: is a [Modifier.padding] that adds [PaddingValues] variable `innerPaddingModifier`
 *  as the padding to the [NavGraph].
 *
 * @param finishActivity a lambda that the [NavGraph] can call to finish the main activity.
 */
@Composable
fun OwlApp(finishActivity: () -> Unit) {
    BlueTheme {
        val tabs: Array<CourseTabs> = remember { CourseTabs.entries.toTypedArray() }
        val navController: NavHostController = rememberNavController()
        Scaffold(
            backgroundColor = MaterialTheme.colors.primarySurface,
            bottomBar = { OwlBottomBar(navController = navController, tabs = tabs) }
        ) { innerPaddingModifier: PaddingValues ->
            NavGraph(
                finishActivity = finishActivity,
                navController = navController,
                modifier = Modifier.padding(paddingValues = innerPaddingModifier)
            )
        }
    }
}

/**
 * This Composable is composed in the `bottomBar` argument of the [Scaffold] of the [OwlApp] Composable.
 *
 * We start by initializing and remembering our [State] wrapped [NavBackStackEntry] variable
 * `val navBackStackEntry` to the current back stack entry of the [NavHostController] parameter
 * [navController] using its [NavController.currentBackStackEntryAsState] method. We then initialize
 * [String] variable `val currentRoute` to the [NavDestination.route] route of the
 * [NavBackStackEntry.destination] of the [NavBackStackEntry] variable `navBackStackEntry`. We
 * initialize and remember our [List] of [String] variable `val routes` to a list of all of the
 * [CourseTabs.route] in the entries of the [CourseTabs] enum. If the [String] variable `currentRoute`
 * is in the [List] variable `routes` we compose a [BottomNavigation] whose `modifier` argument is
 * a [Modifier.windowInsetsBottomHeight] that adds the [WindowInsets.Companion.navigationBars] window
 * insets as well as an additional [WindowInsets] with a `bottom` padding of `56.dp`. In the [RowScope]
 * `content` composable lambda argument we use the [Array.forEach] method to iterate through the
 * [Array] of [CourseTabs] parameter [tabs] of the [CourseTabs] and in the `action` lambda argument
 * we capture the current [CourseTabs] in the variable `tab` then compose a [BottomNavigationItem]
 * whose arguments are:
 *  - `icon`: is a lambda that composes an [Icon] whose `painter` argument is the [Painter] that
 *  [painterResource] creates from the drawable whose resource id is the [CourseTabs.icon] of the
 *  [CourseTabs] variable `tab`, and whose `contentDescription` argument is `null`.
 *  - `label`: is a lambda that composes a [Text] whose `text` argument is the [String] of the
 *  [CourseTabs.title] of the [CourseTabs] variable `tab` converted to uppercase.
 *  - `selected`: is a [Boolean] that is true if the [String] variable `currentRoute` is the same as
 *  the [CourseTabs.route] of the [CourseTabs] variable `tab`.
 *  - `onClick`: is a lambda which if the [String] variable `currentRoute` is not the same as the
 *  [CourseTabs.route] of [CourseTabs] variable `tab` calls the [NavController.navigate] method of
 *  the [NavHostController] parameter [navController] with its `route` argument the [CourseTabs.route]
 *  of the [CourseTabs] variable `tab` and in its [NavOptionsBuilder] `builder` lambda argument it
 *  calls [NavOptionsBuilder.popUpTo] with its `id` argument the [[NavGraph] `startDestinationId`
 *  of the [NavController.graph] of the [NavHostController] parameter [navController] and in the
 *  [PopUpToBuilder] `popUpToBuilder` lambda argument it sets [PopUpToBuilder.saveState] property
 *  to `true`. It also sets [NavOptionsBuilder.launchSingleTop] property to `true` and
 *  the [NavOptionsBuilder.restoreState] property to `true`.
 *  - `alwaysShowLabel`: is a [Boolean] that is `false`.
 *  - `selectedContentColor`: is the [Colors.secondary] of our custom [MaterialTheme.colors].
 *  - `unselectedContentColor`: is the `current` [LocalContentColor].
 *  - `modifier`: is a [Modifier.navigationBarsPadding] to add padding to accommodate the navigation
 *  bars insets
 *
 * @param navController the [NavHostController] of the [OwlApp] Composable.
 * @param tabs the [Array] of all of the [CourseTabs] entries.
 */
@Composable
fun OwlBottomBar(navController: NavController, tabs: Array<CourseTabs>) {

    val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    val currentRoute: String = navBackStackEntry?.destination?.route
        ?: CourseTabs.FEATURED.route

    val routes: List<String> = remember { CourseTabs.entries.map { it.route } }
    if (currentRoute in routes) {
        BottomNavigation(
            modifier = Modifier.windowInsetsBottomHeight(
                insets = WindowInsets.navigationBars.add(WindowInsets(bottom = 56.dp))
            )
        ) {
            tabs.forEach { tab: CourseTabs ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = tab.icon),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(id = tab.title).uppercase(Locale.getDefault())) },
                    selected = currentRoute == tab.route,
                    onClick = {
                        if (tab.route != currentRoute) {
                            navController.navigate(route = tab.route) {
                                popUpTo(id = navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    alwaysShowLabel = false,
                    selectedContentColor = MaterialTheme.colors.secondary,
                    unselectedContentColor = LocalContentColor.current,
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    }
}
