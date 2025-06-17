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

package com.google.samples.apps.nowinandroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.samples.apps.nowinandroid.feature.bookmarks.navigation.bookmarksScreen
import com.google.samples.apps.nowinandroid.feature.foryou.navigation.ForYouBaseRoute
import com.google.samples.apps.nowinandroid.feature.foryou.navigation.forYouSection
import com.google.samples.apps.nowinandroid.feature.interests.navigation.navigateToInterests
import com.google.samples.apps.nowinandroid.feature.search.navigation.searchScreen
import com.google.samples.apps.nowinandroid.feature.topic.navigation.navigateToTopic
import com.google.samples.apps.nowinandroid.feature.topic.navigation.topicScreen
import com.google.samples.apps.nowinandroid.navigation.TopLevelDestination.INTERESTS
import com.google.samples.apps.nowinandroid.ui.NiaAppState
import com.google.samples.apps.nowinandroid.ui.interests2pane.interestsListDetailScreen

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 *
 * We start by initializing our [NavHostController] variable `navController` with the
 * [NiaAppState.navController] property of our [NiaAppState] parameter [appState].
 *
 * Our root composable is a [NavHost] whose `navController` argument is our [NavHostController]
 * variable `navController`, whose `startDestination` argument is the route [ForYouBaseRoute]
 * (the route to the base navigation graph of the [forYouSection] nested graph), and whose
 * `modifier` argument is our [Modifier] parameter [modifier]. In the [NavGraphBuilder] `builder`
 * lambda argument of the [NavHost] we call the following:
 *
 * **First**: We add to our graph the [forYouSection] nested graph with its `onTopicClick` argument
 * the [NavHostController.navigateToTopic] extension function of our [NavHostController] variable
 * `navController`. In its nested [NavGraphBuilder] `builder` lambda argument we comppose a
 * [topicScreen] screen whose arguments are:
 *  - `showBackButton`: a [Boolean] that is `true`
 *  - `onBackClick`: a lambda that calls the [NavHostController.popBackStack] function of our
 *  [NavHostController] variable `navController`
 *  - `onTopicClick`: a lambda that calls the [NavHostController.navigateToTopic] extension function
 *  of our [NavHostController] variable `navController`
 *
 * **Second**: We add to our graph the [bookmarksScreen] screen whose arguments are:
 *  - `onTopicClick`: a lambda that calls the [NavHostController.navigateToInterests] extension
 *  function of our [NavHostController] variable `navController`
 *  - `onShowSnackbar`: is our lambda parameter [onShowSnackbar].
 *
 * **Third**: We add to our graph the [searchScreen] screen whose arguments are:
 *  - `onBackClick`: a lambda that calls the [NavHostController.popBackStack] function of our
 *  [NavHostController] variable `navController`
 *  - `onInterestsClick`: a lambda that calls the [NiaAppState.navigateToTopLevelDestination]
 *  extension function of our [NiaAppState] parameter [appState] with  [TopLevelDestination.INTERESTS]
 *  as its `topLevelDestination` argument.
 *  - `onTopicClick`: a lambda that calls the [NavHostController.navigateToInterests] extension
 *  function of our [NavHostController] variable `navController`
 *
 * **Fourth**: We add to our graph the [interestsListDetailScreen] screen.
 *
 * @param appState [NiaAppState]: The current navigation state.
 * @param onShowSnackbar: A suspend function that shows a snackbar with the given `message` and
 * `actionLabel` and returns a [Boolean] indicating that `Action` on the Snackbar has been clicked
 * before the time out passed if `true`.
 * @param modifier: a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller `NiaApp` (in file com/google/samples/apps/nowinandroid/ui/NiaApp.kt) does
 * not pass us any so the empty, default, or starter [Modifier] that contains no elements is used.
 */
@Composable
fun NiaNavHost(
    appState: NiaAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController: NavHostController = appState.navController
    NavHost(
        navController = navController,
        startDestination = ForYouBaseRoute,
        modifier = modifier,
    ) {
        forYouSection(
            onTopicClick = navController::navigateToTopic,
        ) {
            topicScreen(
                showBackButton = true,
                onBackClick = navController::popBackStack,
                onTopicClick = navController::navigateToTopic,
            )
        }
        bookmarksScreen(
            onTopicClick = navController::navigateToInterests,
            onShowSnackbar = onShowSnackbar,
        )
        searchScreen(
            onBackClick = navController::popBackStack,
            onInterestsClick = { appState.navigateToTopLevelDestination(topLevelDestination = INTERESTS) },
            onTopicClick = navController::navigateToInterests,
        )
        interestsListDetailScreen()
    }
}
