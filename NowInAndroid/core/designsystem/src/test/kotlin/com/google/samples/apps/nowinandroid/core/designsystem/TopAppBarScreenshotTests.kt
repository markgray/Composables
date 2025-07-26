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

import android.R
import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTopAppBar
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
 * Screenshot tests for the [NiaTopAppBar].
 * The meaning of the annotations are:
 *  - @[OptIn] ([ExperimentalMaterial3Api]::class): This indicates that the code uses experimental
 *  APIs from Material 3. It's a way to acknowledge that these APIs might change in future releases.
 *  - @[RunWith] ([RobolectricTestRunner]::class): This annotation specifies that the tests in this
 *  class should be run using RobolectricTestRunner. Robolectric is a framework that allows you to
 *  run Android tests on your local JVM without needing an emulator or a physical device.
 *  - @[GraphicsMode] ([GraphicsMode.Mode.NATIVE]): This Robolectric annotation configures how
 *  graphics are handled. NATIVE mode attempts to use the host machine's native graphics pipeline,
 *  which can be more accurate for rendering.
 *  - @[Config] (application = [HiltTestApplication]::class, qualifiers = "480dpi"):
 *  application = [HiltTestApplication]::class: This Robolectric annotation specifies a custom
 *  Application class ([HiltTestApplication]) to be used for these tests. This is common when using
 *  Hilt for dependency injection, allowing for test-specific configurations.
 *  qualifiers = "480dpi": This sets the screen density qualifier for the test environment to 480dpi
 *  (xxhdpi). This helps ensure that UI elements are rendered at a consistent size for screenshot
 *  comparison.
 *  - @[LooperMode] ([LooperMode.Mode.PAUSED]): This Robolectric annotation controls the Android
 *  Looper. PAUSED mode gives you more control over the execution of asynchronous tasks, which is
 *  crucial for predictable screenshot testing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, qualifiers = "480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
class TopAppBarScreenshotTests {

    /**
     * The compose JUnit Test [Rule] that can be used to control the composables under test.
     * [createAndroidComposeRule]<[ComponentActivity]>() creates a rule that launches a generic
     * [ComponentActivity] for hosting the Composable under test.
     */
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<
        ActivityScenarioRule<ComponentActivity>,
        ComponentActivity,
        > = createAndroidComposeRule<ComponentActivity>()

    /**
     * Test that checks the appearance of the [NiaTopAppBar] in different themes.
     * It uses the [captureMultiTheme] function to capture screenshots of the [NiaTopAppBarExample]
     * composable in various theme configurations.
     */
    @Test
    fun topAppBar_multipleThemes() {
        composeTestRule.captureMultiTheme(name = "TopAppBar") {
            NiaTopAppBarExample()
        }
    }

    /**
     * Tests the [NiaTopAppBar] with a large font scale to ensure it handles accessibility settings
     * correctly.
     *
     * We call the [AndroidComposeTestRule.setContent] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to set the content of the test, and in its `composable` lambda argument we
     * we compose a [CompositionLocalProvider] that provides `true` for [LocalInspectionMode] to
     * disable certain runtime checks that might interfere with screenshot testing. We call
     * [DeviceConfigurationOverride] to override [DeviceConfigurationOverride.Companion.FontScale]
     * to have a `fontScale` of 2f (twice the normal size), and in its `content` Composable lambda
     * argument we compose a [NiaTheme] whose `content` Composable lambda argument is a
     * [NiaTopAppBarExample].
     *
     * Then we call the [AndroidComposeTestRule.onRoot] method of our [AndroidComposeTestRule] property
     * [composeTestRule] to get the root of the compose hierarchy, and call its
     * [SemanticsNodeInteraction.captureRoboImage] method to capture a screenshot of the compose
     * hierarchy to the file path "src/test/screenshots/TopAppBar/TopAppBar_fontScale2.png" with the
     * `roborazziOptions` argument set to our constant [DefaultRoborazziOptions].
     */
    @Test
    fun topAppBar_hugeFont() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                value = LocalInspectionMode provides true,
            ) {
                DeviceConfigurationOverride(
                    override = DeviceConfigurationOverride.FontScale(fontScale = 2f),
                ) {
                    NiaTheme {
                        NiaTopAppBarExample()
                    }
                }
            }
        }
        composeTestRule.onRoot()
            .captureRoboImage(
                filePath = "src/test/screenshots/TopAppBar/TopAppBar_fontScale2.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }

    /**
     * A composable function that displays an example of the [NiaTopAppBar].
     * This is used for screenshot testing purposes.
     *
     * Our root composable is a [NiaTopAppBar] whose arguments are:
     *  - `titleRes`: is `R.string.untitled`
     *  - `navigationIcon`: is [NiaIcons.Search]
     *  - `navigationIconContentDescription`: is "Navigation icon"
     *  - `actionIcon`: is [NiaIcons.MoreVert]
     *  - `actionIconContentDescription`: is "Action icon"
     */
    @Composable
    private fun NiaTopAppBarExample() {
        NiaTopAppBar(
            titleRes = R.string.untitled,
            navigationIcon = NiaIcons.Search,
            navigationIconContentDescription = "Navigation icon",
            actionIcon = NiaIcons.MoreVert,
            actionIconContentDescription = "Action icon",
        )
    }
}
