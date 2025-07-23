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
 * Screenshot tests for the background layout.
 * TODO: Continue here.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, qualifiers = "480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
class BackgroundScreenshotTests {

    /**
     * The compose test rule used in this test.
     */
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity> =
        createAndroidComposeRule<ComponentActivity>()

    /**
     * Tests that the [NiaBackground] component is displayed correctly in different themes.
     * It captures a screenshot of the component in each theme and compares it to a baseline image.
     * The test is run on a 480dpi device.
     * The test is run in paused looper mode.
     * The test is run with the Hilt test application.
     * The test is run with the Robolectric test runner.
     * The test is run with native graphics mode.
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
