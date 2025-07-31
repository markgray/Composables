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

package com.google.samples.apps.nowinandroid.core.testing.repository

import com.google.samples.apps.nowinandroid.core.data.Synchronizer
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

/**
 * Test implementation of [TopicsRepository]
 */
class TestTopicsRepository : TopicsRepository {
    /**
     * The backing hot flow for the [List] of [Topic] for testing. A [MutableSharedFlow] is used
     * to simulate a stream of data that can be controlled from tests.
     */
    private val topicsFlow: MutableSharedFlow<List<Topic>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * Returns a hot flow of the list of topics. Provides public read-only access to the
     * [topicsFlow] backing property.
     */
    override fun getTopics(): Flow<List<Topic>> = topicsFlow

    /**
     * Gets the Topic with the given ID.
     * This method relies on the [topicsFlow] to be populated.
     *
     * @param id the [Topic.id] of the topic to get.
     */
    override fun getTopic(id: String): Flow<Topic> =
        topicsFlow.map { topics: List<Topic> -> topics.find { topic: Topic -> topic.id == id }!! }

    /**
     * A test-only API to allow controlling the list of topics from tests.
     *
     * @param topics the new [List] of [Topic].
     */
    fun sendTopics(topics: List<Topic>) {
        topicsFlow.tryEmit(value = topics)
    }

    /**
     * A test-only implementation of the [TopicsRepository.syncWith], a [Synchronizer]
     * that always returns true.
     *
     * @param synchronizer the [Synchronizer] that will perform the sync.
     * @return Always `true`.
     */
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = true
}
