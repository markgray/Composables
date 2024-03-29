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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.interests.InterestSection
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.data.successOr
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the Interests screen
 */
data class InterestsUiState(
    /**
     * TODO: Add kdoc
     */
    val topics: List<InterestSection> = emptyList(),
    /**
     * TODO: Add kdoc
     */
    val people: List<String> = emptyList(),
    /**
     * TODO: Add kdoc
     */
    val publications: List<String> = emptyList(),
    /**
     * TODO: Add kdoc
     */
    val loading: Boolean = false,
)

/**
 * TODO: Add kdoc
 */
class InterestsViewModel(
    private val interestsRepository: InterestsRepository
) : ViewModel() {

    /**
     * TODO: Add kdoc
     */
    private val _uiState = MutableStateFlow(InterestsUiState(loading = true))

    /**
     * UI state exposed to the UI
     */
    val uiState: StateFlow<InterestsUiState> = _uiState.asStateFlow()

    /**
     * TODO: Add kdoc
     */
    val selectedTopics: StateFlow<Set<TopicSelection>> =
        interestsRepository.observeTopicsSelected().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = emptySet()
        )

    /**
     * TODO: Add kdoc
     */
    val selectedPeople: StateFlow<Set<String>> =
        interestsRepository.observePeopleSelected().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = emptySet()
        )

    /**
     * TODO: Add kdoc
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
     * TODO: Add kdoc
     */
    fun toggleTopicSelection(topic: TopicSelection) {
        viewModelScope.launch {
            interestsRepository.toggleTopicSelection(topic = topic)
        }
    }

    /**
     * TODO: Add kdoc
     */
    fun togglePersonSelected(person: String) {
        viewModelScope.launch {
            interestsRepository.togglePersonSelected(person = person)
        }
    }

    /**
     * TODO: Add kdoc
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
            val topicsDeferred = async { interestsRepository.getTopics() }
            val peopleDeferred = async { interestsRepository.getPeople() }
            val publicationsDeferred = async { interestsRepository.getPublications() }

            // Wait for all requests to finish
            val topics = topicsDeferred.await().successOr(emptyList())
            val people = peopleDeferred.await().successOr(emptyList())
            val publications = publicationsDeferred.await().successOr(emptyList())

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
     * Factory for InterestsViewModel that takes PostsRepository as a dependency
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
