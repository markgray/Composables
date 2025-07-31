/*
 * Copyright 2023 The Android Open Source Project
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

@file:OptIn(ExperimentalRoborazziApi::class)

package com.google.samples.apps.nowinandroid.core.testing.util

import android.graphics.Bitmap.CompressFormat.PNG
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.DarkMode
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziATFAccessibilityCheckOptions
import com.github.takahirom.roborazzi.RoborazziATFAccessibilityChecker
import com.github.takahirom.roborazzi.RoborazziATFAccessibilityChecker.CheckLevel
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.RoborazziOptions.CompareOptions
import com.github.takahirom.roborazzi.RoborazziOptions.RecordOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.checkRoboAccessibility
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityViewCheckResult
import com.google.android.apps.common.testing.accessibility.framework.integrations.espresso.AccessibilityViewCheckException
import com.google.android.apps.common.testing.accessibility.framework.utils.contrast.BitmapImage
import com.google.android.apps.common.testing.accessibility.framework.utils.contrast.Image
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.robolectric.RuntimeEnvironment
import java.io.File
import java.io.FileOutputStream

/**
 * Default Roborazzi options for screenshot tests.
 * This object provides a baseline configuration for Roborazzi, ensuring consistency across tests.
 * It's configured for pixel-perfect matching and resizes images to 50% to reduce file size.
 */
val DefaultRoborazziOptions: RoborazziOptions =
    RoborazziOptions(
        // Pixel-perfect matching
        compareOptions = CompareOptions(changeThreshold = 0f),
        // Reduce the size of the PNGs
        recordOptions = RecordOptions(resizeScale = 0.5),
    )

/**
 * Default test devices to be used for screenshot tests.
 */
enum class DefaultTestDevices(val description: String, val spec: String) {
    /**
     * A typical phone screen.
     */
    PHONE(
        description = "phone",
        spec = "spec:shape=Normal,width=640,height=360,unit=dp,dpi=480",
    ),

    /**
     * A typical foldable screen.
     */
    FOLDABLE(
        description = "foldable",
        spec = "spec:shape=Normal,width=673,height=841,unit=dp,dpi=480",
    ),

    /**
     * A typical tablet screen.
     */
    TABLET(
        description = "tablet",
        spec = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480",
    )
}

/**
 * Captures a screenshot of the [body] composable for each device in [DefaultTestDevices].
 *
 * @param screenshotName The name of the screenshot.
 * @param accessibilitySuppressions A matcher for accessibility checks to suppress.
 * @param body The composable to capture a screenshot of.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiDevice(
    screenshotName: String,
    accessibilitySuppressions: Matcher<in AccessibilityViewCheckResult> = Matchers.not(Matchers.anything()),
    body: @Composable () -> Unit,
) {
    DefaultTestDevices.entries.forEach {
        this.captureForDevice(
            deviceName = it.description,
            deviceSpec = it.spec,
            screenshotName = screenshotName,
            body = body,
            accessibilitySuppressions = accessibilitySuppressions,
        )
    }
}

/**
 * Captures a screenshot of the [body] composable for a given [deviceName] and [deviceSpec].
 *
 * @param deviceName The name of the device.
 * @param deviceSpec The device spec string.
 * @param screenshotName The name of the screenshot.
 * @param roborazziOptions The Roborazzi options to use.
 * @param accessibilitySuppressions A matcher for accessibility checks to suppress.
 * @param darkMode Whether to capture the screenshot in dark mode.
 * @param body The composable to capture.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureForDevice(
    deviceName: String,
    deviceSpec: String,
    screenshotName: String,
    roborazziOptions: RoborazziOptions = DefaultRoborazziOptions,
    accessibilitySuppressions: Matcher<in AccessibilityViewCheckResult> = Matchers.not(Matchers.anything()),
    darkMode: Boolean = false,
    body: @Composable () -> Unit,
) {
    val (width: Int, height: Int, dpi: Int) = extractSpecs(deviceSpec = deviceSpec)

    // Set qualifiers from specs
    RuntimeEnvironment.setQualifiers("w${width}dp-h${height}dp-${dpi}dpi")

    this.activity.setContent {
        CompositionLocalProvider(
            value = LocalInspectionMode provides true,
        ) {
            DeviceConfigurationOverride(
                override = DeviceConfigurationOverride.Companion.DarkMode(isDarkMode = darkMode),
            ) {
                body()
            }
        }
    }

    // Run Accessibility checks first so logging is included
    val accessibilityException: AccessibilityViewCheckException? = try {
        this.onRoot().checkRoboAccessibility(
            roborazziATFAccessibilityCheckOptions = RoborazziATFAccessibilityCheckOptions(
                failureLevel = CheckLevel.Error,
                checker = RoborazziATFAccessibilityChecker(
                    preset = AccessibilityCheckPreset.LATEST,
                    suppressions = accessibilitySuppressions,
                ),
            ),
        )
        null
    } catch (e: AccessibilityViewCheckException) {
        e
    }

    this.onRoot()
        .captureRoboImage(
            filePath = "src/test/screenshots/${screenshotName}_$deviceName.png",
            roborazziOptions = roborazziOptions,
        )

    // Rethrow the Accessibility exception once screenshots have passed
    if (accessibilityException != null) {
        accessibilityException.results.forEachIndexed { index: Int, check: AccessibilityViewCheckResult ->
            val viewImage: Image? = check.viewImage
            if (viewImage is BitmapImage) {
                val file =
                    File("build/outputs/roborazzi/${screenshotName}_${deviceName}_$index.png")
                @Suppress("ReplacePrintlnWithLogging")
                println("Writing check.viewImage to $file")
                FileOutputStream(
                    file,
                ).use {
                    viewImage.bitmap.compress(PNG, 100, it)
                }
            }
        }

        throw accessibilityException
    }
}

/**
 * Takes screenshots of the given [content] in the specified permutations of light/dark,
 * default/Android theme, and dynamic color enabled/disabled.
 *
 * @param name The name of the screenshot.
 * @param overrideFileName The name of the screenshot file. If null, [name] will be used.
 * @param shouldCompareDarkMode Whether to compare dark and light mode.
 * @param shouldCompareDynamicColor Whether to compare dynamic color enabled and disabled.
 * @param shouldCompareAndroidTheme Whether to compare default and Android theme.
 * @param content The composable to capture. The current theme description will be passed to this
 * composable.
 */
fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiTheme(
    name: String,
    overrideFileName: String? = null,
    shouldCompareDarkMode: Boolean = true,
    shouldCompareDynamicColor: Boolean = true,
    shouldCompareAndroidTheme: Boolean = true,
    content: @Composable (desc: String) -> Unit,
) {
    val darkModeValues: List<Boolean> =
        if (shouldCompareDarkMode) listOf(true, false) else listOf(false)
    val dynamicThemingValues: List<Boolean> =
        if (shouldCompareDynamicColor) listOf(true, false) else listOf(false)
    val androidThemeValues: List<Boolean> =
        if (shouldCompareAndroidTheme) listOf(true, false) else listOf(false)

    var darkMode: Boolean by mutableStateOf(value = true)
    var dynamicTheming: Boolean by mutableStateOf(value = false)
    var androidTheme: Boolean by mutableStateOf(value = false)

    this.setContent {
        CompositionLocalProvider(
            value = LocalInspectionMode provides true,
        ) {
            NiaTheme(
                androidTheme = androidTheme,
                darkTheme = darkMode,
                disableDynamicTheming = !dynamicTheming,
            ) {
                // Keying is necessary in some cases (e.g. animations)
                key(androidTheme, darkMode, dynamicTheming) {
                    val description: String = generateDescription(
                        shouldCompareDarkMode,
                        darkMode,
                        shouldCompareAndroidTheme,
                        androidTheme,
                        shouldCompareDynamicColor,
                        dynamicTheming,
                    )
                    content(description)
                }
            }
        }
    }

    // Create permutations
    darkModeValues.forEach { isDarkMode: Boolean ->
        @Suppress("AssignedValueIsNeverRead")
        darkMode = isDarkMode
        val darkModeDesc = if (isDarkMode) "dark" else "light"

        androidThemeValues.forEach { isAndroidTheme: Boolean ->
            @Suppress("AssignedValueIsNeverRead")
            androidTheme = isAndroidTheme
            val androidThemeDesc: String = if (isAndroidTheme) "androidTheme" else "defaultTheme"

            dynamicThemingValues.forEach dynamicTheme@{ isDynamicTheming: Boolean ->
                // Skip tests with both Android Theme and Dynamic color as they're incompatible.
                if (isAndroidTheme && isDynamicTheming) return@dynamicTheme

                @Suppress("AssignedValueIsNeverRead")
                dynamicTheming = isDynamicTheming
                val dynamicThemingDesc: String = if (isDynamicTheming) "dynamic" else "notDynamic"

                val filename: String = overrideFileName ?: name

                this.onRoot()
                    .captureRoboImage(
                        filePath = "src/test/screenshots/" +
                            "$name/$filename" +
                            "_$darkModeDesc" +
                            "_$androidThemeDesc" +
                            "_$dynamicThemingDesc" +
                            ".png",
                        roborazziOptions = DefaultRoborazziOptions,
                    )
            }
        }
    }
}

/**
 * Generates a description based on the given theme parameters.
 *
 * @param shouldCompareDarkMode Whether to include dark mode in the description.
 * @param darkMode Whether dark mode is enabled.
 * @param shouldCompareAndroidTheme Whether to include Android theme in the description.
 * @param androidTheme Whether Android theme is enabled.
 * @param shouldCompareDynamicColor Whether to include dynamic color in the description.
 * @param dynamicTheming Whether dynamic theming is enabled.
 * @return A string describing the current theme configuration.
 */
@Composable
private fun generateDescription(
    shouldCompareDarkMode: Boolean,
    darkMode: Boolean,
    shouldCompareAndroidTheme: Boolean,
    androidTheme: Boolean,
    shouldCompareDynamicColor: Boolean,
    dynamicTheming: Boolean,
): String {
    val description: String = "" +
        if (shouldCompareDarkMode) {
            if (darkMode) "Dark" else "Light"
        } else {
            ""
        } +
        if (shouldCompareAndroidTheme) {
            if (androidTheme) " Android" else " Default"
        } else {
            ""
        } +
        if (shouldCompareDynamicColor) {
            if (dynamicTheming) " Dynamic" else ""
        } else {
            ""
        }

    return description.trim()
}

/**
 * Extracts width, height, and dpi from the device spec string.
 * This function is not exhaustive and only extracts the mentioned properties.
 *
 * @param deviceSpec The device spec string, e.g., "spec:shape=Normal,width=640,height=360,unit=dp,dpi=480".
 * @return [TestDeviceSpecs] containing the extracted width, height, and dpi. Defaults to width=640,
 * height=480, dpi=480 if not found in the spec string.
 */
private fun extractSpecs(deviceSpec: String): TestDeviceSpecs {
    val specs: Map<String, String> = deviceSpec.substringAfter(delimiter = "spec:")
        .split(",").map { it.split("=") }.associate { it[0] to it[1] }
    val width: Int = specs["width"]?.toInt() ?: 640
    val height: Int = specs["height"]?.toInt() ?: 480
    val dpi: Int = specs["dpi"]?.toInt() ?: 480
    return TestDeviceSpecs(width = width, height = height, dpi = dpi)
}

/**
 * Data class representing the specifications of a test device.
 *
 * @property width The width of the device screen in dp.
 * @property height The height of the device screen in dp.
 * @property dpi The screen density in dots per inch (dpi).
 */
data class TestDeviceSpecs(val width: Int, val height: Int, val dpi: Int)
