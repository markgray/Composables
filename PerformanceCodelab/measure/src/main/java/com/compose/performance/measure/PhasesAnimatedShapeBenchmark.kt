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
import androidx.test.uiautomator.By
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This class benchmarks the performance of the "PhasesComposeLogo" animation within the
 * "phases_animatedshape" activity. It specifically measures frame timing and the total time
 * spent within the "PhasesComposeLogo" trace section during the animation.
 *
 * The benchmark focuses on a scenario where a button is clicked to trigger a size change animation.
 *
 * Key Components:
 * - **CompilationMode.Full():**  Specifies that the benchmark should run with full ahead-of-time
 *   (AOT) compilation, providing insights into the performance of the application in a
 *   production-like environment.
 * - **FrameTimingMetric():** Measures frame rendering times to assess animation smoothness and
 *   jank.
 * - **TraceSectionMetric("PhasesComposeLogo", TraceSectionMetric.Mode.Sum):** Tracks the total
 *   duration spent within the "PhasesComposeLogo" trace section. This allows for isolating the
 *   performance of the specific animation logic.
 * - **setupBlock():**  Prepares the device and application for the benchmark. It launches the
 *   "phases_animatedshape" activity.
 * - **measureBlock():**  Contains the core actions performed during the benchmark. It locates and
 *   clicks the "Toggle Size" button, initiating the animation. It then waits for the animation
 *   to complete before finishing measurement.
 * - **toggleButton.click():** This triggers the start of the animation being measured.
 * - **Thread.sleep(2_000):** The time allocated is sufficient for the animation to run and complete.
 *
 * Running the benchmark:
 * 1. Ensure you have a device or emulator connected.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class PhasesAnimatedShapeBenchmark : AbstractBenchmark() {

    /**
     * Measures the performance of compiling and running a complex animation
     * involving multiple phases and shape transformations, with Full Compilation.
     *
     * This test case specifically targets scenarios where the entire application or a significant
     * portion of it is pre-compiled using Full Compilation (AOT). It evaluates the performance
     * characteristics when the code is not subject to Just-In-Time (JIT) compilation during runtime.
     *
     * The animation being tested likely involves:
     * - Multiple phases: distinct stages or steps in the animation sequence.
     * - Shape transformations: changes in the geometry or form of the animated objects.
     * - Complex composition: combination of different animation elements.
     *
     * By using `CompilationMode.Full()`, this benchmark ensures that the entire compilation
     * process happens upfront before the test begins. This allows for measuring the execution speed
     * of the pre-compiled code and identifying potential bottlenecks related to the animation's
     * design or the underlying rendering pipeline when AOT is in place.
     *
     * Use Cases:
     * - Evaluating the effectiveness of AOT compilation for complex animations.
     * - Identifying performance regressions in pre-compiled code.
     * - Comparing the performance of different animation implementations under AOT.
     * - Optimizing code for ahead-of-time compilation.
     *
     * How to interpret results:
     * - Lower execution times indicate better performance.
     * - Significant variations between runs might suggest instability or caching issues.
     *
     * Notes:
     * - This benchmark is expected to be run on a physical device for accurate results.
     * - Warmup iterations should be used to stabilize the results and allow any caching mechanism
     * to operate.
     * - This is a heavier compilation mode for which we expect slow down during compilation time.
     * - This test does not verify the correctness of the rendering output, only its performance.
     */
    @Test
    fun phasesAnimatedShapeCompilationFull(): Unit = benchmark(CompilationMode.Full())

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
     * Sets up the benchmark environment for testing the "phases_animatedshape" activity.
     *
     * This function performs the necessary steps to prepare the device and application
     * for running a macrobenchmark related to the animated shape functionality.
     *
     * The specific steps involved are:
     * 1. Presses the home button: This ensures a clean starting state by returning
     *    to the device's home screen.
     * 2. Starts the target activity: This launches the "phases_animatedshape" activity,
     *    which is the specific part of the application being benchmarked.
     *
     * This function should be called before the main benchmarking loop to properly
     * initialize the environment.
     *
     * @receiver the [MacrobenchmarkScope] in which this setup is performed.
     */
    override fun MacrobenchmarkScope.setupBlock() {
        pressHome()
        startTaskActivity("phases_animatedshape")
    }

    /**
     * Measures a block of UI interaction within the Macrobenchmark.
     *
     * This function performs the following actions:
     * 1. **Finds the "Toggle Size" button:** It locates a UI element with the text "Toggle Size"
     * using UiAutomator's `By.text()` locator.
     * 2. **Clicks the "Toggle Size" button:** It simulates a user clicking the found button. This
     * is expected to trigger some UI change, likely an animation or a layout shift.
     * 3. **Waits for animation/change to complete:** It introduces a 2-second delay using
     * `Thread.sleep(2_000)`. This is crucial to allow any animations or UI updates triggered by
     * the button click to finish before the benchmark proceeds. This is a crucial step because,
     * without waiting, the benchmark could measure performance before the app is in a stable
     * state after the click.
     *
     * This function is designed to measure the time it takes for the UI to transition or update
     * after the "Toggle Size" button is clicked.
     *
     * Note: This function assumes that clicking the "Toggle Size" button triggers a UI change that
     * takes approximately 2 seconds to complete. If the UI change takes longer or shorter, this
     * sleep duration should be adjusted accordingly. A more robust approach would be to listen for
     * a specific UI change event instead of using a fixed sleep duration.
     *
     * @receiver The [MacrobenchmarkScope] in which this measurement is performed.
     */
    override fun MacrobenchmarkScope.measureBlock() {
        val toggleButton = device.findObject(By.text("Toggle Size"))
        toggleButton.click()
        // Wait for the animation to finish
        Thread.sleep(2_000)
    }
}
