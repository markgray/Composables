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

package com.example.jetnews.data.interests

import com.example.jetnews.utils.addOrRemove
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * [List]'s of `topics` indexed by `sections`. It is the type used for the [InterestsRepository.topics]
 * field, and the [TopicsMap] `typealias` is used for the `topics` parameter of `InterestsScreen`
 * (see the file ui/interests/InterestsScreen.kt)
 */
typealias TopicsMap = Map<String, List<String>>

/**
 * Implementation of InterestRepository that returns a hardcoded list of
 * topics, people and publications synchronously.
 */
class InterestsRepository {

    /**
     * List]'s of `topics` indexed by `sections`. There are 3 sections: "Android", "Programming",
     * and "Technology". "Android" holds the `topics` "Jetpack Compose", "Kotlin", and "Jetpack";
     * "Programming" holds the `topics` "Kotlin", "Declarative UIs", and "Java"; and "Technology"
     * holds the `topics` "Pixel", and "Google".
     */
    val topics: Map<String, List<String>> by lazy {
        mapOf(
            "Android" to listOf("Jetpack Compose", "Kotlin", "Jetpack"),
            "Programming" to listOf("Kotlin", "Declarative UIs", "Java"),
            "Technology" to listOf("Pixel", "Google")
        )
    }

    /**
     * for now, keep the selections in memory
     */
    private val selectedTopics = MutableStateFlow(setOf<TopicSelection>())

    /**
     * Used to make suspend functions that read and update state safe to call from any thread
     */
    private val mutex = Mutex()

    /**
     * Toggle between selected and unselected
     */
    suspend fun toggleTopicSelection(topic: TopicSelection) {
        mutex.withLock {
            val set = selectedTopics.value.toMutableSet()
            set.addOrRemove(topic)
            selectedTopics.value = set
        }
    }

    /**
     * Currently selected topics
     */
    fun observeTopicsSelected(): Flow<Set<TopicSelection>> = selectedTopics
}

/**
 * Data class holding a selection of [section] and [topic] from the [InterestsRepository.topics]
 * field.
 *
 * @param section the section [String] used to fetch the [List] of topics.
 * @param topic the topic string selected from the [section] list of topics
 */
data class TopicSelection(val section: String, val topic: String)
