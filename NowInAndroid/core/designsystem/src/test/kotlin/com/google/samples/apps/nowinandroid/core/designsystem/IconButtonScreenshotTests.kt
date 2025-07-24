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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaBackground
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaIconToggleButton
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
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
 * Screenshot tests for the [NiaIconToggleButton].
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
class IconButtonScreenshotTests {

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
     * Test that the [NiaIconToggleButton] is displayed correctly in different themes when its
     * checked argument is `false`.
     *
     * It utilizes the [captureMultiTheme] function to capture screenshots of the component in
     * various themes, ensuring consistent appearance across different UI configurations.
     *
     * [AndroidComposeTestRule.captureMultiTheme] is called with its `name` argument the string
     * "IconButton" and in its `content` Composable lambda argument compose a [NiaIconToggleButton]
     * whose `checked` argument is `false`.
     */
    @Test
    fun iconButton_multipleThemes() {
        composeTestRule.captureMultiTheme(name = "IconButton") {
            NiaIconToggleExample(checked = false)
        }
    }

    /**
     * Test that the [NiaIconToggleButton] renders correctly in multiple themes when its checked
     * argument is `true`.
     *
     * [AndroidComposeTestRule.captureMultiTheme] is called with its `name` argument the string
     * "IconButton" and its `overrideFileName` argument the string "IconButtonUnchecked" (causes
     * the output to be written to the files "IconButtonUnchecked*.png" instead of "IconButton*.png").
     * In its `content` Composable lambda argument we compose a [Surface], and in the `content`
     * Composable lambda argument of the [Surface] we compose a [NiaIconToggleExample] whose `checked`
     * argument is `true`.
     */
    @Test
    fun iconButton_unchecked_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "IconButton",
            overrideFileName = "IconButtonUnchecked",
        ) {
            Surface {
                NiaIconToggleExample(checked = true)
            }
        }
    }

    /**
     * Composable function that displays an example of a [NiaIconToggleButton].
     *
     * This function is used to showcase the appearance and behavior of the [NiaIconToggleButton]
     * in different states (checked and unchecked).
     *
     * Our root composable is a [NiaIconToggleButton] whose arguments are:
     *  - `checked`: our [Boolean] parameter [checked].
     *  - `onCheckedChange`: an empty lambda function.
     *  - `icon`: a lambda function that renders an [Icon] whose `imageVector` argument is the
     *  [ImageVector] drawn by [NiaIcons.BookmarkBorder] and whose `contentDescription` argument is
     *  `null`.
     *  - `checkedIcon`: a lambda function that renders an [Icon] whose `imageVector` argument is the
     *  [ImageVector] drawn by [NiaIcons.Bookmark] and whose `contentDescription` argument is `null`.
     *
     * @param checked A boolean indicating whether the toggle button should be in the checked state.
     */
    @Composable
    private fun NiaIconToggleExample(checked: Boolean) {
        NiaIconToggleButton(
            checked = checked,
            onCheckedChange = { },
            icon = {
                Icon(
                    imageVector = NiaIcons.BookmarkBorder,
                    contentDescription = null,
                )
            },
            checkedIcon = {
                Icon(
                    imageVector = NiaIcons.Bookmark,
                    contentDescription = null,
                )
            },
        )
    }
}
