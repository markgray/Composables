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
import androidx.compose.material3.Text
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
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTopicTag
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
 * Screenshot tests for the [NiaTopicTag] component.
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
class TagScreenshotTests {

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
     * Tests that the [NiaTopicTag] component is rendered correctly in different themes.
     *
     * We call the [AndroidComposeTestRule.captureMultiTheme] extension function of our
     * [AndroidComposeTestRule] property [composeTestRule] with its `name` argument set to
     * "Tag" to capture screenshots of the [NiaTopicTag] component in different themes.
     * And in its `content` Composable lambda argument we compose a [NiaTopicTag] whose `followed`
     * argument is `true`, whose `onClick` argument is an empty lambda, and in its `content`
     * Composable lambda argument we compose a [Text] whose `text` argument is "TOPIC".
     */
    @Test
    fun tag_multipleThemes() {
        composeTestRule.captureMultiTheme(name = "Tag") {
            NiaTopicTag(followed = true, onClick = {}) {
                Text(text = "TOPIC")
            }
        }
    }

    /**
     * Tests the [NiaTopicTag] component with a large font scale to ensure it handles
     * accessibility settings correctly.
     *
     * It sets the font scale to 2f (twice the default size) using [DeviceConfigurationOverride].
     * Then, it renders the [NiaTopicTag] with a long text string ("LOOOOONG TOPIC") within a
     * [NiaTheme]. Finally, it captures a screenshot of the root composable and saves it to
     * "src/test/screenshots/Tag/Tag_fontScale2.png" for visual verification.
     *
     * We call the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the test, and in its `composable` lambda argument we
     * we compose a [CompositionLocalProvider] that provides `true` for [LocalInspectionMode] to
     * disable certain runtime checks that might interfere with screenshot testing. We call
     * [DeviceConfigurationOverride] to override [DeviceConfigurationOverride.Companion.FontScale]
     * to have a `fontScale` of 2f (twice the normal size), and in its `content` Composable lambda
     * argument we compose a [NiaTheme] whose `content` Composable lambda argument is a
     * [NiaTopicTag] whose `followed` argument is `true`, whose `onClick` argument is an empty
     * lambda, and in its `content` Composable lambda argument we compose a [Text] whose `text`
     * argument is "LOOOOONG TOPIC".
     *
     * Then we call the [AndroidComposeTestRule.onRoot] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to get the root of the compose hierarchy, and call its
     * [SemanticsNodeInteraction.captureRoboImage] method to capture a screenshot of the compose
     * hierarchy to the file path "src/test/screenshots/Tag/Tag_fontScale2.png" with the
     * `roborazziOptions` argument set to our constant [DefaultRoborazziOptions].
     */
    @Test
    fun tag_hugeFont() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                value = LocalInspectionMode provides true,
            ) {
                DeviceConfigurationOverride(
                    override = DeviceConfigurationOverride.Companion.FontScale(2f),
                ) {
                    NiaTheme {
                        NiaTopicTag(followed = true, onClick = {}) {
                            Text(text = "LOOOOONG TOPIC")
                        }
                    }
                }
            }
        }
        composeTestRule.onRoot()
            .captureRoboImage(
                filePath = "src/test/screenshots/Tag/Tag_fontScale2.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }
}
