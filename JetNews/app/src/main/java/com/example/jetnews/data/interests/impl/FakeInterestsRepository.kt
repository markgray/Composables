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

import com.example.jetnews.data.Result
import com.example.jetnews.data.interests.InterestSection
import com.example.jetnews.data.interests.InterestsRepository
import com.example.jetnews.data.interests.TopicSelection
import com.example.jetnews.ui.interests.InterestsViewModel
import com.example.jetnews.ui.interests.rememberTabContent
import com.example.jetnews.ui.interests.Sections
import com.example.jetnews.ui.interests.TabContent
import com.example.jetnews.utils.addOrRemove
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
     * method.
     */
    private val selectedPeople = MutableStateFlow(value = setOf<String>())
    private val selectedPublications = MutableStateFlow(value = setOf<String>())

    override suspend fun getTopics(): Result<List<InterestSection>> {
        return Result.Success(data = topics)
    }

    override suspend fun getPeople(): Result<List<String>> {
        return Result.Success(data = people)
    }

    override suspend fun getPublications(): Result<List<String>> {
        return Result.Success(data = publications)
    }

    override suspend fun toggleTopicSelection(topic: TopicSelection) {
        selectedTopics.update {
            it.addOrRemove(element = topic)
        }
    }

    override suspend fun togglePersonSelected(person: String) {
        selectedPeople.update {
            it.addOrRemove(element = person)
        }
    }

    override suspend fun togglePublicationSelected(publication: String) {
        selectedPublications.update {
            it.addOrRemove(element = publication)
        }
    }

    override fun observeTopicsSelected(): Flow<Set<TopicSelection>> = selectedTopics

    override fun observePeopleSelected(): Flow<Set<String>> = selectedPeople

    override fun observePublicationSelected(): Flow<Set<String>> = selectedPublications
}
