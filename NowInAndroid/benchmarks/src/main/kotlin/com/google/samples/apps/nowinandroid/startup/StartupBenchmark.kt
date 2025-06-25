/*
 * Copyright 2022 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.startup

import androidx.benchmark.macro.BaselineProfileMode.Disable
import androidx.benchmark.macro.BaselineProfileMode.Require
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.Metric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupMode.COLD
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.samples.apps.nowinandroid.BaselineProfileMetrics
import com.google.samples.apps.nowinandroid.PACKAGE_NAME
import com.google.samples.apps.nowinandroid.allowNotifications
import com.google.samples.apps.nowinandroid.foryou.forYouWaitForContent
import com.google.samples.apps.nowinandroid.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Enables app startups from various states of baseline profile or [CompilationMode]s.
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance from a cold state.
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class StartupBenchmark {
    /**
     * This rule provides access to the [MacrobenchmarkRule] API for benchmarking. The library
     * outputs benchmarking results to both the Android Studio console and a JSON file with more
     * detail. It also provides trace files that you can load and analyze in Android Studio.
     */
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * Measures the time to start up an app without pre-compilation. It does this by calling our
     * [startup] method with its `compilationMode` argument [CompilationMode.None].
     * Run this benchmark to verify your app's performance on library module installations,
     * or library updates.
     */
    @Test
    fun startupWithoutPreCompilation(): Unit = startup(compilationMode = CompilationMode.None())

    /**
     * Measures the time to start up an app with partial compilation and baseline profile disabled.
     * Run this benchmark to verify your app's performance under these conditions.
     */
    @Test
    fun startupWithPartialCompilationAndDisabledBaselineProfile(): Unit = startup(
        CompilationMode.Partial(baselineProfileMode = Disable, warmupIterations = 1),
    )

    /**
     * Measures the time to start up an app with precompiled baseline profile.
     * Run this benchmark to verify your app's performance with baseline profiles.
     */
    @Test
    fun startupPrecompiledWithBaselineProfile(): Unit =
        startup(CompilationMode.Partial(baselineProfileMode = Require))

    /**
     * Measures the time to start up an app with full compilation.
     * Run this benchmark to verify your app's performance with full compilation.
     */
    @Test
    fun startupFullyPrecompiled(): Unit = startup(CompilationMode.Full())

    /**
     * This method measures the time it takes for the app to start up under a given
     * [CompilationMode]. It uses the [MacrobenchmarkRule.measureRepeated] method to run the
     * benchmark multiple times and collect metrics.
     *
     * We call the [MacrobenchmarkRule.measureRepeated] method of our [MacrobenchmarkRule]
     * property [benchmarkRule] with the arguments:
     *  - `packageName`: The package name of our app is the [String] that our global property
     *  [PACKAGE_NAME] returns
     *  - `metrics`: A list of metrics to collect from the benchmark. We use the [List] of [Metric]
     *  property [BaselineProfileMetrics.allMetrics] to get a list of all the metrics relevant to
     *  startup and baseline profile effectiveness measurement.
     *  - `compilationMode`: The [CompilationMode] to use for the benchmark is our [CompilationMode]
     *  parameter [compilationMode].
     *  - `iterations`: The number of times to run the benchmark is set to 20.
     *  - `startupMode`: The [StartupMode] to use for the benchmark is [COLD].
     *  - `setupBlock`: A lambda function that sets up the environment for the benchmark. We call
     *  the [MacrobenchmarkScope.pressHome] method to press the home button and the [allowNotifications]
     *  method to allow notifications.
     *
     * In the [MacrobenchmarkScope] `measureBlock` lambda argument, we call:
     *  - [startActivityAndAllowNotifications]: This method starts the activity and allows notifications.
     *  - [forYouWaitForContent]: This method waits for content to be loaded in the "For You" screen.
     *
     * @param compilationMode The [CompilationMode] to use for the benchmark.
     */
    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = BaselineProfileMetrics.allMetrics,
        compilationMode = compilationMode,
        // More iterations result in higher statistical significance.
        iterations = 20,
        startupMode = COLD,
        setupBlock = {
            pressHome()
            allowNotifications()
        },
    ) {
        startActivityAndAllowNotifications()
        // Waits until the content is ready to capture Time To Full Display
        forYouWaitForContent()
    }
}
