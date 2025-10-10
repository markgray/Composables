/*
 * Copyright 2022 Google LLC
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

package com.google.samples.apps.sunflower.macrobenchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 *  1) switch your app's active build variant in the Studio (affects Studio runs only)
 *  2) add `<profileable android:shell="true">` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 *
 * The @[RunWith] annotation causes JUnit to invoke the [AndroidJUnit4] class to run the tests in
 * this class instead of the runner built into JUnit.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmarks {
    /**
     * The [MacrobenchmarkRule] is a JUnit rule that provides a way to run benchmarks on a device.
     * It handles process recreation and collecting metrics. It also provides a way to interact with
     * the device, such as pressing the home button or launching an activity.
     *
     * The @[Rule] annotation Annotates fields that reference rules or methods that return a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     */
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * Measures the time from launching the process until the initial UI is drawn and the app is ready
     * for user interaction. This is a cold startup, so the process is killed before each measurement.
     *
     * This test case runs the benchmark with no compilation, which is the worst-case startup scenario.
     * It's equivalent to the app being freshly installed.
     */
    @Test
    fun startupCompilationNone(): Unit = startup(CompilationMode.None())

    /**
     * Measures the time from launching the process until the initial UI is drawn and the app is ready
     * for user interaction. This is a cold startup, so the process is killed before each measurement.
     *
     * This test case runs the benchmark with partial compilation, which is the most realistic startup
     * scenario. It's equivalent to the app being installed from the Play Store and used for some time.
     * It also uses a baseline profile, which is a list of classes and methods that are pre-compiled
     * to improve performance.
     */
    @Test
    fun startupCompilationPartial(): Unit = startup(CompilationMode.Partial())

    /**
     * A "warmup" version of [startupCompilationPartial], to demonstrate how the app
     * performs without a baseline profile. The results of this test are not checked, but are
     * used to show the performance improvement of using a baseline profile.
     *
     * This test case runs the benchmark with partial compilation, but with the baseline profile
     * disabled. This is to represent the app's performance after it has been used for some time
     * but before a baseline profile has been generated.
     */
    @Test
    fun startupCompilationWarmup(): Unit =
        startup(CompilationMode.Partial(BaselineProfileMode.Disable, 2))

    /**
     * Measures the time from launching the process until the initial UI is drawn and the app is ready
     * for user interaction. This is a cold startup, so the process is killed before each measurement.
     *
     * This helper function is used to run the benchmark with different compilation modes. It measures
     * the time from launching the process until the initial UI is drawn and the app is ready for user
     * interaction. This is a cold startup, so the process is killed before each measurement.
     *
     * Before each iteration, the device's home screen is opened. Then, the default activity is
     * launched and the test waits until the main content is displayed.
     *
     * @param compilationMode The [CompilationMode] to use for the benchmark.
     * @see [startupCompilationNone]
     * @see [startupCompilationPartial]
     * @see [startupCompilationWarmup]
     */
    private fun startup(compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            iterations = 5,
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            setupBlock = {
                pressHome()
            }
        ) {
            startActivityAndWait()

            // wait for the content called by reportFullyDrawn is visible
            val recyclerHasChild = By.hasChild(By.res(packageName, "garden_list"))
            device.wait(Until.hasObject(recyclerHasChild), 5_000)
        }
}
