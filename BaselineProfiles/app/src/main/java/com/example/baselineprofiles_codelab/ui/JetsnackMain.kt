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

import android.os.Bundle
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.baselineprofiles_codelab.model.Snack
import com.example.baselineprofiles_codelab.ui.components.JetsnackScaffold
import com.example.baselineprofiles_codelab.ui.components.JetsnackSnackbar
import com.example.baselineprofiles_codelab.ui.home.HomeSections
import com.example.baselineprofiles_codelab.ui.home.JetsnackBottomBar
import com.example.baselineprofiles_codelab.ui.home.addHomeGraph
import com.example.baselineprofiles_codelab.ui.snackdetail.SnackDetail
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * This is the main composable for the Jetsnack app, defining the overall structure
 * including the the bottom navigation bar, a Snackbar for displaying messages, and
 * the navigation graph for moving between different screens.
 *
 * First we apply our [JetsnackTheme] custom [MaterialTheme] to the content of this composable.
 * In its `content` composable lambda argument we first initialize and remember our [JetsnackAppState]
 * variable `appState` to the instance returned by the [rememberJetsnackAppState] function.
 *
 * Then our root composable is a [JetsnackScaffold] with the following parameters:
 *  - `modifier`: is a [Modifier.semantics] in whose [SemanticsPropertyReceiver] `properties` lambda
 *  argument we set the [SemanticsPropertyReceiver.testTagsAsResourceId] property to `true` (this
 *  Allows us to use testTag() for UiAutomator resource-id).
 *  - `bottomBar`: if the [JetsnackAppState.shouldShowBottomBar] property of `appState` is `true` we
 *  compose a [JetsnackBottomBar] whose `tabs` argument is the [JetsnackAppState.bottomBarTabs]
 *  property of `appState`, `currentRoute` argument is the [JetsnackAppState.currentRoute] property
 *  of `appState`, and `navigateToRoute` argument is the [JetsnackAppState.navigateToBottomBarRoute]
 *  function reference of `appState`.
 *  - `snackbarHost`: is a lambda which accepts the [SnackbarHostState] passed the lambda in variable
 *  `snackbarHostState` and composes a [SnackbarHost] whose `hostState` argument is `snackbarHostState`,
 *  whose `modifier` argument is [Modifier.systemBarsPadding] to add padding to accommodate the
 *  system bars insets, and whose `snackbar` argument is a lambda which accepts the [SnackbarData]
 *  passed the lambda in variable `snackbarData` and composes a [JetsnackSnackbar] whose `snackbarData`
 *  argument is `snackbarData`.
 *  - `scaffoldState`: is the [JetsnackAppState.scaffoldState] property of `appState`.
 *
 * In the `content` composable lambda argument we accept the [PaddingValues] passed the lambda in
 * variable `innerPaddingModifier` and compose a [NavHost] whose `navController` argument is the
 * [JetsnackAppState.navController] property of `appState`, whose `startDestination` argument is the
 * [MainDestinations.HOME_ROUTE] string, and whose `modifier` argument is a [Modifier.padding] whose
 * `paddingValues` argument is `innerPaddingModifier`.
 *
 * In the [NavGraphBuilder] `builder` composable lambda argument of the [NavHost] we compose a
 * [jetsnackNavGraph] whose `onSnackSelected` argument is the [JetsnackAppState.navigateToSnackDetail]
 * function reference of `appState`, and whose `upPress` argument is the [JetsnackAppState.upPress]
 * function reference of `appState`.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun JetsnackMain() {
    JetsnackTheme {
        val appState: JetsnackAppState = rememberJetsnackAppState()
        JetsnackScaffold(
            modifier = Modifier.semantics {
                // Allows to use testTag() for UiAutomator resource-id.
                testTagsAsResourceId = true
            },
            bottomBar = {
                if (appState.shouldShowBottomBar) {
                    JetsnackBottomBar(
                        tabs = appState.bottomBarTabs,
                        currentRoute = appState.currentRoute!!,
                        navigateToRoute = appState::navigateToBottomBarRoute
                    )
                }
            },
            snackbarHost = { snackbarHostState: SnackbarHostState ->
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.systemBarsPadding(),
                    snackbar = { snackbarData: SnackbarData ->
                        JetsnackSnackbar(snackbarData = snackbarData)
                    }
                )
            },
            scaffoldState = appState.scaffoldState
        ) { innerPaddingModifier: PaddingValues ->
            NavHost(
                navController = appState.navController,
                startDestination = MainDestinations.HOME_ROUTE,
                modifier = Modifier.padding(paddingValues = innerPaddingModifier)
            ) {
                jetsnackNavGraph(
                    onSnackSelected = appState::navigateToSnackDetail,
                    upPress = appState::upPress
                )
            }
        }
    }
}

/**
 * NavGraph for the Jetsnack app.
 *
 * This function builds the navigation graph for the Jetsnack app using its [NavGraphBuilder] receiver.
 * It defines the destinations for the main screens of the app, including the home section and the
 * snack detail screen.
 *
 * The graph includes:
 *  - A nested navigation graph for the [MainDestinations.HOME_ROUTE] with [HomeSections.FEED] as the
 *  start destination, added by calling [addHomeGraph].
 *  - A [composable] destination for the snack detail screen ([MainDestinations.SNACK_DETAIL_ROUTE]),
 *  which takes a snack ID as a long argument. The [SnackDetail] composable is displayed for this
 *  destination, and the provided [upPress] lambda is used to handle back navigation.
 *
 * First we call [NavGraphBuilder.navigation] whose `route` argument is [MainDestinations.HOME_ROUTE]
 * and whose `startDestination` argument is the [HomeSections.route] of [HomeSections.FEED]. In its
 * [NavGraphBuilder] `builder` composable lambda argument we call [addHomeGraph] whose `onSnackSelected`
 * argument is our lambda parameter [onSnackSelected].
 *
 * Then we call [NavGraphBuilder.composable] whose `route` argument is the string formed by
 * concatenating the [MainDestinations.SNACK_DETAIL_ROUTE] string with the
 * [MainDestinations.SNACK_ID_KEY], and whose `arguments` argument is a [List] of [navArgument]
 * whose `name` argument is [MainDestinations.SNACK_ID_KEY] and whose [NavArgumentBuilder.type]
 * is [NavType.LongType]. In the [AnimatedContentScope] lambda argument we accept the
 * [NavBackStackEntry] passed the lambda in variable `backStackEntry` initalize our [Bundle] variable
 * `arguments` to the [NavBackStackEntry.arguments] property of `backStackEntry`, and initialize
 * our [Long] variable `snackId` to the value returned by the [Bundle.getLong] method for the
 * key [MainDestinations.SNACK_ID_KEY] of `arguments`. Then we compose a [SnackDetail] composable
 * whose `snackId` argument is `snackId` and whose `upPress` argument is our [upPress] lambda
 * parameter.
 *
 * @param onSnackSelected Lambda function invoked when a snack is selected, providing the snack ID
 * and the [NavBackStackEntry]. This is used to navigate to the snack detail screen to display the
 * [Snack] whose [Snack.id] is equal to the [Long] argument `snackId`.
 * @param upPress Lambda function invoked when the "up" action is triggered (e.g., back button
 * on the snack detail screen).
 */
private fun NavGraphBuilder.jetsnackNavGraph(
    onSnackSelected: (Long, NavBackStackEntry) -> Unit,
    upPress: () -> Unit
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination = HomeSections.FEED.route
    ) {
        addHomeGraph(onSnackSelected = onSnackSelected)
    }
    composable(
        route = "${MainDestinations.SNACK_DETAIL_ROUTE}/{${MainDestinations.SNACK_ID_KEY}}",
        arguments =
            listOf(navArgument(name = MainDestinations.SNACK_ID_KEY) {
                type = NavType.LongType
            })
    ) { backStackEntry: NavBackStackEntry ->
        val arguments: Bundle = requireNotNull(backStackEntry.arguments)
        val snackId: Long = arguments.getLong(MainDestinations.SNACK_ID_KEY)
        SnackDetail(snackId = snackId, upPress = upPress)
    }
}
