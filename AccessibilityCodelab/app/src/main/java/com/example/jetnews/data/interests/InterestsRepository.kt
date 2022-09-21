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

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
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
     * This [MutableStateFlow] will emit its value whenever it changes. [observeTopicsSelected]
     * returns a reference to this private field as a [Flow] which the `InterestsScreen` Composable
     * collects using the `collectAsState` method which Collects values from the Flow and represents
     * its latest value via State. Every time there would be new value posted into the Flow the
     * returned State will be updated causing recomposition of every State.value usage. (See the
     * file ui/interests/InterestsScreen.kt). It is updated by the [toggleTopicSelection] method
     * which is called from the `onTopicSelect` lambda which is called from the `onToggle` lambda
     * argument of `TopicItem` which is used as the `onValueChange` argument of the `Modifier.toggleable`
     * [Modifier] of the root [Row] Composable of `TopicItem` (See the file ui/interests/InterestsScreen.kt).
     */
    private val selectedTopics = MutableStateFlow(setOf<TopicSelection>())

    /**
     * Used to make suspend functions that read and update state safe to call from any thread. This
     * is used by the [toggleTopicSelection] method to protect its modification of [selectedTopics].
     */
    private val mutex = Mutex()

    /**
     * Toggle the [TopicSelection] parameter [topic] between selected and unselected. It does this
     * by using the [Mutex.withLock] method of [mutex] to execute a lambda under this mutex's lock
     * which retrieves the [MutableSet] of [TopicSelection] from the [MutableStateFlow.value] of
     * [selectedTopics] to initialize the variable `val set`, then uses the [MutableSet.addOrRemove]
     * extension function to add [topic] to the set if it is not already there or remove it if it is
     * there. Finally it sets the value of [selectedTopics] to `set` (this will cause [selectedTopics]
     * to emit its new value to anyone collecting it).
     */
    suspend fun toggleTopicSelection(topic: TopicSelection) {
        mutex.withLock {
            val set: MutableSet<TopicSelection> = selectedTopics.value.toMutableSet()
            set.addOrRemove(topic)
            selectedTopics.value = set
        }
    }

    /**
     * Read-only access to our Currently selected topics in the [MutableStateFlow] of [Set] of
     * [TopicSelection] field [selectedTopics]. This [Flow] is collected in the `InterestsScreen`
     * Composable using the `collectAsState` method which Collects values from the [Flow] and
     * represents its latest value via State. Every time a new value is posted into the [Flow] the
     * returned State will be updated causing recomposition of every State.value usage.
     * (See the file ui/interests/InterestsScreen.kt).
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
