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

package com.google.samples.apps.nowinandroid.foryou

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.samples.apps.nowinandroid.PACKAGE_NAME
import com.google.samples.apps.nowinandroid.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This benchmark scrolls the For You feed but on a much larger catalog of mock
 * news resources and topics.
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class ScrollForYouFeedBenchmark {

    /**
     * JUnit rule for benchmarking large app operations like startup, scrolling, or animations.
     * The library outputs benchmarking results to both the Android Studio console and a JSON file
     * with more detail. It also provides trace files that you can load and analyze in Android Studio.
     */
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * This test scrolls the For You feed with no compilation.
     */
    @Test
    fun scrollFeedCompilationNone(): Unit = scrollFeed(CompilationMode.None())

    /**
     * This test scrolls the For You feed with a baseline profile using [CompilationMode.Partial]
     * for partial compilation.
     */
    @Test
    fun scrollFeedCompilationBaselineProfile(): Unit = scrollFeed(CompilationMode.Partial())

    /**
     * This test scrolls the For You feed with full compilation using [CompilationMode.Full].
     */
    @Test
    fun scrollFeedCompilationFull(): Unit = scrollFeed(CompilationMode.Full())

    /**
     * Scrolls the For You feed and measures the frame timing.
     * The app is started and notifications are allowed before the measurement.
     * The For You screen is prepared by waiting for content, selecting topics,
     * and then scrolling the feed down and up.
     *
     * We call the [MacrobenchmarkRule.measureRepeated] method to run the benchmark multiple times
     * with the arguments:
     *  - `packageName`: The package name of the app being benchmarked is the [String] returned by
     *  our global property [PACKAGE_NAME].
     *  - `metrics`: The [List] of [FrameTimingMetric] to measure.
     *  - `compilationMode`: The [CompilationMode] to use for the benchmark is our [CompilationMode]
     *  parmeter [compilationMode].
     *  - `iterations`: The number of times to run the benchmark is `10`.
     *  - `startupMode`: The [StartupMode] to use for the benchmark is [StartupMode.WARM].
     *  - `setupBlock`: A lambda function that sets up the benchmark by pressing the home button,
     *  starting the activity, and allowing notifications.
     *
     * In the [MacrobenchmarkScope] `measureBlock` lambda argument we:
     *  - call [forYouWaitForContent] method to wait for content to be loaded in the "For You" screen.
     *  - call [forYouSelectTopics] method to select some topics to show some feed content.
     *  - call [forYouScrollFeedDownUp] method to scroll the "For You" feed down and up to measure
     *  scrolling performance.
     *
     * @param compilationMode The compilation mode to use for the benchmark.
     */
    private fun scrollFeed(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = StartupMode.WARM,
        setupBlock = {
            // Start the app
            pressHome()
            startActivityAndAllowNotifications()
        },
    ) {
        forYouWaitForContent()
        forYouSelectTopics()
        forYouScrollFeedDownUp()
    }
}
