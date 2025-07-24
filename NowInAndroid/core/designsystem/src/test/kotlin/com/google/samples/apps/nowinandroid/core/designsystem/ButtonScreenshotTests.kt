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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaOutlinedButton
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
 * This Kotlin code defines a class [ButtonScreenshotTests] that uses screenshot testing to verify
 * the visual appearance of [NiaButton] and [NiaOutlinedButton] composables in different themes and
 * configurations. The meaning of the annotations is as follows:
 *  - @[RunWith] ([RobolectricTestRunner]::class): This annotation specifies that the tests should
 *  be run using [RobolectricTestRunner]. Robolectric allows you to run Android tests on the JVM
 *  without needing an emulator or physical device.
 *  - @[GraphicsMode] ([GraphicsMode.Mode.NATIVE]): This configures Robolectric's graphics mode to
 *  use native graphics. This is often necessary for accurate screenshot testing.
 *  - @[Config] (application = [HiltTestApplication]::class, qualifiers = "480dpi"):
 *  application = [HiltTestApplication]::class: This likely specifies a custom Application class for
 *  testing for Hilt dependency injection setup.
 *  qualifiers = "480dpi": This sets the screen density for the test environment to 480dpi (xxhdpi).
 *  This helps ensure that screenshots are generated for a specific screen configuration.
 *  - @[LooperMode] ([LooperMode.Mode.PAUSED]): This controls the Android Looper during tests.
 *  PAUSED mode gives you more control over the execution of asynchronous tasks, which can be
 *  helpful in UI testing.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, qualifiers = "480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
class ButtonScreenshotTests {
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
     * Tests that [NiaButton] is rendered correctly in multiple themes.
     * It uses our [AndroidComposeTestRule.captureMultiTheme] extension function to capture
     * screenshots of the component in each theme.
     */
    @Test
    fun niaButton_multipleThemes() {
        composeTestRule.captureMultiTheme(name = "Button") { description: String ->
            Surface {
                NiaButton(onClick = {}, text = { Text(text = "$description Button") })
            }
        }
    }

    /**
     * Test that the [NiaOutlinedButton] outlined button is displayed correctly in multiple themes.
     * It uses our [AndroidComposeTestRule.captureMultiTheme] extension function to capture
     * screenshots of the component in each theme.
     */
    @Test
    fun niaOutlineButton_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "Button",
            overrideFileName = "OutlineButton",
        ) { description: String ->
            Surface {
                NiaOutlinedButton(
                    onClick = {},
                    text = { Text(text = "$description OutlineButton") },
                )
            }
        }
    }

    /**
     * Tests that a [NiaButton] with a leading icon is rendered correctly in multiple themes.
     * It uses our [AndroidComposeTestRule.captureMultiTheme] extension function to capture
     * screenshots of the component in each theme.
     */
    @Test
    fun niaButton_leadingIcon_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "Button",
            overrideFileName = "ButtonLeadingIcon",
            shouldCompareAndroidTheme = false,
        ) { description: String ->
            Surface {
                NiaButton(
                    onClick = {},
                    text = { Text(text = "$description Icon Button") },
                    leadingIcon = { Icon(imageVector = NiaIcons.Add, contentDescription = null) },
                )
            }
        }
    }
}
