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
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import org.junit.Rule

/**
 * Abstract class providing a base for creating macrobenchmarks.
 *
 * This class handles common setup and configuration for macrobenchmarking, such as defining
 * the [MacrobenchmarkRule], [StartupMode], number of iterations, and the structure
 * for setup and measurement blocks.
 *
 * Subclasses must implement [metrics] and [measureBlock]. They may optionally override [setupBlock].
 *
 * @property startupMode The [StartupMode] to use for the benchmark. Defaults to [StartupMode.WARM].
 * Options include [StartupMode.COLD], [StartupMode.WARM], and [StartupMode.HOT].
 * @property iterations The number of times to run the benchmark. Defaults to 1 for demonstration
 * purposes. For more accurate measurements, increase this value (e.g., to 10 or more), considering
 * the trade-off between execution time and stability of results. More iterations generally lead to
 * more stable results, but longer execution time.
 */
abstract class AbstractBenchmark(
    /**
     * The [StartupMode] to use for the benchmark. Defaults to [StartupMode.WARM].
     */
    protected val startupMode: StartupMode = StartupMode.WARM,
    /**
     * The number of times to run the benchmark. For the purpose of this workshop, we have
     * iterations set to low number. For more accurate measurements, you should increase the
     * iterations to at least 10 based on how much variance occurs in the results. In general
     * more iterations = more stable results, but longer execution time.
     */
    protected val iterations: Int = 1
) {
    /**
     * This rule is responsible for running macrobenchmarks.
     * It provides functionality to start and end tracing, compile the application,
     * and measure the performance of interactions or actions within the app.
     * Use it in conjunction with `@Test` annotated methods to define specific benchmark tests.
     */
    @get:Rule
    val rule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * Represents a metric, which can be a count, a gauge, or a timer, for example.
     */
    abstract val metrics: List<Metric>

    /**
     * Sets up the environment for a macrobenchmark test.
     *
     * This function should be called at the beginning of a macrobenchmark test within a
     * `macrobenchmark` block. It performs any necessary setup steps before the actual
     * benchmark measurements begin. This can include actions like:
     *
     * - Starting the application under test.
     * - Navigating to a specific screen or state.
     * - Populating data or performing initial configurations.
     * - Ensuring the app is in a known, stable state.
     * - Waking up the screen if necessary.
     *
     * Note that the code within this function is executed outside the timed region of the benchmark.
     * Therefore, the execution time of operations within this function does not impact the final
     * benchmark results.
     *
     * By default, this function is empty and does nothing.
     * Override it to implement your specific setup logic.
     *
     * @see MacrobenchmarkScope
     */
    open fun MacrobenchmarkScope.setupBlock() {}

    /**
     * Measures the time it takes to execute a block of code within the benchmark.
     *
     * This function is intended to be used within a `MacrobenchmarkScope` to measure the
     * performance of a specific block of code in your app. It allows for precise
     * measurement of the execution time of critical sections, providing valuable insights
     * into performance bottlenecks.
     *
     * Important Considerations:
     * - **Overhead:** Be aware that calling `measureBlock` itself does introduce some minimal
     *   overhead.  For extremely short code blocks, this overhead might be significant relative
     *   to the measured block.
     * - **Context:** `measureBlock` must be called within the context of a `MacrobenchmarkScope`,
     *   typically inside the lambda of `measureRepeated`.
     * - **Metrics:** This function measures the wall-clock time of the code block. If you need
     *   other metrics, like frame timings or memory allocations, you should use different
     *   metrics in conjunction with this like `StartupTiming` or `FrameTimingMetric`.
     * - **Target audience:** This is a developer-facing function, so the documentation aims to aid
     * understanding and correct use for testing purposes.
     *
     * @see MacrobenchmarkScope
     * @see androidx.benchmark.macro.junit4.MacrobenchmarkRule
     * @see androidx.benchmark.macro.StartupTimingMetric
     * @see androidx.benchmark.macro.FrameTimingMetric
     */
    abstract fun MacrobenchmarkScope.measureBlock()

    /**
     * Runs a macrobenchmark test with the specified compilation mode. It calls the
     * [MacrobenchmarkRule.measureRepeated] method of our [rule] with the arguments:
     * - `packageName`: The package name of the app under test.
     * - `metrics`: The metrics to measure.
     * - `compilationMode`: The compilation mode to use.
     * - `startupMode`: The startup mode to use.
     * - `iterations`: The number of iterations to run the benchmark.
     * - `setupBlock`: The setup block to execute before each iteration.
     * - `measureBlock`: The block of code to measure.
     *
     * @param compilationMode The [CompilationMode] to use for the benchmark.
     */
    fun benchmark(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = "com.compose.performance",
            metrics = metrics,
            compilationMode = compilationMode,
            startupMode = startupMode,
            iterations = iterations,
            setupBlock = { setupBlock() },
            measureBlock = { measureBlock() }
        )
    }
}

/**
 * Starts an activity with an intent extra to specify the task to start.
 *
 * This function launches the application's main activity and adds an intent extra
 * named "EXTRA_START_TASK" with the provided [task] string. This can be used to
 * direct the activity to start a specific task or feature within the application.
 *
 * This is commonly used in Macrobenchmark tests to start the app in a specific state or
 * navigate directly to a particular feature.
 *
 * @param task The name of the task to start, which will be passed as an intent extra. This value
 * will be accessible within the activity as `intent.getStringExtra("EXTRA_START_TASK")`.
 */
fun MacrobenchmarkScope.startTaskActivity(task: String): Unit =
    startActivityAndWait { it.putExtra("EXTRA_START_TASK", task) }
