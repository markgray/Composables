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

package com.google.samples.apps.nowinandroid.core.domain

import com.google.samples.apps.nowinandroid.core.data.repository.SearchContentsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.SearchResult
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.UserSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A use case that returns the searched contents matched with the search query.
 *
 * It combines the search results from the `SearchContentsRepository` with the user's data from
 * the `UserDataRepository` to provide a `UserSearchResult` that includes information about
 * whether each topic is followed and the bookmarked state of each news resource.
 *
 * @property searchContentsRepository the repository that provides the search results injected by
 * Hilt.
 * @property userDataRepository the repository that provides the user's data injected by Hilt.
 */
class GetSearchContentsUseCase @Inject constructor(
    private val searchContentsRepository: SearchContentsRepository,
    private val userDataRepository: UserDataRepository,
) {

    /**
     * We call the [SearchContentsRepository.searchContents] method of [SearchContentsRepository]
     * property [searchContentsRepository] with the [String] parameter [searchQuery] to get the
     * [Flow] of [SearchResult]s that match the search query. We then call its [mapToUserSearchResult]
     * extension function with its `userDataStream` argument the [Flow] of [UserData] returned by
     * the [UserDataRepository.userData] property of [UserDataRepository] property [userDataRepository]
     * to convert it to a [Flow] of [UserSearchResult]s.
     *
     * @param searchQuery - The search query.
     * @return A [Flow] of [UserSearchResult]s that match the search query.
     */
    operator fun invoke(
        searchQuery: String,
    ): Flow<UserSearchResult> =
        searchContentsRepository.searchContents(searchQuery = searchQuery)
            .mapToUserSearchResult(userDataStream = userDataRepository.userData)
}

/**
 * Maps a [Flow] of [SearchResult] to a [Flow] of [UserSearchResult] by combining it with a
 * [Flow] of [UserData].
 *
 * This function is used to transform the raw search results into a user-specific view that
 * includes information about followed topics and bookmarked news resources.
 *
 * We call the [Flow.combine] extension function with its `flow` argument our [Flow] of [UserData]
 * parameter [userDataStream] to combine it with our [Flow] of [SearchResult] recciver capturing
 * the [SearchResult] in variable `searchResult` and the [UserData] in variable `userData`. Then we
 * emit a [UserSearchResult] with its `topics` argument the list of [FollowableTopic]s created bu
 * using the [Iterable.map] extension function to loop over each [Topic] in the [SearchResult.topics]
 * capturing the [Topic] in variable `topic` which we use as the `topic` argument of the
 * [FollowableTopic] with its `isFollowed` argument `true` if the [Topic.id] of the [Topic] is in
 * the [Set] pf [String] property [UserData.followedTopics] of `userData`. The `newsResources`
 * argument is the list of [UserNewsResource]s created by using the [Iterable.map] extension function
 * of the [List] of [NewsResource] property [SearchResult.newsResources] of `searchResult` to loop
 * over its contents capturing the [NewsResource] in variable `news` which we use as the `newsResource`
 * argument of the [UserNewsResource] with its `userData` argument `userData`.
 *
 * @param userDataStream A [Flow] of [UserData] that provides the user's data.
 * @return A [Flow] of [UserSearchResult] that includes user-specific information.
 */
private fun Flow<SearchResult>.mapToUserSearchResult(userDataStream: Flow<UserData>): Flow<UserSearchResult> =
    combine(flow = userDataStream) { searchResult: SearchResult, userData: UserData ->
        UserSearchResult(
            topics = searchResult.topics.map { topic: Topic ->
                FollowableTopic(
                    topic = topic,
                    isFollowed = topic.id in userData.followedTopics,
                )
            },
            newsResources = searchResult.newsResources.map { news: NewsResource ->
                UserNewsResource(
                    newsResource = news,
                    userData = userData,
                )
            },
        )
    }
