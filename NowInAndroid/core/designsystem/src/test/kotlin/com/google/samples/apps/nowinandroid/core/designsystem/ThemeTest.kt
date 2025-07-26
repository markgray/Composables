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
 * TODO: Continue here.
 */
@RunWith(RobolectricTestRunner::class)
class ThemeTest {

    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

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

    @Composable
    private fun dynamicLightColorSchemeWithFallback(): ColorScheme = when {
        SDK_INT >= VERSION_CODES.S -> dynamicLightColorScheme(context = LocalContext.current)
        else -> LightDefaultColorScheme
    }

    @Composable
    private fun dynamicDarkColorSchemeWithFallback(): ColorScheme = when {
        SDK_INT >= VERSION_CODES.S -> dynamicDarkColorScheme(context = LocalContext.current)
        else -> DarkDefaultColorScheme
    }

    private fun emptyGradientColors(colorScheme: ColorScheme): GradientColors =
        GradientColors(container = colorScheme.surfaceColorAtElevation(elevation = 2.dp))

    private fun defaultGradientColors(colorScheme: ColorScheme): GradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface,
    )

    private fun dynamicGradientColorsWithFallback(colorScheme: ColorScheme): GradientColors = when {
        SDK_INT >= VERSION_CODES.S -> emptyGradientColors(colorScheme = colorScheme)
        else -> defaultGradientColors(colorScheme = colorScheme)
    }

    private fun defaultBackgroundTheme(colorScheme: ColorScheme): BackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )

    private fun defaultTintTheme(): TintTheme = TintTheme()

    private fun dynamicTintThemeWithFallback(colorScheme: ColorScheme): TintTheme = when {
        SDK_INT >= VERSION_CODES.S -> TintTheme(iconTint = colorScheme.primary)
        else -> TintTheme()
    }

    /**
     * Workaround for the fact that the NiA design system specify all color scheme values.
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
