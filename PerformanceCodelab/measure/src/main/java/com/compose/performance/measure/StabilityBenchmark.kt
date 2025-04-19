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
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [StabilityBenchmark] is a Macrobenchmark test class designed to measure the performance
 * improvement of the "stability_screen" activity when its data structures are made stable.
 *
 * This class extends [AbstractBenchmark] which provides a basic structure for Macrobenchmark tests.
 * It utilizes the UiAutomator library to simulate user interactions and custom metrics
 * to collect performance data.
 *
 * The benchmark specifically measures frame timings and the time spent in specific trace
 * sections within the application ("latest_change" and "item_row").
 *
 * Key features:
 * - **Compilation Mode:** Uses [CompilationMode.Full] for the benchmark test, ensuring the
 *   application is fully compiled before the test.
 * - **Metrics:** Captures [FrameTimingMetric] for frame rendering information and
 *   [TraceSectionMetric] for the duration of specific code sections.
 * - **Setup:** Prepares the application by pressing the home button and launching a specific
 *   activity named "stability_screen".
 * - **Measurement:** Simulates user interaction by repeatedly clicking a Floating Action
 *   Button (FAB) and pausing for a short duration between each click.
 *
 * Usage:
 * 1. Ensure the application is set up for Macrobenchmark tests.
 * 2. Run this test using AndroidJUnit4.
 * 3. Analyze the generated benchmark results to understand the application's stability.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
@OptIn(ExperimentalMetricApi::class)
class StabilityBenchmark : AbstractBenchmark(StartupMode.WARM) {

    /**
     * Runs a macrobenchmark test with the [CompilationMode.Full] compilation mode. It calls the
     * [AbstractBenchmark.benchmark] method with its `compilationMode` argument [CompilationMode.Full]
     * and [AbstractBenchmark.benchmark] executes our overridden [measureBlock] and [setupBlock]
     * methods using our overridden [List] of [Metric] property [metrics] as the data to collect.
     */
    @Test
    fun stabilityCompilationFull(): Unit = benchmark(CompilationMode.Full())

    /**
     * The [List] of [Metric]s to measure. In our case, we're measuring:
     *  - [FrameTimingMetric]: Measures the time spent rendering each frame.
     *  - [TraceSectionMetric]: Measures the time spent in specific trace sections, in our case
     *  "latest_change" and "item_row".
     */
    override val metrics: List<Metric> = listOf(
        FrameTimingMetric(),
        TraceSectionMetric("latest_change", TraceSectionMetric.Mode.Sum),
        TraceSectionMetric("item_row", TraceSectionMetric.Mode.Sum)
    )

    /**
     * Sets up the device state before running a benchmark.
     *
     * This function is called before each benchmark iteration to ensure a consistent starting
     * point. It performs the following actions:
     *
     * 1. **Presses the Home Button:** This action returns the device to the home screen, ensuring
     *    a known state.
     * 2. **Starts the Task Activity:** It then launches the "stability_screen" activity, which is
     *    a specific screen within the application designed for testing the performance improvement
     *    that making the data structures of the application stable provide.
     *
     * By performing these actions, the setupBlock ensures that each benchmark iteration begins
     * with the app's stability screen visible and ready for interaction.
     *
     * This is an overridden function from [MacrobenchmarkScope].
     */
    override fun MacrobenchmarkScope.setupBlock() {
        pressHome()
        startTaskActivity("stability_screen")
    }

    /**
     * Measures the performance of a block of code involving UI interactions.
     *
     * This function simulates clicking a floating action button (FAB) multiple times and
     * observes the performance during these interactions. It uses UI Automator to
     * interact with the application's UI.
     *
     * The specific actions performed within this block are:
     *   1. Locate the FAB using its resource ID ("fab").
     *   2. Repeat the following steps three times:
     *       - Click the located FAB.
     *       - Pause for 300 milliseconds to simulate a potential animation or UI update delay.
     *
     * This method is intended to be called within a `MacrobenchmarkRule` to measure the time
     * taken to execute the above sequence of actions. The recorded performance data can then be
     * analyzed to identify potential bottlenecks or performance regressions in the UI interaction.
     *
     * @receiver [MacrobenchmarkScope] The scope in which the benchmark is being run,
     * providing access to device controls and other benchmarking utilities.
     */
    override fun MacrobenchmarkScope.measureBlock() {
        // Actual code to measure
        val fab: UiObject2 = device.findObject(By.res("fab"))

        repeat(times = 3) {
            fab.click()
            Thread.sleep(300)
        }
    }
}
