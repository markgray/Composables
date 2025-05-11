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

package com.example.baselineprofiles_codelab.ui

import android.content.res.Resources
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.baselineprofiles_codelab.model.Message
import com.example.baselineprofiles_codelab.model.SnackbarManager
import com.example.baselineprofiles_codelab.ui.components.JetsnackScaffold
import com.example.baselineprofiles_codelab.ui.home.HomeSections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Destinations used in the [JetsnackMain].
 *  - [MainDestinations.HOME_ROUTE] is the route for the home screen: ("home")
 *  - [MainDestinations.SNACK_DETAIL_ROUTE] is the route for the detail screen: ("snack/{snackId}")
 *  - [MainDestinations.SNACK_ID_KEY] is the argument key for the detail screen: ("snackId")
 */
object MainDestinations {
    const val HOME_ROUTE: String = "home"
    const val SNACK_DETAIL_ROUTE: String = "snack"
    const val SNACK_ID_KEY: String = "snackId"
}

/**
 * Remembers and creates an instance of [JetsnackAppState].
 *
 * @param scaffoldState [ScaffoldState] of the [JetsnackScaffold].
 * @param navController [NavHostController] associated with the [NavHost] used in [JetsnackMain]
 * @param snackbarManager [SnackbarManager] used by the [JetsnackScaffold], defaults to the
 * singleton instance [SnackbarManager].
 * @param resources [Resources] corresponding to the [LocalContext].
 * @param coroutineScope [CoroutineScope] used to launch coroutines in the app.
 */
@Composable
fun rememberJetsnackAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): JetsnackAppState =
    remember(scaffoldState, navController, snackbarManager, resources, coroutineScope) {
        JetsnackAppState(
            scaffoldState = scaffoldState,
            navController = navController,
            snackbarManager = snackbarManager,
            resources = resources,
            coroutineScope = coroutineScope
        )
    }

/**
 * Responsible for holding state related to [JetsnackMain] and containing UI-related logic.
 *
 * In our init block we use the [CoroutineScope.launch] method of our [CoroutineScope] property
 * [coroutineScope] to launch a coroutine in which we use the [StateFlow.collect] method of the
 * [StateFlow] of [List] of [Message] property [SnackbarManager.messages] of [SnackbarManager]
 * property [snackbarManager] to collect [List] of [Message] in the `currentMessages` variable
 * and if it is not empty set [Message] variable `message` to the first element of `currentMessages`
 * and set [CharSequence] variable `text` to the result of calling the [Resources.getText] method
 * for the [String] whose resource ID is the [Message.messageId] of `message`. Then we call the
 * [SnackbarHostState.showSnackbar] method of the [ScaffoldState.snackbarHostState] of the
 * [ScaffoldState] property [scaffoldState] to show the snackbar with its `message` the result of
 * converting `text` to a [String]. After that we call the [SnackbarManager.setMessageShown] method
 * of the [SnackbarManager] property [snackbarManager] to notify the [SnackbarManager] that the
 * message with the [Message.id] of `message` has been shown.
 *
 * @property scaffoldState [ScaffoldState] used by the [JetsnackScaffold].
 * @property navController [NavHostController] used by the Navigation component.
 * @property snackbarManager [SnackbarManager] used to show Snackbar messages.
 * @property resources [Resources] corresponding to the [LocalContext].
 * @property coroutineScope [CoroutineScope] used to launch coroutines related to this
 * [JetsnackAppState].
 */
@Stable
class JetsnackAppState(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    // Process snackbars coming from SnackbarManager
    init {
        coroutineScope.launch {
            snackbarManager.messages.collect { currentMessages: List<Message> ->
                if (currentMessages.isNotEmpty()) {
                    val message: Message = currentMessages[0]
                    val text: CharSequence = resources.getText(message.messageId)

                    // Display the snackbar on the screen. `showSnackbar` is a function
                    // that suspends until the snackbar disappears from the screen
                    scaffoldState.snackbarHostState.showSnackbar(message = text.toString())
                    // Once the snackbar is gone or dismissed, notify the SnackbarManager
                    snackbarManager.setMessageShown(messageId = message.id)
                }
            }
        }
    }

    // ----------------------------------------------------------
    // BottomBar state source of truth
    // ----------------------------------------------------------

    /**
     * All the bottom bar tabs as an [Array] of [HomeSections].
     */
    val bottomBarTabs: Array<HomeSections> = HomeSections.entries.toTypedArray()

    /**
     * The current [HomeSections.route]'s of the [Array] of [HomeSections] property [bottomBarTabs]
     */
    private val bottomBarRoutes: List<String> = bottomBarTabs.map { it.route }

    /**
     * Whether the bottom bar should be shown. Reading this attribute will cause recompositions when
     * the bottom bar needs to be shown, or not shown. Not all routes need to show the bottom bar,
     * only those in [bottomBarRoutes].
     */
    val shouldShowBottomBar: Boolean
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination?.route in bottomBarRoutes

    // ----------------------------------------------------------
    // Navigation state source of truth
    // ----------------------------------------------------------

    /**
     * The current route. Reading this attribute will cause recompositions when the route changes.
     * It returns the result of calling the [NavHostController.currentDestination] method of our
     * [NavHostController] property [navController] to get the current [NavDestination] and if that
     * is not `null` we return the [NavDestination.route] of that [NavDestination].
     */
    val currentRoute: String?
        get() = navController.currentDestination?.route

    /**
     * Navigate up to the previous screen. Convenience method for the [NavHostController.navigateUp]
     * method of our [NavHostController] property [navController].
     */
    fun upPress() {
        navController.navigateUp()
    }

    /**
     * Navigate to one the bottom bar routes. If the destination [route] is equal to the current
     * route we return having done nothing. Otherwise we call the [NavHostController.navigate]
     * method of our [NavHostController] property [navController] passing the [route] as the
     * destination `route`. In the [NavOptionsBuilder] `builder` lambda argument we set the
     * [NavOptionsBuilder.launchSingleTop] flag to `true` to avoid building up a large stack of
     * destinations on the back stack as you select items, and set the [NavOptionsBuilder.restoreState]
     * flag to `true` to restore the state of the previously selected destination, and call the
     * [NavOptionsBuilder.popUpTo] method to pop up to the start destination of the graph and save
     * state. This makes going back to the start destination happen when pressing back in any bottom
     * tab navigated to using this method.
     *
     * @param route The route of the destination to navigate to.
     */
    fun navigateToBottomBarRoute(route: String) {
        if (route != currentRoute) {
            navController.navigate(route = route) {
                launchSingleTop = true
                restoreState = true
                // Pop up backstack to the first destination and save state. This makes going back
                // to the start destination when pressing back in any other bottom tab.
                popUpTo(id = findStartDestination(graph = navController.graph).id) {
                    saveState = true
                }
            }
        }
    }

    /**
     * Navigate to the snack detail screen. First we check if the [NavBackStackEntry] the navigation
     * is originating from is still in the [NavBackStackEntry.lifecycleIsResumed] state. If it is not
     * we return having done nothing. Otherwise we call the [NavHostController.navigate] method of
     * our [NavHostController] property [navController] passing as its `route` the [String] that
     * results from concatenating [MainDestinations.SNACK_DETAIL_ROUTE] and the [String] value of
     * our [Long] parameter [snackId].
     *
     * @param snackId The ID of the snack to display.
     * @param from The [NavBackStackEntry] the navigation is originating from. This is used to
     * prevent duplicated navigation events.
     */
    fun navigateToSnackDetail(snackId: Long, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(route = "${MainDestinations.SNACK_DETAIL_ROUTE}/$snackId")
        }
    }
}

/**
 * If the lifecycle is not resumed it means this [NavBackStackEntry] already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed(): Boolean =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

/**
 * The start destination of the [NavGraph] receiver. Reading this attribute will cause
 * recompositions when the start destination changes.
 */
private val NavGraph.startDestination: NavDestination?
    get() = findNode(resId = startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph = graph.startDestination!!) else graph
}

/**
 * A composable function that returns the [Resources]. It will be recomposed when `Configuration`
 * gets updated.
 */
@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
