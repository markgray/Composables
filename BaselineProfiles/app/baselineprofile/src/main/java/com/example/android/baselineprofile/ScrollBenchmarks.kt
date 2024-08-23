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
 * TODO: Add kdoc
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class ScrollBenchmarks {

    /**
     * TODO: Add kdoc
     */
    @get:Rule
    val rule: MacrobenchmarkRule = MacrobenchmarkRule()

    /**
     * TODO: Add kdoc
     */
    @Test
    fun scrollCompilationNone(): Unit = scroll(CompilationMode.None())

    /**
     * TODO: Add kdoc
     */
    @Test
    fun scrollCompilationBaselineProfiles(): Unit = scroll(CompilationMode.Partial())

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
