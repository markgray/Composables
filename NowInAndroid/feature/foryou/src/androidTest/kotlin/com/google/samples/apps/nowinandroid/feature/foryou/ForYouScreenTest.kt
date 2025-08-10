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

package com.google.samples.apps.nowinandroid.feature.foryou

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaOverlayLoadingWheel
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.rules.GrantPostNotificationsPermissionRule
import com.google.samples.apps.nowinandroid.core.testing.data.followableTopicTestData
import com.google.samples.apps.nowinandroid.core.testing.data.userNewsResourcesTestData
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [ForYouScreen].
 */
class ForYouScreenTest {

    /**
     * This test rule grants the POST_NOTIFICATIONS permission, which is required on SDK 33+
     * for notifications to be displayed.
     */
    @get:Rule(order = 0)
    val postNotificationsPermission: GrantPostNotificationsPermissionRule =
        GrantPostNotificationsPermissionRule()

    /**
     * The compose test rule which is used to access the composable functions, this test rule
     * also launches the Compose host activity.
     */
    @get:Rule(order = 1)
    val composeTestRule: AndroidComposeTestRule<
        ActivityScenarioRule<ComponentActivity>,
        ComponentActivity,
        > = createAndroidComposeRule<ComponentActivity>()

    /**
     * Convenience property to find the "Done" button on the screen.
     */
    private val doneButtonMatcher by lazy {
        hasText(
            text = composeTestRule.activity.resources.getString(R.string.feature_foryou_done),
        )
    }

    /**
     * When the screen is loading the [NiaOverlayLoadingWheel] circular progress indicator whose
     * content description is the [String] with resource ID `R.string.feature_foryou_loading`
     * ("Loading for you…") should exist.
     * TODO: Continue here.
     */
    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            Box {
                ForYouScreen(
                    isSyncing = false,
                    onboardingUiState = OnboardingUiState.Loading,
                    feedState = NewsFeedUiState.Loading,
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                label = composeTestRule.activity.resources.getString(R.string.feature_foryou_loading),
            )
            .assertExists()
    }

    @Test
    fun circularProgressIndicator_whenScreenIsSyncing_exists() {
        composeTestRule.setContent {
            Box {
                ForYouScreen(
                    isSyncing = true,
                    onboardingUiState = OnboardingUiState.NotShown,
                    feedState = NewsFeedUiState.Success(emptyList()),
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                label = composeTestRule.activity.resources.getString(R.string.feature_foryou_loading),
            )
            .assertExists()
    }

    @Test
    fun topicSelector_whenNoTopicsSelected_showsTopicChipsAndDisabledDoneButton() {
        val testData: List<FollowableTopic> =
            followableTopicTestData.map { it.copy(isFollowed = false) }

        composeTestRule.setContent {
            Box {
                ForYouScreen(
                    isSyncing = false,
                    onboardingUiState = OnboardingUiState.Shown(
                        topics = testData,
                    ),
                    feedState = NewsFeedUiState.Success(
                        feed = emptyList(),
                    ),
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        testData.forEach { testTopic: FollowableTopic ->
            composeTestRule
                .onNodeWithText(text = testTopic.topic.name)
                .assertExists()
                .assertHasClickAction()
        }

        // Scroll until the Done button is visible
        composeTestRule
            .onAllNodes(matcher = hasScrollToNodeAction())
            .onFirst()
            .performScrollToNode(matcher = doneButtonMatcher)

        composeTestRule
            .onNode(matcher = doneButtonMatcher)
            .assertExists()
            .assertIsNotEnabled()
            .assertHasClickAction()
    }

    @Test
    fun topicSelector_whenSomeTopicsSelected_showsTopicChipsAndEnabledDoneButton() {
        composeTestRule.setContent {
            Box {
                ForYouScreen(
                    isSyncing = false,
                    onboardingUiState =
                        OnboardingUiState.Shown(
                            // Follow one topic
                            topics = followableTopicTestData.mapIndexed { index, testTopic ->
                                testTopic.copy(isFollowed = index == 1)
                            },
                        ),
                    feedState = NewsFeedUiState.Success(
                        feed = emptyList(),
                    ),
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        followableTopicTestData.forEach { testTopic: FollowableTopic ->
            composeTestRule
                .onNodeWithText(text = testTopic.topic.name)
                .assertExists()
                .assertHasClickAction()
        }

        // Scroll until the Done button is visible
        composeTestRule
            .onAllNodes(matcher = hasScrollToNodeAction())
            .onFirst()
            .performScrollToNode(matcher = doneButtonMatcher)

        composeTestRule
            .onNode(matcher = doneButtonMatcher)
            .assertExists()
            .assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun feed_whenInterestsSelectedAndLoading_showsLoadingIndicator() {
        composeTestRule.setContent {
            Box {
                ForYouScreen(
                    isSyncing = false,
                    onboardingUiState =
                        OnboardingUiState.Shown(topics = followableTopicTestData),
                    feedState = NewsFeedUiState.Loading,
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                label = composeTestRule.activity.resources.getString(R.string.feature_foryou_loading),
            )
            .assertExists()
    }

    @Test
    fun feed_whenNoInterestsSelectionAndLoading_showsLoadingIndicator() {
        composeTestRule.setContent {
            Box {
                ForYouScreen(
                    isSyncing = false,
                    onboardingUiState = OnboardingUiState.NotShown,
                    feedState = NewsFeedUiState.Loading,
                    deepLinkedUserNewsResource = null,
                    onTopicCheckedChanged = { _, _ -> },
                    onTopicClick = {},
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onDeepLinkOpened = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                label = composeTestRule.activity.resources.getString(R.string.feature_foryou_loading),
            )
            .assertExists()
    }

    @Test
    fun feed_whenNoInterestsSelectionAndLoaded_showsFeed() {
        composeTestRule.setContent {
            ForYouScreen(
                isSyncing = false,
                onboardingUiState = OnboardingUiState.NotShown,
                feedState = NewsFeedUiState.Success(
                    feed = userNewsResourcesTestData,
                ),
                deepLinkedUserNewsResource = null,
                onTopicCheckedChanged = { _, _ -> },
                onTopicClick = {},
                saveFollowedTopics = {},
                onNewsResourcesCheckedChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onDeepLinkOpened = {},
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
}
