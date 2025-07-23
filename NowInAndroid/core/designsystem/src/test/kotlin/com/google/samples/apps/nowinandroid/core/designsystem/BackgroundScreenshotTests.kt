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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaBackground
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaGradientBackground
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
 * This Kotlin code defines a class [BackgroundScreenshotTests] that uses screenshot testing to
 * verify the visual appearance of two Jetpack Compose components, [NiaBackground] and
 * [NiaGradientBackground], across different themes. The meaning of the annotations is as follows:
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
class BackgroundScreenshotTests {

    /**
     * The compose test rule used in this test. Test rules provide a way to run code before and after
     * test methods. [createAndroidComposeRule]<[ComponentActivity]>() creates a rule that provides
     * a testing environment for Jetpack Compose UI. It launches a simple [ComponentActivity] for
     * hosting the composables under test.
     */
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity> =
        createAndroidComposeRule<ComponentActivity>()

    /**
     * Tests that the [NiaBackground] component is displayed correctly in different themes. It uses
     * our [AndroidComposeTestRule.captureMultiTheme] extension function to capture screenshots of
     * the component in each theme.
     *
     * It captures a screenshot of the component in each theme and compares it to a baseline image.
     *  - The test is run on a 480dpi device.
     *  - The test is run in paused looper mode.
     *  - The test is run with the Hilt test application.
     *  - The test is run with the Robolectric test runner.
     *  - The test is run with native graphics mode.
     */
    @Test
    fun niaBackground_multipleThemes() {
        composeTestRule.captureMultiTheme(name = "Background") { description: String ->
            NiaBackground(modifier = Modifier.size(size = 100.dp)) {
                Text(text = "$description background")
            }
        }
    }

    /**
     * Tests that the gradient background is displayed correctly in different themes.
     * It captures screenshots of the [NiaGradientBackground] composable in various themes.
     * It uses our [AndroidComposeTestRule.captureMultiTheme] extension function to capture
     * screenshots of the component in each theme.
     */
    @Test
    fun niaGradientBackground_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "Background",
            overrideFileName = "GradientBackground",
        ) { description: String ->
            NiaGradientBackground(modifier = Modifier.size(size = 100.dp)) {
                Text(text = "$description background")
            }
        }
    }
}
