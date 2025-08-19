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

package com.google.samples.apps.nowinandroid.feature.topic

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.testing.data.followableTopicTestData
import com.google.samples.apps.nowinandroid.core.testing.data.userNewsResourcesTestData
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI test for checking the correct behaviour of the Topic screen;
 * Verifies that, when a specific UiState is set, the corresponding
 * composables and details are shown
 */
class TopicScreenTest {

    /**
     * The compose test rule used in this test. Test rules provide a way to run code before and after
     * test methods. [createAndroidComposeRule]<[ComponentActivity]>() creates a rule that provides
     * a testing environment for Jetpack Compose UI. It launches a simple [ComponentActivity] for
     * hosting the composables under test.
     */
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<
        ActivityScenarioRule<ComponentActivity>,
        ComponentActivity,
        > = createAndroidComposeRule<ComponentActivity>()

    /**
     * The string resource for the loading content description.
     */
    private lateinit var topicLoading: String

    /**
     * Sets up the test environment by getting the string resource for the loading content
     * description. This is necessary because the string resource is used in the tests to verify
     * that the loading indicator is shown when the screen is loading.
     */
    @Before
    fun setup() {
        composeTestRule.activity.apply {
            topicLoading = getString(R.string.feature_topic_loading)
        }
    }

    /**
     * When the screen is loading, the loading wheel is shown.
     *
     * We use the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the activity that we are testing to a [TopicScreen]
     * whose arguments are:
     *  - `topicUiState`: is a [TopicUiState.Loading]
     *  - `newsUiState`: is a [NewsUiState.Loading]
     *  - `showBackButton`: is true
     *  - `onBackClick`: is a lambda that does nothing
     *  - `onFollowClick`: is a lambda that does nothing
     *  - `onTopicClick`: is a lambda that does nothing
     *  - `onBookmarkChanged`: is a lambda that does nothing
     *  - `onNewsResourceViewed`: is a lambda that does nothing
     *
     * Then we use the [AndroidComposeTestRule.onNodeWithContentDescription] method to find the
     * node whose `label` is [topicLoading]. We use the [SemanticsNodeInteraction.assertExists]
     * method of the node returned to verify that the node exists.
     */
    @Test
    fun niaLoadingWheel_whenScreenIsLoading_showLoading() {
        composeTestRule.setContent {
            TopicScreen(
                topicUiState = TopicUiState.Loading,
                newsUiState = NewsUiState.Loading,
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onTopicClick = {},
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription(label = topicLoading)
            .assertExists()
    }

    /**
     * When the topic is successfully loaded, the topic name and description are shown.
     *
     * We start by initializing our [FollowableTopic] variable `testTopic` with the first element of
     * the [List] of [FollowableTopic] global property [followableTopicTestData]. Then we use the
     * [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the activity that we are testing to a [TopicScreen]
     * whose arguments are:
     *  - `topicUiState`: is a [TopicUiState.Success] with its `followableTopic` property set to our
     *  [FollowableTopic] variable `testTopic`.
     *  - `newsUiState`: is a [NewsUiState.Loading].
     *  - `showBackButton`: is true.
     *  - `onBackClick`: is a lambda that does nothing.
     *  - `onFollowClick`: is a lambda that does nothing.
     *  - `onTopicClick`: is a lambda that does nothing.
     *  - `onBookmarkChanged`: is a lambda that does nothing.
     *  - `onNewsResourceViewed`: is a lambda that does nothing.
     *
     * Then we use the [AndroidComposeTestRule.onNodeWithText] method to find the node whose text
     * is the same as the name of the topic. We use the [SemanticsNodeInteraction.assertExists]
     * method of the node returned to verify that the node exists. Finally, we use the
     * [AndroidComposeTestRule.onNodeWithText] method to find the node whose text is the same as
     * the long description of the topic. We use the [SemanticsNodeInteraction.assertExists]
     * method of the node returned to verify that the node exists.
     */
    @Test
    fun topicTitle_whenTopicIsSuccess_isShown() {
        val testTopic: FollowableTopic = followableTopicTestData.first()
        composeTestRule.setContent {
            TopicScreen(
                topicUiState = TopicUiState.Success(followableTopic = testTopic),
                newsUiState = NewsUiState.Loading,
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onTopicClick = {},
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
            )
        }

        // Name is shown
        composeTestRule
            .onNodeWithText(text = testTopic.topic.name)
            .assertExists()

        // Description is shown
        composeTestRule
            .onNodeWithText(text = testTopic.topic.longDescription)
            .assertExists()
    }

    /**
     * When the topic is loading, the news is not shown.
     *
     * We use the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the activity that we are testing to a [TopicScreen]
     * whose arguments are:
     *  - `topicUiState`: is a [TopicUiState.Loading]
     *  - `newsUiState`: is a [NewsUiState.Success] whose `news` argument is our global property
     *  [userNewsResourcesTestData].
     *  - `showBackButton`: is true
     *  - `onBackClick`: is a lambda that does nothing
     *  - `onFollowClick`: is a lambda that does nothing
     *  - `onTopicClick`: is a lambda that does nothing
     *  - `onBookmarkChanged`: is a lambda that does nothing
     *  - `onNewsResourceViewed`: is a lambda that does nothing
     *
     * Then we use the [AndroidComposeTestRule.onNodeWithContentDescription] method to find the
     * node whose `label` is [topicLoading]. We use the [SemanticsNodeInteraction.assertExists]
     * method of the node returned to verify that the node exists. This implies that the news is
     * not shown.
     */
    @Test
    fun news_whenTopicIsLoading_isNotShown() {
        composeTestRule.setContent {
            TopicScreen(
                topicUiState = TopicUiState.Loading,
                newsUiState = NewsUiState.Success(news = userNewsResourcesTestData),
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onTopicClick = {},
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
            )
        }

        // Loading indicator shown
        composeTestRule
            .onNodeWithContentDescription(label = topicLoading)
            .assertExists()
    }

    /**
     * When the news and topic are successfully loaded, the news items are shown.
     *
     * We start by initializing our [FollowableTopic] variable `testTopic` with the first element of
     * the [List] of [FollowableTopic] global property [followableTopicTestData]. Then we use the
     * [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the activity that we are testing to a [TopicScreen]
     * whose arguments are:
     *  - `topicUiState`: is a [TopicUiState.Success] whose `followableTopic` argument is our
     *  [FollowableTopic] variable `testTopic`.
     *  - `newsUiState`: is a [NewsUiState.Success] whose `news` argument is our global property
     *  [userNewsResourcesTestData].
     *  - `showBackButton`: is true
     *  - `onBackClick`: is a lambda that does nothing
     *  - `onFollowClick`: is a lambda that does nothing
     *  - `onTopicClick`: is a lambda that does nothing
     *  - `onBookmarkChanged`: is a lambda that does nothing
     *  - `onNewsResourceViewed`: is a lambda that does nothing
     *
     * Then we find all the nodes that have a scroll to node action, take the first one, and scroll
     * to the node that has text matching the title of the first item in our [userNewsResourcesTestData]
     * global property ([SemanticsNodeInteraction.performScrollToNode] will throw an [AssertionError]
     * if it does not find the node).
     */
    @Test
    fun news_whenSuccessAndTopicIsSuccess_isShown() {
        val testTopic: FollowableTopic = followableTopicTestData.first()
        composeTestRule.setContent {
            TopicScreen(
                topicUiState = TopicUiState.Success(followableTopic = testTopic),
                newsUiState = NewsUiState.Success(
                    news = userNewsResourcesTestData,
                ),
                showBackButton = true,
                onBackClick = {},
                onFollowClick = {},
                onTopicClick = {},
                onBookmarkChanged = { _, _ -> },
                onNewsResourceViewed = {},
            )
        }

        // Scroll to first news title if available
        composeTestRule
            .onAllNodes(matcher = hasScrollToNodeAction())
            .onFirst()
            .performScrollToNode(matcher = hasText(text = userNewsResourcesTestData.first().title))
    }
}
