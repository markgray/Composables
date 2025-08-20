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

package com.google.samples.apps.nowinandroid.feature.interests.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Represents the route for the Interests screen.
 *
 * This class is used for type-safe navigation and argument passing between destinations.
 *
 * @param initialTopicId The ID of the topic to be initially selected when navigating to this
 * destination. If `null`, no specific topic will be selected initially.
 */
@OptIn(InternalSerializationApi::class)
@Serializable
data class InterestsRoute(
    // The ID of the topic which will be initially selected at this destination
    val initialTopicId: String? = null,
)

/**
 * Navigates to the interests route.
 *
 * @param initialTopicId The ID of the topic that should be initially selected when navigating to the
 * interests route. If `null`, no topic will be initially selected.
 * @param navOptions The navigation options to apply to this navigation.
 */
fun NavController.navigateToInterests(
    initialTopicId: String? = null,
    navOptions: NavOptions? = null,
) {
    navigate(route = InterestsRoute(initialTopicId), navOptions = navOptions)
}
