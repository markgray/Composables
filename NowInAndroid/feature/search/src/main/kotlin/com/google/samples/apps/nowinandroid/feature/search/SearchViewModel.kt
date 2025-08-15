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

package com.google.samples.apps.nowinandroid.feature.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent.Param
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.data.model.RecentSearchQuery
import com.google.samples.apps.nowinandroid.core.data.repository.RecentSearchRepository
import com.google.samples.apps.nowinandroid.core.data.repository.SearchContentsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.domain.GetRecentSearchQueriesUseCase
import com.google.samples.apps.nowinandroid.core.domain.GetSearchContentsUseCase
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.UserSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the search screen.
 *
 * @param getSearchContentsUseCase The use case for searching contents, Injected by Hilt.
 * @param recentSearchQueriesUseCase The use case for getting recent search queries, Injected by Hilt.
 * @param searchContentsRepository The repository for searching contents, Injected by Hilt.
 * @param recentSearchRepository The repository for recent searches, Injected by Hilt.
 * @param userDataRepository The repository for user data, Injected by Hilt.
 * @param savedStateHandle The saved state handle.
 * @param analyticsHelper The analytics helper, Injected by Hilt.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    getSearchContentsUseCase: GetSearchContentsUseCase,
    recentSearchQueriesUseCase: GetRecentSearchQueriesUseCase,
    private val searchContentsRepository: SearchContentsRepository,
    private val recentSearchRepository: RecentSearchRepository,
    private val userDataRepository: UserDataRepository,
    private val savedStateHandle: SavedStateHandle,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    /**
     * The current search query, saved in our [SavedStateHandle] under the key [SEARCH_QUERY].
     */
    val searchQuery: StateFlow<String> =
        savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    /**
     * The state of the search result.
     *
     * This flow emits the search result based on the query entered by the user.
     *
     * It checks for the following conditions:
     *  1. If the total number of entities in the FTS table is less than [SEARCH_MIN_FTS_ENTITY_COUNT],
     *  it emits [SearchResultUiState.SearchNotReady].
     *  2. If the search query is blank or less than [SEARCH_QUERY_MIN_LENGTH],
     *  it emits [SearchResultUiState.EmptyQuery].
     *  3. Otherwise, it emits a [SearchResultUiState.Success] with the search results.
     *
     * If an exception is thrown during the search, it emits [SearchResultUiState.LoadFailed].
     *
     * We use the [SearchContentsRepository.getSearchContentsCount] method of our
     * [SearchContentsRepository] property [searchContentsRepository] to retrieve the [Flow]
     * of [Int] of the count of search contents stored in the database, and use its
     * [Flow.flatMapLatest] method capturing the latest value in [Int] variable `totalCount`.
     * If `totalCount` is less than [SEARCH_MIN_FTS_ENTITY_COUNT], we emit a [Flow] of
     * [SearchResultUiState.SearchNotReady]. Otherwise we use the [Flow.flatMapLatest] method of
     * our [StateFlow] of [String] property [searchQuery] capturing the latest value in [String]
     * variable `query`. If `query` is blank or less than [SEARCH_QUERY_MIN_LENGTH], we emit
     * a [Flow] of [SearchResultUiState.EmptyQuery]. Otherwise we use the [GetSearchContentsUseCase]
     * property [getSearchContentsUseCase] to retrieve a [Flow] of [UserSearchResult] for the
     * `searchQuery` arguement `query` and use its [Flow.map] method, capturing the latest
     * [UserSearchResult] in variable `data` to emit a [Flow] of [SearchResultUiState.Success]
     * whose `topics` argument is the list of [FollowableTopic] in the [UserSearchResult.topics]
     * of `data` and whose `newsResources` argument is the list of [UserNewsResource] in the
     * [UserSearchResult.newsResources] of `data`. If an exception is thrown during the search,
     * we emit a [Flow] of [SearchResultUiState.LoadFailed]. The resulting [Flow] of
     * [SearchResultUiState] is fed to the [Flow.stateIn] to create a [StateFlow] of
     * [SearchResultUiState] with a `scope` of [viewModelScope], a `started` of
     * [SharingStarted.WhileSubscribed] with a `stopTimeoutMillis` of 5000, and an initial value
     * of [SearchResultUiState.Loading].
     */
    val searchResultUiState: StateFlow<SearchResultUiState> =
        searchContentsRepository.getSearchContentsCount()
            .flatMapLatest { totalCount: Int ->
                if (totalCount < SEARCH_MIN_FTS_ENTITY_COUNT) {
                    flowOf(value = SearchResultUiState.SearchNotReady)
                } else {
                    searchQuery.flatMapLatest { query: String ->
                        if (query.trim().length < SEARCH_QUERY_MIN_LENGTH) {
                            flowOf(value = SearchResultUiState.EmptyQuery)
                        } else {
                            getSearchContentsUseCase(searchQuery = query)
                                // Not using .asResult() here, because it emits Loading state every
                                // time the user types a letter in the search box, which flickers the screen.
                                .map<UserSearchResult, SearchResultUiState> { data: UserSearchResult ->
                                    SearchResultUiState.Success(
                                        topics = data.topics,
                                        newsResources = data.newsResources,
                                    )
                                }
                                .catch { emit(value = SearchResultUiState.LoadFailed) }
                        }
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = SearchResultUiState.Loading,
            )

    /**
     * The state of the recent search queries. This [StateFlow] emits the list of recent search
     * queries. It is started with an initial value of [RecentSearchQueriesUiState.Loading].
     *
     * We use the [GetRecentSearchQueriesUseCase] property [recentSearchQueriesUseCase] to retrieve
     * the [Flow] of [List] of [RecentSearchQuery], and use its [Flow.map] method to transform if
     * to a [Flow] of [RecentSearchQueriesUiState.Success] and feed that to the [Flow.stateIn] to
     * create a [StateFlow] of [RecentSearchQueriesUiState] with a `scope` of [viewModelScope],
     * a `started` of [SharingStarted.WhileSubscribed] with a `stopTimeoutMillis` of 5000, and
     * an initial value of [RecentSearchQueriesUiState.Loading].
     */
    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        recentSearchQueriesUseCase()
            .map(transform = RecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = RecentSearchQueriesUiState.Loading,
            )

    /**
     * Called when the search query is changed by the user. We just save the new search [query] in
     * our [SavedStateHandle] property [savedStateHandle] under the key [SEARCH_QUERY].
     *
     * @param query The new search query.
     */
    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    /**
     * Called when the search action is explicitly triggered by the user. For example, when the
     * search icon is tapped in the IME or when the enter key is pressed in the search text field.
     * The search results are displayed on the fly as the user types, but to explicitly save the
     * search query in the search text field, this method is called.
     *
     * If our [String] parameter [query] is blank, we return. Otherwise we use [viewModelScope] to
     * launch a coroutine in which we use the [RecentSearchRepository.insertOrReplaceRecentSearch]
     * method of our [RecentSearchRepository] property [recentSearchRepository] to insert or replace
     * the `searchQuery` argument [query] in the database.
     *
     * Finally we call the [AnalyticsHelper.logEventSearchTriggered] method of our [AnalyticsHelper]
     * property [analyticsHelper] to log an event with the search [query] as its `query` argument.
     *
     * @param query The search query.
     */
    fun onSearchTriggered(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            recentSearchRepository.insertOrReplaceRecentSearch(searchQuery = query)
        }
        analyticsHelper.logEventSearchTriggered(query = query)
    }

    /**
     * Clears the recent search queries by launching a coroutine in the [viewModelScope] to call the
     * [RecentSearchRepository.clearRecentSearches] method of our [RecentSearchRepository] property
     * [recentSearchRepository].
     */
    fun clearRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearRecentSearches()
        }
    }

    /**
     * Sets the bookmarked state of a news resource. We use [viewModelScope] to launch a coroutine
     * in which we call the [UserDataRepository.setNewsResourceBookmarked] method of our
     * [UserDataRepository] property [userDataRepository] with its `newsResourceId` argument
     * our [String] parameter [newsResourceId] and its `bookmarked` argument our [Boolean]
     * parameter [isChecked].
     *
     * @param newsResourceId The ID of the news resource.
     * @param isChecked `true` to bookmark the resource, `false` to unbookmark it.
     */
    fun setNewsResourceBookmarked(newsResourceId: String, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceBookmarked(
                newsResourceId = newsResourceId,
                bookmarked = isChecked,
            )
        }
    }

    /**
     * Updates the followed state for a topic. We use [viewModelScope] to launch a coroutine in
     * which we call the [UserDataRepository.setTopicIdFollowed] method of our
     * [UserDataRepository] property [userDataRepository] with its `followedTopicId` argument
     * our [String] parameter [followedTopicId] and its `followed` argument our [Boolean]
     * parameter [followed].
     *
     * @param followedTopicId The ID of the topic to update.
     * @param followed `true` to follow the topic, `false` to unfollow it.
     */
    fun followTopic(followedTopicId: String, followed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(
                followedTopicId = followedTopicId,
                followed = followed,
            )
        }
    }

    /**
     * Sets the viewed state of a news resource. We use [viewModelScope] to launch a coroutine in
     * which we call the [UserDataRepository.setNewsResourceViewed] method of our
     * [UserDataRepository] property [userDataRepository] with its `newsResourceId` argument
     * our [String] parameter [newsResourceId] and its `viewed` argument our [Boolean]
     * parameter [viewed].
     *
     * @param newsResourceId The ID of the news resource.
     * @param viewed `true` if the news resource is viewed, `false` otherwise.
     */
    fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(
                newsResourceId = newsResourceId,
                viewed = viewed,
            )
        }
    }
}

/**
 * A helper function to log the search triggered event.
 *
 * This function creates an [AnalyticsEvent] with the `type` [SEARCH_QUERY] and an extra parameter
 * containing our [String] parameter [query] stored under the key [SEARCH_QUERY]. It then calls the
 * [AnalyticsHelper.logEvent] method of its receiver to log the event.
 *
 * @param query The search query.
 */
private fun AnalyticsHelper.logEventSearchTriggered(query: String) =
    logEvent(
        event = AnalyticsEvent(
            type = SEARCH_QUERY,
            extras = listOf(element = Param(key = SEARCH_QUERY, value = query)),
        ),
    )

/**
 * Minimum length where search query is considered as [SearchResultUiState.EmptyQuery]
 */
private const val SEARCH_QUERY_MIN_LENGTH = 2

/**
 * Minimum number of the fts table's entity count where it's considered as search is not ready
 */
private const val SEARCH_MIN_FTS_ENTITY_COUNT = 1

/**
 * Key for the search query in our [SavedStateHandle]
 */
private const val SEARCH_QUERY = "searchQuery"
