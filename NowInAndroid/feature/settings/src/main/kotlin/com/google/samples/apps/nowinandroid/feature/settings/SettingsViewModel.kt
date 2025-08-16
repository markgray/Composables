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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.feature.settings.SettingsUiState.Loading
import com.google.samples.apps.nowinandroid.feature.settings.SettingsUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * [SettingsViewModel] is the view model for the settings screen.
 * It is responsible for exposing the [UserData] to the UI and for updating the [UserData]
 * when the user changes the settings.
 *
 * @param userDataRepository The [UserDataRepository] that provides the [UserData], injected by Hilt.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    /**
     * Represents the state of the settings screen. It can be either [Loading] or [Success].
     * The [Success] state contains the [UserEditableSettings] that can be modified by the user.
     *
     * We retrieve the [Flow] of [UserData] from the [UserDataRepository.userData] property of our
     * [UserDataRepository] property [userDataRepository]. Then we use its [Flow.map] method with
     * its `transform` lambda capturing the [UserData] in variable `userData` and emitting a
     * [SettingsUiState.Success] with its `settings` property set to a [UserEditableSettings]
     * whose `brand` property is set to the [UserData.themeBrand] of `userData`, whose
     * `useDynamicColor` property is set to the [UserData.useDynamicColor] of `userData`, and whose
     * `darkThemeConfig` property is set to the [UserData.darkThemeConfig] of `userData`. This
     * [Flow] of [SettingsUiState.Success] is then converted to a [StateFlow] of [SettingsUiState]
     * using the [Flow.stateIn] method with its `scope` [viewModelScope], its `started`
     * [WhileSubscribed] with a `stopTimeoutMillis` of 5 seconds, and an `initialValue` of [Loading].
     */
    val settingsUiState: StateFlow<SettingsUiState> =
        userDataRepository.userData
            .map { userData: UserData ->
                Success(
                    settings = UserEditableSettings(
                        brand = userData.themeBrand,
                        useDynamicColor = userData.useDynamicColor,
                        darkThemeConfig = userData.darkThemeConfig,
                    ),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(stopTimeoutMillis = 5.seconds.inWholeMilliseconds),
                initialValue = Loading,
            )

    /**
     * Updates the theme brand. We use the [viewModelScope] to launch a coroutine that calls the
     * [UserDataRepository.setThemeBrand] method of our [UserDataRepository] property
     * [userDataRepository] with its `themeBrand` argument our [ThemeBrand] parameter [themeBrand].
     *
     * @param themeBrand The new theme brand to set.
     */
    fun updateThemeBrand(themeBrand: ThemeBrand) {
        viewModelScope.launch {
            userDataRepository.setThemeBrand(themeBrand = themeBrand)
        }
    }

    /**
     * Updates the dark theme config. We use the [viewModelScope] to launch a coroutine that calls
     * the [UserDataRepository.setDarkThemeConfig] method of our [UserDataRepository] property
     * [userDataRepository] with its `darkThemeConfig` argument our [DarkThemeConfig] parameter
     * [darkThemeConfig].
     *
     * @param darkThemeConfig The new dark theme config to set.
     */
    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userDataRepository.setDarkThemeConfig(darkThemeConfig = darkThemeConfig)
        }
    }

    /**
     * Updates the dynamic color preference. We use the [viewModelScope] to launch a coroutine that
     * calls the [UserDataRepository.setDynamicColorPreference] method of our [UserDataRepository]
     * property [userDataRepository] with its `useDynamicColor` argument our [Boolean] parameter
     * [useDynamicColor].
     *
     * @param useDynamicColor Whether to use dynamic color.
     */
    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {
            userDataRepository.setDynamicColorPreference(useDynamicColor = useDynamicColor)
        }
    }
}

/**
 * Represents the settings which the user can edit within the app.
 *
 * @property brand The [ThemeBrand] of the app to set.
 * @property useDynamicColor Whether to use dynamic color or not.
 * @property darkThemeConfig The [DarkThemeConfig] of the app to set.
 */
data class UserEditableSettings(
    val brand: ThemeBrand,
    val useDynamicColor: Boolean,
    val darkThemeConfig: DarkThemeConfig,
)

/**
 * Represents the settings screen UI state.
 *
 * It can be either [Loading] or [Success].
 * The [Success] state contains the [UserEditableSettings] that can be modified by the user.
 */
sealed interface SettingsUiState {
    /**
     * Represents the loading state of the settings screen.
     */
    data object Loading : SettingsUiState

    /**
     * Represents the success state of the settings screen, and contains the [UserEditableSettings]
     * that can be modified by the user.
     *
     * @property settings The [UserEditableSettings] that can be modified by the user.
     */
    data class Success(val settings: UserEditableSettings) : SettingsUiState
}
