/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.ui

import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import androidx.navigation.compose.NavHost
import com.example.jetnews.ui.home.HomeRoute
import com.example.jetnews.ui.interests.InterestsRoute

/**
 * Destinations used in the [JetnewsApp].
 */
object JetnewsDestinations {
    /**
     * This route causes [JetnewsNavGraph] to navigate to display the [HomeRoute] Composable.
     */
    const val HOME_ROUTE: String = "home"

    /**
     * This route causes [JetnewsNavGraph] to navigate to display the [InterestsRoute] Composable.
     */
    const val INTERESTS_ROUTE: String = "interests"
}

/**
 * Models the navigation actions in the app, by supplying methods that can be called to instruct
 * our [NavHostController] parameter `navController` to navigate to one of the two routes that
 * are provided by [JetnewsNavGraph]. By calling our [navigateToHome] method [JetnewsNavGraph]
 * will start to display our [HomeRoute] Composable, while calling our [navigateToInterests] method
 * [JetnewsNavGraph] will start to display our [InterestsRoute] Composable.
 *
 * @param navController the [NavHostController] to use to navigate to the desired `route`.
 */
class JetnewsNavigationActions(val navController: NavHostController) {
    /**
     * Uses the [NavHostController.navigate] method of our [NavHostController] field [navController]
     * to have the [NavHost] of [JetnewsNavGraph] display the [HomeRoute] composable. In the block
     * of our lambda we call the [NavHostController.navigate] of [navController] with its `route`
     * argument [JetnewsDestinations.HOME_ROUTE]. Then in its [NavOptionsBuilder] `builder` lambda
     * argument we call [NavOptionsBuilder.popUpTo] with the `id` parameter the [NavDestination.id]
     * returned by a call to the [NavGraph.findStartDestination] of the [NavController.graph] of our
     * [NavHostController] field [navController] (Pops up to the start destination of the graph) and
     * in its [PopUpToBuilder] lambda argument we set the [PopUpToBuilder.saveState] property to
     * `true` (which sets the [NavOptionsBuilder] private field `saveState` to `true`).
     *
     * Next in the [NavOptionsBuilder] we set the [NavOptionsBuilder.launchSingleTop] property to
     * `true` to avoid multiple copies of the same destination when reselecting the same item, and
     * set the [NavOptionsBuilder.restoreState] property to `true` to restore state when reselecting
     * a previously selected item.
     */
    val navigateToHome: () -> Unit = {
        navController.navigate(route = JetnewsDestinations.HOME_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(id = navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
    /**
     * TODO: Add kdoc
     */
    val navigateToInterests: () -> Unit = {
        navController.navigate(route = JetnewsDestinations.INTERESTS_ROUTE) {
            popUpTo(id = navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}
