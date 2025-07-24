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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaNavigationBar
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaNavigationBarItem
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
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
 * Screenshot tests for the [NiaNavigationBar] navigation bar.
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
class NavigationScreenshotTests {

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
     * Tests that the [NiaNavigationBar] navigation bar is displayed correctly in different themes.
     * [AndroidComposeTestRule.captureMultiTheme] is called with its `name` argument the string
     * "Navigation". In its `content` Composable lambda argument we compose a [Surface], and in the
     * `content` Composable lambda argument of the [Surface] we compose a [NiaNavigationBarExample].
     */
    @Test
    fun navigation_multipleThemes() {
        composeTestRule.captureMultiTheme(name = "Navigation") {
            Surface {
                NiaNavigationBarExample()
            }
        }
    }

    /**
     * Tests the [NiaNavigationBarItem] navigation bar with a large font size.
     * It sets the font scale to 2f (twice the normal size) and captures a screenshot.
     * The [CompositionLocalProvider] that specifies that [LocalInspectionMode] provides `true`
     * is used to disable certain runtime checks that might interfere with screenshot testing.
     * The [DeviceConfigurationOverride] function is used to simulate a specific device
     * configuration for the test, in this case, a larger font scale and a fixed size for
     * the composable.
     */
    @Test
    fun navigation_hugeFont() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                value = LocalInspectionMode provides true,
            ) {
                DeviceConfigurationOverride(
                    override = DeviceConfigurationOverride.FontScale(fontScale = 2f),
                ) {
                    NiaTheme {
                        NiaNavigationBarExample(label = "Looong item")
                    }
                }
            }
        }
        composeTestRule.onRoot()
            .captureRoboImage(
                filePath = "src/test/screenshots/Navigation" +
                    "/Navigation_fontScale2.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }

    /**
     * Composes an example of the [NiaNavigationBar] using fake [NiaNavigationBarItem]'s.
     *
     * Our root composable is a [NiaNavigationBar] in whose [RowScope] `content` Composable lambda
     * argument we use the [Iterable.forEach] method of the range `0 .. 2` to loop three times,
     * capturing the [Int] passed to the `action` lambda argument in variable `index` and then
     * composing a [NiaNavigationBarItem] whose arguments are:
     *  - `icon`: is a lambda that composes an [Icon] whose `imageVector` argument is the
     *  [ImageVector] drawn by [NiaIcons.UpcomingBorder], and whose `contentDescription` argument
     *  is an empty string.
     *  - `selectedIcon`: is a lambda that composes an [Icon] whose `imageVector` argument is the
     *  [ImageVector] drawn by [NiaIcons.Upcoming], and whose `contentDescription` argument is an
     *  empty string.
     *  - `label`: is a lambda that composes a [Text] whose `text` argument is our [String] parameter
     *  [label].
     *  - `selected`: is `true` if our [Int] variable `index` is equal to `0`, and `false` otherwise.
     *  - `onClick`: is a lambda that does nothing.
     *
     * @param label The label for each [NiaNavigationBarItem] navigation item.
     */
    @Composable
    private fun NiaNavigationBarExample(label: String = "Item") {
        NiaNavigationBar {
            (0..2).forEach { index: Int ->
                NiaNavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = NiaIcons.UpcomingBorder,
                            contentDescription = "",
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = NiaIcons.Upcoming,
                            contentDescription = "",
                        )
                    },
                    label = { Text(text = label) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}
