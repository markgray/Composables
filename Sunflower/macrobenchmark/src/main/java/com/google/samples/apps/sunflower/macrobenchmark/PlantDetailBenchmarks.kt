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
 * This is a macrobenchmark for the Plant Detail screen using three different compilation modes.
 * It measures the time it takes to scroll the plant list and navigate to the detail screen.
 *
 * The @[RunWith] annotation causes JUnit to invoke the [AndroidJUnit4] class to run the tests in
 * this class instead of the runner built into JUnit.
 */
@RunWith(AndroidJUnit4::class)
class PlantDetailBenchmarks {
    /**
     * This rule provides access to the [MacrobenchmarkScope] class, which is used to run the
     * macrobenchmark.
     *
     * The @[Rule] annotation Annotates fields that reference rules or methods that return a rule.
     * In Kotlin, @get:SomeAnnotation is used to specify that an annotation should be applied to
     * the property's getter method, rather than the property itself.
     */
    @get:Rule
    val benchmarkRule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * This benchmark tests the performance of the plant detail screen with no compilation, which is
     * the closest to a cold start from a Play Store installation.
     */
    @Test
    fun plantDetailCompilationNone(): Unit = benchmarkPlantDetail(CompilationMode.None())

    /**
     * This benchmark tests the performance of the plant detail screen with partial compilation.
     * Partial compilation is the most realistic benchmark mode as it mimics the AOT compilation
     * that is done by the Play Store and on the device as a background job.
     */
    @Test
    fun plantDetailCompilationPartial(): Unit = benchmarkPlantDetail(CompilationMode.Partial())

    /**
     * This benchmark tests the performance of the plant detail screen with full compilation.
     * Full compilation provides the best performance, but it is not representative of a real-world
     * scenario. This is because it is not possible to ship a fully compiled app to users.
     */
    @Test
    fun plantDetailCompilationFull(): Unit = benchmarkPlantDetail(CompilationMode.Full())

    /**
     * Measures the performance of scrolling through the plant list and navigating to the detail
     * screen.
     *
     * This function serves as a helper for the actual benchmark tests, which are annotated with
     * @[Test]. It uses the [MacrobenchmarkRule.measureRepeated] method to run the benchmark
     * multiple times and collect performance metrics.
     *
     * The benchmark is configured to start the activity from a cold state, navigate to the plant
     * list, and then measure the action of clicking on a plant to go to its detail screen.
     *
     * @param compilationMode The [CompilationMode] to use for the benchmark, which determines how
     * much of the app's code is pre-compiled.
     */
    private fun benchmarkPlantDetail(compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            iterations = 10,
            startupMode = StartupMode.COLD,
            setupBlock = {
                startActivityAndWait()
                goToPlantListTab()
            }
        ) {
            goToPlantDetail()
        }
}

/**
 * Navigates from the plant list screen to the detail screen of a specific plant.
 *
 * This function finds the plant list `LazyVerticalGrid` (which has the testTag "plant_list"),
 * selects a plant item, and clicks on it to trigger the navigation to the plant detail screen.
 * It then waits until the plant list is no longer visible, confirming the transition has completed.
 *
 * @param index The index of the plant to click. If `null`, it defaults to the current
 * iteration number modulo the number of visible children. This ensures a different plant is
 * selected for each iteration of the benchmark.
 */
fun MacrobenchmarkScope.goToPlantDetail(index: Int? = null) {
    val plantListSelector: BySelector = By.res(packageName, "plant_list")
    val recycler: UiObject2 = device.findObject(plantListSelector)

    // select different item each iteration, but only from the visible ones
    val currentChildIndex: Int = index ?: ((iteration ?: 0) % recycler.childCount)

    val child: UiObject2 = recycler.children[currentChildIndex]
    child.click()
    // wait until plant list is gone
    device.wait(Until.gone(plantListSelector), 5_000)
}
