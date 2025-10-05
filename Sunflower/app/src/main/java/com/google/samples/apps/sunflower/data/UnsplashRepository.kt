/*
 * Copyright 2020 Google LLC
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

@file:Suppress("unused")

package com.google.samples.apps.sunflower.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.samples.apps.sunflower.api.UnsplashService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository class that works with Unsplash API. The @[Inject] annotation identifies our constructor
 * as an injectable constructor to Hilt and Hilt generates a `UnsplashRepository_Factory` java class
 * from this file which it will use when a [UnsplashRepository] class injection is requested.
 *
 * @property service The [UnsplashService] instance used to make API calls, injected by Hilt.
 */
class UnsplashRepository @Inject constructor(private val service: UnsplashService) {

    /**
     * Let's the user search for photos on Unsplash. We construct a [Pager] object with its `config`
     * argument a [PagingConfig] object which defines how the data is loaded, `enablePlaceholders`
     * is set to `false` to disable placeholders, and `pageSize` is set to [NETWORK_PAGE_SIZE] and
     * the `pagingSourceFactory` argument is set to a lambda which returns a new instance of
     * [UnsplashPagingSource] whose `service` argument is set to [service] and `query` argument is
     * set to [query]. Then we return the [Flow] of [PagingData] of [UnsplashPhoto] that the
     * [Pager.flow] property returns.
     *
     * @param query The search query. This will be sent to the Unsplash API.
     * @return a [Flow] of [PagingData] of [UnsplashPhoto] that contains the search results.
     */
    fun getSearchResultStream(query: String): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = { UnsplashPagingSource(service = service, query = query) }
        ).flow
    }

    companion object {
        /**
         * The number of items to request from the Unsplash API in a single network request.
         * This is used to configure the page size in the [PagingConfig] for the [Pager].
         */
        private const val NETWORK_PAGE_SIZE = 25
    }
}
