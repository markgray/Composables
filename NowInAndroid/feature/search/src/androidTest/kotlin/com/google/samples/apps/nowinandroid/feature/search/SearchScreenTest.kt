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

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.data.model.RecentSearchQuery
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig.DARK
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand.ANDROID
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.testing.data.followableTopicTestData
import com.google.samples.apps.nowinandroid.core.testing.data.newsResourcesTestData
import com.google.samples.apps.nowinandroid.core.ui.R.string
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI test for checking the correct behaviour of the Search screen.
 */
class SearchScreenTest {

    /**
     * The compose test rule used in this test. Test rules provide a way to run code before and after
     * test methods. [createAndroidComposeRule]<[ComponentActivity]>() creates a rule that provides
     * a testing environment for Jetpack Compose UI. It launches a simple [ComponentActivity] for
     * hosting the composables under test.
     */
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<
        ActivityScenarioRule<ComponentActivity>,
        ComponentActivity
        > = createAndroidComposeRule<ComponentActivity>()

    /**
     * The content description for the clear search text button. ("Clear search text")
     */
    private lateinit var clearSearchContentDesc: String

    /**
     * The content description for the follow button. ("Follow interest")
     */
    private lateinit var followButtonContentDesc: String

    /**
     * The content description for the unfollow button. ("Unfollow interest")
     */
    private lateinit var unfollowButtonContentDesc: String

    /**
     * The content description for the clear recent searches button. ("Clear searches")
     */
    private lateinit var clearRecentSearchesContentDesc: String

    /**
     * The string for the topics header. ("Topics")
     */
    private lateinit var topicsString: String

    /**
     * The string for the updates header. ("Updates")
     */
    private lateinit var updatesString: String

    /**
     * The string for the try another search button. ("Try another search or explorer...")
     */
    private lateinit var tryAnotherSearchString: String

    /**
     * The string for the search not ready message. ("Sorry, we are still processing the search
     * index. Please come back later")
     */
    private lateinit var searchNotReadyString: String

    /**
     * The dummy [UserData] used in this test.
     */
    private val userData: UserData = UserData(
        bookmarkedNewsResources = setOf("1", "3"),
        viewedNewsResources = setOf("1", "2", "4"),
        followedTopics = emptySet(),
        themeBrand = ANDROID,
        darkThemeConfig = DARK,
        shouldHideOnboarding = true,
        useDynamicColor = false,
    )

    /**
     * Sets up the necessary string resources for the test.
     * This method is called before each test case.
     */
    @Before
    fun setup() {
        composeTestRule.activity.apply {
            clearSearchContentDesc =
                getString(R.string.feature_search_clear_search_text_content_desc)
            clearRecentSearchesContentDesc =
                getString(R.string.feature_search_clear_recent_searches_content_desc)
            followButtonContentDesc =
                getString(string.core_ui_interests_card_follow_button_content_desc)
            unfollowButtonContentDesc =
                getString(string.core_ui_interests_card_unfollow_button_content_desc)
            topicsString = getString(R.string.feature_search_topics)
            updatesString = getString(R.string.feature_search_updates)
            tryAnotherSearchString = getString(R.string.feature_search_try_another_search) +
                " " + getString(R.string.feature_search_interests) + " " + getString(R.string.feature_search_to_browse_topics)
            searchNotReadyString = getString(R.string.feature_search_not_ready)
        }
    }

    /**
     * When the search screen is shown, the search text field is shown as focused.
     */
    @Test
    fun searchTextField_isFocused() {
        composeTestRule.setContent {
            SearchScreen()
        }

        composeTestRule
            .onNodeWithTag(testTag = "searchTextField")
            .assertIsFocused()
    }

    /**
     * When the search results are empty, the empty search screen is displayed.
     * "Try another search"
     */
    @Test
    fun emptySearchResult_emptyScreenIsDisplayed() {
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.Success(),
            )
        }

        composeTestRule
            .onNodeWithText(text = tryAnotherSearchString)
            .assertIsDisplayed()
    }

    /**
     * When the search results are empty and there are recent searches, the empty search screen
     * is displayed and the recent searches are displayed.
     */
    @Test
    fun emptySearchResult_nonEmptyRecentSearches_emptySearchScreenAndRecentSearchesAreDisplayed() {
        val recentSearches: List<String> = listOf("kotlin")
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.Success(),
                recentSearchesUiState = RecentSearchQueriesUiState.Success(
                    recentQueries = recentSearches.map(::RecentSearchQuery),
                ),
            )
        }

        composeTestRule
            .onNodeWithText(text = tryAnotherSearchString)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription(label = clearRecentSearchesContentDesc)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = "kotlin")
            .assertIsDisplayed()
    }

    /**
     * When a search result contains topics, all topics are displayed and the follow buttons are
     * displayed for the number of followed topics.
     * For example, if there are 3 topics and 1 is followed, then 2 follow buttons and 1 unfollow
     * button should be displayed.
     */
    @Test
    fun searchResultWithTopics_allTopicsAreVisible_followButtonsVisibleForTheNumOfFollowedTopics() {
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.Success(topics = followableTopicTestData),
            )
        }

        composeTestRule
            .onNodeWithText(text = topicsString)
            .assertIsDisplayed()

        val scrollableNode = composeTestRule
            .onAllNodes(matcher = hasScrollToNodeAction())
            .onFirst()

        followableTopicTestData.forEachIndexed { index: Int, followableTopic: FollowableTopic ->
            scrollableNode.performScrollToIndex(index = index)

            composeTestRule
                .onNodeWithText(text = followableTopic.topic.name)
                .assertIsDisplayed()
        }

        composeTestRule
            .onAllNodesWithContentDescription(label = followButtonContentDesc)
            .assertCountEquals(expectedSize = 2)
        composeTestRule
            .onAllNodesWithContentDescription(label = unfollowButtonContentDesc)
            .assertCountEquals(expectedSize = 1)
    }

    /**
     * When a search result contains news resources, the first news resource is visible.
     */
    @Test
    fun searchResultWithNewsResources_firstNewsResourcesIsVisible() {
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.Success(
                    newsResources = newsResourcesTestData.map { newsResource: NewsResource ->
                        UserNewsResource(
                            newsResource = newsResource,
                            userData = userData,
                        )
                    },
                ),
            )
        }

        composeTestRule
            .onNodeWithText(text = updatesString)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = newsResourcesTestData[0].title)
            .assertIsDisplayed()
    }

    /**
     * When the search query is empty and there are recent searches, the clear searches button is
     * displayed.
     */
    @Test
    fun emptyQuery_notEmptyRecentSearches_verifyClearSearchesButton_displayed() {
        val recentSearches: List<String> = listOf("kotlin", "testing")
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.EmptyQuery,
                recentSearchesUiState = RecentSearchQueriesUiState.Success(
                    recentQueries = recentSearches.map(transform = ::RecentSearchQuery),
                ),
            )
        }

        composeTestRule
            .onNodeWithContentDescription(label = clearRecentSearchesContentDesc)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = "kotlin")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = "testing")
            .assertIsDisplayed()
    }

    /**
     * When the search is not ready, the "Search not ready" message is displayed.
     * "Sorry, we are still processing the search index. Please come back later"
     */
    @Test
    fun searchNotReady_verifySearchNotReadyMessageIsVisible() {
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.SearchNotReady,
            )
        }

        composeTestRule
            .onNodeWithText(text = searchNotReadyString)
            .assertIsDisplayed()
    }
}
