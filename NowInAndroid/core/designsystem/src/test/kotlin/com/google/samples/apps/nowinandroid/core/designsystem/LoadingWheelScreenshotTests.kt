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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaLoadingWheel
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaOverlayLoadingWheel
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
 * Screenshot tests for the [NiaLoadingWheel] and [NiaOverlayLoadingWheel] loading wheels.
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
class LoadingWheelScreenshotTests {

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
     * Test that the [NiaLoadingWheel] loading wheel is displayed correctly in multiple themes.
     * It captures a screenshot of the loading wheel in each theme.
     * [AndroidComposeTestRule.captureMultiTheme] is called with its `name` argument the string
     * "LoadingWheel" and in its `content` Composable lambda argument we compose a [Surface] in
     * whose `content` Composable lambda argument we compose a [NiaLoadingWheel] whose `contentDesc`
     * argument is the string "test".
     */
    @Test
    fun loadingWheel_multipleThemes() {
        composeTestRule.captureMultiTheme(name = "LoadingWheel") {
            Surface {
                NiaLoadingWheel(contentDesc = "test")
            }
        }
    }

    /**
     * Test that the overlay loading wheel is displayed correctly in multiple themes.
     * It captures a screenshot of the [NiaOverlayLoadingWheel] in a [Surface] for each theme.
     * [AndroidComposeTestRule.captureMultiTheme] is called with its `name` argument the string
     * "LoadingWheel" and its `overrideFileName` argument the string "OverlayLoadingWheel" (causes
     * the output to be written to the files "OverlayLoadingWheel*.png" instead of "LoadingWheel*.png").
     * In its `content` Composable lambda argument we compose a [Surface] in whose `content` Composable
     * lambda argument we compose a [NiaOverlayLoadingWheel] whose `contentDesc` argument is the
     * string "test".
     */
    @Test
    fun overlayLoadingWheel_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "LoadingWheel",
            overrideFileName = "OverlayLoadingWheel",
        ) {
            Surface {
                NiaOverlayLoadingWheel(contentDesc = "test")
            }
        }
    }

    /**
     * Test for the animation of the [NiaLoadingWheel]. It captures screenshots of the loading wheel
     * at different time intervals to verify the animation. The [MainTestClock.autoAdvance] property
     * of the [AndroidComposeTestRule.mainClock] is set to `false` in order to disable the automatic
     * advancing of the clock when new compositions are scheduled. We then call the
     * [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] variable
     * `composeTestRule` test rule to have it compose a [NiaLoadingWheel] wrapped in our [NiaTheme]
     * custom [MaterialTheme] into the UI. We then loop through a [List] of [Long] millisecond
     * values: 20L, 115L, 724L, and 1000L, and in each iteration we advance the `mainClock` by that
     * amount, then capture a screenshot of the root composable and save it to a PNG file whose name
     * includes the `deltaTime` value.
     */
    @Test
    fun loadingWheelAnimation() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            NiaTheme {
                NiaLoadingWheel(contentDesc = "")
            }
        }
        // Try multiple frames of the animation; some arbitrary, some synchronized with duration.
        listOf(20L, 115L, 724L, 1000L).forEach { deltaTime: Long ->
            composeTestRule.mainClock.advanceTimeBy(milliseconds = deltaTime)
            composeTestRule.onRoot()
                .captureRoboImage(
                    filePath = "src/test/screenshots/LoadingWheel/LoadingWheel_animation_$deltaTime.png",
                    roborazziOptions = DefaultRoborazziOptions,
                )
        }
    }
}
