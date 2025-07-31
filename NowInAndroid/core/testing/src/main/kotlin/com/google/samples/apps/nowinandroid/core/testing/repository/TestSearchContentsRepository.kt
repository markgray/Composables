/*
 * Copyright 2023 The Android Open Source Project
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

import com.google.samples.apps.nowinandroid.core.data.repository.SearchContentsRepository
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.SearchResult
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

/**
 * Test implementation of [SearchContentsRepository] that allows adding [Topic]s and [NewsResource]s
 * to backing [MutableStateFlow]s, which are then searched through for the query.
 *
 * This allows easy testing of search functionality without requiring a full FTS-backed implementation.
 */
class TestSearchContentsRepository : SearchContentsRepository {

    /**
     * A backing prop that can be used to add and remove topics.
     */
    private val cachedTopics = MutableStateFlow(value = emptyList<Topic>())

    /**
     * A backing prop that can be used to add news resources to this repository for searching.
     */
    private val cachedNewsResources = MutableStateFlow(value = emptyList<NewsResource>())

    /**
     * Populate the fts tables for the search contents.
     *
     * This implementation of [SearchContentsRepository] does not use FTS, so this method does
     * nothing.
     */
    override suspend fun populateFtsData(): Unit = Unit

    /**
     * Gets a [Flow] of [SearchResult] that match the search query.
     *
     * In this implementation, the search query is matched against the topic name, short description,
     * and long description, and the search query is matched against the content of the news resources,
     * and the title of the news resources.
     *
     * @param searchQuery The search query.
     * @return A [Flow] of [SearchResult] whose topics match the search query, or whose [NewsResource]
     * match the search query.
     */
    override fun searchContents(searchQuery: String): Flow<SearchResult> =
        combine(
            flow = cachedTopics,
            flow2 = cachedNewsResources,
        ) { topics: List<Topic>, news: List<NewsResource> ->
            SearchResult(
                topics = topics.filter { topic: Topic ->
                    searchQuery in topic.name ||
                        searchQuery in topic.shortDescription ||
                        searchQuery in topic.longDescription
                },
                newsResources = news.filter { newsResource: NewsResource ->
                    searchQuery in newsResource.content || searchQuery in newsResource.title
                },
            )
        }

    /**
     * Gets the count of all searchable topics and news resources.
     *
     * In this implementation, the count is the sum of the number of topics and the number of news
     * resources.
     *
     * @return A [Flow] of [Int] that emits the count of searchable topics and news resources.
     */
    override fun getSearchContentsCount(): Flow<Int> =
        combine(
            flow = cachedTopics,
            flow2 = cachedNewsResources,
        ) { topics: List<Topic>, news: List<NewsResource> ->
            topics.size + news.size
        }

    /**
     * A test-only API to allow adding user-editable topics to this repository.
     *
     * @param topics The [List] of [Topic] to add.
     */
    @TestOnly
    fun addTopics(topics: List<Topic>): Unit = cachedTopics.update { it + topics }

    /**
     * A test-only API to allow adding news resources to this repository.
     *
     * @param newsResources The [List] of [NewsResource] to add.
     */
    @TestOnly
    fun addNewsResources(newsResources: List<NewsResource>): Unit =
        cachedNewsResources.update { it + newsResources }
}
