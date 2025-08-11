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

package com.google.samples.apps.nowinandroid.feature.foryou

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResultUtils.matchesElements
import com.google.android.apps.common.testing.accessibility.framework.checks.TextContrastCheck
import com.google.android.apps.common.testing.accessibility.framework.matcher.ElementMatchers.withText
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaBackground
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.testing.util.DefaultTestDevices
import com.google.samples.apps.nowinandroid.core.testing.util.captureForDevice
import com.google.samples.apps.nowinandroid.core.testing.util.captureMultiDevice
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState.Success
import com.google.samples.apps.nowinandroid.core.ui.UserNewsResourcePreviewParameterProvider
import com.google.samples.apps.nowinandroid.feature.foryou.OnboardingUiState.Loading
import com.google.samples.apps.nowinandroid.feature.foryou.OnboardingUiState.NotShown
import com.google.samples.apps.nowinandroid.feature.foryou.OnboardingUiState.Shown
import dagger.hilt.android.testing.HiltTestApplication
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode
import java.util.TimeZone

/**
 * Screenshot tests for the [ForYouScreen]. The meanings of the annotations are:
 *  - @[RunWith] (value = [RobolectricTestRunner]::class): This annotation indicates that the tests
 *  will be run using Robolectric, which is a framework that allows you to run Android tests on your
 *  local JVM without needing an emulator or device.
 *  - @[GraphicsMode] (value = [GraphicsMode.Mode.NATIVE]): This configures Robolectric to use
 *  native graphics, which is important for accurate screenshot testing of Compose UIs.
 *  - @[Config] (application = [HiltTestApplication]::class): This specifies a custom Application
 *  class ([HiltTestApplication]) to be used for these tests. This is often used with Hilt for
 *  dependency injection in tests.
 *  - @[LooperMode] (value = [LooperMode.Mode.PAUSED]): This controls how the Android Looper (which
 *  manages the message queue for a thread) behaves during tests. PAUSED mode gives you more control
 *  over the execution of asynchronous tasks.
 */
@RunWith(value = RobolectricTestRunner::class)
@GraphicsMode(value = GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class)
@LooperMode(value = LooperMode.Mode.PAUSED)
class ForYouScreenScreenshotTests {

    /**
     * A test rule that allows us to use Compose functionalities in an Android environment.
     *
     * It is used to set the content of the Compose application for testing purposes.
     * The [AndroidComposeTestRule] is created with a [ComponentActivity] as the activity
     * that will host the Compose content.
     */
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<
        ActivityScenarioRule<ComponentActivity>,
        ComponentActivity,
        > = createAndroidComposeRule<ComponentActivity>()

    /**
     * The list of user news resources used for testing.
     * It is initialized with the first value from the [UserNewsResourcePreviewParameterProvider].
     */
    private val userNewsResources: List<UserNewsResource> =
        UserNewsResourcePreviewParameterProvider().values.first()

    /**
     * Used to make time zone deterministic in tests.
     *
     * It sets the default time zone to "UTC" before each test is run, ensuring that any time-related
     * operations within the tests are consistent regardless of the local time zone of the machine
     * running the tests. This is important for creating reliable and reproducible tests that
     * involve date and time formatting or comparisons.
     */
    @Before
    fun setTimeZone() {
        // Make time zone deterministic in tests
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    /**
     * Tests the For You screen when the feed is populated.
     *
     * It captures screenshots of the For You screen with a populated feed on multiple devices.
     * The [ForYouScreen] is displayed with the following parameters:
     * - `isSyncing` is false, indicating that data synchronization is not in progress.
     * - `onboardingUiState` is [NotShown], meaning the onboarding UI is not displayed.
     * - `feedState` is [Success] with a list of [userNewsResources], representing a successfully
     * loaded feed.
     * - Various event handlers (`onTopicCheckedChanged`, `saveFollowedTopics`, etc.) are provided
     * as empty lambdas, as their specific behavior is not the focus of this screenshot test.
     * - `deepLinkedUserNewsResource` is null, indicating no deep-linked news resource.
     * - `onDeepLinkOpened` is an empty lambda.
     *
     * The test uses [captureMultiDevice] to take screenshots on different device configurations,
     * ensuring the UI renders correctly across various screen sizes and densities.
     */
    @Test
    fun forYouScreenPopulatedFeed() {
        composeTestRule.captureMultiDevice(screenshotName = "ForYouScreenPopulatedFeed") {
            NiaTheme {
                ForYouScreen(
                    isSyncing = false,
                    onboardingUiState = NotShown,
                    feedState = Success(
                        feed = userNewsResources,
                    ),
                    onTopicCheckedChanged = { _, _ -> },
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onTopicClick = {},
                    deepLinkedUserNewsResource = null,
                    onDeepLinkOpened = {},
                )
            }
        }
    }

    /**
     * Tests the For You screen when it is in a loading state.
     *
     * This test captures screenshots of the For You screen when both the onboarding UI and the
     * news feed are in a loading state. This is a common scenario when the app is first launched
     * or when data is being fetched.
     *
     * The [ForYouScreen] is displayed with the following parameters:
     * - `isSyncing` is false.
     * - `onboardingUiState` is [Loading], indicating the onboarding section is loading.
     * - `feedState` is [NewsFeedUiState.Loading], indicating the news feed is loading.
     * - Event handlers are provided as empty lambdas.
     * - `deepLinkedUserNewsResource` is null.
     * - `onDeepLinkOpened` is an empty lambda.
     *
     * The [captureMultiDevice] function is used to take screenshots on various device
     * configurations, ensuring the loading state is displayed correctly across different screens.
     */
    @Test
    fun forYouScreenLoading() {
        composeTestRule.captureMultiDevice(screenshotName = "ForYouScreenLoading") {
            NiaTheme {
                ForYouScreen(
                    isSyncing = false,
                    onboardingUiState = Loading,
                    feedState = NewsFeedUiState.Loading,
                    onTopicCheckedChanged = { _, _ -> },
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onTopicClick = {},
                    deepLinkedUserNewsResource = null,
                    onDeepLinkOpened = {},
                )
            }
        }
    }

    /**
     * Tests the For You screen when topic selection is shown.
     *
     * This test captures screenshots of the For You screen when the onboarding UI for topic
     * selection is visible. The news feed is populated with content. It composes the
     * [ForYouScreenTopicSelection] composable which composes the [ForYouScreen] with the arguments:
     * - `isSyncing` set to false.
     * - `onboardingUiState` is [Shown], providing a list of distinct followable topics extracted
     *   from [userNewsResources].
     * - `feedState` as [Success] with the [userNewsResources].
     * - Event handlers are empty lambdas.
     * - `deepLinkedUserNewsResource` is null.
     * - `onDeepLinkOpened` is an empty lambda.
     *
     * It uses [captureMultiDevice] to take screenshots across various device configurations.
     *
     * Accessibility suppressions are applied for:
     * - [TextContrastCheck] for elements like the "Done" button (when disabled),
     *   "What are you interested in?", and "UI" text, which might have contrast issues
     *   that need further investigation or are known false positives.
     */
    @Test
    fun forYouScreenTopicSelection() {
        composeTestRule.captureMultiDevice(
            screenshotName = "ForYouScreenTopicSelection",
            accessibilitySuppressions = Matchers.allOf(
                AccessibilityCheckResultUtils.matchesCheck(TextContrastCheck::class.java),
                Matchers.anyOf(
                    // Disabled Button
                    matchesElements(withText("Done")),

                    // TODO investigate, seems a false positive
                    matchesElements(withText("What are you interested in?")),
                    matchesElements(withText("UI")),
                ),
            ),
        ) {
            ForYouScreenTopicSelection()
        }
    }

    /**
     * Tests the For You screen when topic selection is shown in dark mode.
     *
     * This test specifically captures a screenshot of the For You screen with the topic selection
     * UI visible, running on a phone device configuration with dark mode enabled.
     *
     * The [ForYouScreenTopicSelection] composable is used, which sets up the [ForYouScreen] with:
     * - `isSyncing` as false.
     * - `onboardingUiState` as [Shown] with followable topics.
     * - `feedState` as [Success] with populated [userNewsResources].
     * - Event handlers as empty lambdas.
     * - `deepLinkedUserNewsResource` as null.
     * - `onDeepLinkOpened` as an empty lambda.
     *
     * The [captureForDevice] function ensures that the screenshot is taken on a "phone_dark"
     * device configuration, using the default phone specifications, and with dark mode explicitly
     * enabled. This helps verify that the UI adapts correctly to dark theme settings.
     */
    @Test
    fun forYouScreenTopicSelection_dark() {
        composeTestRule.captureForDevice(
            deviceName = "phone_dark",
            deviceSpec = DefaultTestDevices.PHONE.spec,
            screenshotName = "ForYouScreenTopicSelection",
            darkMode = true,
        ) {
            ForYouScreenTopicSelection()
        }
    }

    /**
     * Tests the For You screen when the feed is populated but also in a loading state.
     *
     * This scenario might occur if the app has some cached data to display but is also fetching
     * fresh data in the background.
     *
     * The [ForYouScreenPopulatedAndLoading] composable is used, which composes [ForYouScreen] with:
     * - `isSyncing` set to true, indicating background data synchronization.
     * - `onboardingUiState` is [Loading], showing the onboarding section is loading.
     * - `feedState` is [Success] with [userNewsResources], displaying existing feed content.
     * - Event handlers as empty lambdas.
     * - `deepLinkedUserNewsResource` as null.
     * - `onDeepLinkOpened` as an empty lambda.
     *
     * Screenshots are taken on multiple device configurations using [captureMultiDevice].
     */
    @Test
    fun forYouScreenPopulatedAndLoading() {
        composeTestRule.captureMultiDevice("ForYouScreenPopulatedAndLoading") {
            ForYouScreenPopulatedAndLoading()
        }
    }

    /**
     * Tests the For You screen when the feed is populated but also in a loading state, in dark mode.
     *
     * This test captures a screenshot of the [ForYouScreenPopulatedAndLoading] composable on a
     * phone device configuration with dark mode enabled. It verifies how the UI appears when
     * existing feed content is displayed while new data or onboarding information is still loading
     * under dark theme conditions.
     *
     * The [ForYouScreenPopulatedAndLoading] composable configures the [ForYouScreen] with:
     * - `isSyncing` as true (background sync).
     * - `onboardingUiState` as [Loading].
     * - `feedState` as [Success] with [userNewsResources].
     * - Event handlers as empty lambdas.
     * - `deepLinkedUserNewsResource` as null.
     * - `onDeepLinkOpened` as an empty lambda.
     *
     * The [captureForDevice] function ensures the screenshot is taken specifically for a "phone_dark"
     * setup, using default phone specifications and with dark mode enabled.
     */
    @Test
    fun forYouScreenPopulatedAndLoading_dark() {
        composeTestRule.captureForDevice(
            deviceName = "phone_dark",
            deviceSpec = DefaultTestDevices.PHONE.spec,
            screenshotName = "ForYouScreenPopulatedAndLoading",
            darkMode = true,
        ) {
            ForYouScreenPopulatedAndLoading()
        }
    }

    /**
     * Composable function that displays the For You screen with the topic selection UI.
     *
     * This is a helper composable used in screenshot tests to encapsulate the setup of the
     * [ForYouScreen] when the onboarding topic selection is shown. It provides predefined
     * states for the screen, making it easier to test this specific UI configuration.
     *
     * The [ForYouScreen] is configured with:
     * - `isSyncing` set to `false`.
     * - `onboardingUiState` as [Shown], providing a list of distinct followable topics extracted
     *   from the [userNewsResources]. This makes the topic selection part of the onboarding
     *   visible.
     * - `feedState` as [Success], populated with the [userNewsResources], so the news feed is
     *   also visible alongside the topic selection.
     * - All event handlers (`onTopicCheckedChanged`, `saveFollowedTopics`, etc.) are implemented
     *   as empty lambdas, as their interaction is not the focus of the screenshot test.
     * - `deepLinkedUserNewsResource` is `null`.
     * - `onDeepLinkOpened` is an empty lambda.
     *
     * The entire screen is wrapped in [NiaTheme] and [NiaBackground] to ensure consistent
     * theming and background for the screenshot.
     */
    @Composable
    private fun ForYouScreenTopicSelection() {
        NiaTheme {
            NiaBackground {
                ForYouScreen(
                    isSyncing = false,
                    onboardingUiState = Shown(
                        topics = userNewsResources.flatMap { news: UserNewsResource ->
                            news.followableTopics
                        }
                            .distinctBy { it.topic.id },
                    ),
                    feedState = Success(
                        feed = userNewsResources,
                    ),
                    onTopicCheckedChanged = { _, _ -> },
                    saveFollowedTopics = {},
                    onNewsResourcesCheckedChanged = { _, _ -> },
                    onNewsResourceViewed = {},
                    onTopicClick = {},
                    deepLinkedUserNewsResource = null,
                    onDeepLinkOpened = {},
                )
            }
        }
    }

    /**
     * Composable function that displays the For You screen with populated feed content
     * while also indicating a loading state for onboarding and background sync.
     *
     * This setup simulates a scenario where the app has cached feed data to show,
     * but is simultaneously fetching new data or onboarding information.
     *
     * It wraps the [ForYouScreen] with [NiaTheme] and [NiaBackground].
     * The [ForYouScreen] is configured with:
     * - `isSyncing = true` to show a sync indicator.
     * - `onboardingUiState = Loading` to show the onboarding section is loading.
     * - `feedState = Success(feed = userNewsResources)` to display the available news feed.
     * - Empty lambda functions for various interaction callbacks as they are not the focus
     *   of this visual test state.
     * - `deepLinkedUserNewsResource = null` and an empty `onDeepLinkOpened` lambda.
     */
    @Composable
    private fun ForYouScreenPopulatedAndLoading() {
        NiaTheme {
            NiaBackground {
                NiaTheme {
                    ForYouScreen(
                        isSyncing = true,
                        onboardingUiState = Loading,
                        feedState = Success(
                            feed = userNewsResources,
                        ),
                        onTopicCheckedChanged = { _, _ -> },
                        saveFollowedTopics = {},
                        onNewsResourcesCheckedChanged = { _, _ -> },
                        onNewsResourceViewed = {},
                        onTopicClick = {},
                        deepLinkedUserNewsResource = null,
                        onDeepLinkOpened = {},
                    )
                }
            }
        }
    }
}
