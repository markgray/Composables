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

import com.google.samples.apps.nowinandroid.core.data.model.RecentSearchQuery
import com.google.samples.apps.nowinandroid.core.data.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Test implementation of [RecentSearchRepository] that stores recent searches in memory.
 */
class TestRecentSearchRepository : RecentSearchRepository {

    /**
     * In-memory list of recent searches.
     */
    private val cachedRecentSearches: MutableList<RecentSearchQuery> = mutableListOf()

    /**
     * Returns a flow of the most recent search queries, sorted by date, up to the given limit.
     *
     * @param limit The maximum number of recent search queries to return.
     * @return A flow of a list of recent search queries.
     */
    override fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>> =
        flowOf(value = cachedRecentSearches.sortedByDescending { it.queriedDate }.take(n = limit))

    /**
     * Inserts or replaces a recent search query in the in-memory list.
     *
     * @param searchQuery The search query to insert or replace.
     */
    override suspend fun insertOrReplaceRecentSearch(searchQuery: String) {
        cachedRecentSearches.add(RecentSearchQuery(searchQuery))
    }

    /**
     * Clears all recent searches from the in-memory list.
     */
    override suspend fun clearRecentSearches(): Unit = cachedRecentSearches.clear()
}
