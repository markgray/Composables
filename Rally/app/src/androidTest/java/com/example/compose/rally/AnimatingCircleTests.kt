/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.rally

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.filters.SdkSuppress
import com.example.compose.rally.ui.components.AnimatedCircle
import com.example.compose.rally.ui.theme.RallyTheme
import org.junit.Rule
import org.junit.Test

/**
 * Test to showcase `AnimationClockTestRule` present in [ComposeContentTestRule]. It allows for
 * animation testing at specific points in time.
 *
 * For assertions, a simple screenshot testing framework is used. It requires SDK 26+ and to
 * be run on a device with 420dpi, as that the density used to generate the golden images
 * present in androidTest/assets. It runs bitmap comparisons on device.
 *
 * Note that different systems can produce slightly different screenshots making the test fail.
 */
@ExperimentalTestApi
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
class AnimatingCircleTests {

    /**
     * Test rule for Compose UIs.
     * It allows setting up the content under test using `setContent` and interact with it using
     * `onNode` series of methods.
     */
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    /**
     * Tests that the animation is in the correct state when it's idle by enabling autoadvance
     * (the clock should be advanced by the testing framework while awaiting idleness in order to
     * process any pending work that is driven by this clock), starting the animation, then
     * comparing a screenshot when it has finished with the "circle_done.png".
     */
    @Test
    fun circleAnimation_idle_screenshot() {
        composeTestRule.mainClock.autoAdvance = true
        showAnimatedCircle()
        assertScreenshotMatchesGolden("circle_done", composeTestRule.onRoot())
    }

    /**
     * Tests the initial state of the animation by comparing a screenshot with a golden image.
     */
    @Test
    fun circleAnimation_initial_screenshot() {
        compareTimeScreenshot(0, "circle_initial")
    }

    /**
     * Tests the state of the animation just before the delay ends by comparing a screenshot
     * with a golden image.
     */
    @Test
    fun circleAnimation_beforeDelay_screenshot() {
        compareTimeScreenshot(499, "circle_initial")
    }

    /**
     * Tests the animation at a point in time by comparing a screenshot with a golden image.
     */
    @Test
    fun circleAnimation_midAnimation_screenshot() {
        compareTimeScreenshot(600, "circle_100")
    }

    /**
     * Tests the state of the animation when it's finished by comparing a screenshot with a
     * golden image.
     */
    @Test
    fun circleAnimation_animationDone_screenshot() {
        compareTimeScreenshot(1500, "circle_done")
    }

    /**
     * Pauses the clock, sets the [AnimatedCircle] to be displayed and advances the clock by
     * [timeMs]. Finally, it captures a screenshot and compares it with the golden image named
     * [goldenName].
     *
     * @param timeMs The amount of time in milliseconds to advance the clock by.
     * @param goldenName The name of the golden image to compare the screenshot with.
     */
    private fun compareTimeScreenshot(timeMs: Long, goldenName: String) {
        // Start with a paused clock
        composeTestRule.mainClock.autoAdvance = false

        // Start the unit under test
        showAnimatedCircle()

        // Advance clock (keeping it paused)
        composeTestRule.mainClock.advanceTimeBy(timeMs)

        // Take screenshot and compare with golden image in androidTest/assets
        assertScreenshotMatchesGolden(goldenName, composeTestRule.onRoot())
    }

    /**
     * Shows an [AnimatedCircle] with a fixed set of proportions and colors.
     */
    private fun showAnimatedCircle() {
        composeTestRule.setContent {
            RallyTheme {
                AnimatedCircle(
                    modifier = Modifier.background(Color.White).size(320.dp),
                    proportions = listOf(0.25f, 0.5f, 0.25f),
                    colors = listOf(Color.Red, Color.DarkGray, Color.Black)
                )
            }
        }
    }
}
