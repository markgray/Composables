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

package com.google.samples.apps.nowinandroid.feature.foryou.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.notifications.DEEP_LINK_URI_PATTERN
import com.google.samples.apps.nowinandroid.feature.foryou.ForYouScreen
import kotlinx.serialization.Serializable

/**
 * Represents the route to the For You screen.
 */
@Serializable
data object ForYouRoute // route to ForYou screen

/**
 * Base route for the For You navigation graph.
 */
@Serializable
data object ForYouBaseRoute // route to base navigation graph

/**
 * Navigates to the For You screen.
 *
 * @param navOptions The navigation options.
 */
fun NavController.navigateToForYou(navOptions: NavOptions): Unit =
    navigate(route = ForYouRoute, navOptions)

/**
 * Builds the navigation graph for the For You section of the app.
 * This section displays personalized content and allows users to explore topics.
 *
 * We cqll the [NavGraphBuilder.navigation] extension function to create a nested navigation graph
 * with the base route [ForYouBaseRoute] with the start destination [ForYouRoute]. We then call the
 * [NavGraphBuilder.composable] extension function to add a composable destination for the
 * [ForYouRoute] with the deep link [DEEP_LINK_URI_PATTERN] that allows a specific news resource to
 * be opened from a notification. In the [AnimatedContentScope] `content` Composable lambda argument
 * we compose a [ForYouScreen] whose `onTopicClick` lambda argument is our [onTopicClick] lambda
 * parameter.
 *
 * Also in the [NavGraphBuilder.navigation] extension function's `builder` lambda argument we call
 * our [topicDestination] lambda argument to add the topic specific content to the graph.
 *
 * @param onTopicClick called when a topic is clicked with the [Topic.id] of the selected topic.
 * @param topicDestination A lambda function that defines the navigation destination for topic
 * specific content. This allows for modularity, as the topic content can be defined in a
 * separate module.
 */
fun NavGraphBuilder.forYouSection(
    onTopicClick: (String) -> Unit,
    topicDestination: NavGraphBuilder.() -> Unit,
) {
    navigation<ForYouBaseRoute>(startDestination = ForYouRoute) {
        composable<ForYouRoute>(
            deepLinks = listOf(
                navDeepLink {
                    /**
                     * This destination has a deep link that enables a specific news resource to be
                     * opened from a notification (@see SystemTrayNotifier for more). The news resource
                     * ID is sent in the URI rather than being modelled in the route type because it's
                     * transient data (stored in SavedStateHandle) that is cleared after the user has
                     * opened the news resource.
                     */
                    uriPattern = DEEP_LINK_URI_PATTERN
                },
            ),
        ) {
            ForYouScreen(onTopicClick = onTopicClick)
        }
        topicDestination()
    }
}
