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

package com.google.samples.apps.nowinandroid.feature.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig.DARK
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand.ANDROID
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand.DEFAULT
import com.google.samples.apps.nowinandroid.feature.settings.SettingsUiState.Loading
import com.google.samples.apps.nowinandroid.feature.settings.SettingsUiState.Success
import org.junit.Rule
import org.junit.Test

/**
 * Tests for [SettingsDialog].
 */
class SettingsDialogTest {

    /**
     * The compose test rule used in this test. Test rules provide a way to run code before and after
     * test methods. [createAndroidComposeRule]<[ComponentActivity]>() creates a rule that provides
     * a testing environment for Jetpack Compose UI. It launches a simple [ComponentActivity] for
     * hosting the composables under test.
     */
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<
        ActivityScenarioRule<ComponentActivity>,
        ComponentActivity
        > = createAndroidComposeRule<ComponentActivity>()

    /**
     * Convenience function to get a string resource.
     */
    private fun getString(id: Int): String = composeTestRule.activity.resources.getString(id)

    /**
     * When the settings dialog is loading, check that the loading indicator is shown. We use the
     * [AndroidComposeTestRule.setContent] method of our [composeTestRule] to set the content of
     * the activity that our test is running in to a [SettingsDialog] whose arguments are:
     *  - `settingsUiState`: is [Loading]
     *  - `onDismiss`: is a lambda that does nothing
     *  - `onChangeDynamicColorPreference`: is a lambda that does nothing
     *  - `onChangeThemeBrand`: is a lambda that does nothing
     *  - `onChangeDarkThemeConfig`: is a lambda that does nothing
     *
     * The we use the [AndroidComposeTestRule.onNodeWithText] method to find the node with the text
     * "Loading...". We then use the [SemanticsNodeInteraction.assertExists] method of the node
     * returned to check that the node is displayed.
     */
    @Test
    fun whenLoading_showsLoadingText() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Loading,
                onDismiss = {},
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule
            .onNodeWithText(text = getString(R.string.feature_settings_loading))
            .assertExists()
    }

    /**
     * When the settings dialog is in a [Success] state, check that all the default settings are
     * displayed, and that the correct settings are selected for the [UserEditableSettings] that
     * are supplied to the [SettingsDialog]. We use the [AndroidComposeTestRule.setContent] method
     * of our [composeTestRule] to set the content of the activity that our test is running in to
     * a [SettingsDialog] whose arguments are:
     *  - `settingsUiState`: is [Success] whose `settings` argument is a [UserEditableSettings]
     *  whose `brand` is [ANDROID] (theme based on the Android color palette), whose `useDynamicColor`
     *  is `false` (do not use dynamic color), and whose `darkThemeConfig` is [DARK] (Always use
     *  dark theme).
     *  - `onDismiss`: is a lambda that does nothing
     *  - `onChangeDynamicColorPreference`: is a lambda that does nothing
     *  - `onChangeThemeBrand`: is a lambda that does nothing
     *  - `onChangeDarkThemeConfig`: is a lambda that does nothing
     *
     * Then we check that all the possible settings are displayed:
     *  - The [String] whose resource ID is `R.string.feature_settings_brand_default` ("Default")
     *  should exist in the hierarchy.
     *  - The [String] whose resource ID is `R.string.feature_settings_brand_android` ("Android")
     *  should exist in the hierarchy.
     *  - The [String] whose resource ID is `R.string.feature_settings_dark_mode_config_system_default`
     *  ("System Default") should exist in the hierarchy.
     *  - The [String] whose resource ID is `R.string.feature_settings_dark_mode_config_light`
     *  ("Light") should exist in the hierarchy.
     *  - The [String] whose resource ID is `R.string.feature_settings_dark_mode_config_dark`
     *  ("Dark") should exist in the hierarchy.
     *
     * And finally we check that the correct settings are selected: "Android" and "Dark".
     */
    @Test
    fun whenStateIsSuccess_allDefaultSettingsAreDisplayed() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Success(
                    settings = UserEditableSettings(
                        brand = ANDROID,
                        useDynamicColor = false,
                        darkThemeConfig = DARK,
                    ),
                ),
                onDismiss = { },
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        // Check that all the possible settings are displayed.
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_brand_default))
            .assertExists()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_brand_android))
            .assertExists()
        composeTestRule.onNodeWithText(
            text = getString(R.string.feature_settings_dark_mode_config_system_default),
        ).assertExists()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dark_mode_config_light))
            .assertExists()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dark_mode_config_dark))
            .assertExists()

        // Check that the correct settings are selected.
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_brand_android))
            .assertIsSelected()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dark_mode_config_dark))
            .assertIsSelected()
    }

    /**
     * When the settings dialog is in a success state, the device supports dynamic color, and the
     * theme brand is "DEFAULT", the dynamic color option is displayed. We use the
     * [AndroidComposeTestRule.setContent] method of our [composeTestRule] to set the content of
     * the activity that our test is running in to a [SettingsDialog] whose arguments are:
     *  - `settingsUiState`: is [Success] whose [UserEditableSettings] has its `brand` property
     *  set to [DEFAULT] and `useDynamicColor` to `false` (all other properties are irrelevant).
     *  - `supportDynamicColor`: is `true`.
     *  - `onDismiss`: is a lambda that does nothing
     *  - `onChangeDynamicColorPreference`: is a lambda that does nothing
     *  - `onChangeThemeBrand`: is a lambda that does nothing
     *  - `onChangeDarkThemeConfig`: is a lambda that does nothing
     *
     * Then we assert that the `SettingsDialog` contains UI nodes with text matching the string
     * resources with ID `R.string.feature_settings_dynamic_color_preference` ("Use Dynamic color"),
     * `R.string.feature_settings_dynamic_color_yes` ("Yes"), and
     * `R.string.feature_settings_dynamic_color_no` ("No"). Finally we assert that the UI node with
     * the text `R.string.feature_settings_dynamic_color_no` ("No") is selected (since our
     * `settingsUiState` argument to [SettingsDialog] specified that `useDynamicColor` was `false`).
     */
    @Test
    fun whenStateIsSuccess_supportsDynamicColor_usesDefaultBrand_DynamicColorOptionIsDisplayed() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Success(
                    settings = UserEditableSettings(
                        brand = DEFAULT,
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                supportDynamicColor = true,
                onDismiss = {},
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dynamic_color_preference))
            .assertExists()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dynamic_color_yes))
            .assertExists()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dynamic_color_no))
            .assertExists()

        // Check that the correct default dynamic color setting is selected.
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dynamic_color_no))
            .assertIsSelected()
    }

    /**
     * When the settings dialog is in a success state AND the device does not support dynamic color,
     * the dynamic color option is not displayed.
     * We use the [AndroidComposeTestRule.setContent] method of our [composeTestRule] to set the
     * content of the activity that our test is running in to a [SettingsDialog] whose arguments are:
     *  - `settingsUiState`: is [Success] whose `settings` argument is a [UserEditableSettings]
     *  whose `brand` is [ANDROID], `darkThemeConfig` is [DARK], and `useDynamicColor` is `false`.
     *  - `onDismiss`: is a lambda that does nothing
     *  - `onChangeDynamicColorPreference`: is a lambda that does nothing
     *  - `onChangeThemeBrand`: is a lambda that does nothing
     *  - `onChangeDarkThemeConfig`: is a lambda that does nothing
     *
     * Then we locate the node with text "Dynamic color preference", the node with text "Yes", and
     * the node with text "No" and assert that they do not exist.
     */
    @Test
    fun whenStateIsSuccess_notSupportDynamicColor_DynamicColorOptionIsNotDisplayed() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Success(
                    settings = UserEditableSettings(
                        brand = ANDROID,
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                onDismiss = {},
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dynamic_color_preference))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dynamic_color_yes))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dynamic_color_no))
            .assertDoesNotExist()
    }

    /**
     * When the settings dialog is in a success state, the theme brand is "ANDROID", and the
     * dynamic color option is not displayed. We use the [AndroidComposeTestRule.setContent]
     * method of our [composeTestRule] to set the content of the activity that our test is
     * running in to a [SettingsDialog] whose arguments are:
     *  - `settingsUiState`: is [Success] whose `settings` argument is a [UserEditableSettings]
     *  whose `brand` is [ANDROID], `darkThemeConfig` is [DARK], and `useDynamicColor` is `false`.
     *  - `onDismiss`: is a lambda that does nothing
     *  - `onChangeDynamicColorPreference`: is a lambda that does nothing
     *  - `onChangeThemeBrand`: is a lambda that does nothing
     *  - `onChangeDarkThemeConfig`: is a lambda that does nothing
     *
     * Then we locate the node with text "Dynamic color preference", the node with text "Yes", and
     * the node with text "No" and assert that they do not exist.
     */
    @Test
    fun whenStateIsSuccess_usesAndroidBrand_DynamicColorOptionIsNotDisplayed() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Success(
                    settings = UserEditableSettings(
                        brand = ANDROID,
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                onDismiss = {},
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dynamic_color_preference))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dynamic_color_yes))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_dynamic_color_no))
            .assertDoesNotExist()
    }

    /**
     * When the settings dialog is in a success state, all the settings links are displayed. We use
     * the [AndroidComposeTestRule.setContent] method of our [composeTestRule] to set the content
     * of the activity that our test is running in to a [SettingsDialog] whose arguments are:
     *  - `settingsUiState`: is [Success] whose `settings` argument is a [UserEditableSettings]
     *  whose `brand` is [ANDROID], `darkThemeConfig` is [DARK], and `useDynamicColor` is `false`.
     *  - `onDismiss`: is a lambda that does nothing
     *  - `onChangeDynamicColorPreference`: is a lambda that does nothing
     *  - `onChangeThemeBrand`: is a lambda that does nothing
     *  - `onChangeDarkThemeConfig`: is a lambda that does nothing
     *
     * Then we locate the nodes with texts for the "Privacy policy", "Licenses", "Brand guidelines",
     * and "Feedback" links and assert that they all exist.
     */
    @Test
    fun whenStateIsSuccess_allLinksAreDisplayed() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Success(
                    settings = UserEditableSettings(
                        brand = ANDROID,
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                onDismiss = {},
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_privacy_policy))
            .assertExists()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_licenses))
            .assertExists()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_brand_guidelines))
            .assertExists()
        composeTestRule.onNodeWithText(text = getString(R.string.feature_settings_feedback))
            .assertExists()
    }
}
