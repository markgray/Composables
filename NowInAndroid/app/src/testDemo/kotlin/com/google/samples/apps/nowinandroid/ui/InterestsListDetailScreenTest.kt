/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.ui

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.ui.interests2pane.InterestsListDetailScreen
import com.google.samples.apps.nowinandroid.uitesthiltmanifest.HiltComponentActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject
import kotlin.properties.ReadOnlyProperty
import kotlin.test.assertTrue
import com.google.samples.apps.nowinandroid.feature.topic.R as FeatureTopicR

// TODO: Continue here on i7

/**
 * The screen dimension to use for tests that require a width where both panes are visible.
 * This is an arbitrary dimension chosen from the values defined in
 * [WindowSizeClass](https://developer.android.com/reference/kotlin/androidx/compose/material3/windowsizeclass/WindowSizeClass)
 */
private const val EXPANDED_WIDTH = "w1200dp-h840dp"

/**
 * The screen dimension to use for tests that require a width where only one pane is visible.
 * This is an arbitrary dimension chosen from the values defined in
 * [WindowSizeClass](https://developer.android.com/reference/kotlin/androidx/compose/material3/windowsizeclass/WindowSizeClass)
 */
private const val COMPACT_WIDTH = "w412dp-h915dp"

/**
 * UI tests for [InterestsListDetailScreen]. This test class thoroughly verifies the UI behavior of
 * the InterestsListDetailScreen under different screen width configurations and user interactions
 * (selecting a topic, pressing the back button). It uses Hilt for dependency injection, Robolectric
 * for running tests on the JVM, and Jetpack Compose testing APIs to interact with and assert the
 * state of the UI.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class InterestsListDetailScreenTest {

    /**
     * A [Rule] to enable injecting Hilt dependencies in tests. `order = 0`: Specifies the order in
     * which rules are executed. [HiltAndroidRule] needs to run before other rules that might depend
     * on Hilt.
     */
    @get:Rule(order = 0)
    val hiltRule: HiltAndroidRule = HiltAndroidRule(this)

    /**
     * A [Rule] to create a [AndroidComposeTestRule] that launches the [HiltComponentActivity] for
     * testing. This rule allows for testing Compose UIs within an Activity context, with Hilt
     * dependency injection enabled.
     */
    @get:Rule(order = 1)
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<HiltComponentActivity>, HiltComponentActivity> =
        createAndroidComposeRule<HiltComponentActivity>()

    /**
     * The [TopicsRepository] used for accessing [Topic] data. Injected by Hilt.
     */
    @Inject
    lateinit var topicsRepository: TopicsRepository

    /**
     * Retrieves a list of [Topic]s from the [TopicsRepository] synchronously.
     */
    private fun getTopics(): List<Topic> = runBlocking {
        topicsRepository.getTopics().first().sortedBy { it.name }
    }

    // The strings used for matching in these tests.

    /**
     * String resource for the placeholder text displayed in the detail pane when no topic is
     * selected. Used for assertions in tests.
     */
    private val placeholderText by composeTestRule.stringResource(FeatureTopicR.string.feature_topic_select_an_interest)

    /**
     * A test tag used to find the list pane in the InterestsListDetailScreen. This is used in UI
     * tests to assert the visibility and content of the list pane under various conditions.
     */
    private val listPaneTag = "interests:topics"

    /**
     * A test tag prefix for the topic detail pane. This is used to disambiguate the topic detail
     * pane from other elements in the UI.
     */
    private val Topic.testTag
        get() = "topic:${this.id}"

    /**
     * Sets up the test environment by injecting dependencies using Hilt. This method is annotated
     * with `@Before` to ensure it's executed before each test case.
     */
    @Before
    fun setup() {
        hiltRule.inject()
    }

    /**
     * Test for verifying the initial state of the InterestsListDetailScreen on an expanded width
     * device.
     *
     * This test checks that:
     * - Both the list pane (identified by [listPaneTag]) and the detail pane (showing the
     *   [placeholderText]) are displayed.
     */
    @Test
    @Config(qualifiers = EXPANDED_WIDTH)
    fun expandedWidth_initialState_showsTwoPanesWithPlaceholder() {
        composeTestRule.apply {
            setContent {
                NiaTheme {
                    InterestsListDetailScreen()
                }
            }

            onNodeWithTag(listPaneTag).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsDisplayed()
        }
    }

    /**
     * Test for verifying the initial state of the InterestsListDetailScreen on a compact width
     * device.
     *
     * This test checks that:
     * - Only the list pane (identified by [listPaneTag]) is displayed.
     * - The detail pane (showing the [placeholderText]) is NOT displayed.
     */
    @Test
    @Config(qualifiers = COMPACT_WIDTH)
    fun compactWidth_initialState_showsListPane() {
        composeTestRule.apply {
            setContent {
                NiaTheme {
                    InterestsListDetailScreen()
                }
            }

            onNodeWithTag(listPaneTag).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
        }
    }

    /**
     * Test for verifying the behavior of selecting a topic on an expanded width device.
     *
     * This test checks that when a topic is selected:
     *  - The list pane (identified by [listPaneTag]) remains displayed.
     *  - The placeholder text in the detail pane (identified by [placeholderText]) is no longer
     *  displayed.
     *  - The detail pane for the selected topic (identified by [Topic.testTag]) is displayed.
     */
    @Test
    @Config(qualifiers = EXPANDED_WIDTH)
    fun expandedWidth_topicSelected_updatesDetailPane() {
        composeTestRule.apply {
            setContent {
                NiaTheme {
                    InterestsListDetailScreen()
                }
            }

            val firstTopic = getTopics().first()
            onNodeWithText(firstTopic.name).performClick()

            onNodeWithTag(listPaneTag).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
            onNodeWithTag(firstTopic.testTag).assertIsDisplayed()
        }
    }

    /**
     * Test for verifying the behavior of InterestsListDetailScreen on a compact width device when a
     * topic is selected.
     *
     * This test checks that:
     *  - After selecting a topic, the list pane (identified by [listPaneTag]) is NOT displayed.
     *  - The placeholder text (identified by [placeholderText]) is NOT displayed.
     *  - The detail pane for the selected topic (identified by [Topic.testTag]) IS displayed.
     */
    @Test
    @Config(qualifiers = COMPACT_WIDTH)
    fun compactWidth_topicSelected_showsTopicDetailPane() {
        composeTestRule.apply {
            setContent {
                NiaTheme {
                    InterestsListDetailScreen()
                }
            }

            val firstTopic = getTopics().first()
            onNodeWithText(firstTopic.name).performClick()

            onNodeWithTag(listPaneTag).assertIsNotDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
            onNodeWithTag(firstTopic.testTag).assertIsDisplayed()
        }
    }

    /**
     * Test for verifying the behavior of pressing the back button from the topic detail view on an
     * expanded width device.
     *
     * This test ensures that when the back button is pressed:
     *  - The back press is not handled by the two-pane layout (InterestsListDetailScreen).
     *  - The back press "falls through" to the `BackHandler` set up in the test, indicating that
     *  the Interests screen itself remains active.
     */
    @Test
    @Config(qualifiers = EXPANDED_WIDTH)
    fun expandedWidth_backPressFromTopicDetail_leavesInterests() {
        var unhandledBackPress = false
        composeTestRule.apply {
            setContent {
                NiaTheme {
                    // Back press should not be handled by the two pane layout, and thus
                    // "fall through" to this BackHandler.
                    BackHandler {
                        unhandledBackPress = true
                    }
                    InterestsListDetailScreen()
                }
            }

            val firstTopic: Topic = getTopics().first()
            onNodeWithText(firstTopic.name).performClick()

            waitForIdle()
            Espresso.pressBack()

            assertTrue(unhandledBackPress)
        }
    }

    /**
     * Test for verifying the behavior of InterestsListDetailScreen on a compact width device when
     * pressing the back button from the topic detail view.
     *
     * This test simulates the following user interaction:
     *  1. A topic is selected from the list.
     *  2. The user navigates back (e.g., by pressing the system back button).
     *
     * It then checks that:
     *  - The list pane (identified by [listPaneTag]) is displayed again.
     *  - The placeholder text (identified by [placeholderText]) is NOT displayed.
     *  - The detail pane for the previously selected topic (identified by [Topic.testTag]) is NOT
     *  displayed.
     */
    @Test
    @Config(qualifiers = COMPACT_WIDTH)
    fun compactWidth_backPressFromTopicDetail_showsListPane() {
        composeTestRule.apply {
            setContent {
                NiaTheme {
                    InterestsListDetailScreen()
                }
            }

            val firstTopic = getTopics().first()
            onNodeWithText(firstTopic.name).performClick()

            waitForIdle()
            Espresso.pressBack()

            onNodeWithTag(listPaneTag).assertIsDisplayed()
            onNodeWithText(placeholderText).assertIsNotDisplayed()
            onNodeWithTag(firstTopic.testTag).assertIsNotDisplayed()
        }
    }
}

/**
 * A [ReadOnlyProperty] that provides the string resource for the given [resId].
 * This is useful for accessing string resources in tests, as string resources are not directly
 * accessible in tests.
 *
 * @param resId The resource ID of the string to retrieve.
 * @return A [ReadOnlyProperty] that provides the string resource for the given [resId].
 */
private fun AndroidComposeTestRule<*, *>.stringResource(
    @StringRes resId: Int,
): ReadOnlyProperty<Any, String> =
    ReadOnlyProperty { _, _ -> activity.getString(resId) }
