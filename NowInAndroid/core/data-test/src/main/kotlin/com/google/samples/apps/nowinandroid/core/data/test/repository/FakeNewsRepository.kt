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
import com.google.samples.apps.nowinandroid.core.data.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.data.repository.NewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.NewsResourceQuery
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.IO
import com.google.samples.apps.nowinandroid.core.network.demo.DemoNiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Fake implementation of the [NewsRepository] that retrieves the news resources from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 *
 * @property ioDispatcher a coroutine dispatcher that is optimized for I/O operations injected by Hilt.
 * @property datasource a [DemoNiaNetworkDataSource] injected by Hilt.
 */
class FakeNewsRepository @Inject constructor(
    @param:Dispatcher(niaDispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: DemoNiaNetworkDataSource,
) : NewsRepository {

    /**
     * Returns available news resources that match the specified [query].
     *
     * We start by calling [flow] to create a [Flow] of [List] of [NewsResource], and in its
     * [FlowCollector] of [List] of [NewsResource] `block` suspend lambda argument we:
     *  - initialize our [List] of [NetworkNewsResource] variable `newsResources` with the value
     *  returned by the [DemoNiaNetworkDataSource.getNewsResources] method of our
     *  [DemoNiaNetworkDataSource]. property [datasource].
     *  - initialize our [List] of [NetworkTopic] variable `topics` with the value returned by
     *  the [DemoNiaNetworkDataSource.getTopics] method of our [DemoNiaNetworkDataSource]. property
     *  [datasource].
     *  - we call the [FlowCollector.emit] method of our receiver with the `value` returned by
     *  feeding the [List] of [NetworkNewsResource] variable `newsResources` to its [Iterable.filter]
     *  extension function. In the `predicate` lambda argument of [Iterable.filter] we capture the
     *  [NetworkNewsResource] passed the lambda in variable `networkNewsResource` and filter out any
     *  `netWorkNewsResource` that do not match the [NewsResourceQuery] parameter [query]. We feed
     *  the resulting [List] of [NetworkNewsResource] to the [Iterable.map] extension function to
     *  convert each [NetworkNewsResource] to a [NewsResource] using its
     *  [NetworkNewsResource.asExternalModel] method.
     *
     * The resulting [Flow] of [List] of [NewsResource] is then fed to the [Flow.flowOn] operator
     * to change the `context` that the [Flow] is emitted on to the [CoroutineDispatcher] provided
     * by our [CoroutineDispatcher] property [ioDispatcher].
     *
     * @param query - A [NewsResourceQuery] query that specifies what news resources to provide.
     * @return A Flow of a list of news resources that match [query].
     */
    override fun getNewsResources(
        query: NewsResourceQuery,
    ): Flow<List<NewsResource>> =
        flow {
            val newsResources: List<NetworkNewsResource> = datasource.getNewsResources()
            val topics: List<NetworkTopic> = datasource.getTopics()

            emit(
                value = newsResources
                    .filter { networkNewsResource: NetworkNewsResource ->
                        // Filter out any news resources which don't match the current query.
                        // If no query parameters (filterTopicIds or filterNewsIds) are specified
                        // then the news resource is returned.
                        listOfNotNull(
                            true,
                            query.filterNewsIds?.contains(element = networkNewsResource.id),
                            query.filterTopicIds?.let { filterTopicIds: Set<String> ->
                                networkNewsResource.topics.intersect(other = filterTopicIds)
                                    .isNotEmpty()
                            },
                        )
                            .all(predicate = true::equals)
                    }
                    .map { it.asExternalModel(topics = topics) },
            )
        }.flowOn(context = ioDispatcher)

    /**
     * A fake implementation of the [NewsRepository.syncWith] method which just returns `true`
     * without doing anything.
     *
     * @param synchronizer a [Synchronizer] that we ignore.
     * @return `true` always.
     */
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = true
}
