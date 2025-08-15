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
import com.google.samples.apps.nowinandroid.core.analytics.NoOpAnalyticsHelper
import com.google.samples.apps.nowinandroid.core.data.model.RecentSearchQuery
import com.google.samples.apps.nowinandroid.core.data.repository.RecentSearchRepository
import com.google.samples.apps.nowinandroid.core.data.repository.SearchContentsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.domain.GetRecentSearchQueriesUseCase
import com.google.samples.apps.nowinandroid.core.domain.GetSearchContentsUseCase
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.UserSearchResult
import com.google.samples.apps.nowinandroid.core.testing.data.newsResourcesTestData
import com.google.samples.apps.nowinandroid.core.testing.data.topicsTestData
import com.google.samples.apps.nowinandroid.core.testing.repository.TestRecentSearchRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestSearchContentsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.emptyUserData
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import com.google.samples.apps.nowinandroid.feature.search.RecentSearchQueriesUiState.Success
import com.google.samples.apps.nowinandroid.feature.search.SearchResultUiState.EmptyQuery
import com.google.samples.apps.nowinandroid.feature.search.SearchResultUiState.Loading
import com.google.samples.apps.nowinandroid.feature.search.SearchResultUiState.SearchNotReady
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * Unit tests for [SearchViewModel].
 *
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class SearchViewModelTest {

    /**
     * A JUnit rule that overrides the Main dispatcher to ensure that tasks are run synchronously
     * for testing. This is crucial for tests involving coroutines to behave predictably.
     */
    @get:Rule
    val dispatcherRule: MainDispatcherRule = MainDispatcherRule()

    /**
     * A test implementation of [UserDataRepository] that allows for setting specific user data
     * for testing purposes.
     */
    private val userDataRepository = TestUserDataRepository()

    /**
     * A test implementation of [SearchContentsRepository] that allows for adding specific
     * news resources and topics for testing purposes.
     */
    private val searchContentsRepository = TestSearchContentsRepository()

    /**
     * A use case that returns the searched contents matched with the search query.
     * It combines the search results from the [TestSearchContentsRepository] property
     * [searchContentsRepository] with the user's data from the [TestUserDataRepository]
     * property [userDataRepository] to provide a [UserSearchResult] that includes
     * information about whether each topic is followed and the bookmarked state of each
     * [UserNewsResource].
     */
    private val getSearchContentsUseCase = GetSearchContentsUseCase(
        searchContentsRepository = searchContentsRepository,
        userDataRepository = userDataRepository,
    )

    /**
     * A test implementation of [RecentSearchRepository] that allows for adding specific
     * recent search queries for testing purposes.
     */
    private val recentSearchRepository = TestRecentSearchRepository()

    /**
     * A use case that returns the most recent search queries from the [TestRecentSearchRepository]
     * property [recentSearchRepository].
     */
    private val getRecentQueryUseCase =
        GetRecentSearchQueriesUseCase(recentSearchRepository = recentSearchRepository)

    /**
     * The instance of [SearchViewModel] being tested. This is initialized in the [setup] method
     * before each test.
     */
    private lateinit var viewModel: SearchViewModel

    /**
     * Sets up the test environment @[Before] each test.
     *
     * This method initializes the [viewModel] with the necessary dependencies, including mock
     * repositories and use cases. It also sets the initial user data in the [userDataRepository]
     * to [emptyUserData].
     */
    @Before
    fun setup() {
        viewModel = SearchViewModel(
            getSearchContentsUseCase = getSearchContentsUseCase,
            recentSearchQueriesUseCase = getRecentQueryUseCase,
            searchContentsRepository = searchContentsRepository,
            savedStateHandle = SavedStateHandle(),
            recentSearchRepository = recentSearchRepository,
            userDataRepository = userDataRepository,
            analyticsHelper = NoOpAnalyticsHelper(),
        )
        userDataRepository.setUserData(userData = emptyUserData)
    }

    /**
     * Test case to verify that the initial state of the [SearchViewModel] is [Loading].
     *
     * This test ensures that when the [SearchViewModel] is created, its
     * [SearchViewModel.searchResultUiState] Flow emits [Loading] as its first value.
     * This indicates that the ViewModel is in the process of loading data or waiting for user
     * input.
     */
    @Test
    fun stateIsInitiallyLoading(): TestResult = runTest {
        assertEquals(expected = Loading, actual = viewModel.searchResultUiState.value)
    }

    /**
     * Test case to verify that when the search query is empty, the [SearchViewModel]
     * state becomes [EmptyQuery].
     *
     * This test simulates a scenario where news resources and topics are available,
     * but the user enters an empty search query.
     * It ensures that the [SearchViewModel.searchResultUiState] Flow emits [EmptyQuery],
     * indicating that there are no results to display because the query is empty.
     */
    @Test
    fun stateIsEmptyQuery_withEmptySearchQuery(): TestResult = runTest {
        searchContentsRepository.addNewsResources(newsResources = newsResourcesTestData)
        searchContentsRepository.addTopics(topics = topicsTestData)
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.searchResultUiState.collect()
        }

        viewModel.onSearchQueryChanged(query = "")

        assertEquals(expected = EmptyQuery, actual = viewModel.searchResultUiState.value)
    }

    /**
     * Test case to verify that when the search query does not match any content,
     * the [SearchViewModel] state is [SearchResultUiState.Success] with an empty result.
     *
     * This test simulates a scenario where news resources and topics are available,
     * but the user enters a search query that does not match any of them.
     * It ensures that the [SearchViewModel.searchResultUiState] Flow emits
     * [SearchResultUiState.Success] with an empty list of topics and news resources,
     * indicating that the search was successful but yielded no matching results.
     */
    @Test
    fun emptyResultIsReturned_withNotMatchingQuery(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.searchResultUiState.collect()
        }

        viewModel.onSearchQueryChanged(query = "XXX")
        searchContentsRepository.addNewsResources(newsResources = newsResourcesTestData)
        searchContentsRepository.addTopics(topics = topicsTestData)

        val result = viewModel.searchResultUiState.value
        assertIs<SearchResultUiState.Success>(value = result)
    }

    /**
     * Test case to verify that when a search is triggered, the [SearchViewModel]
     * state is [Success].
     *
     * This test simulates a scenario where the user triggers a search with a non-empty query.
     * It ensures that the [SearchViewModel.recentSearchQueriesUiState] Flow emits [Success],
     * indicating that the recent search queries were successfully retrieved.
     */
    @Test
    fun recentSearches_verifyUiStateIsSuccess(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.recentSearchQueriesUiState.collect()
        }
        viewModel.onSearchTriggered(query = "kotlin")

        val result: RecentSearchQueriesUiState = viewModel.recentSearchQueriesUiState.value
        assertIs<Success>(value = result)
    }

    /**
     * Test case to verify that when the FTS table is not populated, the [SearchViewModel] state
     * is [SearchNotReady].
     *
     * This test simulates a scenario where the underlying FTS table in the database,
     * which is used for searching, has not been populated yet.
     * It ensures that the [SearchViewModel.searchResultUiState] Flow emits [SearchNotReady],
     * indicating that the search functionality is not yet available.
     */
    @Test
    fun searchNotReady_withNoFtsTableEntity(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.searchResultUiState.collect()
        }

        viewModel.onSearchQueryChanged(query = "")

        assertEquals(expected = SearchNotReady, actual = viewModel.searchResultUiState.value)
    }

    /**
     * Test case to verify that an empty search query is not added to the recent searches.
     *
     * This test ensures that if the user triggers a search with an empty query,
     * it is not saved in the recent search history.
     * It checks that the [GetRecentSearchQueriesUseCase] does not return any
     * [RecentSearchQuery] after an empty search is performed.
     */
    @Test
    fun emptySearchText_isNotAddedToRecentSearches(): TestResult = runTest {
        viewModel.onSearchTriggered(query = "")

        val recentSearchQueriesStream: Flow<List<RecentSearchQuery>> = getRecentQueryUseCase()
        val recentSearchQueries: List<RecentSearchQuery> = recentSearchQueriesStream.first()
        val recentSearchQuery: RecentSearchQuery? = recentSearchQueries.firstOrNull()

        assertNull(actual = recentSearchQuery)
    }

    /**
     * Test case to verify that a search query with three spaces is treated as an empty query.
     *
     * This test ensures that if the user enters a search query consisting only of spaces,
     * it is considered an empty query.
     * It checks that the [SearchViewModel.searchResultUiState] Flow emits [EmptyQuery]
     * in this scenario.
     */
    @Test
    fun searchTextWithThreeSpaces_isEmptyQuery(): TestResult = runTest {
        searchContentsRepository.addNewsResources(newsResources = newsResourcesTestData)
        searchContentsRepository.addTopics(topics = topicsTestData)
        val collectJob: Job = launch(context = UnconfinedTestDispatcher()) {
            viewModel.searchResultUiState.collect()
        }

        viewModel.onSearchQueryChanged(query = "   ")

        assertIs<EmptyQuery>(value = viewModel.searchResultUiState.value)

        collectJob.cancel()
    }

    /**
     * Test case to verify that a search query with leading spaces is treated as an empty query
     * if it contains fewer than [SEARCH_QUERY_MIN_LENGTH] characters after trimming.
     *
     * This test simulates a scenario where the user enters a search query with three leading
     * spaces followed by a single letter.
     * It ensures that the [SearchViewModel.searchResultUiState] Flow emits [EmptyQuery],
     * indicating that the search query is considered empty because it does not meet the
     * minimum length requirement after trimming.
     */
    @Test
    fun searchTextWithThreeSpacesAndOneLetter_isEmptyQuery(): TestResult = runTest {
        searchContentsRepository.addNewsResources(newsResources = newsResourcesTestData)
        searchContentsRepository.addTopics(topics = topicsTestData)
        val collectJob = launch(context = UnconfinedTestDispatcher()) {
            viewModel.searchResultUiState.collect()
        }

        viewModel.onSearchQueryChanged(query = "   a")

        assertIs<EmptyQuery>(value = viewModel.searchResultUiState.value)

        collectJob.cancel()
    }

    /**
     * Test case to verify that when a news resource is bookmarked or unbookmarked,
     * the bookmark state is updated correctly.
     *
     * This test simulates toggling the bookmark state of a news resource.
     * It ensures that when [SearchViewModel.setNewsResourceBookmarked] is called:
     * - If `isChecked` is true, the news resource ID is added to the set of bookmarked
     *   news resources in the [userDataRepository].
     * - If `isChecked` is false, the news resource ID is removed from the set of bookmarked
     *   news resources in the [userDataRepository].
     */
    @Test
    fun whenToggleNewsResourceSavedIsCalled_bookmarkStateIsUpdated(): TestResult = runTest {
        val newsResourceId = "123"
        viewModel.setNewsResourceBookmarked(newsResourceId = newsResourceId, isChecked = true)

        assertEquals(
            expected = setOf(newsResourceId),
            actual = userDataRepository.userData.first().bookmarkedNewsResources,
        )

        viewModel.setNewsResourceBookmarked(newsResourceId = newsResourceId, isChecked = false)

        assertEquals(
            expected = emptySet(),
            actual = userDataRepository.userData.first().bookmarkedNewsResources,
        )
    }
}
