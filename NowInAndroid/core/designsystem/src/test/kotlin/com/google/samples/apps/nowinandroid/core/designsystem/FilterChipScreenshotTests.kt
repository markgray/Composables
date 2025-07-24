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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.then
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaBackground
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaFilterChip
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
 * This code defines a set of screenshot tests for the [NiaFilterChip] Jetpack Compose component.
 * Screenshot tests are a way to verify the visual appearance of your UI components by comparing
 * them against pre-recorded reference images. The meaning of the annotations is as follows:
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
class FilterChipScreenshotTests {

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
     * Tests that the [NiaFilterChip] is rendered correctly in multiple themes when its `selected`
     * argument is `false`.
     * It uses our [AndroidComposeTestRule.captureMultiTheme] extension function to capture
     * screenshots of the component in each theme.
     */
    @Test
    fun filterChip_multipleThemes() {
        composeTestRule.captureMultiTheme(name = "FilterChip") {
            Surface {
                NiaFilterChip(selected = false, onSelectedChange = {}) {
                    Text(text = "Unselected chip")
                }
            }
        }
    }

    /**
     * Tests that the [NiaFilterChip] is rendered correctly in multiple themes when its `selected`
     * argument is `true`.
     * It uses our [AndroidComposeTestRule.captureMultiTheme] extension function to capture
     * screenshots of the selected [NiaFilterChip] in each theme.
     * The `overrideFileName` argument is used to specify a different filename for the
     * generated screenshots, allowing for separate comparison of selected and unselected states.
     */
    @Test
    fun filterChip_multipleThemes_selected() {
        composeTestRule.captureMultiTheme(
            name = "FilterChip",
            overrideFileName = "FilterChipSelected",
        ) {
            Surface {
                NiaFilterChip(selected = true, onSelectedChange = {}) {
                    Text(text = "Selected Chip")
                }
            }
        }
    }

    /**
     * Tests the `selected` [NiaFilterChip] with a large font size.
     * It sets the font scale to 2f (twice the normal size) and captures a screenshot.
     * The [CompositionLocalProvider] that specifies that [LocalInspectionMode] provides `true`
     * is used to disable certain runtime checks that might interfere with screenshot testing.
     * The [DeviceConfigurationOverride] function is used to simulate a specific device
     * configuration for the test, in this case, a larger font scale and a fixed size for
     * the composable.
     */
    @Test
    fun filterChip_hugeFont() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                value = LocalInspectionMode provides true,
            ) {
                DeviceConfigurationOverride(
                    override = DeviceConfigurationOverride.FontScale(fontScale = 2f) then
                        DeviceConfigurationOverride.ForcedSize(
                            size = DpSize(
                                width = 80.dp,
                                height = 40.dp,
                            ),
                        ),
                ) {
                    NiaTheme {
                        NiaBackground {
                            NiaFilterChip(selected = true, onSelectedChange = {}) {
                                Text(text = "Chip")
                            }
                        }
                    }
                }
            }
        }
        composeTestRule.onRoot()
            .captureRoboImage(
                filePath = "src/test/screenshots/FilterChip/FilterChip_fontScale2.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }
}
