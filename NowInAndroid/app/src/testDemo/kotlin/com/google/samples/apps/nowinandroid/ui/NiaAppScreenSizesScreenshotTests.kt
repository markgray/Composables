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

package com.google.samples.apps.nowinandroid.ui

import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.util.NetworkMonitor
import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneMonitor
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.testing.util.DefaultRoborazziOptions
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
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode
import java.util.TimeZone
import javax.inject.Inject

/**
 * Tests that the navigation UI is rendered correctly on different screen sizes.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// Configure Robolectric to use a very large screen size that can fit all of the test sizes.
// This allows enough room to render the content under test without clipping or scaling.
@Config(application = HiltTestApplication::class, qualifiers = "w1000dp-h1000dp-480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
@HiltAndroidTest
class NiaAppScreenSizesScreenshotTests {

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
     * Used to observe the current network status. Injected by Hilt.
     */
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    /**
     * Used to observe the current time zone. Injected by Hilt.
     */
    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    /**
     * Repository for accessing and managing [UserData]. Injected by Hilt.
     */
    @Inject
    lateinit var userDataRepository: UserDataRepository

    /**
     * Repository for accessing and managing [Topic] data. Injected by Hilt.
     */
    @Inject
    lateinit var topicsRepository: TopicsRepository

    /**
     * Repository for accessing and managing [UserNewsResource] data. Injected by Hilt.
     */
    @Inject
    lateinit var userNewsResourceRepository: UserNewsResourceRepository

    /**
     * Sets up the test environment by injecting Hilt dependencies and configuring initial user data.
     * This function is executed before each test case.
     * - Injects dependencies using [HiltAndroidRule.inject].
     * - Configures user data by:
     *     - Setting `shouldHideOnboarding` to `true` in [UserDataRepository].
     *     - Setting the first available topic as a followed topic in [UserDataRepository].
     *
     * It is run in a [runBlocking] coroutine and blocks the current thread interruptibly until its
     * completion.
     */
    @Before
    fun setup() {
        hiltRule.inject()

        // Configure user data
        runBlocking {
            userDataRepository.setShouldHideOnboarding(true)

            userDataRepository.setFollowedTopicIds(
                setOf(topicsRepository.getTopics().first().first().id),
            )
        }
    }

    /**
     * Sets the time zone to UTC for all tests in this class. This ensures that time-dependent
     * UI elements are rendered consistently across different test environments.
     */
    @Before
    fun setTimeZone() {
        // Make time zone deterministic in tests
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    /**
     * Helper function to capture screenshots of the [NiaApp] at a specific screen [width] and
     * [height].
     *
     * It sets the content of the [composeTestRule] to the [NiaApp] composable, providing necessary
     * dependencies and configurations:
     *  - [LocalInspectionMode] is set to `true` to enable inspection mode features for testing.
     *  - [DeviceConfigurationOverride] forces the screen size to the specified [width] and [height].
     *  - A `fakeAppState` is created using [rememberNiaAppState] with injected dependencies.
     *  - The [NiaApp] is rendered with the `fakeAppState` and a [WindowAdaptiveInfo] computed
     *  based on the provided [width] and [height].
     *
     * Finally, it captures a Robolectric image of the root composable and saves it to the specified
     * [screenshotName] in the "src/testDemo/screenshots/" directory.
     *
     * @param width The width of the screen in Dp.
     * @param height The height of the screen in Dp.
     * @param screenshotName The name of the screenshot file (without the .png extension).
     */
    private fun testNiaAppScreenshotWithSize(width: Dp, height: Dp, screenshotName: String) {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
            ) {
                DeviceConfigurationOverride(
                    override = DeviceConfigurationOverride.ForcedSize(DpSize(width, height)),
                ) {
                    NiaTheme {
                        val fakeAppState: NiaAppState = rememberNiaAppState(
                            networkMonitor = networkMonitor,
                            userNewsResourceRepository = userNewsResourceRepository,
                            timeZoneMonitor = timeZoneMonitor,
                        )
                        NiaApp(
                            appState = fakeAppState,
                            windowAdaptiveInfo = WindowAdaptiveInfo(
                                windowSizeClass = WindowSizeClass.Companion.BREAKPOINTS_V1
                                    .computeWindowSizeClass(
                                        widthDp = width.value,
                                        heightDp = height.value,
                                    ),
                                windowPosture = Posture(),
                            ),
                        )
                    }
                }
            }
        }

        composeTestRule.onRoot()
            .captureRoboImage(
                filePath = "src/testDemo/screenshots/$screenshotName.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }

    /**
     * Tests that the navigation bar is shown when the screen width and height are compact.
     * The test uses a screen size of 400dp width and 400dp height.
     * It captures a screenshot of the UI and saves it as
     * "compactWidth_compactHeight_showsNavigationBar.png".
     */
    @Test
    fun compactWidth_compactHeight_showsNavigationBar() {
        testNiaAppScreenshotWithSize(
            width = 400.dp,
            height = 400.dp,
            screenshotName = "compactWidth_compactHeight_showsNavigationBar",
        )
    }

    /**
     * Tests that the navigation bar is shown when the screen width is medium and height is compact.
     * The test uses a screen size of 610dp width and 400dp height.
     * It captures a screenshot of the UI and saves it as
     * "mediumWidth_compactHeight_showsNavigationBar.png".
     */
    @Test
    fun mediumWidth_compactHeight_showsNavigationBar() {
        testNiaAppScreenshotWithSize(
            width = 610.dp,
            height = 400.dp,
            screenshotName = "mediumWidth_compactHeight_showsNavigationBar",
        )
    }

    /**
     * Tests that the navigation bar is shown when the screen width is expanded and height is compact.
     * The test uses a screen size of 900dp width and 400dp height.
     * It captures a screenshot of the UI and saves it as
     * "expandedWidth_compactHeight_showsNavigationBar.png".
     */
    @Test
    fun expandedWidth_compactHeight_showsNavigationBar() {
        testNiaAppScreenshotWithSize(
            width = 900.dp,
            height = 400.dp,
            screenshotName = "expandedWidth_compactHeight_showsNavigationBar",
        )
    }

    /**
     * Tests that the navigation bar is shown when the screen width is compact and height is medium.
     * The test uses a screen size of 400dp width and 500dp height.
     * It captures a screenshot of the UI and saves it as
     * "compactWidth_mediumHeight_showsNavigationBar.png".
     */
    @Test
    fun compactWidth_mediumHeight_showsNavigationBar() {
        testNiaAppScreenshotWithSize(
            width = 400.dp,
            height = 500.dp,
            screenshotName = "compactWidth_mediumHeight_showsNavigationBar",
        )
    }

    /**
     * Tests that the navigation rail is shown when the screen width is medium and height is medium.
     * The test uses a screen size of 610dp width and 500dp height.
     * It captures a screenshot of the UI and saves it as
     * "mediumWidth_mediumHeight_showsNavigationRail.png".
     */
    @Test
    fun mediumWidth_mediumHeight_showsNavigationRail() {
        testNiaAppScreenshotWithSize(
            width = 610.dp,
            height = 500.dp,
            screenshotName = "mediumWidth_mediumHeight_showsNavigationRail",
        )
    }

    /**
     * Tests that the navigation rail is shown when the screen width is expanded and height is medium.
     * The test uses a screen size of 900dp width and 500dp height.
     * It captures a screenshot of the UI and saves it as
     * "expandedWidth_mediumHeight_showsNavigationRail.png".
     */
    @Test
    fun expandedWidth_mediumHeight_showsNavigationRail() {
        testNiaAppScreenshotWithSize(
            width = 900.dp,
            height = 500.dp,
            screenshotName = "expandedWidth_mediumHeight_showsNavigationRail",
        )
    }

    /**
     * Tests that the navigation bar is shown when the screen width is compact and height is expanded.
     * The test uses a screen size of 400dp width and 1000dp height.
     * It captures a screenshot of the UI and saves it as
     * "compactWidth_expandedHeight_showsNavigationBar.png".
     */
    @Test
    fun compactWidth_expandedHeight_showsNavigationBar() {
        testNiaAppScreenshotWithSize(
            width = 400.dp,
            height = 1000.dp,
            screenshotName = "compactWidth_expandedHeight_showsNavigationBar",
        )
    }

    /**
     * Tests that the navigation rail is shown when the screen width is medium and height is expanded.
     * The test uses a screen size of 610dp width and 1000dp height.
     * It captures a screenshot of the UI and saves it as
     * "mediumWidth_expandedHeight_showsNavigationRail.png".
     */
    @Test
    fun mediumWidth_expandedHeight_showsNavigationRail() {
        testNiaAppScreenshotWithSize(
            width = 610.dp,
            height = 1000.dp,
            screenshotName = "mediumWidth_expandedHeight_showsNavigationRail",
        )
    }

    /**
     * Tests that the navigation rail is shown when the screen height is expanded.
     * The test uses a screen size of 900.dp width and 1000,dp height.
     * It captures a screenshot of the UI and saves it as
     * "expandedWidth_expandedHeight_showsNavigationRail.png"
     */
    @Test
    fun expandedWidth_expandedHeight_showsNavigationRail() {
        testNiaAppScreenshotWithSize(
            width = 900.dp,
            height = 1000.dp,
            screenshotName = "expandedWidth_expandedHeight_showsNavigationRail",
        )
    }
}
