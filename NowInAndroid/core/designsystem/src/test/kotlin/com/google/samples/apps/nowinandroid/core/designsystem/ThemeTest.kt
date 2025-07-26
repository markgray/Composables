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

@file:Suppress("RedundantValueArgument")

package com.google.samples.apps.nowinandroid.core.designsystem

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.theme.BackgroundTheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.DarkAndroidBackgroundTheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.DarkAndroidColorScheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.DarkAndroidGradientColors
import com.google.samples.apps.nowinandroid.core.designsystem.theme.DarkDefaultColorScheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.GradientColors
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LightAndroidBackgroundTheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LightAndroidColorScheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LightAndroidGradientColors
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LightDefaultColorScheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LocalBackgroundTheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LocalGradientColors
import com.google.samples.apps.nowinandroid.core.designsystem.theme.LocalTintTheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.TintTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

/**
 * Tests [NiaTheme] using different combinations of the theme mode parameters:
 * darkTheme, disableDynamicTheming, and androidTheme.
 *
 * It verifies that the various composition locals — [MaterialTheme], [LocalGradientColors] and
 * [LocalBackgroundTheme] — have the expected values for a given theme mode, as specified by the
 * design system.
 *
 * @[RunWith] ([RobolectricTestRunner]::class): This annotation indicates that the tests in this
 * class should be run using the Robolectric test runner. Robolectric allows you to run Android
 * tests on your local JVM without needing an emulator or physical device.
 */
@RunWith(RobolectricTestRunner::class)
class ThemeTest {

    /**
     * This sets up the JUnit rule for testing Jetpack Compose UIs. [createComposeRule] provides
     * utilities to set Compose content ([ComposeContentTestRule.setContent]) and interact with
     * and assert on the UI.
     */
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    /**
     * Test case for light theme, dynamic color disabled, and android theme disabled.
     * Verifies that the resulting [ColorScheme] of our custom [MaterialTheme.colorScheme] is
     * [LightDefaultColorScheme], and that the [GradientColors] of our custom [LocalGradientColors]
     * is [LightAndroidGradientColors], and that the [BackgroundTheme] of our custom
     * [LocalBackgroundTheme] is [LightAndroidBackgroundTheme], and that the [TintTheme] of our
     * custom [LocalTintTheme] is [defaultTintTheme].
     *
     */
    @Test
    fun darkThemeFalse_dynamicColorFalse_androidThemeFalse() {
        composeTestRule.setContent {
            NiaTheme(
                darkTheme = false,
                disableDynamicTheming = true,
                androidTheme = false,
            ) {
                val colorScheme: ColorScheme = LightDefaultColorScheme
                assertColorSchemesEqual(
                    expectedColorScheme = colorScheme,
                    actualColorScheme = MaterialTheme.colorScheme,
                )
                val gradientColors: GradientColors =
                    defaultGradientColors(colorScheme = colorScheme)
                assertEquals(
                    expected = gradientColors,
                    actual = LocalGradientColors.current,
                )
                val backgroundTheme: BackgroundTheme = defaultBackgroundTheme(colorScheme)
                assertEquals(
                    expected = backgroundTheme,
                    actual = LocalBackgroundTheme.current,
                )
                val tintTheme: TintTheme = defaultTintTheme()
                assertEquals(
                    expected = tintTheme,
                    actual = LocalTintTheme.current,
                )
            }
        }
    }

    /**
     * Test case for dark theme, dynamic color disabled, and android theme disabled.
     * Verifies that the applied theme is [DarkDefaultColorScheme].
     */
    @Test
    fun darkThemeTrue_dynamicColorFalse_androidThemeFalse() {
        composeTestRule.setContent {
            NiaTheme(
                darkTheme = true,
                disableDynamicTheming = true,
                androidTheme = false,
            ) {
                val colorScheme: ColorScheme = DarkDefaultColorScheme
                assertColorSchemesEqual(
                    expectedColorScheme = colorScheme,
                    actualColorScheme = MaterialTheme.colorScheme,
                )
                val gradientColors: GradientColors =
                    defaultGradientColors(colorScheme = colorScheme)
                assertEquals(
                    expected = gradientColors,
                    actual = LocalGradientColors.current,
                )
                val backgroundTheme: BackgroundTheme =
                    defaultBackgroundTheme(colorScheme = colorScheme)
                assertEquals(
                    expected = backgroundTheme,
                    actual = LocalBackgroundTheme.current,
                )
                val tintTheme: TintTheme = defaultTintTheme()
                assertEquals(
                    expected = tintTheme,
                    actual = LocalTintTheme.current,
                )
            }
        }
    }

    /**
     * Test case for light theme, dynamic color enabled, and android theme disabled.
     * Verifies that the applied theme is [dynamicLightColorSchemeWithFallback].
     */
    @Test
    fun darkThemeFalse_dynamicColorTrue_androidThemeFalse() {
        composeTestRule.setContent {
            NiaTheme(
                darkTheme = false,
                disableDynamicTheming = false,
                androidTheme = false,
            ) {
                val colorScheme: ColorScheme = dynamicLightColorSchemeWithFallback()
                assertColorSchemesEqual(
                    expectedColorScheme = colorScheme,
                    actualColorScheme = MaterialTheme.colorScheme,
                )
                val gradientColors: GradientColors =
                    dynamicGradientColorsWithFallback(colorScheme = colorScheme)
                assertEquals(
                    expected = gradientColors,
                    actual = LocalGradientColors.current,
                )
                val backgroundTheme: BackgroundTheme =
                    defaultBackgroundTheme(colorScheme = colorScheme)
                assertEquals(
                    expected = backgroundTheme,
                    actual = LocalBackgroundTheme.current,
                )
                val tintTheme: TintTheme = dynamicTintThemeWithFallback(colorScheme)
                assertEquals(
                    expected = tintTheme,
                    actual = LocalTintTheme.current,
                )
            }
        }
    }

    /**
     * Test case for dark theme, dynamic color enabled, and android theme disabled.
     * Verifies that the applied theme is [dynamicDarkColorSchemeWithFallback].
     */
    @Test
    fun darkThemeTrue_dynamicColorTrue_androidThemeFalse() {
        composeTestRule.setContent {
            NiaTheme(
                darkTheme = true,
                disableDynamicTheming = false,
                androidTheme = false,
            ) {
                val colorScheme: ColorScheme = dynamicDarkColorSchemeWithFallback()
                assertColorSchemesEqual(
                    expectedColorScheme = colorScheme,
                    actualColorScheme = MaterialTheme.colorScheme,
                )
                val gradientColors: GradientColors =
                    dynamicGradientColorsWithFallback(colorScheme = colorScheme)
                assertEquals(
                    expected = gradientColors,
                    actual = LocalGradientColors.current,
                )
                val backgroundTheme: BackgroundTheme = defaultBackgroundTheme(colorScheme)
                assertEquals(
                    expected = backgroundTheme,
                    actual = LocalBackgroundTheme.current,
                )
                val tintTheme: TintTheme = dynamicTintThemeWithFallback(colorScheme = colorScheme)
                assertEquals(
                    expected = tintTheme,
                    actual = LocalTintTheme.current,
                )
            }
        }
    }

    /**
     * Test case for light theme, dynamic color disabled, and android theme enabled.
     * Verifies that the applied theme is [LightAndroidColorScheme].
     */
    @Test
    fun darkThemeFalse_dynamicColorFalse_androidThemeTrue() {
        composeTestRule.setContent {
            NiaTheme(
                darkTheme = false,
                disableDynamicTheming = true,
                androidTheme = true,
            ) {
                val colorScheme: ColorScheme = LightAndroidColorScheme
                assertColorSchemesEqual(
                    expectedColorScheme = colorScheme,
                    actualColorScheme = MaterialTheme.colorScheme,
                )
                val gradientColors: GradientColors = LightAndroidGradientColors
                assertEquals(
                    expected = gradientColors,
                    actual = LocalGradientColors.current,
                )
                val backgroundTheme: BackgroundTheme = LightAndroidBackgroundTheme
                assertEquals(
                    expected = backgroundTheme,
                    actual = LocalBackgroundTheme.current,
                )
                val tintTheme: TintTheme = defaultTintTheme()
                assertEquals(
                    expected = tintTheme,
                    actual = LocalTintTheme.current,
                )
            }
        }
    }

    /**
     * Test case for dark theme, dynamic color disabled, and android theme enabled.
     * Verifies that the applied theme is [DarkAndroidColorScheme].
     */
    @Test
    fun darkThemeTrue_dynamicColorFalse_androidThemeTrue() {
        composeTestRule.setContent {
            NiaTheme(
                darkTheme = true,
                disableDynamicTheming = true,
                androidTheme = true,
            ) {
                val colorScheme: ColorScheme = DarkAndroidColorScheme
                assertColorSchemesEqual(
                    expectedColorScheme = colorScheme,
                    actualColorScheme = MaterialTheme.colorScheme,
                )
                val gradientColors: GradientColors = DarkAndroidGradientColors
                assertEquals(
                    expected = gradientColors,
                    actual = LocalGradientColors.current,
                )
                val backgroundTheme: BackgroundTheme = DarkAndroidBackgroundTheme
                assertEquals(
                    expected = backgroundTheme,
                    actual = LocalBackgroundTheme.current,
                )
                val tintTheme: TintTheme = defaultTintTheme()
                assertEquals(
                    expected = tintTheme,
                    actual = LocalTintTheme.current,
                )
            }
        }
    }

    /**
     * Test case for light theme, dynamic color enabled, and android theme enabled.
     * Verifies that the applied theme is [LightAndroidColorScheme].
     * Dynamic color is available from SDK_INT >= S (API 31), so if this test is run on an older
     * SDK, the dynamic color will not be applied and the theme will be the default light theme.
     * However, the test logic uses [LightAndroidColorScheme] which means that this test case
     * assumes dynamic theming is always available.
     *
     * This means that this test will fail on SDK < S, as the actual theme will be
     * [LightDefaultColorScheme] but the expected will be [LightAndroidColorScheme].
     *
     * TODO: Update this test to correctly verify dynamic theming behavior across different SDK
     *  levels.
     */
    @Test
    fun darkThemeFalse_dynamicColorTrue_androidThemeTrue() {
        composeTestRule.setContent {
            NiaTheme(
                darkTheme = false,
                disableDynamicTheming = false,
                androidTheme = true,
            ) {
                val colorScheme: ColorScheme = LightAndroidColorScheme
                assertColorSchemesEqual(
                    expectedColorScheme = colorScheme,
                    actualColorScheme = MaterialTheme.colorScheme,
                )
                val gradientColors: GradientColors = LightAndroidGradientColors
                assertEquals(
                    expected = gradientColors,
                    actual = LocalGradientColors.current,
                )
                val backgroundTheme: BackgroundTheme = LightAndroidBackgroundTheme
                assertEquals(
                    expected = backgroundTheme,
                    actual = LocalBackgroundTheme.current,
                )
                val tintTheme: TintTheme = defaultTintTheme()
                assertEquals(
                    expected = tintTheme,
                    actual = LocalTintTheme.current,
                )
            }
        }
    }

    /**
     * Test case for dark theme, dynamic color enabled, and android theme enabled.
     * Verifies that the applied theme is [DarkAndroidColorScheme].
     * Dynamic color is available from SDK_INT >= S (API 31), so if this test is run on an older
     * SDK, the dynamic color will not be applied and the theme will be the default dark theme.
     * However, the test logic uses [DarkAndroidColorScheme] which means that this test case
     * assumes dynamic theming is always available.
     *
     * This means that this test will fail on SDK < S, as the actual theme will be
     * [DarkDefaultColorScheme] but the expected will be [DarkAndroidColorScheme].
     *
     * TODO: Update this test to correctly verify dynamic theming behavior across different SDK
     *  levels.
     */
    @Test
    fun darkThemeTrue_dynamicColorTrue_androidThemeTrue() {
        composeTestRule.setContent {
            NiaTheme(
                darkTheme = true,
                disableDynamicTheming = false,
                androidTheme = true,
            ) {
                val colorScheme: ColorScheme = DarkAndroidColorScheme
                assertColorSchemesEqual(
                    expectedColorScheme = colorScheme,
                    actualColorScheme = MaterialTheme.colorScheme,
                )
                val gradientColors: GradientColors = DarkAndroidGradientColors
                assertEquals(
                    expected = gradientColors,
                    actual = LocalGradientColors.current,
                )
                val backgroundTheme: BackgroundTheme = DarkAndroidBackgroundTheme
                assertEquals(
                    expected = backgroundTheme,
                    actual = LocalBackgroundTheme.current,
                )
                val tintTheme: TintTheme = defaultTintTheme()
                assertEquals(
                    expected = tintTheme,
                    actual = LocalTintTheme.current,
                )
            }
        }
    }

    /**
     * Composable function that returns a light [ColorScheme] based on the device's SDK version.
     * - If the SDK version is S (API 31) or higher, it returns a dynamic light color scheme using
     *   [dynamicLightColorScheme] with the current [LocalContext].
     * - Otherwise, it returns the [LightDefaultColorScheme].
     *
     * @return The appropriate light [ColorScheme].
     */
    @Composable
    private fun dynamicLightColorSchemeWithFallback(): ColorScheme = when {
        SDK_INT >= VERSION_CODES.S -> dynamicLightColorScheme(context = LocalContext.current)
        else -> LightDefaultColorScheme
    }

    /**
     * Composable function that returns a dark [ColorScheme] based on the device's SDK version.
     * - If the SDK version is S (API 31) or higher, it returns a dynamic dark color scheme using
     *   [dynamicDarkColorScheme] with the current [LocalContext].
     * - Otherwise, it returns the [DarkDefaultColorScheme].
     *
     * @return The appropriate dark [ColorScheme].
     */
    @Composable
    private fun dynamicDarkColorSchemeWithFallback(): ColorScheme = when {
        SDK_INT >= VERSION_CODES.S -> dynamicDarkColorScheme(context = LocalContext.current)
        else -> DarkDefaultColorScheme
    }

    /**
     * Creates an empty [GradientColors] object.
     *
     * This function is used to create a [GradientColors] object with no gradient,
     * effectively making the background a solid color. The container color is set
     * to the surface color at an elevation of 2 dp, which is a common practice for
     * creating a subtle depth effect.
     *
     * @param colorScheme The [ColorScheme] to use for the gradient colors.
     * @return An empty [GradientColors] object.
     */
    private fun emptyGradientColors(colorScheme: ColorScheme): GradientColors =
        GradientColors(container = colorScheme.surfaceColorAtElevation(elevation = 2.dp))

    /**
     * Returns the default [GradientColors] for the given [colorScheme].
     *
     * @param colorScheme The [ColorScheme] to use for the gradient colors.
     * @return The default [GradientColors].
     */
    private fun defaultGradientColors(colorScheme: ColorScheme): GradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface,
    )

    /**
     * Composable function that returns a [GradientColors] object based on the device's SDK version.
     * - If the SDK version is S (API 31) or higher, it returns an empty [GradientColors] object
     *   using [emptyGradientColors].
     * - Otherwise, it returns the default [GradientColors] using [defaultGradientColors].
     *
     * @param colorScheme The [ColorScheme] to use for the gradient colors.
     * @return The appropriate [GradientColors] object.
     */
    private fun dynamicGradientColorsWithFallback(colorScheme: ColorScheme): GradientColors = when {
        SDK_INT >= VERSION_CODES.S -> emptyGradientColors(colorScheme = colorScheme)
        else -> defaultGradientColors(colorScheme = colorScheme)
    }

    /**
     * Returns the default [BackgroundTheme] for the given [colorScheme].
     *
     * @param colorScheme The [ColorScheme] to use for the background theme.
     * @return The default [BackgroundTheme].
     */
    private fun defaultBackgroundTheme(colorScheme: ColorScheme): BackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )

    /**
     * Returns the default [TintTheme] which is an empty [TintTheme] object.
     * This is used when dynamic theming is not available or not enabled.
     *
     * @return An empty [TintTheme] object.
     */
    private fun defaultTintTheme(): TintTheme = TintTheme()

    /**
     * Returns a [TintTheme] that is either based on the primary color of the given [colorScheme]
     * if the SDK version is S (API 31) or higher, or an empty [TintTheme] otherwise.
     *
     * This function is used to provide a fallback mechanism for devices that do not support
     * dynamic theming.
     *
     * @param colorScheme The [ColorScheme] to use for the tint theme.
     * @return A [TintTheme] object.
     */
    private fun dynamicTintThemeWithFallback(colorScheme: ColorScheme): TintTheme = when {
        SDK_INT >= VERSION_CODES.S -> TintTheme(iconTint = colorScheme.primary)
        else -> TintTheme()
    }

    /**
     * Asserts that two [ColorScheme] objects are equal.
     *
     * This function is a workaround for the fact that the NiA design system specifies all color
     * scheme values, even those that are not used. This means that a direct comparison of two
     * [ColorScheme] objects using `assertEquals` will fail, as the unused values will not be
     * equal.
     *
     * This function compares only the used color scheme values, and will therefore pass if the
     * two [ColorScheme] objects are functionally equivalent.
     *
     * @param expectedColorScheme The expected [ColorScheme].
     * @param actualColorScheme The actual [ColorScheme].
     */
    private fun assertColorSchemesEqual(
        expectedColorScheme: ColorScheme,
        actualColorScheme: ColorScheme,
    ) {
        assertEquals(
            expected = expectedColorScheme.primary,
            actual = actualColorScheme.primary,
        )
        assertEquals(
            expected = expectedColorScheme.onPrimary,
            actual = actualColorScheme.onPrimary,
        )
        assertEquals(
            expected = expectedColorScheme.primaryContainer,
            actual = actualColorScheme.primaryContainer,
        )
        assertEquals(
            expected = expectedColorScheme.onPrimaryContainer,
            actual = actualColorScheme.onPrimaryContainer,
        )
        assertEquals(
            expected = expectedColorScheme.secondary,
            actual = actualColorScheme.secondary,
        )
        assertEquals(
            expected = expectedColorScheme.onSecondary,
            actual = actualColorScheme.onSecondary,
        )
        assertEquals(
            expected = expectedColorScheme.secondaryContainer,
            actual = actualColorScheme.secondaryContainer,
        )
        assertEquals(
            expected = expectedColorScheme.onSecondaryContainer,
            actual = actualColorScheme.onSecondaryContainer,
        )
        assertEquals(
            expected = expectedColorScheme.tertiary,
            actual = actualColorScheme.tertiary,
        )
        assertEquals(
            expected = expectedColorScheme.onTertiary,
            actual = actualColorScheme.onTertiary,
        )
        assertEquals(
            expected = expectedColorScheme.tertiaryContainer,
            actual = actualColorScheme.tertiaryContainer,
        )
        assertEquals(
            expected = expectedColorScheme.onTertiaryContainer,
            actual = actualColorScheme.onTertiaryContainer,
        )
        assertEquals(
            expected = expectedColorScheme.error,
            actual = actualColorScheme.error,
        )
        assertEquals(
            expected = expectedColorScheme.onError,
            actual = actualColorScheme.onError,
        )
        assertEquals(
            expected = expectedColorScheme.errorContainer,
            actual = actualColorScheme.errorContainer,
        )
        assertEquals(
            expected = expectedColorScheme.onErrorContainer,
            actual = actualColorScheme.onErrorContainer,
        )
        assertEquals(
            expected = expectedColorScheme.background,
            actual = actualColorScheme.background,
        )
        assertEquals(
            expected = expectedColorScheme.onBackground,
            actual = actualColorScheme.onBackground,
        )
        assertEquals(
            expected = expectedColorScheme.surface,
            actual = actualColorScheme.surface,
        )
        assertEquals(
            expected = expectedColorScheme.onSurface,
            actual = actualColorScheme.onSurface,
        )
        assertEquals(
            expected = expectedColorScheme.surfaceVariant,
            actual = actualColorScheme.surfaceVariant,
        )
        assertEquals(
            expected = expectedColorScheme.onSurfaceVariant,
            actual = actualColorScheme.onSurfaceVariant,
        )
        assertEquals(
            expected = expectedColorScheme.inverseSurface,
            actual = actualColorScheme.inverseSurface,
        )
        assertEquals(
            expected = expectedColorScheme.inverseOnSurface,
            actual = actualColorScheme.inverseOnSurface,
        )
        assertEquals(
            expected = expectedColorScheme.outline,
            actual = actualColorScheme.outline,
        )
    }
}
