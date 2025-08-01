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
import com.google.samples.apps.nowinandroid.core.data.repository.NewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.NewsResourceQuery
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

/**
 * Test implementation of the [NewsRepository] that allows injecting specific news resources
 * for testing purposes. This class is useful for creating controlled test environments
 * where the behavior of the news data can be precisely managed.
 *
 * It uses a [MutableSharedFlow] to simulate a stream of news resources,
 * allowing tests to emit new lists of news resources and observe how the UI or other
 * components react to these changes.
 *
 * The `syncWith` method is overridden to always return `true`, simplifying synchronization
 * logic in tests where the actual synchronization process is not the focus of the test.
 */
class TestNewsRepository : NewsRepository {

    /**
     * The backing hot flow for the list of news resources for testing.
     */
    private val newsResourcesFlow: MutableSharedFlow<List<NewsResource>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * Retrieves a flow of [NewsResource] instances that match the given [NewsResourceQuery].
     *
     * This implementation filters the news resources based on the `filterTopicIds` and
     * `filterNewsIds` properties of the [NewsResourceQuery]. If `filterTopicIds` is provided,
     * it filters the news resources to include only those that have at least one topic whose ID
     * is present in the `filterTopicIds` set. If `filterNewsIds` is provided, it further
     * filters the news resources to include only those whose ID is present in the `filterNewsIds` set.
     *
     * @param query The query parameters to filter the news resources.
     * @return A flow emitting a list of [NewsResource] objects that satisfy the query.
     */
    override fun getNewsResources(query: NewsResourceQuery): Flow<List<NewsResource>> =
        newsResourcesFlow.map { newsResources: List<NewsResource> ->
            var result: List<NewsResource> = newsResources
            query.filterTopicIds?.let { filterTopicIds: Set<String> ->
                result = newsResources.filter { newsResource: NewsResource ->
                    newsResource.topics.map(transform = Topic::id)
                        .intersect(other = filterTopicIds).isNotEmpty()
                }
            }
            query.filterNewsIds?.let { filterNewsIds: Set<String> ->
                result = newsResources.filter { newsResource -> newsResource.id in filterNewsIds }
            }
            result
        }

    /**
     * A test-only API to allow controlling the list of news resources from tests.
     */
    fun sendNewsResources(newsResources: List<NewsResource>) {
        newsResourcesFlow.tryEmit(value = newsResources)
    }

    /**
     * Synchronizes the local database backing the repository with the network.
     * Returns if the sync was successful or not.
     *
     * @param synchronizer [Synchronizer] that will perform the sync.
     * @return `true` if the sync was successful or `false` if it failed, we always return `true`
     */
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = true
}
