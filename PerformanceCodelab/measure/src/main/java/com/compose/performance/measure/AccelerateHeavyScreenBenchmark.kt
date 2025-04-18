/*
 * Copyright 2024 The Android Open Source Project
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
package com.compose.performance.measure

import android.graphics.Point
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [AccelerateHeavyScreenBenchmark] measures the performance of the "heavy" screen within the app,
` * (`AccelerateHeavyScreen`) simulating user interactions like scrolling and loading content.
 *
 * This benchmark specifically targets a screen identified as "accelerate_heavy" by its
 * `TaskScreen.id`, containing a complex screen with many elements that could impact rendering
 * performance.
 *
 * It utilizes different metrics to capture frame timings and the duration of specific code
 * sections, providing detailed insights into the performance bottlenecks.
 *
 * Key features:
 *  - **Compilation Mode:** Runs with `CompilationMode.Full()` which ensures that the app is fully
 *  compiled before the benchmark, simulating a real user scenario after app installation and usage.
 *  - **Startup Mode:** Uses `StartupMode.COLD`, meaning the app is fully restarted for each test run.
 *  - **Metrics:** Tracks [FrameTimingMetric] to measure rendering performance and
 *  [TraceSectionMetric] to measure the time spent in specific code sections:
 *      - "ImagePlaceholder": Time spent loading or rendering image placeholders.
 *      - "PublishDate.registerReceiver": Time spent in the `registerReceiver` related to
 *      publishing date.
 *      - "ItemTag": Time spent processing or rendering item tags.
 *  - **Interaction:** Simulates user interaction by scrolling a list view multiple times,
 *  which is typical in "heavy" screen scenarios.
 *  - **Verification:**  waits for the list to be visible before scrolling.
 *
 * To interpret the results:
 *  - Lower frame time values are better, indicating smoother rendering.
 *  - Lower trace section times are better, indicating that specific code sections are
 *  executing quickly.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class AccelerateHeavyScreenBenchmark : AbstractBenchmark(StartupMode.COLD) {

    /**
     * This test measures the startup time of a heavy screen compilation using the Full
     * compilation mode.
     *
     * It simulates a scenario where a complex screen, potentially with many UI elements and
     * interactions, is being compiled using the full ahead-of-time (AOT) compilation strategy.
     *
     * This test helps to understand:
     *  - The impact of full AOT compilation on startup time for complex screens.
     *  - The potential overhead introduced by full compilation compared to other compilation modes.
     *  - If any code changes introduce regressions in startup time under full compilation.
     *
     * By running this test, developers can identify performance bottlenecks specifically related to
     * full compilation and optimize the code or build process accordingly.
     *
     * The [benchmark] method, which is part of [AbstractBenchmark], provides tools to measure and
     * report detailed performance metrics. The [CompilationMode.Full] option specifies that the
     * application's code should be compiled entirely ahead of time.
     */
    @Test
    fun accelerateHeavyScreenCompilationFull(): Unit = benchmark(CompilationMode.Full())

    /**
     * The [List] of [Metric]s to measure.
     *  - [FrameTimingMetric]: Measures the time spent rendering each frame.
     *  - [TraceSectionMetric]: Measures the time spent in specific code sections.
     */
    override val metrics: List<Metric> =
        listOf(
            FrameTimingMetric(),
            TraceSectionMetric(
                sectionName = "ImagePlaceholder",
                mode = TraceSectionMetric.Mode.Sum
            ),
            TraceSectionMetric(
                sectionName = "PublishDate.registerReceiver",
                mode = TraceSectionMetric.Mode.Sum
            ),
            TraceSectionMetric(
                sectionName = "ItemTag",
                mode = TraceSectionMetric.Mode.Sum
            )
        )

    /**
     * This function defines the block of actions to be measured during a macrobenchmark.
     * It simulates a user navigating to a specific activity and interacting with a feed
     * by repeatedly dragging it upwards.
     *
     * The function performs the following actions:
     *  1. **Presses the Home Button:** Simulates returning to the device's home screen.
     *  2. **Starts a Specific Activity:** Launches the activity named "accelerate_heavy",
     *     which is the target activity for this benchmark.
     *  3. **Waits for a UI Element:** Waits for up to 5 seconds for a list-like UI element
     *     with the resource ID "list_of_items" to become visible. This ensures that the activity
     *     is fully loaded and the target element is available for interaction.
     *  4. **Locates the UI Element:** Finds the "list_of_items" element using its resource ID.
     *     This element is expected to be a scrollable list or feed.
     *  5. **Sets Gesture Margin:**  Sets a gesture margin for the found element. The margin is
     *     calculated to be one-fifth of the device's display width. This helps define the area
     *     where gestures on the feed are considered valid.
     *  6. **Repeatedly Drags the Feed:** Performs the core interaction of the benchmark. It repeats
     *     the following steps twice:
     *      - **Drag Upwards:** Drags the feed upwards, starting from the visible center and ending
     *      at the top of the visible bounds.
     *      - **Sleep:** Pauses for 500 milliseconds after each drag. This adds a realistic delay
     *      and can help in observing frame drops.
     *
     * This function helps measure the performance of scrolling or interacting with feed elements
     * in a specified activity. By repeating the drag interaction, it can also help identify
     * potential performance degradations over time.
     */
    override fun MacrobenchmarkScope.measureBlock() {
        pressHome()
        startTaskActivity(task = "accelerate_heavy")

        device.wait(Until.hasObject(By.res("list_of_items")), 5_000)
        val feed: UiObject2 = device.findObject(By.res("list_of_items"))
        feed.setGestureMargin(device.displayWidth / 5)

        repeat(times = 2) {
            feed.drag(Point(feed.visibleCenter.x, feed.visibleBounds.top))
            Thread.sleep(500)
        }
    }
}
