package com.example.android.baselineprofile

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [ScrollBenchmarks] is a collection of macrobenchmark tests designed to measure the scrolling
 * performance of the application's snack list under different compilation modes.
 *
 * These benchmarks help analyze the impact of Just-In-Time (JIT) compilation and Baseline Profiles
 * on the smoothness and efficiency of list scrolling. By measuring frame timing metrics, we can
 * identify potential performance bottlenecks and optimize the application for better user
 * experience.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class ScrollBenchmarks {

    /**
     * This rule is responsible for running macrobenchmarks.
     * It provides functionality to start and end tracing, compile the application,
     * and measure the performance of interactions or actions within the app.
     * Use it in conjunction with `@Test` annotated methods to define specific benchmark tests.
     */
    @get:Rule
    val rule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * Tests scrolling behavior when compilation is set to None.
     *
     * This test case verifies that the scroll function operates correctly
     * when the compilation mode is set to `CompilationMode.None`. This typically
     * indicates a scenario where no specific compilation or optimization steps
     * are performed during the scroll operation. It's useful for testing
     * raw or unoptimized scrolling behavior.
     *
     * The test calls the [scroll] method with its `compilationMode` argument
     * [CompilationMode.None].
     */
    @Test
    fun scrollCompilationNone(): Unit = scroll(compilationMode = CompilationMode.None())

    /**
     * Tests scrolling behavior when compilation is set to Partial.
     *
     * This test case verifies that the scroll function operates correctly
     * when the compilation mode is set to `CompilationMode.Partial()`. This
     * typically indicates a scenario where some specific compilation steps
     * are performed during the scroll operation. It's useful for testing
     * partial compilation optimizations.
     *
     * This test calls the [scroll] method with its `compilationMode` argument
     * [CompilationMode.Partial].
     */
    @Test
    fun scrollCompilationBaselineProfiles(): Unit = scroll(compilationMode = CompilationMode.Partial())

    /**
     * Measures the performance of scrolling through a list of snacks in the application.
     *
     * This function uses the [MacrobenchmarkRule.measureRepeated] function to repeatedly measure
     * the time it takes to scroll through a snack list in the application under different
     * compilation modes. It simulates a user journey by starting from the home screen,
     * launching the activity, waiting for content to load, and then performing the scroll
     * action.
     *
     * The arguments that this function takes are:
     *  - `packageName`: The package name of the application being benchmarked.
     *  - `metrics`: A list of metrics to measure during the benchmark. In this case,
     *  it's a single metric, [FrameTimingMetric].
     *  - `compilationMode`: The compilation mode to use for the benchmark.
     *  - `startupMode`: The startup mode to use for the benchmark.
     *  - `iterations`: The number of times the benchmark should be run.
     *  - `setupBlock`: A block of code to execute before each iteration of the benchmark.
     *  In this case, it's used to press the home button, start the activity, and wait
     *  for content to load.
     *  - `measureBlock`: A block of code to execute for each iteration of the benchmark.
     *  In this case, it's used to scroll through the snack list by first calling
     *  [waitForAsyncContent] to wait for content to load and then [scrollSnackListJourney]
     *  to scroll through the list.
     *
     * @param compilationMode The compilation mode to use for the benchmark (e.g.,
     * [CompilationMode.None], [CompilationMode.Partial]). This determines how the
     * application's code is compiled before the benchmark is run, which can significantly
     * affect performance.
     *
     * @see MacrobenchmarkRule
     * @see FrameTimingMetric
     * @see CompilationMode
     * @see StartupMode
     */
    private fun scroll(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = "com.example.baselineprofiles_codelab",
            metrics = listOf(FrameTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.WARM,
            iterations = 10,
            setupBlock = {
                pressHome()
                startActivityAndWait()
            },
            measureBlock = {
                waitForAsyncContent()
                scrollSnackListJourney()
            }
        )
    }

    private fun MacrobenchmarkScope.waitForAsyncContent() {
        device.wait(Until.hasObject(By.res("snack_list")), 5_000)
        val contentList = device.findObject(By.res("snack_list"))
        // Wait until a snack collection item within the list is rendered.
        contentList.wait(Until.hasObject(By.res("snack_collection")), 5_000)
    }

    private fun MacrobenchmarkScope.scrollSnackListJourney() {
        val snackList = device.findObject(By.res("snack_list"))
        // Set gesture margin to avoid triggering gesture navigation.
        snackList.setGestureMargin(device.displayWidth / 5)
        snackList.fling(Direction.DOWN)
        device.waitForIdle()
    }
}
