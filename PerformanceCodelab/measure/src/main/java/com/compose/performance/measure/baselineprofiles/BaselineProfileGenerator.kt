package com.compose.performance.measure.baselineprofiles

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiObject2
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * BaselineProfileGenerator is an instrumentation test class designed to generate Baseline Profiles
 * for the "com.compose.performance" application.
 *
 * Baseline Profiles are a powerful way to optimize the startup and runtime performance of an
 * application. They provide the system with information about critical code paths, allowing it to
 * ahead-of-time (AOT) compile these parts, resulting in faster execution.
 *
 * This class utilizes the [androidx.benchmark.macro.junit4.BaselineProfileRule] to automate the
 * process of collecting and generating these profiles.
 *
 * **Key Functionality:**
 *
 * *   **Generates Baseline Profiles:** By executing a series of UI interactions, this test
 *     captures the execution paths of key functionalities within the app, which are then used
 *     to create the Baseline Profile.
 * *   **Targets Specific Package:** The `packageName` parameter ensures that the
 *     generated profile is specific to the "com.compose.performance" application.
 * *   **Includes Startup Profile:** The `includeInStartupProfile` flag specifies that
 *     the generated profile should also be used to optimize the app's startup performance.
 * *   **Simulates User Interactions:** The test simulates user interactions, such as clicking
 *     on a "Next task" button multiple times, to cover various code paths.
 *
 * **Usage:**
 *
 * 1.  Ensure that the application "com.compose.performance" is installed on the test device.
 * 2.  Run this test using Android Studio or the command line:
 *     `./gradlew connectedAndroidTest`
 * 3.  The generated Baseline Profiles will be saved in the project's `src/main/` directory under
 * the profile destination folder.
 */
@LargeTest
@RequiresApi(Build.VERSION_CODES.P)
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    /**
     * This rule allows to generate Baseline Profiles.
     *
     * The [BaselineProfileRule] provides methods to interact with the Baseline Profile generation
     * process. It's crucial for generating and managing baseline profiles which are essential for
     * improving application startup and runtime performance.
     *
     * Use this rule in instrumented tests to:
     *
     * 1.  **Generate Baseline Profiles:** Define code paths to be included in the profile,
     * allowing the system to optimize these paths during installation.
     * 2. **Verify Profile Generation:**  Ensure that the generated profile includes the
     * expected code paths.
     *
     * **How to use:**
     *
     * 1. Annotate the property with `@get:Rule`.
     * 2. Instantiate `BaselineProfileRule`.
     * 3. Use `collectBaselineProfile()` method within a test function to start profile generation.
     *
     * **Key Considerations:**
     *
     * - Profile generation can take a significant amount of time.
     * - The device must have enough storage to accommodate the generated profile.
     * - Ensure the package name is correct for the app being profiled.
     * - It's recommended to run the profile generation on a physical device if possible.
     */
    @get:Rule
    val rule: BaselineProfileRule = BaselineProfileRule()

    /**
     * Generates Baseline Profiles for the "com.compose.performance" application.
     *
     * This function utilizes the [BaselineProfileRule] to collect performance data and
     * generate a baseline profile. The profile is intended to optimize the application's
     * startup and common user interactions.
     *
     * The generated baseline profile will include information about:
     * - Application startup.
     * - Navigation to several tasks by clicking the "Next task" button.
     *
     * The function performs the following steps:
     * 1. **Collects Profile Information:** Initiates the profile collection process using
     * [BaselineProfileRule.collect].
     * 2. **Specifies Package Name:** Sets the target package to "com.compose.performance".
     * 3. **Includes in Startup Profile:** Indicates that the collected information should
     * be included in the startup profile.
     * 4. **Starts and Waits:** Launches the target activity and waits for it to be ready.
     * 5. **Simulates User Interaction:** Repeats the following steps four times:
     *    - Finds the "Next task" button using its description.
     *    - Clicks the "Next task" button.
     *    - Pauses for 1 second to allow the application to update its UI.
     *
     * **Prerequisites:**
     * - The "com.compose.performance" application must be installed on the test device.
     * - The application must have a UI element with the description "Next task".
     * - The application must be designed to handle clicks on the "Next task" button.
     *
     * **Output:**
     * - A baseline profile file generated in the `outputs/baseline-prof.txt` directory,
     *    which can be used to optimize the application performance by passing the output to
     *    the compiler as input.
     */
    @Test
    fun generateBaselineProfiles(): Unit = rule.collect(
        packageName = "com.compose.performance",
        includeInStartupProfile = true,
    ) {
        startActivityAndWait()

        repeat(times = 4) {
            val nextTaskButton: UiObject2 = device.findObject(By.desc("Next task"))
            nextTaskButton.click()
            Thread.sleep(1_000)
        }
    }
}
