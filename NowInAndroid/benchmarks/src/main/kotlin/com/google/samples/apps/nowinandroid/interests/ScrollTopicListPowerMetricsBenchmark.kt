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

import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.PowerCategory
import androidx.benchmark.macro.PowerCategoryDisplayLevel
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import com.google.samples.apps.nowinandroid.PACKAGE_NAME
import com.google.samples.apps.nowinandroid.allowNotifications
import com.google.samples.apps.nowinandroid.foryou.forYouScrollFeedDownUp
import com.google.samples.apps.nowinandroid.foryou.forYouSelectTopics
import com.google.samples.apps.nowinandroid.foryou.forYouWaitForContent
import com.google.samples.apps.nowinandroid.foryou.setAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This benchmark measures the power usage of scrolling through the topic list in the "For You" screen.
 * It includes setup steps to navigate to the "For You" screen, select some topics, and then scrolls
 * the feed up and down multiple times.
 * This benchmark is designed to be run on Android Q (API level 29) and above.
 */
@OptIn(ExperimentalMetricApi::class)
@RequiresApi(VERSION_CODES.Q)
@RunWith(AndroidJUnit4::class)
class ScrollTopicListPowerMetricsBenchmark {
    /**
     * This rule provides access to the [MacrobenchmarkRule] API for benchmarking. The library
     * outputs benchmarking results to both the Android Studio console and a JSON file with more
     * detail. It also provides trace files that you can load and analyze in Android Studio.
     */
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * This property defines the power categories to be measured and their display levels.
     * It maps all available [PowerCategory] entries to [PowerCategoryDisplayLevel.TOTAL],
     * meaning that the total power usage for each category will be displayed.
     */
    private val categories = PowerCategory.entries
        .associateWith { PowerCategoryDisplayLevel.TOTAL }

    /**
     * This test measures the performance of scrolling through the topic list in the "For You" screen
     * with a light theme and partial compilation.
     * It includes setup steps to navigate to the "For You" screen, select some topics, and then scrolls
     * the feed up and down multiple times.
     * The benchmark then records frame timing and power metrics.
     */
    @Test
    fun benchmarkStateChangeCompilationLight(): Unit =
        benchmarkStateChangeWithTheme(CompilationMode.Partial(), false)

    /**
     * This test measures the performance of scrolling through the topic list in the "For You" screen
     * with a dark theme, using partial compilation mode.
     * It includes setup steps to navigate to the "For You" screen, select some topics, and then scrolls
     * the feed up and down multiple times.
     */
    @Test
    fun benchmarkStateChangeCompilationDark(): Unit =
        benchmarkStateChangeWithTheme(CompilationMode.Partial(), true)

    /**
     * This function performs the actual benchmark measurement. It takes the compilation mode and
     * theme (light or dark) as parameters.
     * It uses [MacrobenchmarkRule.measureRepeated] to measure the performance of the app.
     * The setup block navigates to the "For You" screen and sets the specified theme.
     * The measure block then simulates user interaction by waiting for content to load, selecting
     * topics, and scrolling the feed up and down multiple times.
     * The benchmark records frame timing and power metrics.
     *
     * We call the [MacrobenchmarkRule.measureRepeated] method of our [MacrobenchmarkRule] property
     * [benchmarkRule] with the arguments:
     *  - `packageName`: The package name of the app being benchmarked is the [String] returned by
     *  our global property [PACKAGE_NAME].
     *  - `metrics`: The [List] of [FrameTimingMetric] and [PowerMetric] to measure. The [PowerMetric]
     *  `type` is set to [PowerMetric.Energy] with its `categories` argument set to our
     *  [PowerCategory.entries] property [categories].
     *  - `compilationMode`: The [CompilationMode] to use for the benchmark is our [CompilationMode]
     *  parmeter [compilationMode].
     *  - `iterations`: The number of times to run the benchmark is `2`.
     *  - `startupMode`: The [StartupMode] to use for the benchmark is [StartupMode.WARM].
     *  - `setupBlock`: A lambda function that sets up the benchmark by pressing the home button by
     *  calling the [MacrobenchmarkScope.pressHome] method, starting the activity by calling
     *  [MacrobenchmarkScope.startActivityAndWait], allowing notifications by calling
     *  [MacrobenchmarkScope.allowNotifications], finding the [UiObject2] on the [UiDevice] with the
     *  description "Settings" and clicking it, waiting for the UI to become idle by calling
     *  [UiDevice.waitForIdle], and setting the app theme to our [Boolean] parameter [isDark] by
     *  calling [setAppTheme].
     *
     * In the [MacrobenchmarkScope] `measureBlock` lambda argument we:
     *  - call [forYouWaitForContent] to Wait until content of the "For You" screen is loaded.
     *  - call [forYouSelectTopics] to Select some topics to show some feed content.
     *  - call [forYouScrollFeedDownUp] to Scroll the "For You" feed down and up 3 times.
     *
     * @param compilationMode The compilation mode to use for the benchmark.
     * @param isDark Whether to use a dark theme for the benchmark.
     */
    private fun benchmarkStateChangeWithTheme(compilationMode: CompilationMode, isDark: Boolean) =
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(
                FrameTimingMetric(),
                PowerMetric(type = PowerMetric.Energy(categories = categories)),
            ),
            compilationMode = compilationMode,
            iterations = 2,
            startupMode = StartupMode.WARM,
            setupBlock = {
                // Start the app
                pressHome()
                startActivityAndWait()
                allowNotifications()
                // Navigate to Settings
                device.findObject(By.desc("Settings")).click()
                device.waitForIdle()
                setAppTheme(isDark)
            },
        ) {
            forYouWaitForContent()
            forYouSelectTopics()
            repeat(3) {
                forYouScrollFeedDownUp()
            }
        }
}
