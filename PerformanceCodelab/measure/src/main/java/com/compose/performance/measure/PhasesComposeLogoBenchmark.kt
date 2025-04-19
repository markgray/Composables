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
import androidx.benchmark.macro.TraceSectionMetric
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [PhasesComposeLogoBenchmark] benchmarks the performance of the "Phases Compose Logo" screen.
 *
 * This benchmark measures the frame timing and the time spent in specific trace sections
 * during the animation of the Phases Compose Logo.
 *
 * The screen being tested is a static animation, without any user interaction.
 *
 * The benchmark is executed in [CompilationMode.Full], ensuring the most optimized
 * execution environment.
 *
 * Metrics Collected:
 * - [FrameTimingMetric]: Measures the time taken to render each frame.
 * - [TraceSectionMetric]: Measures the total time spent in the "PhasesComposeLogo" trace section.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class PhasesComposeLogoBenchmark : AbstractBenchmark() {

    /**
     * Runs a macrobenchmark test with the [CompilationMode.Full] compilation mode. It calls the
     * [AbstractBenchmark.benchmark] method with its `compilationMode` argument [CompilationMode.Full]
     * and [AbstractBenchmark.benchmark] then executes our overridden [measureBlock] and [setupBlock]
     * methods using our overridden [List] of [Metric] property [metrics] as the data to collect.
     */
    @Test
    fun phasesComposeLogoCompilationFull(): Unit = benchmark(CompilationMode.Full())

    /**
     * The [List] of [Metric]s to measure. In our case, we're measuring:
     *  - [FrameTimingMetric]: Measures the time spent rendering each frame.
     *  - [TraceSectionMetric]: Measures the time spent in specific trace sections, in our case
     *  "PhasesComposeLogo".
     */
    override val metrics: List<Metric> =
        listOf(
            FrameTimingMetric(),
            TraceSectionMetric("PhasesComposeLogo", TraceSectionMetric.Mode.Sum),
        )

    /**
     * Sets up the device for the macrobenchmark test.
     *
     * This function performs the necessary initial actions to prepare the device
     * before the start of a macrobenchmark. Specifically, it:
     *
     * 1. **Presses the Home Button:** Navigates the device to the home screen, ensuring
     *    a consistent starting point for the benchmark.
     * 2. **Starts the Target Activity:** Launches the specified activity ("phases_logo" in
     *    this case) within the target application. This ensures the benchmark begins with the
     *    application in the desired state.
     *
     * This function should be called within a [MacrobenchmarkScope] to define the setup
     * phase of a macrobenchmark test.
     *
     * @see MacrobenchmarkScope
     */
    override fun MacrobenchmarkScope.setupBlock() {
        pressHome()
        startTaskActivity("phases_logo")
    }

    /**
     * Measures a block of code that simulates observing an animation without user interaction.
     *
     * This function is designed to be used within a Macrobenchmark test scenario where a
     * specific screen or UI element's performance during animation is being evaluated.
     * In this case, the screen under test does not involve any user interactions, so the
     * benchmark focuses solely on observing the animation's duration.
     *
     * The function simulates this observation by pausing the execution thread for a
     * predetermined amount of time (1 second in this implementation), allowing the
     * animation to play out without any interruptions from the test code.
     *
     * Note:
     *   - This function is intended for scenarios where user interaction is not part
     *     of the performance being measured.
     *   - The duration of the Thread.sleep() call should be adjusted according to
     *     the length of the animation being observed.
     */
    override fun MacrobenchmarkScope.measureBlock() {
        // This screen doesn't have any interaction, we will just observe the animation for the duration.
        Thread.sleep(1_000)
    }
}
