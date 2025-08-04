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

package com.google.samples.apps.nowinandroid.feature.bookmarks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.google.samples.apps.nowinandroid.feature.bookmarks.BookmarksRoute
import kotlinx.serialization.Serializable

@Serializable
object BookmarksRoute

/**
 * Navigates to the bookmarks screen.
 *
 * @param navOptions Navigation options to apply to this navigation call.
 */
fun NavController.navigateToBookmarks(navOptions: NavOptions): Unit =
    navigate(route = BookmarksRoute, navOptions = navOptions)

/**
 * Adds the bookmarks screen to the navigation graph.
 *
 * @param onTopicClick Callback called when a topic is clicked.
 * @param onShowSnackbar Callback called to show a snackbar.
 */
fun NavGraphBuilder.bookmarksScreen(
    onTopicClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<BookmarksRoute> {
        BookmarksRoute(onTopicClick = onTopicClick, onShowSnackbar = onShowSnackbar)
    }
}
