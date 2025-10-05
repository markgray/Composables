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

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.google.samples.apps.sunflower.api.UnsplashService

/**
 * Unsplash API page numbers start from 1, so this is the initial page number.
 */
private const val UNSPLASH_STARTING_PAGE_INDEX = 1

/**
 * A [PagingSource] that loads search results from the Unsplash API.
 * This class is responsible for fetching paginated data for a specific search query.
 *
 * @property service The [UnsplashService] instance used to make API calls.
 * @property query The search query to be used when fetching photos from Unsplash.
 */
class UnsplashPagingSource(
    private val service: UnsplashService,
    private val query: String
) : PagingSource<Int, UnsplashPhoto>() {

    /**
     * PagingSource.load() is a suspend function that is called by the Paging library to fetch
     * more data to be displayed in the UI. It takes a LoadParams object as a parameter, which
     * includes the key of the page to be loaded and the requested load size.
     *
     * This function returns a [LoadResult] object, which can be one of two types:
     *  - [LoadResult.Page], if the load was successful. This object contains the list of fetched
     *  data, as well as keys for the previous and next pages to be loaded.
     *  - [LoadResult.Error], if the load failed. This object contains the exception that caused
     *  the failure.
     *
     * In this implementation, the function fetches photos from the Unsplash API based on a query.
     * The 'page' number is determined by the 'key' in LoadParams, defaulting to the starting
     * page index if the key is null. If the API call is successful, it returns a [LoadResult.Page]
     * with the list of photos and keys for the previous and next pages. If the API call fails,
     * it catches the exception and returns a [LoadResult.Error].
     *
     * @param params The [LoadParams] object containing the index of the first page to be loaded in
     * its [LoadParams.key] and the requested load size in its [LoadParams.loadSize].
     * @return A [LoadResult] object that can be either a [LoadResult.Page] containing the [List]
     * of [UnsplashPhoto] objects from the [UnsplashSearchResponse.results] or a [LoadResult.Error]
     * containing the [Exception] that caused the failure.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val page: Int = params.key ?: UNSPLASH_STARTING_PAGE_INDEX
        return try {
            val response: UnsplashSearchResponse =
                service.searchPhotos(query = query, page = page, perPage = params.loadSize)
            val photos: List<UnsplashPhoto> = response.results
            LoadResult.Page(
                data = photos,
                prevKey = if (page == UNSPLASH_STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (page == response.totalPages) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(throwable = exception)
        }
    }

    /**
     * The refresh key is used for subsequent calls to [PagingSource.load] after the initial load.
     *
     * @param state [PagingState] of the currently fetched data, which includes the most recently
     * accessed position in the list via [PagingState.anchorPosition].
     * @return  we return the [Page.prevKey] of the closest page to the [PagingState.anchorPosition],
     * or `null` if there is no previous page.
     */
    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        return state.anchorPosition?.let { anchorPosition: Int ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(anchorPosition = anchorPosition)?.prevKey
        }
    }
}
