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

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.test.repository.FakeUserDataRepository
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
 * Tests that the Snackbar is correctly displayed on different screen sizes.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// Configure Robolectric to use a very large screen size that can fit all of the test sizes.
// This allows enough room to render the content under test without clipping or scaling.
@Config(application = HiltTestApplication::class, qualifiers = "w1000dp-h1000dp-480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
@HiltAndroidTest
class SnackbarScreenshotTests {

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
    lateinit var userDataRepository: FakeUserDataRepository

    /**
     * Repository for accessing and managing [Topic] data. Injected by Hilt.
     */
    @Inject
    lateinit var topicsRepository: TopicsRepository

    /**
     * Repository for accessing and managing [UserNewsResource]. Injected by Hilt.
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
     * Tests that the Snackbar is not shown on a phone sized screen until the
     * [SnackbarHostState.showSnackbar] method is called.
     */
    @Test
    fun phone_noSnackbar() {
        val snackbarHostState = SnackbarHostState()
        testSnackbarScreenshotWithSize(
            snackbarHostState = snackbarHostState,
            width = 400.dp,
            height = 500.dp,
            screenshotName = "snackbar_compact_medium_noSnackbar",
            action = { },
        )
    }

    /**
     * Tests that the Snackbar is shown on a phone sized screen when the
     * [SnackbarHostState.showSnackbar] method is called.
     */
    @Test
    fun snackbarShown_phone() {
        val snackbarHostState = SnackbarHostState()
        testSnackbarScreenshotWithSize(
            snackbarHostState = snackbarHostState,
            width = 400.dp,
            height = 500.dp,
            screenshotName = "snackbar_compact_medium",
        ) {
            snackbarHostState.showSnackbar(
                message = "This is a test snackbar message",
                actionLabel = "Action Label",
                duration = Indefinite,
            )
        }
    }

    /**
     * Tests that the Snackbar is shown on a foldable sized screen when the
     * [SnackbarHostState.showSnackbar] method is called.
     */
    @Test
    fun snackbarShown_foldable() {
        val snackbarHostState = SnackbarHostState()
        testSnackbarScreenshotWithSize(
            snackbarHostState = snackbarHostState,
            width = 600.dp,
            height = 600.dp,
            screenshotName = "snackbar_medium_medium",
        ) {
            snackbarHostState.showSnackbar(
                message = "This is a test snackbar message",
                actionLabel = "Action Label",
                duration = Indefinite,
            )
        }
    }

    /**
     * Tests that the Snackbar is shown on a tablet sized screen when the
     * [SnackbarHostState.showSnackbar] method is called.
     */
    @Test
    fun snackbarShown_tablet() {
        val snackbarHostState = SnackbarHostState()
        testSnackbarScreenshotWithSize(
            snackbarHostState = snackbarHostState,
            width = 900.dp,
            height = 900.dp,
            screenshotName = "snackbar_expanded_expanded",
        ) {
            snackbarHostState.showSnackbar(
                message = "This is a test snackbar message",
                actionLabel = "Action Label",
                duration = Indefinite,
            )
        }
    }

    /**
     * Helper function to test the Snackbar UI at a given screen [width] and [height].
     *
     * It sets up the [NiaApp] composable, providing the necessary dependencies and configurations.
     * It then executes the given [action] (e.g., showing a snackbar) and captures a screenshot
     * of the UI with the specified [screenshotName].
     *
     * @param snackbarHostState The [SnackbarHostState] used to control the snackbar.
     * @param width The width of the screen.
     * @param height The height of the screen.
     * @param screenshotName The name of the screenshot file.
     * @param action A suspend function that defines the action to be performed before taking the
     * screenshot. This is typically used to show a snackbar.
     */
    private fun testSnackbarScreenshotWithSize(
        snackbarHostState: SnackbarHostState,
        width: Dp,
        height: Dp,
        screenshotName: String,
        action: suspend () -> Unit,
    ) {
        lateinit var scope: CoroutineScope
        composeTestRule.setContent {
            CompositionLocalProvider(
                // Replaces images with placeholders
                LocalInspectionMode provides true,
            ) {
                scope = rememberCoroutineScope()

                DeviceConfigurationOverride(
                    DeviceConfigurationOverride.ForcedSize(size = DpSize(width, height)),
                ) {
                    BoxWithConstraints {
                        NiaTheme {
                            val appState: NiaAppState = rememberNiaAppState(
                                networkMonitor = networkMonitor,
                                userNewsResourceRepository = userNewsResourceRepository,
                                timeZoneMonitor = timeZoneMonitor,
                            )
                            NiaApp(
                                appState = appState,
                                snackbarHostState = snackbarHostState,
                                showSettingsDialog = false,
                                onSettingsDismissed = {},
                                onTopAppBarActionClick = {},
                                windowAdaptiveInfo = WindowAdaptiveInfo(
                                    windowSizeClass = WindowSizeClass.Companion.BREAKPOINTS_V1
                                        .computeWindowSizeClass(
                                            widthDp = maxWidth.value,
                                            heightDp = maxHeight.value,
                                        ),
                                    windowPosture = Posture(),
                                ),
                            )
                        }
                    }
                }
            }
        }

        scope.launch {
            action()
        }

        composeTestRule.onRoot()
            .captureRoboImage(
                filePath = "src/testDemo/screenshots/$screenshotName.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }
}
