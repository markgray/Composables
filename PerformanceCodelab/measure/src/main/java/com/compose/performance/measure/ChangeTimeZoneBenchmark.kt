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
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import androidx.tracing.Trace
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark that measures the performance of changing the device's time zone.
 *
 * This benchmark simulates a scenario where an app needs to adapt to changes in the device's
 * time zone, potentially triggering updates in UI elements or data displayed to the user.
 *
 * It focuses on the time taken to switch between two specific time zones ("Europe/Warsaw" and
 * "America/Los_Angeles") after the app has been launched and initial content has been loaded.
 *
 * The benchmark utilizes [FrameTimingMetric] to measure frame rendering performance, and
 * [TraceSectionMetric] to measure the time spent in specific trace sections (e.g.
 * "PublishDate.registerReceiver"). This can be used to pinpoint bottlenecks related to handling
 * time zone changes.
 *
 * The benchmark starts in [StartupMode.WARM], meaning the app has already been launched once
 * before the measurements begin, which offers a more realistic scenario than a cold start.
 *
 * The "accelerate_heavy" activity is launched as test subject, to be as similar as possible
 * to a real scenario.
 *
 * The benchmark process is broken into two key blocks:
 *
 * - **setupBlock**: This block prepares the environment for measurement. It first sets the
 *   device's time zone to "America/Los_Angeles", then launches the `accelerate_heavy` activity,
 *   and waits for the UI to be ready.
 * - **measureBlock**: This block performs the core actions that are being measured. It repeatedly
 *   switches the device's time zone between "Europe/Warsaw" and "America/Los_Angeles".
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class ChangeTimeZoneBenchmark : AbstractBenchmark(StartupMode.WARM) {
    /**
     * Runs a macrobenchmark test with the [CompilationMode.Full] compilation mode. It calls the
     * [AbstractBenchmark.benchmark] method with its `compilationMode` argument [CompilationMode.Full]
     * which calls [MacrobenchmarkRule.measureRepeated] method with our overridden [measureBlock],
     * [setupBlock], and [metrics].
     */
    @Test
    fun changeTimeZoneCompilationFull(): Unit = benchmark(compilationMode = CompilationMode.Full())

    /**
     * The [List] of [Metric]s to measure:
     *  - [FrameTimingMetric]: Measures the time spent rendering each frame.
     *  - [TraceSectionMetric]: Measures the time spent in specific trace sections, in our case
     *  "PublishDate.registerReceiver".
     */
    override val metrics: List<Metric> =
        listOf(
            FrameTimingMetric(),
            TraceSectionMetric("PublishDate.registerReceiver", TraceSectionMetric.Mode.Sum)
        )

    /**
     * Sets up the device and application state before each benchmark iteration.
     *
     * This function performs the following actions:
     * 1. **Sets the device's time zone:** Changes the device's time zone to "America/Los_Angeles".
     *    This ensures consistency across benchmark runs and allows for testing time-zone-specific
     *    behavior if needed.
     * 2. **Presses the Home button:** Simulates pressing the device's Home button, returning to
     *    the home screen. This provides a consistent starting point for each benchmark run,
     *    ensuring the app starts from a known state.
     * 3. **Starts the target activity:** Launches the "accelerate_heavy" task activity.
     *    This is the specific activity that will be benchmarked.
     * 4. **Waits for the UI to be ready:** Waits for a maximum of 5 seconds for a UI element with
     *    resource ID "list_of_items" to appear. This ensures that the target activity is fully
     *    loaded and the relevant UI elements are present before the benchmark begins. It prevents
     *    the benchmark from starting before the app is in a stable, measurable state.
     *
     * This setup ensures consistent and reliable benchmark results by controlling the device and
     * application state.
     */
    override fun MacrobenchmarkScope.setupBlock() {
        device.changeTimeZone("America/Los_Angeles")
        pressHome()
        startTaskActivity("accelerate_heavy")
        device.wait(Until.hasObject(By.res("list_of_items")), 5_000)
    }

    /**
     * Measures the performance of a block of code that changes the device's time zone twice.
     *
     * This function simulates a scenario where the device's time zone is changed multiple times
     * within a short period. It first changes the time zone to "Europe/Warsaw" and then
     * immediately changes it to "America/Los_Angeles". This can be useful for evaluating the
     * performance impact of time zone changes on the application or system.
     *
     * Note that this function relies on the `device` object within the `MacrobenchmarkScope`
     * to interact with the device and change its settings.
     *
     * The time zone changes are performed sequentially, and the overall performance of both
     * changes is captured by the macrobenchmark.
     *
     * @see MacrobenchmarkScope
     * @see androidx.test.uiautomator.UiDevice.changeTimeZone
     */
    override fun MacrobenchmarkScope.measureBlock() {
        device.changeTimeZone("Europe/Warsaw")
        device.changeTimeZone("America/Los_Angeles")
    }
}

/**
 * Changes the device's timezone to the specified zone ID.
 *
 * This function utilizes the Android shell command `service call alarm 3 s16 <zoneId>` to modify
 * the device's timezone. It also wraps the operation within a [Trace] section to enable performance
 * analysis and debugging. A brief sleep is introduced to ensure the system has time to apply the
 * timezone change.
 *
 * @param zoneId The desired timezone ID, represented as a string (e.g., "America/Los_Angeles",
 * "Europe/Paris", "GMT"). See IANA Time Zone Database for valid time zone identifiers.
 */
fun UiDevice.changeTimeZone(zoneId: String) {
    // We add here a trace section, so that we can easily find in the trace where the timezone change occurs
    Trace.beginAsyncSection("change timezone", 0)
    executeShellCommand("service call alarm 3 s16 $zoneId")
    Trace.endAsyncSection("change timezone", 0)
    Thread.sleep(1_000)
}
