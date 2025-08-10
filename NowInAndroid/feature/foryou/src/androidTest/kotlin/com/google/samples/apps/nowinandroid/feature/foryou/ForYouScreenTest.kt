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
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
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
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
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
     * When the screen is loading, the [NiaOverlayLoadingWheel] circular progress indicator whose
     * content description is the [String] with resource ID `R.string.feature_foryou_loading`
     * ("Loading for you…") should exist.
     *
     * We use the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the screen to a [Box] containing a [ForYouScreen] whose
     * arguments are:
     *  - `isSyncing`: is `false`.
     *  - `onboardingUiState`: is [OnboardingUiState.Loading].
     *  - `feedState`: is [NewsFeedUiState.Loading].
     *  - `deepLinkedUserNewsResource`: is `null`.
     *  - `onTopicCheckedChanged`: is a lambda that does nothing.
     *  - `onTopicClick`: is a lambda that does nothing.
     *  - `saveFollowedTopics`: is a lambda that does nothing.
     *  - `onNewsResourcesCheckedChanged`: is a lambda that does nothing.
     *  - `onNewsResourceViewed`: is a lambda that does nothing.
     *  - `onDeepLinkOpened`: is a lambda that does nothing.
     *
     * Then we call the [AndroidComposeTestRule.onNodeWithContentDescription] method of our
     * [AndroidComposeTestRule] property [composeTestRule] to find the node with the content
     * description `label` [String] with resource ID `R.string.feature_foryou_loading`
     * ("Loading for you…"), and we assert that the node exists.
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

    /**
     * When the screen is syncing the [NiaOverlayLoadingWheel] circular progress indicator whose
     * content description is the [String] with resource ID `R.string.feature_foryou_loading`
     * ("Loading for you…") should exist.
     *
     * We use the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the screen to a [Box] containing a [ForYouScreen] whose
     * arguments are:
     *  - `isSyncing`: is `true`.
     *  - `onboardingUiState`: is [OnboardingUiState.NotShown].
     *  - `feedState`: is [NewsFeedUiState.Success] whose `feed` argument is an [emptyList].
     *  - `deepLinkedUserNewsResource`: is `null`.
     *  - `onTopicCheckedChanged`: is a lambda that does nothing.
     *  - `onTopicClick`: is a lambda that does nothing.
     *  - `saveFollowedTopics`: is a lambda that does nothing.
     *  - `onNewsResourcesCheckedChanged`: is a lambda that does nothing.
     *  - `onNewsResourceViewed`: is a lambda that does nothing.
     *  - `onDeepLinkOpened`: is a lambda that does nothing.
     *
     * Then we call the [AndroidComposeTestRule.onNodeWithContentDescription] method of our
     * [AndroidComposeTestRule] field [composeTestRule] to find the node with the content description
     * given by the [String] with resource ID `R.string.feature_foryou_loading` ("Loading for you…")
     * and on the [SemanticsNodeInteraction] that is returned we chain an `assertExists` call which
     * asserts that the node exists.
     */
    @Test
    fun circularProgressIndicator_whenScreenIsSyncing_exists() {
        composeTestRule.setContent {
            Box {
                ForYouScreen(
                    isSyncing = true,
                    onboardingUiState = OnboardingUiState.NotShown,
                    feedState = NewsFeedUiState.Success(feed = emptyList()),
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

    /**
     * When the `onboardingUiState` is `Shown` then the topic chips should be displayed.
     * Furthermore, if none of the topics are `selected` then the `Done` button should be disabled.
     *
     * We initialize our [List] of [FollowableTopic] variable `testData` using the [Iterable.map]
     * extension function of the [List] of [FollowableTopic] object [followableTopicTestData] to
     * loop through each [FollowableTopic] object and create a new [FollowableTopic] object with
     * its `isFollowed` property set to `false`. Then we use the [AndroidComposeTestRule.setContent]
     * method of our [AndroidComposeTestRule] property [composeTestRule] to set the content of the
     * screen to a [Box] containing a [ForYouScreen] whose arguments are:
     *  - `isSyncing`: is `false`.
     *  - `onboardingUiState`: is [OnboardingUiState.Shown] whose `topics` argument is `testData`.
     *  - `feedState`: is [NewsFeedUiState.Success] whose `feed` argument is an [emptyList].
     *  - `deepLinkedUserNewsResource`: is `null`.
     *  - `onTopicCheckedChanged`: is a lambda that does nothing.
     *  - `onTopicClick`: is a lambda that does nothing.
     *  - `saveFollowedTopics`: is a lambda that does nothing.
     *  - `onNewsResourcesCheckedChanged`: is a lambda that does nothing.
     *  - `onNewsResourceViewed`: is a lambda that does nothing.
     *  - `onDeepLinkOpened`: is a lambda that does nothing.
     *
     * Then we use the [Iterable.forEach] extension function of the [List] of [FollowableTopic]
     * variable `testData` to loop through each [FollowableTopic] object and call the
     * [AndroidComposeTestRule.onNodeWithText] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to find the node with the text given by the [Topic.name] `name` property
     * of the [FollowableTopic.topic] property of the current [FollowableTopic] object and on the
     * [SemanticsNodeInteraction] that is returned we assert that the node exists and
     * [SemanticsNodeInteraction.assertHasClickAction] asserts that the node has a click action.
     *
     * Next, we use the [AndroidComposeTestRule.onAllNodes] method of our [AndroidComposeTestRule]
     * to find all nodes with the `matcher` [hasScrollToNodeAction] and call the
     * [SemanticsNodeInteractionCollection.onFirst] to find the first node, then call the
     * [SemanticsNodeInteraction.performScrollToNode] method to scroll to the node with the `matcher`
     * [doneButtonMatcher].
     *
     * Finally, we use the [AndroidComposeTestRule.onNode] method of our [AndroidComposeTestRule] to
     * find the node with the `matcher` [doneButtonMatcher] and assert that the node exists,
     * [SemanticsNodeInteraction.assertIsNotEnabled] asserts it is disabled, and
     * [SemanticsNodeInteraction.assertHasClickAction] asserts that the node has a click action.
     */
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

    /**
     * When some topics are selected then the topic chips should be displayed, and the `Done` button
     * should be enabled.
     *
     * We use the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the screen to a [Box] containing a [ForYouScreen] whose
     * arguments are:
     *  - `isSyncing`: is `false`.
     *  - `onboardingUiState`: is [OnboardingUiState.Shown] whose `topics` argument is the [List]
     *  of [FollowableTopic] objects in [followableTopicTestData] with the `isFollowed` property
     *  of the second (index is `1`) [FollowableTopic] object set to `true`.
     *  - `feedState`: is [NewsFeedUiState.Success] whose `feed` argument is an [emptyList].
     *  - `deepLinkedUserNewsResource`: is `null`.
     *  - `onTopicCheckedChanged`: is a lambda that does nothing.
     *  - `onTopicClick`: is a lambda that does nothing.
     *  - `saveFollowedTopics`: is a lambda that does nothing.
     *  - `onNewsResourcesCheckedChanged`: is a lambda that does nothing.
     *  - `onNewsResourceViewed`: is a lambda that does nothing.
     *  - `onDeepLinkOpened`: is a lambda that does nothing.
     *
     * Then we use the [Iterable.forEach] extension function of the [List] of [FollowableTopic]
     * property [followableTopicTestData] to loop through each [FollowableTopic] object and call the
     * [AndroidComposeTestRule.onNodeWithText] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to find the node with the text given by the [Topic.name] `name` property
     * of the [FollowableTopic.topic] property of the current [FollowableTopic] object and on the
     * [SemanticsNodeInteraction] that is returned we assert that the node exists and
     * [SemanticsNodeInteraction.assertHasClickAction] asserts that the node has a click action.
     *
     * Next, we use the [AndroidComposeTestRule.onAllNodes] method of our [AndroidComposeTestRule]
     * to find all nodes with the `matcher` [hasScrollToNodeAction] and call the
     * [SemanticsNodeInteractionCollection.onFirst] method to find the first node, then call the
     * [SemanticsNodeInteraction.performScrollToNode] method to scroll to the node with the `matcher`
     * [doneButtonMatcher].
     *
     * Finally, we use the [AndroidComposeTestRule.onNode] method of our [AndroidComposeTestRule] to
     * find the node with the `matcher` [doneButtonMatcher] and assert that the node exists,
     * [SemanticsNodeInteraction.assertIsEnabled] asserts it is enabled, and
     * [SemanticsNodeInteraction.assertHasClickAction] asserts that the node has a click action.
     */
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

    /**
     * When the `onboardingUiState` argument is [OnboardingUiState.Shown] and the `feedState` is
     * [NewsFeedUiState.Loading] the [NiaOverlayLoadingWheel] circular progress indicator whose
     * content description is the [String] with resource ID `R.string.feature_foryou_loading`
     * ("Loading for you…") should exist.
     *
     * We use the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the screen to a [Box] containing a [ForYouScreen] whose
     * arguments are:
     *  - `isSyncing`: is `false`.
     *  - `onboardingUiState`: is [OnboardingUiState.Shown] whose `topics` argument is our [List]
     *  of [FollowableTopic] property [followableTopicTestData]
     *  - `feedState`: is [NewsFeedUiState.Loading].
     *  - `deepLinkedUserNewsResource`: is `null`.
     *  - `onTopicCheckedChanged`: is a lambda that does nothing.
     *  - `onTopicClick`: is a lambda that does nothing.
     *  - `saveFollowedTopics`: is a lambda that does nothing.
     *  - `onNewsResourcesCheckedChanged`: is a lambda that does nothing.
     *  - `onNewsResourceViewed`: is a lambda that does nothing.
     *  - `onDeepLinkOpened`: is a lambda that does nothing.
     *
     * Then we call the [AndroidComposeTestRule.onNodeWithContentDescription] method of our
     * [AndroidComposeTestRule] property [composeTestRule] to find the node with the content
     * description `label` [String] with resource ID `R.string.feature_foryou_loading`
     * ("Loading for you…"), and we assert that the node exists.
     */
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

    /**
     * When the `onboardingUiState` is `NotShown` and the `feedState` is `Loading`, the
     * [NiaOverlayLoadingWheel] circular progress indicator whose content description is the
     * [String] with resource ID `R.string.feature_foryou_loading` ("Loading for you…") should
     * exist.
     *
     * We use the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule]
     * property [composeTestRule] to set the content of the screen to a [Box] containing a
     * [ForYouScreen] whose arguments are:
     *  - `isSyncing`: is `false`.
     *  - `onboardingUiState`: is [OnboardingUiState.NotShown].
     *  - `feedState`: is [NewsFeedUiState.Loading].
     *  - `deepLinkedUserNewsResource`: is `null`.
     *  - `onTopicCheckedChanged`: is a lambda that does nothing.
     *  - `onTopicClick`: is a lambda that does nothing.
     *  - `saveFollowedTopics`: is a lambda that does nothing.
     *  - `onNewsResourcesCheckedChanged`: is a lambda that does nothing.
     *  - `onNewsResourceViewed`: is a lambda that does nothing.
     *  - `onDeepLinkOpened`: is a lambda that does nothing.
     *
     * Then we call the [AndroidComposeTestRule.onNodeWithContentDescription] method of our
     * [AndroidComposeTestRule] property [composeTestRule] to find the node with the content
     * description `label` [String] with resource ID `R.string.feature_foryou_loading`
     * ("Loading for you…"), and we assert that the node exists.
     */
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

    /**
     * When the `onboardingUiState` is `NotShown` and the `feedState` is [NewsFeedUiState.Success]
     * then the `feed` of the [NewsFeedUiState.Success] should be displayed.
     *
     * We use the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the screen to a [Box] containing a [ForYouScreen] whose
     * arguments are:
     *  - `isSyncing`: is `false`.
     *  - `onboardingUiState`: is [OnboardingUiState.NotShown].
     *  - `feedState`: is [NewsFeedUiState.Success] whose `feed` argument is our [List] of
     *  [UserNewsResource] test data [userNewsResourcesTestData].
     *  - `deepLinkedUserNewsResource`: is `null`.
     *  - `onTopicCheckedChanged`: is a lambda that does nothing.
     *  - `onTopicClick`: is a lambda that does nothing.
     *  - `saveFollowedTopics`: is a lambda that does nothing.
     *  - `onNewsResourcesCheckedChanged`: is a lambda that does nothing.
     *  - `onNewsResourceViewed`: is a lambda that does nothing.
     *  - `onDeepLinkOpened`: is a lambda that does nothing.
     *
     * Then we use the [AndroidComposeTestRule.onNodeWithText] method of our [AndroidComposeTestRule]
     * property [composeTestRule] to find the node with the `text` given by the `title` property of
     * the first `UserNewsResource` in our [userNewsResourcesTestData] test data and on the
     * [SemanticsNodeInteraction] that is returned we assert that the node exists and
     * [SemanticsNodeInteraction.assertHasClickAction] asserts that the node has a click action.
     *
     * Next, we use the [AndroidComposeTestRule.onNode] method of our [AndroidComposeTestRule] to
     * find the node with the `matcher` [hasScrollToNodeAction] and call the
     * [SemanticsNodeInteraction.performScrollToNode] method to scroll to the node with the `matcher`
     * [hasText] with the `text` given by the `title` property of the second `UserNewsResource` in
     * our [userNewsResourcesTestData] test data, and the `substring` property set to `true`.
     *
     * Finally, we use the [AndroidComposeTestRule.onNodeWithText] method of our [AndroidComposeTestRule]
     * property [composeTestRule] to find the node with the `text` given by the `title` property
     * of the second `UserNewsResource` in our [userNewsResourcesTestData] test data and on the
     * [SemanticsNodeInteraction] that is returned we assert that the node exists and
     * [SemanticsNodeInteraction.assertHasClickAction] asserts that the node has a click action.
     */
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
