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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.owl.ui.courses.CourseTabs
import com.example.owl.ui.theme.BlueTheme
import java.util.Locale

/**
 * This is the main Composable of our app. It uses our [BlueTheme] custom [MaterialTheme] to wrap
 * its children. It initializes and remembers its [Array] of [CourseTabs] variable `val tabs` to all of the
 * entries in the [CourseTabs] enum, and initializes and remembers its [NavHostController] variable
 * `val navController` to a new instance. Its root Composable is a [Scaffold] whose `backgroundColor` argument is the
 * [Colors.primarySurface] color of our custom [MaterialTheme.colors], and whose `bottomBar` argument is an
 * [OwlBottomBar] whose `navController` argument is our `navController` variable and whose `tabs`
 * argument is our `tabs` variable. In its `content` composable lambda argument we accept the [PaddingValues]
 * passed the lambda in variable `innerPaddingModifier` then compose a [NavGraph] whose arguments are:
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
 * TODO:Continue here.
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
            Modifier.windowInsetsBottomHeight(
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
