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

package com.google.samples.apps.nowinandroid.core.data.test.repository

import com.google.samples.apps.nowinandroid.core.data.model.RecentSearchQuery
import com.google.samples.apps.nowinandroid.core.data.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Fake implementation of the [RecentSearchRepository]
 */
internal class FakeRecentSearchRepository @Inject constructor() : RecentSearchRepository {
    /**
     * A fake implementation of the [RecentSearchRepository.insertOrReplaceRecentSearch] method
     * that does nothing.
     *
     * @param searchQuery The search query to be inserted or replaced.
     */
    override suspend fun insertOrReplaceRecentSearch(searchQuery: String) = Unit

    /**
     * Returns the recent search queries, up to the specified [limit].
     * In this fake implementation, it always returns an empty list.
     *
     * @param limit The maximum number of recent search queries to return.
     * @return A flow that emits a list of recent search queries.
     */
    override fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>> =
        flowOf(value = emptyList())

    /**
     * Clears all recent searches.
     * In this fake implementation, this function does nothing.
     */
    override suspend fun clearRecentSearches() = Unit
}
