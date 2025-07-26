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

package com.google.samples.apps.nowinandroid.core.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTab
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTabRow
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.testing.util.DefaultRoborazziOptions
import com.google.samples.apps.nowinandroid.core.testing.util.captureMultiTheme
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

/**
 * Screenshot tests for the [NiaTab] and [NiaTabRow] composables.
 * The meaning of the annotations is as follows:
 *  - @[RunWith] ([RobolectricTestRunner]::class): This annotation specifies that the tests in this
 *  class should be run using [RobolectricTestRunner]. Robolectric is a framework that allows you to
 *  run Android tests on your local JVM without needing an emulator or physical device.
 *  - @[GraphicsMode] ([GraphicsMode.Mode.NATIVE]): This configures Robolectric to use native
 *  graphics for rendering, which can be more accurate for UI tests.
 *  - @[Config] (application = [HiltTestApplication]::class, qualifiers = "480dpi"):
 *  application = [HiltTestApplication]::class: This specifies a custom Application class
 *  ([HiltTestApplication]) to be used for these tests. This is common when using Hilt for
 *  dependency injection to set up a test-specific Hilt configuration.
 *  qualifiers = "480dpi": This sets the screen density for the test environment to 480dpi (xxhdpi).
 *  This helps ensure consistent rendering for screenshot comparisons.
 *  - @[LooperMode] ([LooperMode.Mode.PAUSED]): This controls the Android Looper during tests.
 *  PAUSED mode gives you more control over the execution of asynchronous tasks, which is often
 *  useful in UI testing.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, qualifiers = "480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
class TabsScreenshotTests {

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
     * Test that tabs are displayed correctly in different themes.
     *
     * This test uses the [captureMultiTheme] utility to capture screenshots of the [NiaTabsExample]
     * composable in different themes.
     *
     * The [NiaTabsExample] composable displays a [NiaTabRow] with two [NiaTab]s.
     */
    @Test
    fun tabs_multipleThemes() {
        composeTestRule.captureMultiTheme(name = "Tabs") {
            NiaTabsExample()
        }
    }

    /**
     * Tests that the tabs are rendered correctly with a large font size.
     *
     * We call the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the test, and in its `composable` lambda argument we
     * we compose a [CompositionLocalProvider] that provides `true` for [LocalInspectionMode] to
     * disable certain runtime checks that might interfere with screenshot testing. We call
     * [DeviceConfigurationOverride] to override [DeviceConfigurationOverride.Companion.FontScale]
     * to have a `fontScale` of 2f (twice the normal size), and in its `content` Composable lambda
     * argument we compose a [NiaTheme] whose `content` Composable lambda argument is a
     * [NiaTabsExample] whose `label` argument is "Looooong item".
     *
     * Then we call the [AndroidComposeTestRule.onRoot] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to get the root of the compose hierarchy, and call its
     * [SemanticsNodeInteraction.captureRoboImage] method to capture a screenshot of the compose
     * hierarchy to the file path "src/test/screenshots/Tabs/Tabs_fontScale2.png" with the
     * `roborazziOptions` argument set to our constant [DefaultRoborazziOptions].
     */
    @Test
    fun tabs_hugeFont() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                value = LocalInspectionMode provides true,
            ) {
                DeviceConfigurationOverride(
                    override = DeviceConfigurationOverride.FontScale(fontScale = 2f),
                ) {
                    NiaTheme {
                        NiaTabsExample(label = "Looooong item")
                    }
                }
            }
        }
        composeTestRule.onRoot()
            .captureRoboImage(
                filePath = "src/test/screenshots/Tabs/Tabs_fontScale2.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }

    /**
     * An example of [NiaTabRow] with two [NiaTab]'s
     *
     * Our root composable is a [Surface] in whose `content` Composable lambda argument we initialize
     * our [List] of [String] variable `titles` with a list containing our [String] parameter [label]
     * and the [String] "People". We then compose a [NiaTabRow] whose `selectedTabIndex` is `0`, and
     * in its `tabs` Composable lambda argument we use the [Iterable.forEachIndexed] method of our
     * [List] of [String] variable `titles` to iterate though its contents capturing the [Int] passed
     * the `action` lambda in variable `index` and the [String] passed the `action` lambda in variable
     * `title`, then we compose a [NiaTab] whose `selected` argument is `true` if `index == 0`, whose
     * `onClick` argument is an empty lambda, and in its `text` Composable lambda argument we compose
     * a [Text] whose `text` argument is `title`.
     *
     * @param label The label to display on the first tab.
     */
    @Composable
    private fun NiaTabsExample(label: String = "Topics") {
        Surface {
            val titles: List<String> = listOf(label, "People")
            NiaTabRow(selectedTabIndex = 0) {
                titles.forEachIndexed { index: Int, title: String ->
                    NiaTab(
                        selected = index == 0,
                        onClick = { },
                        text = { Text(text = title) },
                    )
                }
            }
        }
    }
}
