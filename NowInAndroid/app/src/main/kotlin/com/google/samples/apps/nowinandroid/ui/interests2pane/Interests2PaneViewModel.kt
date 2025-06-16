/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.ui.interests2pane

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.google.samples.apps.nowinandroid.feature.interests.navigation.InterestsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * The key used to save and restore the selected topic ID.
 */
const val TOPIC_ID_KEY: String = "selectedTopicId"

/**
 * ViewModel for the 2-pane interests screen.
 *
 * It is used to manage the selected topic ID, which is saved and restored via [SavedStateHandle].
 *
 * @param savedStateHandle The [SavedStateHandle] used to save and restore the selected topic ID.
 */
@HiltViewModel
class Interests2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /**
     * The navigation route for the interests screen.
     * This is used to extract the initial topic ID from the navigation arguments.
     */
    val route: InterestsRoute = savedStateHandle.toRoute<InterestsRoute>()
    
    /**
     * The ID of the currently selected topic. `null` if no topic is selected.
     */
    val selectedTopicId: StateFlow<String?> = savedStateHandle.getStateFlow(
        key = TOPIC_ID_KEY,
        initialValue = route.initialTopicId,
    )

    /**
     * Sets the selected topic ID to the [String] parameter [topicId].
     *
     * @param topicId The ID of the topic to select, or `null` to clear the selection.
     */
    fun onTopicClick(topicId: String?) {
        savedStateHandle[TOPIC_ID_KEY] = topicId
    }
}
