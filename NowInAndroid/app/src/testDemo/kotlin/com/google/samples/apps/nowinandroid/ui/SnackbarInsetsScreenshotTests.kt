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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsStartWidth
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.roundToIntRect
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsCompat
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
@Suppress("DEPRECATION")
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// Configure Robolectric to use a very large screen size that can fit all of the test sizes.
// This allows enough room to render the content under test without clipping or scaling.
@Config(application = HiltTestApplication::class, qualifiers = "w1000dp-h1000dp-480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
@HiltAndroidTest
class SnackbarInsetsScreenshotTests {

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
     * Repository for accessing and managing [UserNewsResource] data. Injected by Hilt.
     */
    @Inject
    lateinit var userNewsResourceRepository: UserNewsResourceRepository

    /**
     * Sets up the test environment by injecting dependencies and configuring user data.
     * This function is executed before each test case.
     *  - Injects dependencies using [HiltAndroidRule.inject].
     *  - Configures user data by:
     *      - Setting `shouldHideOnboarding` to `true` in [FakeUserDataRepository].
     *      - Setting the first available topic as a followed topic in [FakeUserDataRepository].
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
            screenshotName = "insets_snackbar_compact_medium_noSnackbar",
            action = { },
        )
    }

    /**
     * Tests that the Snackbar is shown correctly on a phone-sized screen when the
     * [SnackbarHostState.showSnackbar] method is called.
     */
    @Test
    fun snackbarShown_phone() {
        val snackbarHostState = SnackbarHostState()
        testSnackbarScreenshotWithSize(
            snackbarHostState = snackbarHostState,
            width = 400.dp,
            height = 500.dp,
            screenshotName = "insets_snackbar_compact_medium",
        ) {
            snackbarHostState.showSnackbar(
                message = "This is a test snackbar message",
                actionLabel = "Action Label",
                duration = Indefinite,
            )
        }
    }

    /**
     * Tests that the Snackbar is correctly displayed on a foldable device.
     */
    @Test
    fun snackbarShown_foldable() {
        val snackbarHostState = SnackbarHostState()
        testSnackbarScreenshotWithSize(
            snackbarHostState = snackbarHostState,
            width = 600.dp,
            height = 600.dp,
            screenshotName = "insets_snackbar_medium_medium",
        ) {
            snackbarHostState.showSnackbar(
                message = "This is a test snackbar message",
                actionLabel = "Action Label",
                duration = Indefinite,
            )
        }
    }

    /**
     * Tests that the Snackbar is correctly displayed on a tablet.
     */
    @Test
    fun snackbarShown_tablet() {
        val snackbarHostState = SnackbarHostState()
        testSnackbarScreenshotWithSize(
            snackbarHostState = snackbarHostState,
            width = 900.dp,
            height = 900.dp,
            screenshotName = "insets_snackbar_expanded_expanded",
        ) {
            snackbarHostState.showSnackbar(
                message = "This is a test snackbar message",
                actionLabel = "Action Label",
                duration = Indefinite,
            )
        }
    }

    /**
     * Test a snackbar screenshot with a given size.
     *
     * @param snackbarHostState The snackbar host state.
     * @param width The width of the device.
     * @param height The height of the device.
     * @param screenshotName The name of the screenshot.
     * @param action The action to perform before taking the screenshot.
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
                    DeviceConfigurationOverride(
                        override = DeviceConfigurationOverride.WindowInsets(
                            WindowInsetsCompat.Builder()
                                .setInsets(
                                    WindowInsetsCompat.Type.statusBars(),
                                    DpRect(
                                        left = 0.dp,
                                        top = 64.dp,
                                        right = 0.dp,
                                        bottom = 0.dp,
                                    ).toInsets(),
                                )
                                .setInsets(
                                    WindowInsetsCompat.Type.navigationBars(),
                                    DpRect(
                                        left = 64.dp,
                                        top = 0.dp,
                                        right = 64.dp,
                                        bottom = 64.dp,
                                    ).toInsets(),
                                )
                                .build(),
                        ),
                    ) {
                        BoxWithConstraints(modifier = Modifier.testTag(tag = "root")) {
                            NiaTheme {
                                val appState = rememberNiaAppState(
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
                                DebugVisibleWindowInsets()
                            }
                        }
                    }
                }
            }
        }

        scope.launch {
            action()
        }

        composeTestRule.onNodeWithTag("root")
            .captureRoboImage(
                filePath = "src/testDemo/screenshots/$screenshotName.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }
}

/**
 * A composable function that displays visible window insets for debugging purposes.
 * It draws rectangles representing the safe drawing insets (status bar, navigation bar, etc.)
 * on the screen.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param debugColor The color used to draw the debug rectangles.
 * Defaults to a semi-transparent magenta.
 */
@Composable
fun DebugVisibleWindowInsets(
    modifier: Modifier = Modifier,
    debugColor: Color = Color.Magenta.copy(alpha = 0.5f),
) {
    Box(modifier = modifier.fillMaxSize()) {
        Spacer(
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .fillMaxHeight()
                .windowInsetsStartWidth(insets = WindowInsets.safeDrawing)
                .windowInsetsPadding(insets = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
                .background(color = debugColor),
        )
        Spacer(
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .fillMaxHeight()
                .windowInsetsEndWidth(insets = WindowInsets.safeDrawing)
                .windowInsetsPadding(insets = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
                .background(color = debugColor),
        )
        Spacer(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsTopHeight(insets = WindowInsets.safeDrawing)
                .background(color = debugColor),
        )
        Spacer(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsBottomHeight(insets = WindowInsets.safeDrawing)
                .background(color = debugColor),
        )
    }
}

/**
 * Converts a [DpRect] to an [Insets] object using the current [Density].
 * This is a convenience composable function that retrieves the current density from the
 * [LocalDensity] composition local.
 *
 * @return The [Insets] object representing the [DpRect] in pixels.
 */
@Composable
private fun DpRect.toInsets(): Insets = toInsets(LocalDensity.current)

/**
 * Converts a [DpRect] to an [Insets] object using the provided [Density].
 *
 * @param density The density to use for the conversion.
 * @return The [Insets] object representing the [DpRect] in pixels.
 */
private fun DpRect.toInsets(density: Density): Insets =
    Insets.of(with(density) { toRect() }.roundToIntRect().toAndroidRect())
