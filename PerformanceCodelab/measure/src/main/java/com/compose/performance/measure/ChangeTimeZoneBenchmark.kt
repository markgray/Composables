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

    override val metrics: List<Metric> =
        listOf(
            FrameTimingMetric(),
            TraceSectionMetric("PublishDate.registerReceiver", TraceSectionMetric.Mode.Sum)
        )

    override fun MacrobenchmarkScope.setupBlock() {
        device.changeTimeZone("America/Los_Angeles")
        pressHome()
        startTaskActivity("accelerate_heavy")
        device.wait(Until.hasObject(By.res("list_of_items")), 5_000)
    }

    override fun MacrobenchmarkScope.measureBlock() {
        device.changeTimeZone("Europe/Warsaw")
        device.changeTimeZone("America/Los_Angeles")
    }
}

fun UiDevice.changeTimeZone(zoneId: String) {
    // We add here a trace section, so that we can easily find in the trace where the timezone change occurs
    Trace.beginAsyncSection("change timezone", 0)
    executeShellCommand("service call alarm 3 s16 $zoneId")
    Trace.endAsyncSection("change timezone", 0)
    Thread.sleep(1_000)
}
