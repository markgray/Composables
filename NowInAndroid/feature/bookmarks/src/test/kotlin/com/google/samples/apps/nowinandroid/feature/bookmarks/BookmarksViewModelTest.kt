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

package com.google.samples.apps.nowinandroid.feature.bookmarks

import com.google.samples.apps.nowinandroid.core.data.repository.CompositeUserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.testing.data.newsResourcesTestData
import com.google.samples.apps.nowinandroid.core.testing.repository.TestNewsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState.Loading
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState.Success
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Unit tests for [BookmarksViewModel].
 *
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class BookmarksViewModelTest {
    /**
     * A JUnit rule that overrides the Main dispatcher for tests.
     *
     * This rule is used to replace the main dispatcher with a test dispatcher
     * in order to control the execution of coroutines in tests.
     */
    @get:Rule
    val dispatcherRule: MainDispatcherRule = MainDispatcherRule()

    /**
     * The user data repository used to interact with user data.
     */
    private val userDataRepository = TestUserDataRepository()

    /**
     * The news repository used to interact with news data.
     */
    private val newsRepository = TestNewsRepository()

    /**
     * The user news resource repository used to interact with user news resources.
     */
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )

    /**
     * The view model being tested.
     */
    private lateinit var viewModel: BookmarksViewModel

    /**
     * Sets up the test environment by initializing the [BookmarksViewModel] with the
     * test repositories. Runs before each test.
     */
    @Before
    fun setup() {
        viewModel = BookmarksViewModel(
            userDataRepository = userDataRepository,
            userNewsResourceRepository = userNewsResourceRepository,
        )
    }

    /**
     * Test that the initial state of the feed UI is [Loading].
     *
     * This test verifies that the [BookmarksViewModel.feedUiState] value is
     * initially set to [NewsFeedUiState.Loading].
     */
    @Test
    fun stateIsInitiallyLoading(): TestResult = runTest {
        assertEquals(expected = Loading, actual = viewModel.feedUiState.value)
    }

    /**
     * Tests that when a news resource is bookmarked, it is displayed in the feed.
     */
    @Test
    fun oneBookmark_showsInFeed(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedUiState.collect()
        }

        newsRepository.sendNewsResources(newsResources = newsResourcesTestData)
        userDataRepository.setNewsResourceBookmarked(
            newsResourceId = newsResourcesTestData[0].id,
            bookmarked = true,
        )
        val item: NewsFeedUiState = viewModel.feedUiState.value
        assertIs<Success>(value = item)
        assertEquals(expected = item.feed.size, actual = 1)
    }

    /**
     * Test case to verify that removing a bookmarked news resource from the feed
     * results in an empty feed and displays the undo bookmark option.
     *
     * This test performs the following steps:
     * 1. Launches a coroutine to collect the feed UI state.
     * 2. Sets the news resources to be used by the test.
     * 3. Bookmarks a news resource.
     * 4. Removes the bookmarked news resource from the feed.
     * 5. Verifies that the feed is empty.
     * 6. Verifies that the undo bookmark option is displayed.
     */
    @Test
    fun oneBookmark_whenRemoving_removesFromFeed(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedUiState.collect()
        }
        // Set the news resources to be used by this test
        newsRepository.sendNewsResources(newsResources = newsResourcesTestData)
        // Start with the resource saved
        userDataRepository.setNewsResourceBookmarked(
            newsResourceId = newsResourcesTestData[0].id,
            bookmarked = true,
        )
        // Use viewModel to remove saved resource
        viewModel.removeFromSavedResources(newsResourceId = newsResourcesTestData[0].id)
        // Verify list of saved resources is now empty
        val item: NewsFeedUiState = viewModel.feedUiState.value
        assertIs<Success>(value = item)
        assertEquals(expected = item.feed.size, actual = 0)
        assertTrue(actual = viewModel.shouldDisplayUndoBookmark)
    }

    /**
     * Test case to verify that when a news resource is viewed, its viewed state is updated
     * in the feed UI state.
     *
     * This test performs the following steps:
     * 1. Launches a coroutine to collect the feed UI state.
     * 2. Sets the news resources to be used by the test.
     * 3. Bookmarks a news resource.
     * 4. Verifies that the news resource is not yet viewed.
     * 5. Marks the news resource as viewed.
     * 6. Verifies that the news resource is marked as viewed in the feed UI state.
     */
    @Test
    fun feedUiState_resourceIsViewed_setResourcesViewed(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedUiState.collect()
        }

        // Given
        newsRepository.sendNewsResources(newsResources = newsResourcesTestData)
        userDataRepository.setNewsResourceBookmarked(
            newsResourceId = newsResourcesTestData[0].id,
            bookmarked = true,
        )
        val itemBeforeViewed: NewsFeedUiState = viewModel.feedUiState.value
        assertIs<Success>(value = itemBeforeViewed)
        assertFalse(actual = itemBeforeViewed.feed.first().hasBeenViewed)

        // When
        viewModel.setNewsResourceViewed(newsResourceId = newsResourcesTestData[0].id, viewed = true)

        // Then
        val item: NewsFeedUiState = viewModel.feedUiState.value
        assertIs<Success>(value = item)
        assertTrue(actual = item.feed.first().hasBeenViewed)
    }

    /**
     * Test case to verify that undoing a bookmark removal restores the bookmark
     * in the feed UI state.
     *
     * This test performs the following steps:
     * 1. Launches a coroutine to collect the feed UI state.
     * 2. Sets the news resources to be used by the test.
     * 3. Bookmarks a news resource.
     * 4. Removes the bookmarked news resource from the feed.
     * 5. Verifies that the feed is empty and the undo bookmark option is displayed.
     * 6. Undoes the bookmark removal.
     * 7. Verifies that the bookmark is restored in the feed UI state and the undo
     *    bookmark option is hidden.
     */
    @Test
    fun feedUiState_undoneBookmarkRemoval_bookmarkIsRestored(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedUiState.collect()
        }

        // Given
        newsRepository.sendNewsResources(newsResources = newsResourcesTestData)
        userDataRepository.setNewsResourceBookmarked(
            newsResourceId = newsResourcesTestData[0].id,
            bookmarked = true,
        )
        viewModel.removeFromSavedResources(newsResourceId = newsResourcesTestData[0].id)
        assertTrue(actual = viewModel.shouldDisplayUndoBookmark)
        val itemBeforeUndo: NewsFeedUiState = viewModel.feedUiState.value
        assertIs<Success>(value = itemBeforeUndo)
        assertEquals(expected = 0, actual = itemBeforeUndo.feed.size)

        // When
        viewModel.undoBookmarkRemoval()

        // Then
        assertFalse(actual = viewModel.shouldDisplayUndoBookmark)
        val item: NewsFeedUiState = viewModel.feedUiState.value
        assertIs<Success>(value = item)
        assertEquals(expected = 1, actual = item.feed.size)
    }
}
