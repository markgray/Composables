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

package com.example.jetnews.ui.interests

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.Result
import com.example.jetnews.data.interests.InterestSection
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.data.successOr
import com.example.jetnews.ui.JetnewsDestinations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the [InterestsScreen] Interests screen
 */
data class InterestsUiState(
    /**
     * Used by the `TabWithSections` widget for the [TabContent] of the [Sections.Topics] "People"
     * tab
     */
    val topics: List<InterestSection> = emptyList(),
    /**
     * Used by the `TabWithTopics` widget for the [TabContent] of the [Sections.People] "Topics"
     * tab
     */
    val people: List<String> = emptyList(),
    /**
     * Used by the `TabWithTopics` widget for the [TabContent] of the [Sections.Publications]
     * "Publications" tab.
     */
    val publications: List<String> = emptyList(),
    /**
     * Used to simulate network delay [InterestsViewModel.refreshAll] method, but never read.
     */
    val loading: Boolean = false,
)

/**
 * This is used as the [ViewModel] for the [InterestsRoute] that is composed for the `route`
 * [JetnewsDestinations.INTERESTS_ROUTE]
 *
 * @param interestsRepository the [InterestsRepository] we should use to retrieve our data from.
 */
class InterestsViewModel(
    private val interestsRepository: InterestsRepository
) : ViewModel() {

    /**
     * The private [MutableStateFlow] wrapped [InterestsUiState] that is exposed to the UI by our
     * [StateFlow] wrapped [InterestsUiState] property [uiState].
     */
    private val _uiState = MutableStateFlow(value = InterestsUiState(loading = true))

    /**
     * Read only UI state exposed to the UI. [rememberTabContent] uses the [collectAsStateWithLifecycle]
     * extension function on this property to initialize the [State] wrapped [InterestsUiState] that
     * it uses to keep the [TabContent]'s it creates and remembers updated with the latest data.
     */
    val uiState: StateFlow<InterestsUiState> = _uiState.asStateFlow()

    /**
     * The current [StateFlow] wrapped [Set] of selected [TopicSelection] that we keep updated to
     * the latest value emitted by the [InterestsRepository.observeTopicsSelected] method. It is
     * collected by [rememberTabContent] for the [TabContent] whose `section` is [Sections.Topics]
     */
    val selectedTopics: StateFlow<Set<TopicSelection>> =
        interestsRepository.observeTopicsSelected().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = emptySet()
        )

    /**
     * The current [StateFlow] wrapped [Set] of selected [String] that we keep updated to the latest
     * value emitted by the [InterestsRepository.observePeopleSelected] method. It is collected by
     * [rememberTabContent] for the [TabContent] whose `section` is [Sections.People].
     */
    val selectedPeople: StateFlow<Set<String>> =
        interestsRepository.observePeopleSelected().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = emptySet()
        )

    /**
     * The current [StateFlow] wrapped [Set] of selected [String] that we keep updated to the latest
     * value emitted by the [InterestsRepository.observePublicationSelected] method. It is collected
     * by [rememberTabContent] for the [TabContent] whose `section` is [Sections.Publications].
     */
    val selectedPublications: StateFlow<Set<String>> =
        interestsRepository.observePublicationSelected().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = emptySet()
        )

    init {
        refreshAll()
    }

    /**
     * Toggles the presence of our [TopicSelection] parameter [topic] in the [MutableStateFlow] of
     * [Set] of [TopicSelection] maintained by [InterestsRepository] which we read for our [StateFlow]
     * of [Set] of [TopicSelection] property [selectedTopics]. We call the [CoroutineScope.launch]
     * method of our [viewModelScope] to launch a coroutine which calls the
     * [InterestsRepository.toggleTopicSelection] method of our [interestsRepository] field with its
     * `topic` argument our [TopicSelection] parameter [topic].
     *
     * @param topic the [TopicSelection] whose presence in the [Set] of [TopicSelection] maintained
     * by [InterestsRepository] that we follow for our [selectedTopics] property  we wish to toggle.
     */
    fun toggleTopicSelection(topic: TopicSelection) {
        viewModelScope.launch {
            interestsRepository.toggleTopicSelection(topic = topic)
        }
    }

    /**
     * Toggles the presence of our [String] parameter [person] in the [MutableStateFlow] of [Set] of
     * [String] maintained by [InterestsRepository] which we read for our [StateFlow] of [Set] of
     * [String] property [selectedPeople]. We call the [CoroutineScope.launch] method of our
     * [viewModelScope] to launch a coroutine which calls the [InterestsRepository.togglePersonSelected]
     * method of our [interestsRepository] field with its `person` argument our [String] parameter
     * [person].
     *
     * @param person the [String] whose presence in the [Set] of [String] maintained by
     * [InterestsRepository] that we follow for our [selectedPeople] property we wish to toggle.
     */
    fun togglePersonSelected(person: String) {
        viewModelScope.launch {
            interestsRepository.togglePersonSelected(person = person)
        }
    }

    /**
     * Toggles the presence of our [String] parameter [publication] in the [MutableStateFlow] of [Set]
     * of [String] maintained by [InterestsRepository] which we read for our [StateFlow] of [Set] of
     * [String] property [selectedPublications]. We call the [CoroutineScope.launch] method of our
     * [viewModelScope] to launch a coroutine which calls the [InterestsRepository.togglePublicationSelected]
     * method of our [interestsRepository] field with its `publication` argument our [String] parameter
     * [publication].
     *
     * @param publication the [String] whose presence in the [Set] of [String] maintained by
     * [InterestsRepository] that we follow for our [selectedPublications] property we wish to toggle.
     */
    fun togglePublicationSelected(publication: String) {
        viewModelScope.launch {
            interestsRepository.togglePublicationSelected(publication = publication)
        }
    }

    /**
     * Refresh topics, people, and publications
     */
    private fun refreshAll() {
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            // Trigger repository requests in parallel
            val topicsDeferred: Deferred<Result<List<InterestSection>>> =
                async { interestsRepository.getTopics() }
            val peopleDeferred: Deferred<Result<List<String>>> =
                async { interestsRepository.getPeople() }
            val publicationsDeferred: Deferred<Result<List<String>>> =
                async { interestsRepository.getPublications() }

            // Wait for all requests to finish
            val topics: List<InterestSection> = topicsDeferred.await().successOr(emptyList())
            val people: List<String> = peopleDeferred.await().successOr(emptyList())
            val publications: List<String> = publicationsDeferred.await().successOr(emptyList())

            _uiState.update {
                @Suppress("RedundantValueArgument")
                it.copy(
                    loading = false,
                    topics = topics,
                    people = people,
                    publications = publications
                )
            }
        }
    }

    /**
     * Factory for [InterestsViewModel] that takes [InterestsRepository] as a dependency
     */
    companion object {
        /**
         * TODO: Add kdoc
         */
        fun provideFactory(
            interestsRepository: InterestsRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return InterestsViewModel(interestsRepository) as T
            }
        }
    }
}
