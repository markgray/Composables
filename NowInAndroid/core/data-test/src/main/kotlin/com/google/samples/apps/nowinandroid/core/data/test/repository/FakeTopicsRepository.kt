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

package com.google.samples.apps.nowinandroid.core.data.test.repository

import com.google.samples.apps.nowinandroid.core.data.Synchronizer
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.IO
import com.google.samples.apps.nowinandroid.core.network.demo.DemoNiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Fake implementation of the [TopicsRepository] that retrieves the topics from a JSON String, and
 * uses a local DataStore instance to save and retrieve followed topic ids.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 *
 * @property ioDispatcher a coroutine dispatcher that is used for running blocking code like
 * suspend function calls injected by HILT.
 * @property datasource the instance of [DemoNiaNetworkDataSource] that will be used to retrieve the
 * fake data, injected by HILT.
 */
internal class FakeTopicsRepository @Inject constructor(
    @param:Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: DemoNiaNetworkDataSource,
) : TopicsRepository {
    /**
     * Returns a [Flow] of [Topic]s from the [datasource].
     *
     * The [Flow] emits a list of [Topic]s every time the list of topics in the [datasource]
     * changes.
     *
     * We call the [flow] method to create a [Flow] of [List] of [Topic]s, and in its
     * [FlowCollector] of [List] of [Topic] `block` suspend lambda argument we call the
     * [FlowCollector.emit] method with its `value` argument the result of feeding the
     * [List] of [NetworkTopic] returned by the [DemoNiaNetworkDataSource.getTopics] method of our
     * [DemoNiaNetworkDataSource] property [datasource] to its [Iterable.map] extension function
     * which converts each [NetworkTopic] to a [Topic] producing a [List] of [Topic]s. The resulting
     * [Flow] of [List] of [Topic]s is then fed to the [Flow.flowOn] operator to change the
     * `context` that the [Flow] is emitted on to the [CoroutineDispatcher] provided by our
     * [CoroutineDispatcher] property [ioDispatcher].
     *
     * @return a [Flow] of [Topic]s.
     */
    override fun getTopics(): Flow<List<Topic>> = flow {
        emit(
            value = datasource.getTopics().map {
                Topic(
                    id = it.id,
                    name = it.name,
                    shortDescription = it.shortDescription,
                    longDescription = it.longDescription,
                    url = it.url,
                    imageUrl = it.imageUrl,
                )
            },
        )
    }.flowOn(context = ioDispatcher)

    /**
     * Returns the [Topic] with the given [id] from the [datasource].
     *
     * We first call the [getTopics] method to get a [Flow] of [List] of [Topic]s. We then call its
     * [Flow.map] extension function to transform the [List] of [Topic]s into a [Flow] of a single
     * [Topic] whose [Topic.id] matches the [String] parameter [id].
     *
     * @param id the id of the [Topic] to retrieve.
     * @return the [Topic] with the given [id].
     */
    override fun getTopic(id: String): Flow<Topic> = getTopics()
        .map { it.first { topic: Topic -> topic.id == id } }

    /**
     * Synchronizes the local database backing the repository with the network.
     * This method is a no-op in this fake implementation, and always returns `true`.
     *
     * @param synchronizer [Synchronizer] that will perform the sync.
     * @return `true` if the sync was successful or `false` if it failed. In this fake
     * implementation it always returns `true`.
     */
    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
