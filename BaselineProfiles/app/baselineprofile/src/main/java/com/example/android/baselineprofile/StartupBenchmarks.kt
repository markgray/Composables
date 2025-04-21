package com.example.android.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class benchmarks the speed of app startup.
 * Run this benchmark to verify how effective a Baseline Profile is.
 * It does this by comparing [CompilationMode.None], which represents the app with no Baseline
 * Profiles optimizations, and [CompilationMode.Partial], which uses Baseline Profiles.
 *
 * Run this benchmark to see startup measurements and captured system traces for verifying
 * the effectiveness of your Baseline Profiles. You can run it directly from Android
 * Studio as an instrumentation test, or run all benchmarks for a variant, for example
 * benchmarkRelease, with this Gradle task:
 * ```
 * ./gradlew :app:baselineprofile:connectedBenchmarkReleaseAndroidTest
 * ```
 *
 * You should run the benchmarks on a physical device, not an Android emulator, because the
 * emulator doesn't represent real world performance and shares system resources with its host.
 *
 * For more information, see the
 * [Macrobenchmark documentation](https://d.android.com/macrobenchmark#create-macrobenchmark)
 * and the
 * [instrumentation arguments documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args).
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmarks {

    /**
     * This rule is responsible for running macrobenchmarks.
     * It provides functionality to start and end tracing, compile the application,
     * and measure the performance of interactions or actions within the app.
     * Use it in conjunction with `@Test` annotated methods to define specific benchmark tests.
     */
    @get:Rule
    val rule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * Tests app startup time with no startup compilation.
     *
     * This test measures the cold startup time of the app when no pre-compilation
     * has been performed. This means the app's code will be interpreted or
     * JIT-compiled at runtime, resulting in the slowest possible startup time.
     *
     * It's crucial to understand the performance implications of a completely
     * uncompiled startup, as this represents the worst-case scenario for
     * users experiencing a fresh installation or a forced stop.
     *
     * By comparing the results of this benchmark with those using other compilation
     * modes (e.g., [CompilationMode.Full]), developers can quantify the benefits
     * of pre-compilation strategies and optimize their app's startup experience.
     */
    @Test
    fun startupCompilationNone(): Unit =
        benchmark(CompilationMode.None())

    /**
     * This method runs Macrobenchmark tests that measure the performance of app startup
     * with baseline profiles.
     *
     * It just calls our [benchmark] method with its `compilationMode` argument an
     * [CompilationMode.Partial] instance with its `baselineProfileMode` argument
     * [BaselineProfileMode.Require] (Requires the BaselineProfile methods/classes from the target
     * app to be pre-compiled).
     */
    @Test
    fun startupCompilationBaselineProfiles(): Unit =
        benchmark(CompilationMode.Partial(baselineProfileMode = BaselineProfileMode.Require))

    /**
     * Measures the performance of app startup.
     *
     * This function uses the [MacrobenchmarkRule.measureRepeated] function to repeatedly measure
     * the time it takes to start the application under different compilation modes. It simulates
     * a user journey by starting from the home screen, launching the activity, waiting for
     * content to load, and then performing some interactions.
     *
     * The arguments that it passes to [MacrobenchmarkRule.measureRepeated] are:
     *  - `packageName`: The package name of the application being benchmarked which it reads from
     *  the instrumentation arguments using the key "targetAppId".
     *  - `metrics`: A list of metrics to measure during the benchmark. In this case, it's a single
     *  metric, [StartupTimingMetric].
     *  - `compilationMode`: The compilation mode to use for the benchmark, is our [CompilationMode]
     *  parameter `compilationMode`.
     *  - `startupMode`: The startup mode to use for the benchmark is [StartupMode.COLD] (Startup
     *  from scratch - app's process is not alive, and must be started in addition to Activity
     *  creation).
     *  - `iterations`: The number of times the benchmark should be run, in our case `10`.
     *  - `setupBlock`: A block of code to execute before each iteration of the benchmark. In this
     *  case, it's used to press the home button.
     *  - `measureBlock`: A block of code to execute for each iteration of the benchmark. In this
     *  case, it's used to start the activity by calling the [MacrobenchmarkScope.startActivityAndWait]
     *  method, and then wait for content to load by calling [MacrobenchmarkScope.waitForAsyncContent].
     *
     * @param compilationMode The compilation mode to use for the benchmark (e.g.,
     * [CompilationMode.None], [CompilationMode.Partial]). This determines how the
     * application's code is compiled before the benchmark is run, which can significantly
     * affect performance.
     */
    private fun benchmark(compilationMode: CompilationMode) {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.measureRepeated(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 10,
            setupBlock = {
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()

                // TODO Add interactions to wait for when your app is fully drawn.
                // The app is fully drawn when Activity.reportFullyDrawn is called.
                // For Jetpack Compose, you can use ReportDrawn, ReportDrawnWhen and ReportDrawnAfter
                // from the AndroidX Activity library.
                waitForAsyncContent() // <------- Added to wait for async content.

                // Check the UiAutomator documentation for more information on how to
                // interact with the app.
                // https://d.android.com/training/testing/other-components/ui-automator
            }
        )
    }

    /**
     * Waits for asynchronous content to be rendered on the screen.
     *
     * This function specifically waits for the presence of a list with the resource ID "snack_list"
     * and then for at least one item within that list (identified by the resource ID
     * "snack_collection") to be rendered. This is useful for ensuring that UI elements that are
     * loaded asynchronously are present before proceeding with further interactions or measurements
     * in a macrobenchmark.
     *
     * The function uses `device.wait` to pause execution until a condition is met, with a timeout
     * of 5 seconds. It first checks for the "snack_list" and then for the presence of a
     * "snack_collection" within that list.
     *
     * @receiver MacrobenchmarkScope The scope in which the benchmark is executed, providing access
     * to the device.
     */
    private fun MacrobenchmarkScope.waitForAsyncContent() {
        device.wait(Until.hasObject(By.res("snack_list")), 5_000)
        val contentList: UiObject2 = device.findObject(By.res("snack_list"))
        // Wait until a snack collection item within the list is rendered.
        contentList.wait(Until.hasObject(By.res("snack_collection")), 5_000)
    }
}