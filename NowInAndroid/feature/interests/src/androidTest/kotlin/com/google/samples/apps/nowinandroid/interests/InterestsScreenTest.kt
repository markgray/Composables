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

package com.google.samples.apps.nowinandroid.interests

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.testing.data.followableTopicTestData
import com.google.samples.apps.nowinandroid.feature.interests.InterestsScreen
import com.google.samples.apps.nowinandroid.feature.interests.InterestsUiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.samples.apps.nowinandroid.core.ui.R as CoreUiR
import com.google.samples.apps.nowinandroid.feature.interests.R as InterestsR

/**
 * UI test for checking the correct behaviour of the Interests screen;
 * Verifies that, when a specific UiState is set, the corresponding
 * composables and details are shown
 */
class InterestsScreenTest {

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
     * String resource for the loading content description
     */
    private lateinit var interestsLoading: String

    /**
     * String resource for the header of the empty screen
     */
    private lateinit var interestsEmptyHeader: String

    /**
     * String resource for the follow button content description
     */
    private lateinit var interestsTopicCardFollowButton: String

    /**
     * String resource for the unfollow button content description
     */
    private lateinit var interestsTopicCardUnfollowButton: String

    /**
     * Initializes our [String] properties to the [String]s in our resources that they are supposed
     * to match. The @[Before] annotation ensures that this method is executed before each the tests
     * in the class.
     */
    @Before
    fun setup() {
        composeTestRule.activity.apply {
            interestsLoading = getString(InterestsR.string.feature_interests_loading)
            interestsEmptyHeader = getString(InterestsR.string.feature_interests_empty_header)
            interestsTopicCardFollowButton =
                getString(CoreUiR.string.core_ui_interests_card_follow_button_content_desc)
            interestsTopicCardUnfollowButton =
                getString(CoreUiR.string.core_ui_interests_card_unfollow_button_content_desc)
        }
    }

    /**
     * When the screen is loading, show a loading wheel.
     *
     * This test verifies that when the [InterestsUiState] is [InterestsUiState.Loading], the loading
     * wheel with the content description [interestsLoading] is displayed.
     *
     * We call the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule]
     * property [composeTestRule] to set the content of the screen that we want to test to a
     * [InterestsScreen] whose `uiState` is [InterestsUiState.Loading]. Then we use the
     * [AndroidComposeTestRule.onNodeWithContentDescription] method to find the node with the
     * content description [interestsLoading] and assert that it exists.
     */
    @Test
    fun niaLoadingWheel_inTopics_whenScreenIsLoading_showLoading() {
        composeTestRule.setContent {
            InterestsScreen(uiState = InterestsUiState.Loading)
        }

        composeTestRule
            .onNodeWithContentDescription(label = interestsLoading)
            .assertExists()
    }

    /**
     * When topics are followed, show followed and unfollowed topics with info.
     * This test verifies that when the [InterestsUiState] is [InterestsUiState.Interests] with a
     * non-empty list of [FollowableTopic] objects, the screen displays the names of the topics and
     * the correct number of follow buttons.
     *
     * It sets the content of the screen to an [InterestsScreen] with the `uiState` set to a
     * [InterestsUiState.Interests], using [followableTopicTestData] as the list of `topics` and
     * `null` as the `selectedTopicId`.
     *
     * It then asserts that the names of the first three topics in [followableTopicTestData] are
     * displayed on the screen. Finally, it asserts that the number of nodes with the content
     * description [interestsTopicCardFollowButton] is equal to [numberOfUnfollowedTopics].
     */
    @Test
    fun interestsWithTopics_whenTopicsFollowed_showFollowedAndUnfollowedTopicsWithInfo() {
        composeTestRule.setContent {
            InterestsScreen(
                uiState = InterestsUiState.Interests(
                    topics = followableTopicTestData,
                    selectedTopicId = null,
                ),
            )
        }

        composeTestRule
            .onNodeWithText(text = followableTopicTestData[0].topic.name)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = followableTopicTestData[1].topic.name)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = followableTopicTestData[2].topic.name)
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithContentDescription(label = interestsTopicCardFollowButton)
            .assertCountEquals(expectedSize = numberOfUnfollowedTopics)
    }

    /**
     * When data is empty, show the empty screen. This test verifies that when the [InterestsUiState]
     * is [InterestsUiState.Empty], the empty screen with the header [interestsEmptyHeader] is
     * displayed.
     *
     * It sets the content of the screen to an [InterestsScreen] with the `uiState` set to
     * [InterestsUiState.Empty]. Then, it asserts that the node with the text [interestsEmptyHeader]
     * is displayed on the screen.
     */
    @Test
    fun topicsEmpty_whenDataIsEmptyOccurs_thenShowEmptyScreen() {
        composeTestRule.setContent {
            InterestsScreen(uiState = InterestsUiState.Empty)
        }

        composeTestRule
            .onNodeWithText(text = interestsEmptyHeader)
            .assertIsDisplayed()
    }

    /**
     * This is a wrapper Composable that allows us to provide a default implementation for the
     * `followTopic` and `onTopicClick` parameters of the [InterestsScreen] Composable that we are
     * testing. It composes the original [InterestsScreen] with its `uiState` argument the [uiState]
     * that it was called with, and with lambda stubs for the `followTopic` and `onTopicClick`
     * parameters.
     *
     * @param uiState the [InterestsUiState] that should be used to render the [InterestsScreen].
     */
    @Composable
    private fun InterestsScreen(uiState: InterestsUiState) {
        InterestsScreen(
            uiState = uiState,
            followTopic = { _, _ -> },
            onTopicClick = {},
        )
    }
}

/**
 * The number of topics in [followableTopicTestData] that are not followed. This is used in the
 * `interestsWithTopics_whenTopicsFollowed_showFollowedAndUnfollowedTopicsWithInfo` test to assert
 * that the correct number of follow buttons are displayed.
 */
private val numberOfUnfollowedTopics = followableTopicTestData.filter { topic: FollowableTopic ->
    !topic.isFollowed
}.size
