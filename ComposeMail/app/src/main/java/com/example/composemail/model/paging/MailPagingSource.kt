/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.composemail.model.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composemail.model.data.MailInfoPeek
import com.example.composemail.model.repo.MailRepository

private const val PROVIDE_INITIAL_PLACEHOLDER = false

/**
 * Paging source for loading email conversations.
 *
 * This class is responsible for loading pages of [MailInfoPeek] items from the [MailRepository].
 * It uses an integer as the key for pages.
 *
 * @param mailRepo The repository to fetch mail conversations from.
 */
class MailPagingSource(private val mailRepo: MailRepository) : PagingSource<Int, MailInfoPeek>() {
    /**
     * The refresh key is used for subsequent calls to [load] after the initial load.
     *
     * This implementation is not needed as the initial load is started with `null` and
     * the subsequent loads are offset from that. If the initial load was started with a specific
     * key, this function would be used to later refresh the list starting from that key.
     *
     * For more information, see [PagingSource.getRefreshKey].
     *
     * @param state The current paging state.
     * @return The refresh key for the initial load.
     */
    override fun getRefreshKey(state: PagingState<Int, MailInfoPeek>): Int {
        return 0
    }

    /**
     * Loads a page of data from the [MailRepository].
     *
     * This function is called by the Paging library to fetch a chunk of data.
     * The [params] object contains the key for the page to load and the requested load size.
     *
     * For the initial load, `params.key` is `null`. In this case, we start loading from page 0.
     * Optionally, if `PROVIDE_INITIAL_PLACEHOLDER` is true, an empty list with a placeholder
     * is returned for the initial load to enable a shimmer-like UI.
     *
     * For subsequent loads, it fetches the next set of conversations from the repository
     * and returns a [LoadResult.Page]. The `prevKey` and `nextKey` are calculated to
     * enable bi-directional paging.
     *
     * @param params Parameters for the load request, including the key for the page to be loaded
     * and the number of items to load.
     * @return A [LoadResult] object, which is either a [LoadResult.Page] on success or a
     * [LoadResult.Error] on failure.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MailInfoPeek> {
        val nextPage = params.key ?: 0

        @Suppress("KotlinConstantConditions", "SimplifyBooleanWithConstants")
        if (PROVIDE_INITIAL_PLACEHOLDER && params.key == null) {
            // You can provide initial placeholders by returning an empty List for the initial request.
            // Note that doing this consumes the request that corresponds to `initialLoadSize` in
            // PagingConfig().
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = 0,
                // Provide a single placeholder item for initial request, alternatively, use
                // params.loadSize to have a placeholder for each item which is typically better for
                // shimmer-like placeholders.
                itemsAfter = 1,
            )
        }
        val nextMails = mailRepo.getNextSetOfConversations(params.loadSize)
        return LoadResult.Page(
            data = nextMails.conversations,
            prevKey = if (nextPage == 0) null else nextMails.page - 1,
            // In this case, we assume infinite amount of pages
            nextKey = nextMails.page + 1,
            // An additional item that will work as a loading placeholder
            // while the next page is produced
            itemsAfter = 1
        )
    }
}