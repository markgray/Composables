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

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.testing.data.userNewsResourcesTestData
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * UI tests for [BookmarksScreen] composable.
 */
class BookmarksScreenTest {

    /**
     * The [AndroidComposeTestRule] used to run our tests. Test rules provide a way to run code
     * before and after test methods. [createAndroidComposeRule]<[ComponentActivity]>() creates a
     * rule that provides a testing environment for Jetpack Compose UI. It launches a simple
     * [ComponentActivity] for hosting the composables under test.
     */
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<
        ActivityScenarioRule<ComponentActivity>,
        ComponentActivity,
        > = createAndroidComposeRule<ComponentActivity>()

    /**
     * Tests whether the loading spinner is shown when the feed is loading. We call the
     * [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the Activity that we are testing to a
     * [BookmarksScreen] whose arguments are:
     *  - `feedState` is a [NewsFeedUiState.Loading] state
     *  - `onShowSnackbar` is a lambda that always returns `false`
     *  - `removeFromBookmarks` is a lambda that does nothing.
     *  - `onTopicClick` is a lambda that does nothing.
     *  - `onNewsResourceViewed` is a lambda that does nothing.
     *
     * Then we use the [AndroidComposeTestRule.onNodeWithContentDescription] method to find the
     * node with the content description of the loading spinner and apply the `assertExists`
     * assertion to verify that it exists.
     */
    @Test
    fun loading_showsLoadingSpinner() {
        composeTestRule.setContent {
            BookmarksScreen(
                feedState = NewsFeedUiState.Loading,
                onShowSnackbar = { _, _ -> false },
                removeFromBookmarks = {},
                onTopicClick = {},
                onNewsResourceViewed = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription(
                label = composeTestRule
                    .activity.resources.getString(R.string.feature_bookmarks_loading),
            )
            .assertExists()
    }

    /**
     * Tests that the correct information is displayed for each bookmarked news item. It initializes
     * the [BookmarksScreen] with two bookmarked news items from [userNewsResourcesTestData].
     * Then it checks if the first item's title is displayed and if it has a click action.
     * It then scrolls to the second item and performs the same checks.
     */
    @Test
    fun feed_whenHasBookmarks_showsBookmarks() {
        composeTestRule.setContent {
            BookmarksScreen(
                feedState = NewsFeedUiState.Success(
                    feed = userNewsResourcesTestData.take(n = 2),
                ),
                onShowSnackbar = { _, _ -> false },
                removeFromBookmarks = {},
                onTopicClick = {},
                onNewsResourceViewed = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                text = userNewsResourcesTestData[0].title,
                substring = true,
            )
            .assertExists()
            .assertHasClickAction()

        composeTestRule.onNode(matcher = hasScrollToNodeAction())
            .performScrollToNode(
                matcher = hasText(
                    text = userNewsResourcesTestData[1].title,
                    substring = true,
                ),
            )

        composeTestRule
            .onNodeWithText(
                text = userNewsResourcesTestData[1].title,
                substring = true,
            )
            .assertExists()
            .assertHasClickAction()
    }

    /**
     * Tests that the unbookmark action on a news item calls the `removeFromBookmarks` lambda with
     * the correct news resource ID. It initializes the [BookmarksScreen] with two bookmarked news
     * items. It then finds the unbookmark button for the first news item and clicks it.
     * Finally, it asserts that the `removeFromBookmarks` lambda was called with the ID of the
     * first news item.
     */
    @Test
    fun feed_whenRemovingBookmark_removesBookmark() {
        var removeFromBookmarksCalled = false

        composeTestRule.setContent {
            BookmarksScreen(
                feedState = NewsFeedUiState.Success(
                    feed = userNewsResourcesTestData.take(n = 2),
                ),
                onShowSnackbar = { _, _ -> false },
                removeFromBookmarks = { newsResourceId: String ->
                    assertEquals(
                        expected = userNewsResourcesTestData[0].id,
                        actual = newsResourceId,
                    )
                    removeFromBookmarksCalled = true
                },
                onTopicClick = {},
                onNewsResourceViewed = {},
            )
        }

        composeTestRule
            .onAllNodesWithContentDescription(
                label = composeTestRule.activity.getString(
                    com.google.samples.apps.nowinandroid.core.ui.R.string.core_ui_unbookmark,
                ),
            ).filter(
                matcher = hasAnyAncestor(
                    matcher = hasText(
                        text = userNewsResourcesTestData[0].title,
                        substring = true,
                    ),
                ),
            )
            .assertCountEquals(expectedSize = 1)
            .onFirst()
            .performClick()

        assertTrue(actual = removeFromBookmarksCalled)
    }

    /**
     * Tests that the empty state is shown when there are no bookmarked news items. It initializes
     * the [BookmarksScreen] with an empty list of bookmarked news items. Then it checks if the
     * empty state title and description are displayed.
     */
    @Test
    fun feed_whenHasNoBookmarks_showsEmptyState() {
        composeTestRule.setContent {
            BookmarksScreen(
                feedState = NewsFeedUiState.Success(emptyList()),
                onShowSnackbar = { _, _ -> false },
                removeFromBookmarks = {},
                onTopicClick = {},
                onNewsResourceViewed = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                text = composeTestRule.activity.getString(R.string.feature_bookmarks_empty_error),
            )
            .assertExists()

        composeTestRule
            .onNodeWithText(
                text = composeTestRule.activity.getString(R.string.feature_bookmarks_empty_description),
            )
            .assertExists()
    }

    /**
     * Tests that the undo state is cleared when the lifecycle stops. It initializes the
     * [BookmarksScreen] and a [TestLifecycleOwner] in the started state. It then moves the
     * lifecycle to the stopped state and asserts that the `clearUndoState` lambda was called.
     */
    @Test
    fun feed_whenLifecycleStops_undoBookmarkedStateIsCleared(): TestResult = runTest {
        var undoStateCleared = false
        val testLifecycleOwner = TestLifecycleOwner(initialState = Lifecycle.State.STARTED)

        composeTestRule.setContent {
            CompositionLocalProvider(value = LocalLifecycleOwner provides testLifecycleOwner) {
                BookmarksScreen(
                    feedState = NewsFeedUiState.Success(emptyList()),
                    onShowSnackbar = { _, _ -> false },
                    removeFromBookmarks = {},
                    onTopicClick = {},
                    onNewsResourceViewed = {},
                    clearUndoState = {
                        undoStateCleared = true
                    },
                )
            }
        }

        assertEquals(expected = false, actual = undoStateCleared)
        testLifecycleOwner.handleLifecycleEvent(event = Lifecycle.Event.ON_STOP)
        assertEquals(expected = true, actual = undoStateCleared)
    }
}
