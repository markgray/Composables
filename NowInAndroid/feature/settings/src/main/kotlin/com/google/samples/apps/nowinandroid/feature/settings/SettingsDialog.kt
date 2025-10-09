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

@file:Suppress("ktlint:standard:max-line-length")

package com.google.samples.apps.nowinandroid.feature.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaTextButton
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.designsystem.theme.supportsDynamicTheming
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig.DARK
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig.FOLLOW_SYSTEM
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig.LIGHT
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand.ANDROID
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand.DEFAULT
import com.google.samples.apps.nowinandroid.core.ui.TrackScreenViewEvent
import com.google.samples.apps.nowinandroid.feature.settings.R.string
import com.google.samples.apps.nowinandroid.feature.settings.SettingsUiState.Loading
import com.google.samples.apps.nowinandroid.feature.settings.SettingsUiState.Success
import kotlinx.coroutines.flow.StateFlow

/**
 * Composable that displays the settings dialog. We initialize our [State] wrapped [SettingsUiState]
 * variable `settingsUiState` using the [collectAsStateWithLifecycle] extension function of the
 * [StateFlow] of [SettingsUiState] property [SettingsViewModel.settingsUiState] of our
 * [SettingsViewModel] parameter [viewModel]. Then we compose our stateless [SettingsDialog]
 * overload with the arguments:
 *  - `onDismiss`: our [onDismiss] lambda parameter.
 *  - `settingsUiState`: our [State] wrapped [SettingsUiState] variable `settingsUiState`.
 *  - `onChangeThemeBrand`: a reference to the [SettingsViewModel.updateThemeBrand] method of our
 *  [SettingsViewModel] parameter [viewModel].
 *  - `onChangeDynamicColorPreference`: a reference to the [SettingsViewModel.updateDynamicColorPreference]
 *  method of our [SettingsViewModel] parameter [viewModel].
 *  - `onChangeDarkThemeConfig`: a reference to the [SettingsViewModel.updateDarkThemeConfig] method
 *  of our [SettingsViewModel] parameter [viewModel].
 *
 * @param onDismiss Called when the dialog is dismissed.
 * @param viewModel The view model that is used to manage the settings.
 */
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
        null,
    ),
) {
    val settingsUiState: SettingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()
    SettingsDialog(
        onDismiss = onDismiss,
        settingsUiState = settingsUiState,
        onChangeThemeBrand = viewModel::updateThemeBrand,
        onChangeDynamicColorPreference = viewModel::updateDynamicColorPreference,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
    )
}

/**
 * The stateless settings dialog.
 *
 * We start by initializing our [Configuration] variable `configuration` to the `current`
 * [LocalConfiguration]. Then we compose our [AlertDialog] with the arguments:
 *  - `properties`: a [DialogProperties] whose [DialogProperties.usePlatformDefaultWidth] property
 *  is `false`. (a kludge to get around a compose bug which may or may not be fixed in the future).
 *  - `modifier`: a [Modifier.widthIn] whose `max` argument is `configuration.screenWidthDp.dp - 80.dp`.
 *  - `onDismissRequest`: a lambda that calls our [onDismiss] lambda parameter.
 *  - `title`: a lambda that composes a [Text] whose `text` argument is the [String] with resource
 *  id `string.feature_settings_title` ("Settings")
 *  - `text`: a lambda that composes a [HorizontalDivider] followed by a [Column] whose `modifier`
 *  argument is a [Modifier.verticalScroll] whose `state` argument is the remember [ScrollState]
 *  returned by [rememberScrollState]. In the [ColumnScope] `content` composable lambda argument of
 *  the [Column] we branch on the type of [SettingsUiState] parameter [settingsUiState], and when it
 *  is a [SettingsUiState.Loading] we compose a [Text] whose `text` argument is the [String] with
 *  resource id `string.feature_settings_loading` ("Loading") and whose `modifier` argument is a
 *  [Modifier.padding] that adds `16.dp` to the vertical sides When it is a [SettingsUiState.Success]
 *  we compose a [SettingsPanel] whose `settings` is the [Success.settings] property of the
 *  [SettingsUiState] parameter [settingsUiState], whose `supportDynamicColor` argument is our
 *  [Boolean] parameter [supportDynamicColor], whose `onChangeThemeBrand` argument is our lambda
 *  parameter [onChangeThemeBrand], whose `onChangeDynamicColorPreference` argument is our lambda
 *  parameter [onChangeDynamicColorPreference], and whose `onChangeDarkThemeConfig` argument is our
 *  lambda parameter [onChangeDarkThemeConfig]. Below these two alternative composables in the
 *  [Column] we compose a [HorizontalDivider] whose `modifier` argument is a [Modifier.padding] that
 *  adds `8.dp` to the `top` side, and below that we compose a [LinksPanel]. At the end of the `text`
 *  argument we call the [TrackScreenViewEvent] method to record a screen view event whose `screenName`
 *  is "Settings".
 *  - `confirmButton`: a lambda that composes a [NiaTextButton] whose `onClick` argument is our lambda
 *  parameter [onDismiss] and whose `modifier` argument is a [Modifier.padding] that adds `8.dp` to
 *  the horizontal sides. In the [NiaTextButton] lambda argument we compose a [Text] whose `text`
 *  argument is the [String] with resource id `string.feature_settings_dismiss_dialog_button_text`
 *  ("OK"), whose [TextStyle] `style` argument is the [Typography.labelLarge] of our custom
 *  [MaterialTheme.typography], and whose [Color] `color` argument is the [ColorScheme.primary] of
 *  our custom [MaterialTheme.colorScheme].
 *
 * @param settingsUiState The UI state of the settings dialog.
 * @param supportDynamicColor Whether the device supports dynamic color.
 * @param onDismiss The action to perform when the dialog is dismissed.
 * @param onChangeThemeBrand The action to perform when the theme brand is changed.
 * @param onChangeDynamicColorPreference The action to perform when the dynamic color preference is
 * changed.
 * @param onChangeDarkThemeConfig The action to perform when the dark theme config is changed.
 */
@Composable
fun SettingsDialog(
    settingsUiState: SettingsUiState,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onDismiss: () -> Unit,
    onChangeThemeBrand: (themeBrand: ThemeBrand) -> Unit,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
    val configuration: Configuration = LocalConfiguration.current

    /**
     * usePlatformDefaultWidth = false is use as a temporary fix to allow
     * height recalculation during recomposition. This, however, causes
     * Dialog's to occupy full width in Compact mode. Therefore max width
     * is configured below. This should be removed when there's fix to
     * https://issuetracker.google.com/issues/221643630
     */
    @SuppressLint("ConfigurationScreenWidthHeight") // TODO: Remove when fixed, or use currentWindowAdaptiveInfo
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(id = string.feature_settings_title),
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            HorizontalDivider()
            Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
                when (settingsUiState) {
                    Loading -> {
                        Text(
                            text = stringResource(id = string.feature_settings_loading),
                            modifier = Modifier.padding(vertical = 16.dp),
                        )
                    }

                    is Success -> {
                        SettingsPanel(
                            settings = settingsUiState.settings,
                            supportDynamicColor = supportDynamicColor,
                            onChangeThemeBrand = onChangeThemeBrand,
                            onChangeDynamicColorPreference = onChangeDynamicColorPreference,
                            onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                LinksPanel()
            }
            TrackScreenViewEvent(screenName = "Settings")
        },
        confirmButton = {
            NiaTextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(horizontal = 8.dp),
            ) {
                Text(
                    text = stringResource(id = string.feature_settings_dismiss_dialog_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}

// [ColumnScope] is used for using the [ColumnScope.AnimatedVisibility] extension overload composable.
/**
 * This composable allows the user to change the settings that affect the appearance of the app.
 * It is used as part of the `content` of the [Column] in the `text` lambda argument of the
 * [AlertDialog] displayed by the [SettingsDialog] composable's [SettingsUiState.Success] branch.
 *
 * We start by composing a [SettingsDialogSectionTitle] whose `text` argument is the [String] with
 * resource id `string.feature_settings_theme` ("Theme"). Then we compose a [Column] whose `modifier`
 * argument is a [Modifier.selectableGroup]. In the [ColumnScope] `content` composable lambda argument
 * we compose two [SettingsDialogThemeChooserRow] composables, one for the [DEFAULT] theme and one for
 * the [ANDROID] theme. The [DEFAULT] theme is selected if the [UserEditableSettings.brand] property
 * of the [UserEditableSettings] parameter [settings] is [DEFAULT], and the [ANDROID] theme is
 * selected if the [UserEditableSettings.brand] property of the [UserEditableSettings] parameter
 * [settings] is [ANDROID]. The `text` argument of the [DEFAULT] is "Default" and the `text` argument
 * of the [ANDROID] is "Android" and the `onClick` argument of the [DEFAULT] is a lambda that calls
 * our [onChangeThemeBrand] lambda parameter with the [DEFAULT] value, and the `onClick` argument
 * of the [ANDROID] is a lambda that calls our [onChangeThemeBrand] lambda parameter with the
 * [ANDROID] value. Below these two composables we compose a [AnimatedVisibility] whose `visible`
 * argument is `true` if the [UserEditableSettings.brand] property of the [UserEditableSettings]
 * parameter [settings] is [DEFAULT] and the [Boolean] parameter [supportDynamicColor] is `true`.
 * Inside the [AnimatedVisibilityScope] `content` composable lambda argument we compose a [Column]
 * which contains a [SettingsDialogSectionTitle] whose `text` argument is the [String] with
 * resource id `string.feature_settings_dynamic_color_preference` ("Use Dynamic color") followed by
 * a [Column] whose `modifier` argument is a [Modifier.selectableGroup]. In the [ColumnScope]
 * `content` composable lambda argument of this inner [Column] we compose two
 * [SettingsDialogThemeChooserRow] composables, one for the `true` value and one for the `false`
 * value. The `text` argument of the `true` is "Yes" and the `text` argument of the `false` is
 * "No". The `selected` argument of the `true` is `settings.useDynamicColor` and the `selected`
 * argument of the `false` is `!settings.useDynamicColor`. The `onClick` argument of the `true`
 * is a lambda that calls our [onChangeDynamicColorPreference] lambda parameter with the `true`
 * value, and the `onClick` argument of the `false` is a lambda that calls our
 * [onChangeDynamicColorPreference] lambda parameter with the `false` value.
 *
 * Next we compose a [SettingsDialogSectionTitle] whose `text` argument is the [String] with
 * resource id `string.feature_settings_dark_mode_preference` ("Dark mode preference") followed by
 * a [Column] whose `modifier` argument is a [Modifier.selectableGroup]. In the [ColumnScope]
 * `content` composable lambda argument of this [Column] we compose three
 * [SettingsDialogThemeChooserRow] composables, one for the [FOLLOW_SYSTEM] value, one for the
 * [LIGHT] value, and one for the [DARK] value. The `text` argument of the [FOLLOW_SYSTEM] is
 * "System default" and the `text` argument of the [LIGHT] is "Light" and the `text` argument of
 * the [DARK] is "Dark". The `selected` argument of the [FOLLOW_SYSTEM] is `true` if
 * the [UserEditableSettings.darkThemeConfig] property of the [UserEditableSettings] parameter
 * [settings] is [FOLLOW_SYSTEM], and `false` otherwise. The `selected` argument of the [LIGHT] is
 * `true` if the [UserEditableSettings.darkThemeConfig] property of the [UserEditableSettings]
 * parameter [settings] is [LIGHT], and `false` otherwise. The `selected` argument of the [DARK]
 * is `true` if the [UserEditableSettings.darkThemeConfig] property of the [UserEditableSettings]
 * parameter [settings] is [DARK], and `false` otherwise. The `onClick` argument of the [FOLLOW_SYSTEM]
 * is a lambda that calls our [onChangeDarkThemeConfig] lambda parameter with the [FOLLOW_SYSTEM]
 * value, the `onClick` argument of the [LIGHT] is a lambda that calls our
 * [onChangeDarkThemeConfig] lambda parameter with the [LIGHT] value, and the `onClick` argument
 * of the [DARK] is a lambda that calls our [onChangeDarkThemeConfig] lambda parameter with the
 * [DARK] value. The `text` argument of the [FOLLOW_SYSTEM] is "System default", the `text`
 * argument of the [LIGHT] is "Light" and the `text` argument of the [DARK] is "Dark".
 *
 * @param settings The [UserEditableSettings] settings that are being changed.
 * @param supportDynamicColor Whether the device supports dynamic color.
 * @param onChangeThemeBrand The action to perform when the theme brand is changed.
 * @param onChangeDynamicColorPreference The action to perform when the dynamic color preference is
 * changed.
 * @param onChangeDarkThemeConfig The action to perform when the dark theme config is changed.
 */
@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserEditableSettings,
    supportDynamicColor: Boolean,
    onChangeThemeBrand: (themeBrand: ThemeBrand) -> Unit,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
    SettingsDialogSectionTitle(text = stringResource(id = string.feature_settings_theme))
    Column(modifier = Modifier.selectableGroup()) {
        SettingsDialogThemeChooserRow(
            text = stringResource(id = string.feature_settings_brand_default),
            selected = settings.brand == DEFAULT,
            onClick = { onChangeThemeBrand(DEFAULT) },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(id = string.feature_settings_brand_android),
            selected = settings.brand == ANDROID,
            onClick = { onChangeThemeBrand(ANDROID) },
        )
    }
    AnimatedVisibility(visible = settings.brand == DEFAULT && supportDynamicColor) {
        Column {
            SettingsDialogSectionTitle(text = stringResource(id = string.feature_settings_dynamic_color_preference))
            Column(modifier = Modifier.selectableGroup()) {
                SettingsDialogThemeChooserRow(
                    text = stringResource(id = string.feature_settings_dynamic_color_yes),
                    selected = settings.useDynamicColor,
                    onClick = { onChangeDynamicColorPreference(true) },
                )
                SettingsDialogThemeChooserRow(
                    text = stringResource(id = string.feature_settings_dynamic_color_no),
                    selected = !settings.useDynamicColor,
                    onClick = { onChangeDynamicColorPreference(false) },
                )
            }
        }
    }
    SettingsDialogSectionTitle(text = stringResource(id = string.feature_settings_dark_mode_preference))
    Column(Modifier.selectableGroup()) {
        SettingsDialogThemeChooserRow(
            text = stringResource(id = string.feature_settings_dark_mode_config_system_default),
            selected = settings.darkThemeConfig == FOLLOW_SYSTEM,
            onClick = { onChangeDarkThemeConfig(FOLLOW_SYSTEM) },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(id = string.feature_settings_dark_mode_config_light),
            selected = settings.darkThemeConfig == LIGHT,
            onClick = { onChangeDarkThemeConfig(LIGHT) },
        )
        SettingsDialogThemeChooserRow(
            text = stringResource(id = string.feature_settings_dark_mode_config_dark),
            selected = settings.darkThemeConfig == DARK,
            onClick = { onChangeDarkThemeConfig(DARK) },
        )
    }
}

/**
 * This composable is used to display the title of a section of the settings dialog. It is a [Text]
 * composable whose `text` argument is our [String] parameter [text], whose [TextStyle] `style`
 * argument is the [Typography.titleMedium] of our custom [MaterialTheme.typography], and whose
 * `modifier` argument is a [Modifier.padding] that adds `16.dp` to the `top` and `8.dp` to the
 * `bottom`.
 *
 * @param text the [String] to display.
 */
@Composable
private fun SettingsDialogSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

/**
 * This composable is used to display a row in the settings dialog that allows the user to choose
 * one of a number of settings for a particular category of settings. It consists of a [Row] that
 * contains a [RadioButton] and a [Text]. The [Row] is selectable and when selected will call our
 * [onClick] lambda parameter.
 *
 * The root composable is a [Row]. Its `modifier` argument is a [Modifier.fillMaxWidth] to have the
 * [Row] occupy its entire incoming width constraint, with a [Modifier.selectable] chained to it
 * whose `selected` argument is our [Boolean] parameter [selected], whose `role` argument is
 * [Role.RadioButton], and whose `onClick` argument is our lambda parameter [onClick]. A
 * [Modifier.padding] is chained to that which adds `12.dp` to `all` sides of the [Row]. The
 * `verticalAlignment` argument of the [Row] is [Alignment.CenterVertically] to center its children
 * vertically.
 *
 * The [RowScope] `content` of the [Row] consists of:
 *  - A [RadioButton] whose `selected` argument is our [Boolean] parameter [selected], and whose
 *  `onClick` argument is `null` (the `onClick` of the [Modifier.selectable] of the [Row] is used
 *  instead).
 *  - A [Spacer] whose `modifier` argument is a [Modifier.width] that sets its `width` to `8.dp`.
 *  - A [Text] whose `text` argument is our [String] parameter [text].
 *
 * @param text the [String] to display for this choice.
 * @param selected `true` if this choice is currently selected.
 * @param onClick a lambda that will be called when this choice is clicked.
 */
@Composable
fun SettingsDialogThemeChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(modifier = Modifier.width(width = 8.dp))
        Text(text = text)
    }
}

/**
 * This composable is used to display links to various resources in the settings dialog. It is used
 * in the `text` lambda argument of the [AlertDialog] displayed by the [SettingsDialog] composable's
 * after the [SettingsPanel] (if the [SettingsUiState] is a [SettingsUiState.Success]) or the
 * "Loading..." [Text] (if the [SettingsUiState] is a [SettingsUiState.Loading]).
 *
 * It consists of a [FlowRow] whose `horizontalArrangement` argument is an [Arrangement.spacedBy]
 * whose `space` argument is `16.dp` and whose `alignment` argument is [Alignment.CenterHorizontally]
 * (this places children in a horizontal flow similar to [Row]). Its `modifier` argument is a
 * [Modifier.fillMaxWidth] to have it occupy its entire incoming width constraint.
 *
 * The `content` of the [FlowRow] consists of four [NiaTextButton] composables:
 *  - A "Privacy policy" button which when clicked will open the URI [PRIVACY_POLICY_URL]
 *  ("https://policies.google.com/privacy").
 *  - A "Licenses" button which when clicked will start the [OssLicensesMenuActivity].
 *  - A "Brand guidelines" button which when clicked will open the URI [BRAND_GUIDELINES_URL]
 *  ("https://developer.android.com/distribute/marketing-tools/brand-guidelines").
 *  - A "Feedback" button which when clicked will open the URI [FEEDBACK_URL]
 *  ("https://goo.gle/nia-app-feedback").
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LinksPanel() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        val uriHandler: UriHandler = LocalUriHandler.current
        NiaTextButton(
            onClick = { uriHandler.openUri(uri = PRIVACY_POLICY_URL) },
        ) {
            Text(text = stringResource(id = string.feature_settings_privacy_policy))
        }
        val context = LocalContext.current
        NiaTextButton(
            onClick = {
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            },
        ) {
            Text(text = stringResource(id = string.feature_settings_licenses))
        }
        NiaTextButton(
            onClick = { uriHandler.openUri(uri = BRAND_GUIDELINES_URL) },
        ) {
            Text(text = stringResource(id = string.feature_settings_brand_guidelines))
        }
        NiaTextButton(
            onClick = { uriHandler.openUri(uri = FEEDBACK_URL) },
        ) {
            Text(text = stringResource(id = string.feature_settings_feedback))
        }
    }
}

/**
 * Preview of the [SettingsDialog] composable. We display it wrapped in our [NiaTheme] custom Material
 * Theme. The arguments to the [SettingsDialog] are:
 *  - `onDismiss`: a do nothing lambda.
 *  - `settingsUiState`: a [Success] whose [Success.settings] argument is a [UserEditableSettings]
 *  whose `brand` is [DEFAULT], whose `darkThemeConfig` is [FOLLOW_SYSTEM], and whose `useDynamicColor`
 *  is `false`.
 *  - `onChangeThemeBrand`: a do nothing lambda.
 *  - `onChangeDynamicColorPreference`: a do nothing lambda.
 *  - `onChangeDarkThemeConfig`: a do nothing lambda.
 */
@Preview
@Composable
private fun PreviewSettingsDialog() {
    NiaTheme {
        SettingsDialog(
            onDismiss = {},
            settingsUiState = Success(
                settings = UserEditableSettings(
                    brand = DEFAULT,
                    darkThemeConfig = FOLLOW_SYSTEM,
                    useDynamicColor = false,
                ),
            ),
            onChangeThemeBrand = {},
            onChangeDynamicColorPreference = {},
            onChangeDarkThemeConfig = {},
        )
    }
}

/**
 * This is a preview of the [SettingsDialog] composable when the [SettingsUiState] is [Loading].
 * It is displayed wrapped in our [NiaTheme] custom Material Theme.
 * The arguments to the [SettingsDialog] are:
 *  - `onDismiss`: a do nothing lambda.
 *  - `settingsUiState`: a [Loading] object.
 *  - `onChangeThemeBrand`: a do nothing lambda.
 *  - `onChangeDynamicColorPreference`: a do nothing lambda.
 *  - `onChangeDarkThemeConfig`: a do nothing lambda.
 */
@Preview
@Composable
private fun PreviewSettingsDialogLoading() {
    NiaTheme {
        SettingsDialog(
            onDismiss = {},
            settingsUiState = Loading,
            onChangeThemeBrand = {},
            onChangeDynamicColorPreference = {},
            onChangeDarkThemeConfig = {},
        )
    }
}

/**
 * The URL of the privacy policy for the app.
 */
private const val PRIVACY_POLICY_URL = "https://policies.google.com/privacy"

/**
 * The URL of the brand guidelines for the app.
 */
private const val BRAND_GUIDELINES_URL =
    "https://developer.android.com/distribute/marketing-tools/brand-guidelines"

/**
 * The URL of the feedback form for the app.
 */
private const val FEEDBACK_URL = "https://goo.gle/nia-app-feedback"
