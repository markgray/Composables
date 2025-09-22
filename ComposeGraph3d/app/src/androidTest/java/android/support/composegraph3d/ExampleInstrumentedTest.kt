package android.support.composegraph3d

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    /**
     * Tests that the application context can be retrieved and its package name is correct.
     *
     * This test verifies that the [InstrumentationRegistry] can provide the target application's
     * context and that the package name of this context matches the expected package name,
     * "android.support.composegraph3d".
     */
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("android.support.composegraph3d", appContext.packageName)
    }
}