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

package com.google.samples.apps.nowinandroid.feature.topic.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.feature.topic.TopicScreen
import com.google.samples.apps.nowinandroid.feature.topic.TopicViewModel
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * @[Serializable] Type for navigating to the Topic screen to display the [Topic] whose [Topic.id]
 * is our [id] property.
 *
 * @param id The ID of the topic to display.
 */
@OptIn(InternalSerializationApi::class)
@Serializable
data class TopicRoute(val id: String)

/**
 * Navigates to the Topic screen to display the [Topic] whose [Topic.id] is our [String] parameter
 * [topicId].
 *
 * @param topicId The ID of the topic to display.
 * @param navOptions Optional navigation options to apply to this navigation call.
 */
fun NavController.navigateToTopic(topicId: String, navOptions: NavOptionsBuilder.() -> Unit = {}) {
    navigate(route = TopicRoute(topicId)) {
        navOptions()
    }
}

/**
 * Adds the Topic screen to the navigation graph. We call the [NavGraphBuilder.composable] method of
 * our [NavGraphBuilder] receiver to add the screen to the navigation graph, and in its
 * [AnimatedContentScope] `content` composable lambda argument we first initialize our [String]
 * variable `id` using the [NavBackStackEntry.toRoute] method of the [NavBackStackEntry] passed the
 * lambda in variable `entry` to retrieve the [TopicRoute.id] then compose a [TopicScreen] whose
 * arguments are:
 *  - `showBackButton`: our [Boolean] parameter [showBackButton].
 *  - `onBackClick`: our lambda parameter [onBackClick].
 *  - `onTopicClick`: our lambda parameter [onTopicClick].
 *  - `viewModel`: a [TopicViewModel] that is built using [hiltViewModel] with the `key` our
 *  [String] variable `id`, and in its `creationCallback` lambda argument we capture the
 *  [TopicViewModel.Factory] passed the lambda in variable `factory` and then return the
 *  [TopicViewModel] created by the [TopicViewModel.Factory.create] method of `factory` when
 *  called with the `topicId` argument our [String] variable `id`.
 *
 * @param showBackButton Whether to show the back button.
 * @param onBackClick Called when the back button is clicked.
 * @param onTopicClick Called when a topic is clicked.
 */
fun NavGraphBuilder.topicScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
) {
    composable<TopicRoute> { entry: NavBackStackEntry ->
        val id: String = entry.toRoute<TopicRoute>().id
        TopicScreen(
            showBackButton = showBackButton,
            onBackClick = onBackClick,
            onTopicClick = onTopicClick,
            viewModel = hiltViewModel(
                checkNotNull(LocalViewModelStoreOwner.current) {
                    "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
                },
                id,
            ) { factory: TopicViewModel.Factory ->
                factory.create(topicId = id)
            },
        )
    }
}
