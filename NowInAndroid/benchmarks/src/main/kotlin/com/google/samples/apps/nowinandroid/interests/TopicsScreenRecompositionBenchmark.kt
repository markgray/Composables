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

package com.google.samples.apps.nowinandroid.interests

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import com.google.samples.apps.nowinandroid.PACKAGE_NAME
import com.google.samples.apps.nowinandroid.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark that measures the recomposition performance of the Topics screen.
 */
@RunWith(AndroidJUnit4::class)
class TopicsScreenRecompositionBenchmark {
    /**
     * This rule provides access to the [MacrobenchmarkRule] API for benchmarking. The library
     * outputs benchmarking results to both the Android Studio console and a JSON file with more
     * detail. It also provides trace files that you can load and analyze in Android Studio.
     */
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * Measures the recomposition performance of the Topics screen after a state change using a
     * [CompilationMode] of [CompilationMode.Partial].
     */
    @Test
    fun benchmarkStateChangeCompilationBaselineProfile(): Unit =
        benchmarkStateChange(CompilationMode.Partial())

    /**
     * Measures the time it takes for the Topics screen to recompose when the bookmarked
     * topics change. This is a proxy for measuring the recomposition performance of the screen.
     *
     * We call the [MacrobenchmarkRule.measureRepeated] method of our [MacrobenchmarkRule] property
     * [benchmarkRule] with the arguments:
     *  - `packageName`: the package name of the app being benchmarked, is the [String] returned by
     *  our global property [PACKAGE_NAME].
     *  - `metrics`: a list of [FrameTimingMetric] to measure.
     *  - `compilationMode`: the [CompilationMode] to use for the benchmark is our [CompilationMode]
     *  parameter [compilationMode].
     *  - `iterations`: the number of times to repeat the benchmark is `10`.
     *  - `startupMode`: the [StartupMode] to use for the benchmark is [StartupMode.WARM].
     *  - `setupBlock`: A lambda function that sets up the benchmark by pressing the home button by
     *  calling the [MacrobenchmarkScope.pressHome] method, starting the activity and allowing
     *  notification by calling [MacrobenchmarkScope.startActivityAndAllowNotifications], navigating
     *  to the interests screen by finding the [UiObject2] on the [UiDevice] with the text "Interests"
     *  and clicking it, then waiting for the UI to become idle by calling [UiDevice.waitForIdle].
     *
     * In the [MacrobenchmarkScope] `measureBlock` lambda argument we:
     *  - call [interestsWaitForTopics] to Wait until the Topics screen is loaded.
     *  - call [interestsToggleBookmarked] to Toggle the bookmark state of some topics 3 times.
     *
     * @param compilationMode the compilation mode to use for the benchmark.
     */
    private fun benchmarkStateChange(compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            iterations = 10,
            startupMode = StartupMode.WARM,
            setupBlock = {
                // Start the app
                pressHome()
                startActivityAndAllowNotifications()
                // Navigate to interests screen
                device.findObject(By.text("Interests")).click()
                device.waitForIdle()
            },
        ) {
            interestsWaitForTopics()
            repeat(3) {
                interestsToggleBookmarked()
            }
        }
}
