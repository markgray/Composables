/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.jetnews.data.interests.impl

import androidx.lifecycle.viewModelScope
import com.example.jetnews.data.Result
import com.example.jetnews.data.interests.InterestSection
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.ui.interests.InterestsUiState
import com.example.jetnews.ui.interests.InterestsViewModel
import com.example.jetnews.ui.interests.rememberTabContent
import com.example.jetnews.ui.interests.Sections
import com.example.jetnews.ui.interests.TabContent
import com.example.jetnews.utils.addOrRemove
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Implementation of [InterestsRepository] that returns a hardcoded list of
 * topics, people and publications synchronously.
 */
class FakeInterestsRepository : InterestsRepository {

    /**
     * Holds the [List] of [InterestSection] that is returned by our [getTopics] method. It is
     * read by [rememberTabContent] for the [TabContent] whose `section` is [Sections.Topics].
     */
    private val topics: List<InterestSection> by lazy {
        listOf(
            InterestSection(
                title = "Android",
                interests = listOf("Jetpack Compose", "Kotlin", "Jetpack")
            ),
            InterestSection(
                title = "Programming",
                interests = listOf("Kotlin", "Declarative UIs", "Java", "Unidirectional Data Flow", "C++")
            ),
            InterestSection(
                title = "Technology",
                interests = listOf("Pixel", "Google")
            )
        )
    }

    /**
     * Holds the [List] of [String] that is returned by our [getPeople] method. It is read by
     * [rememberTabContent] for the [TabContent] whose `section` is [Sections.People].
     */
    private val people: List<String> by lazy {
        listOf(
            "Kobalt Toral",
            "K'Kola Uvarek",
            "Kris Vriloc",
            "Grala Valdyr",
            "Kruel Valaxar",
            "L'Elij Venonn",
            "Kraag Solazarn",
            "Tava Targesh",
            "Kemarrin Muuda"
        )
    }

    /**
     * Holds the [List] of [String] that is returned by our [getPublications] method. It is read by
     * [rememberTabContent] for the [TabContent] whose `section` is [Sections.Publications].
     */
    private val publications: List<String> by lazy {
        listOf(
            "Kotlin Vibe",
            "Compose Mix",
            "Compose Breakdown",
            "Android Pursue",
            "Kotlin Watchman",
            "Jetpack Ark",
            "Composeshack",
            "Jetpack Point",
            "Compose Tribune"
        )
    }

    // for now, keep the selections in memory

    /**
     * This is the [MutableStateFlow] of set of [TopicSelection] returned by our [observeTopicsSelected]
     * method. [InterestsViewModel] uses the [Flow] of [Set] of [TopicSelection] returned to produce
     * the [StateFlow] of [Set] of [TopicSelection] property [InterestsViewModel.selectedTopics] which
     * is collected by [rememberTabContent] for the [TabContent] whose `section` is [Sections.Topics].
     */
    private val selectedTopics = MutableStateFlow(value = setOf<TopicSelection>())

    /**
     * This is the [MutableStateFlow] of set of [String] returned by our [observePeopleSelected]
     * method. [InterestsViewModel] uses the [Flow] of [Set] of [String] returned to produce the
     * [StateFlow] of [Set] of [String] property [InterestsViewModel.selectedPeople] which is
     * collected by [rememberTabContent] for the [TabContent] whose `section` is [Sections.People].
     */
    private val selectedPeople = MutableStateFlow(value = setOf<String>())

    /**
     * This is the [MutableStateFlow] of set of [String] returned by our [observePublicationSelected]
     * method. [InterestsViewModel] uses the [Flow] of [Set] of [String] returned to produce the
     * [StateFlow] of [Set] of [String] property [InterestsViewModel.selectedPublications] which is
     * collected by [rememberTabContent] for the [TabContent] whose `section` is [Sections.Publications].
     */
    private val selectedPublications = MutableStateFlow(value = setOf<String>())

    /**
     * Returns a [Result.Success] whose [Result.Success.data] is our [List] of [InterestSection]
     * field [topics]. The private `refreshAll` method of [InterestsViewModel] calls this in an
     * [async] block to produce a [Deferred] of [Result] of [List] of [InterestSection] then calls
     * the [Deferred.await] method of that to initialze a [List] of [InterestSection] which it uses
     * to update its private [MutableStateFlow] of [InterestsUiState] field `_uiState` with a new
     * [InterestsUiState.topics] field whose read-only value is exposed by its [StateFlow] of
     * [InterestsUiState] property [InterestsViewModel.uiState] which is collected by
     * [rememberTabContent] for the [TabContent] whose `section` is [Sections.Topics].
     */
    override suspend fun getTopics(): Result<List<InterestSection>> {
        return Result.Success(data = topics)
    }

    /**
     * Returns a [Result.Success] whose [Result.Success.data] is our [List] of [String] field
     * [people]. The private `refreshAll` method of [InterestsViewModel] calls this in an [async]
     * block to produce a [Deferred] of [Result] of [List] of [String] then calls the [Deferred.await]
     * method of that to initialze a [List] of [String] which it uses to update its private
     * [MutableStateFlow] of [InterestsUiState] field `_uiState` with a new [InterestsUiState.people]
     * field whose read-only value is exposed by its [StateFlow] of [InterestsUiState] property
     * [InterestsViewModel.uiState] which is collected by [rememberTabContent] for the [TabContent]
     * whose `section` is [Sections.People].
     */
    override suspend fun getPeople(): Result<List<String>> {
        return Result.Success(data = people)
    }

    /**
     * Returns a [Result.Success] whose [Result.Success.data] is our [List] of [String] field
     * [publications]. The private `refreshAll` method of [InterestsViewModel] calls this in an
     * [async] block to produce a [Deferred] of [Result] of [List] of [String] then calls the
     * [Deferred.await] method of that to initialze a [List] of [String] which it uses to update
     * its private [MutableStateFlow] of [InterestsUiState] field `_uiState` with a new
     * [InterestsUiState.publications] field whose read-only value is exposed by its [StateFlow]
     * of [InterestsUiState] property [InterestsViewModel.uiState] which is collected by
     * [rememberTabContent] for the [TabContent] whose `section` is [Sections.Publications].
     */
    override suspend fun getPublications(): Result<List<String>> {
        return Result.Success(data = publications)
    }

    /**
     * Toggles the presence of its [TopicSelection] parameter [topic] in our [MutableStateFlow] of
     * set of [TopicSelection] field [selectedTopics]. We just call the [MutableStateFlow.update]
     * method of [selectedTopics] with its `function` lambda argument a call to our [Set.addOrRemove]
     * extension function with [selectedTopics] the receiver and its `element` argument our
     * [TopicSelection] parameter [topic].
     *
     * @param topic the [TopicSelection] whose presence in our [MutableStateFlow] of set of
     * [TopicSelection] field [selectedTopics] we wish to toggle.
     */
    override suspend fun toggleTopicSelection(topic: TopicSelection) {
        selectedTopics.update {
            it.addOrRemove(element = topic)
        }
    }

    /**
     * Toggles the presence of its [String] parameter [person] in our [MutableStateFlow] of
     * set of [String] field [selectedPeople]. We just call the [MutableStateFlow.update]
     * method of [selectedPeople] with its `function` lambda argument a call to our [Set.addOrRemove]
     * extension function with [selectedPeople] the receiver and its `element` argument our
     * [String] parameter [person].
     *
     * @param person the [String] whose presence in our [MutableStateFlow] of set of [String] field
     * [selectedPeople] we wish to toggle.
     */
    override suspend fun togglePersonSelected(person: String) {
        selectedPeople.update {
            it.addOrRemove(element = person)
        }
    }

    /**
     * Toggles the presence of its [String] parameter [publication] in our [MutableStateFlow] of
     * set of [String] field [selectedPublications]. We just call the [MutableStateFlow.update]
     * method of [selectedPeople] with its `function` lambda argument a call to our [Set.addOrRemove]
     * extension function with [selectedPublications] the receiver and its `element` argument our
     * [String] parameter [publication].
     *
     * @param publication the [String] whose presence in our [MutableStateFlow] of set of [String]
     * field [selectedPublications] we wish to toggle.
     */
    override suspend fun togglePublicationSelected(publication: String) {
        selectedPublications.update {
            it.addOrRemove(element = publication)
        }
    }

    /**
     * This is called to retrieve a read-only version of our [MutableStateFlow] of [Set] of
     * [TopicSelection] property [selectedTopics]. [InterestsViewModel] calls this for its [StateFlow]
     * of [Set] of [TopicSelection] property [InterestsViewModel.selectedTopics] using the [stateIn]
     * extension function of [Flow] to convert the cold [Flow] that we return to a hot [StateFlow]
     * that is started in the [viewModelScope] coroutine scope, sharing the most recently emitted
     * value from a single running instance of the upstream flow with multiple downstream subscribers.
     */
    override fun observeTopicsSelected(): Flow<Set<TopicSelection>> = selectedTopics

    /**
     * This is called to retrieve a read-only version of our [MutableStateFlow] of [Set] of [String]
     * property [selectedPeople]. [InterestsViewModel] calls this for its [StateFlow] of [Set] of
     * [String] property [InterestsViewModel.selectedPeople] using the [stateIn] extension function of
     * [Flow] to convert the cold [Flow] that we return to a hot [StateFlow] that is started in the
     * [viewModelScope] coroutine scope, sharing the most recently emitted value from a single running
     * instance of the upstream flow with multiple downstream subscribers.
     */
    override fun observePeopleSelected(): Flow<Set<String>> = selectedPeople

    /**
     * This is called to retrieve a read-only version of our [MutableStateFlow] of [Set] of [String]
     * property [selectedPublications]. [InterestsViewModel] calls this for its [StateFlow] of [Set] of
     * [String] property [InterestsViewModel.selectedPublications] using the [stateIn] extension
     * function of [Flow] to convert the cold [Flow] that we return to a hot [StateFlow] that is
     * started in the [viewModelScope] coroutine scope, sharing the most recently emitted value from
     * a single running instance of the upstream flow with multiple downstream subscribers.
     */
    override fun observePublicationSelected(): Flow<Set<String>> = selectedPublications
}
