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

package com.google.samples.apps.nowinandroid.interests

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import com.google.samples.apps.nowinandroid.PACKAGE_NAME
import com.google.samples.apps.nowinandroid.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This benchmark navigates to the interests screen and scrolls the topic list up and down several
 * times.
 */
@RunWith(AndroidJUnit4::class)
class ScrollTopicListBenchmark {
    /**
     * This rule provides access to the [MacrobenchmarkRule] API for benchmarking. The library
     * outputs benchmarking results to both the Android Studio console and a JSON file with more
     * detail. It also provides trace files that you can load and analyze in Android Studio.
     */
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * This benchmark navigates to the interests screen and scrolls the topic list up and down several
     * times using a baseline profile with a [CompilationMode] of [CompilationMode.Partial].
     */
    @Test
    fun benchmarkStateChangeCompilationBaselineProfile(): Unit =
        benchmarkStateChange(CompilationMode.Partial())

    /**
     * Measures the performance of scrolling the topic list on the Interests screen.
     *
     * This function uses the [MacrobenchmarkRule.measureRepeated] method to record frame timings
     * while the UI is scrolling. It performs the following steps:
     *
     * 1. **Setup:**
     *    - Presses the home button to ensure a clean state.
     *    - Starts the main activity of the application and allows notifications.
     *    - Navigates to the "Interests" screen.
     *    - Waits for the UI to become idle.
     * 2. **Measurement:**
     *    - Waits for the topics to load on the Interests screen.
     *    - Scrolls the topic list down and then up three times.
     *
     * The benchmark is run using the [CompilationMode] parameter [compilationMode], 10 iterations,
     * and warm startup.
     *
     * We call the [MacrobenchmarkRule.measureRepeated] method of our [MacrobenchmarkRule] property
     * [benchmarkRule] with the arguments:
     *  - `packageName`: The package name of the app being benchmarked is the [String] returned by
     *  our global property [PACKAGE_NAME].
     *  - `metrics`: The [List] of [FrameTimingMetric] to measure.
     *  - `compilationMode`: The [CompilationMode] to use for the benchmark is our [CompilationMode]
     *  parmeter [compilationMode].
     *  - `iterations`: The number of times to run the benchmark is `10`.
     *  - `startupMode`: The [StartupMode] to use for the benchmark is [StartupMode.WARM].
     *  - `setupBlock`: A lambda function that sets up the benchmark by pressing the home button,
     *  calling [startActivityAndAllowNotifications] to start the activity allowing notifications,
     *  then navigating to the "Interests" screen by clicking on the [UiObject2] that is found on
     *  the [UiDevice] that contains the text "Interests", and then waiting for the UI to become idle.
     *
     * In the [MacrobenchmarkScope] `measureBlock` lambda argument we:
     *  - call [interestsWaitForTopics] method to wait for the topics to be displayed on screen.
     *  - call [interestsScrollTopicsDownUp] method to scroll the topics list down and up three times.
     *
     * @param compilationMode The compilation mode to use for the benchmark.
     */
    private fun benchmarkStateChange(compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            iterations = 10,
            startupMode = StartupMode.WARM,
            setupBlock = {
                // Start the app
                pressHome()
                startActivityAndAllowNotifications()
                // Navigate to interests screen
                device.findObject(By.text("Interests")).click()
                device.waitForIdle()
            },
        ) {
            interestsWaitForTopics()
            repeat(3) {
                interestsScrollTopicsDownUp()
            }
        }
}
