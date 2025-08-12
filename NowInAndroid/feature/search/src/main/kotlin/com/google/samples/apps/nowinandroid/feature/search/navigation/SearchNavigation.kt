/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.feature.search.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.google.samples.apps.nowinandroid.feature.search.SearchRoute
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation route for the search screen
 */
@Serializable
data object SearchRoute

/**
 * Navigates to the search route. It just calls the [NavController.navigate] method of its receiver
 * with the `route` argument [SearchRoute] and the `navOptions` argument its [NavOptions] parmeter
 * [navOptions].
 *
 * @param navOptions The navigation options to use for this navigation.
 */
fun NavController.navigateToSearch(navOptions: NavOptions? = null): Unit =
    navigate(route = SearchRoute, navOptions = navOptions)

/**
 * Adds the [SearchRoute] search screen to the navigation graph. It just calls the
 * [NavGraphBuilder.composable] method of its receiver with the `route` argument [SearchRoute] and
 * in its [AnimatedContentScope] `content` composable lambda argument it composes a [SearchRoute]
 * whose arguments are identical with the parameters of the extension function.
 *
 * @param onBackClick Called when the user clicks the back button.
 * @param onInterestsClick Called when the user clicks the interests button.
 * @param onTopicClick Called when the user clicks a topic.
 */
fun NavGraphBuilder.searchScreen(
    onBackClick: () -> Unit,
    onInterestsClick: () -> Unit,
    onTopicClick: (String) -> Unit,
) {
    // TODO: Handle back stack for each top-level destination. At the moment each top-level
    // destination may have own search screen's back stack.
    composable<SearchRoute> {
        SearchRoute(
            onBackClick = onBackClick,
            onInterestsClick = onInterestsClick,
            onTopicClick = onTopicClick,
        )
    }
}
