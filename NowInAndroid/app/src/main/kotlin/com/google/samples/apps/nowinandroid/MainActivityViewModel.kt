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

package com.google.samples.apps.nowinandroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.MainActivityUiState.Loading
import com.google.samples.apps.nowinandroid.MainActivityUiState.Success
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the main activity.
 *
 * @param userDataRepository The repository for user data injected by Hilt.
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
) : ViewModel() {
    /**
     * The main activity UI state. We use the [Flow.map] method of [Flow] of [UserData] property
     * [UserDataRepository.userData] of our [UserDataRepository] property [userDataRepository] to
     * loop through its entries capturing each [UserData] in variable `data` and emitting the
     * [MainActivityUiState] that [Success] returns for the [UserData] in variable `data` and
     * convert the [Flow] of [MainActivityUiState] to a [StateFlow] of [MainActivityUiState] using
     * the [Flow.stateIn] method with its `scope` argument the [viewModelScope], its `initialValue`
     * argument [Loading], and its `started` argument [SharingStarted.WhileSubscribed] with its
     * `stopTimeoutMillis` argument `5,000` and assign that our to [StateFlow] of [MainActivityUiState]
     * property [uiState].
     */
    val uiState: StateFlow<MainActivityUiState> =
        userDataRepository.userData.map { data: UserData ->
            Success(userData = data)
        }.stateIn(
            scope = viewModelScope,
            initialValue = Loading,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        )
}

/**
 * Represents the UI state for the main activity.
 * This sealed interface defines the possible states: [Loading] and [Success].
 * It also provides utility methods to determine theme-related settings based
 * on the current state and user data.
 */
sealed interface MainActivityUiState {
    /**
     * Loading state for the main activity, it is the initial state of [StateFlow] of
     * [MainActivityUiState] property [MainActivityViewModel.uiState].
     */
    data object Loading : MainActivityUiState

    /**
     * Success state for the main activity, it is the final state of [StateFlow] of
     * [MainActivityUiState] property [MainActivityViewModel.uiState].
     *
     * @property userData The [UserData] associated with the success state.
     */
    data class Success(val userData: UserData) : MainActivityUiState {
        /**
         * Dynamic theming should be disabled..
         */
        override val shouldDisableDynamicTheming: Boolean = !userData.useDynamicColor

        /**
         * Use [ThemeBrand.ANDROID] when `true` and [ThemeBrand.DEFAULT] when `false`.
         */
        override val shouldUseAndroidTheme: Boolean = when (userData.themeBrand) {
            ThemeBrand.DEFAULT -> false
            ThemeBrand.ANDROID -> true
        }

        /**
         * Returns `true` if dark theme should be used.
         *
         * @param isSystemDarkTheme Whether the system is in dark theme mode.
         */
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean =
            when (userData.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }
    }

    /**
     * Returns `true` if the state wasn't loaded yet and it should keep showing the splash screen.
     */
    fun shouldKeepSplashScreen(): Boolean = this is Loading

    /**
     * Returns `true` if the dynamic color is disabled.
     */
    val shouldDisableDynamicTheming: Boolean get() = true

    /**
     * Returns `true` if the Android theme should be used.
     */
    val shouldUseAndroidTheme: Boolean get() = false

    /**
     * Returns `true` if dark theme should be used.
     */
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean = isSystemDarkTheme
}
