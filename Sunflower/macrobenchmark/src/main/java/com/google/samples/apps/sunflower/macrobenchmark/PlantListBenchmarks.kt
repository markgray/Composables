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

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is a macrobenchmark for the Plant List screen.
 * It measures the time it takes to open the screen from the home screen and scroll through
 * the list of plants. The benchmarks are run with three different compilation modes to assess
 * the impact of code optimization on performance.
 *
 * The @[RunWith] annotation causes JUnit to invoke the [AndroidJUnit4] class to run the tests in
 * this class instead of the runner built into JUnit.
 */
@RunWith(AndroidJUnit4::class)
class PlantListBenchmarks {
    /**
     * This rule provides access to the [MacrobenchmarkRule] API for writing benchmarks.
     *
     * The @[Rule] annotation Annotates fields that reference rules or methods that return a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     */
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * Measures the time it takes to open the plant list screen from the home screen,
     * with no compilation. This is a baseline measurement to compare against
     * partially and fully compiled code.
     */
    @Test
    fun openPlantList(): Unit = openPlantList(CompilationMode.None())

    /**
     * Measures the time it takes to open the plant list screen from the home screen,
     * with partial compilation. This is useful for testing the performance of the app
     * with baseline profiles.
     */
    @Test
    fun plantListCompilationPartial(): Unit = openPlantList(CompilationMode.Partial())

    /**
     * Measures the time it takes to open the plant list screen from the home screen,
     * with full compilation. This is a measurement of the app's performance after
     * it has been fully optimized.
     */
    @Test
    fun plantListCompilationFull(): Unit = openPlantList(CompilationMode.Full())

    /**
     * Measures the performance of opening the plant list screen and displaying the list of plants.
     * This function is invoked by the public test methods with different [CompilationMode] settings.
     *
     * @param compilationMode The [CompilationMode] to use for the benchmark, which determines the
     * level of ahead-of-time (AOT) compilation.
     */
    private fun openPlantList(compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            iterations = 5,
            startupMode = StartupMode.COLD,
            setupBlock = {
                pressHome()
                // Start the default activity, but don't measure the frames yet
                startActivityAndWait()
            }
        ) {
            goToPlantListTab()
        }
}

/**
 * Navigates to the plant list screen by clicking on the corresponding tab.
 * It waits until the plant list is displayed and the UI is idle before proceeding.
 * This function is an extension of [MacrobenchmarkScope], intended for use within a benchmark.
 */
fun MacrobenchmarkScope.goToPlantListTab() {
    // Find the tab with plants list
    val plantListTab: UiObject2 = device.findObject(By.descContains("Plant list"))
    plantListTab.click()

    // Wait until plant list has children
    val recyclerHasChild: BySelector =
        By.hasChild(By.res(packageName, "plant_list"))
    device.wait(Until.hasObject(recyclerHasChild), 5_000)

    // Wait until idle
    device.waitForIdle()
}
