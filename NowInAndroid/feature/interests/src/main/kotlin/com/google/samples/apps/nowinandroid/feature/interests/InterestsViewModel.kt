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

package com.google.samples.apps.nowinandroid.feature.interests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.domain.GetFollowableTopicsUseCase
import com.google.samples.apps.nowinandroid.core.domain.TopicSortField
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.feature.interests.navigation.InterestsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Interests screen.
 *
 * @property savedStateHandle key-value map that allows for saving and restoring UI-related state
 * within a ViewModel, particularly useful for surviving configuration changes and process death.
 * @property userDataRepository [UserDataRepository] repository for user data operations,
 * injected by Hilt.
 * @property getFollowableTopics use case for getting followable topics, injected by Hilt.
 */
@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val userDataRepository: UserDataRepository,
    getFollowableTopics: GetFollowableTopicsUseCase,
) : ViewModel() {

    /**
     * Key used to save and retrieve the currently selected topic id from the [SavedStateHandle].
     */
    private val selectedTopicIdKey = "selectedTopicIdKey"

    /**
     * Route parameter for the interests screen. The [SavedStateHandle.toRoute] extension function
     * Extrapolates arguments from [SavedStateHandle] and recreates an [InterestsRoute] instance.
     */
    private val interestsRoute: InterestsRoute = savedStateHandle.toRoute()

    /**
     * The ID of the currently selected topic, or `null` if no topic is selected.
     * This is a [StateFlow] that can be observed for changes.
     * The initial value is taken from the `initialTopicId` of the [InterestsRoute]
     * if available, otherwise it defaults to `null`.
     * The selected topic ID is saved and restored in our [SavedStateHandle] under the key
     * [selectedTopicIdKey].
     */
    private val selectedTopicId = savedStateHandle.getStateFlow(
        key = selectedTopicIdKey,
        initialValue = interestsRoute.initialTopicId,
    )

    /**
     * Represents the UI state for the Interests screen.
     * This is a [StateFlow] that emits [InterestsUiState] values.
     * It combines the latest values from [selectedTopicId] and the list of followable topics
     * (obtained from [getFollowableTopics]) to create the current UI state.
     * The UI state is transformed into an [InterestsUiState.Interests] object.
     * The flow is started when the ViewModel is created and stops after a 5-second timeout
     * when there are no subscribers. The initial value is [InterestsUiState.Loading].
     */
    val uiState: StateFlow<InterestsUiState> = combine(
        flow = selectedTopicId,
        flow2 = getFollowableTopics(sortBy = TopicSortField.NAME),
        transform = InterestsUiState::Interests,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = InterestsUiState.Loading,
    )

    /**
     * Updates the followed status of a topic. It uses the [viewModelScope] to launch a coroutine
     * in which it calls the [UserDataRepository.setTopicIdFollowed] method of our [UserDataRepository]
     * property [userDataRepository] with its `followedTopicId` argument our [String] parameter
     * [followedTopicId] and its `followed` argument our [Boolean] parameter [followed].
     *
     * @param followedTopicId The ID of the topic to update.
     * @param followed `true` to follow the topic, `false` to unfollow it.
     */
    fun followTopic(followedTopicId: String, followed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(
                followedTopicId = followedTopicId,
                followed = followed,
            )
        }
    }

    /**
     * Handles the click event on a topic.
     * Sets the selected topic ID in the [SavedStateHandle].
     *
     * @param topicId The ID of the clicked topic, or `null` if no topic is clicked.
     */
    fun onTopicClick(topicId: String?) {
        savedStateHandle[selectedTopicIdKey] = topicId
    }
}

/**
 * Represents the different UI states for the Interests screen.
 * This sealed interface defines the possible states the UI can be in.
 */
sealed interface InterestsUiState {
    /**
     * Represents the loading state of the Interests screen.
     * This state is typically shown when the data is being fetched.
     */
    data object Loading : InterestsUiState

    /**
     * Represents the state where the interests data has been successfully loaded.
     *
     * @property selectedTopicId The ID of the currently selected topic, or `null` if no topic is
     * selected. This is used to highlight the selected topic in the UI.
     * @property topics A list of [FollowableTopic] objects representing the topics that the user
     * can follow or unfollow. Each [FollowableTopic] contains information about a topic, including
     * its ID, name, and whether the user is currently following it.
     */
    data class Interests(
        val selectedTopicId: String?,
        val topics: List<FollowableTopic>,
    ) : InterestsUiState

    /**
     * Represents the state where there are no interests to display.
     * This state is typically shown when the list of topics is empty.
     */
    data object Empty : InterestsUiState
}
