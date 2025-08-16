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

import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig.DARK
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand.ANDROID
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import com.google.samples.apps.nowinandroid.feature.settings.SettingsUiState.Loading
import com.google.samples.apps.nowinandroid.feature.settings.SettingsUiState.Success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [SettingsViewModel].
 */
class SettingsViewModelTest {

    /**
     * A rule that swaps the main dispatcher for a test dispatcher.
     * This is useful for testing coroutines that use the main dispatcher.
     */
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    /**
     * The test [UserDataRepository] user data repository for managing user preferences.
     */
    private val userDataRepository = TestUserDataRepository()

    /**
     * The ViewModel under test. It is initialized in our [setup] method.
     */
    private lateinit var viewModel: SettingsViewModel

    /**
     * Sets up the test environment before each test.
     * Initializes the [SettingsViewModel] with the test [UserDataRepository].
     */
    @Before
    fun setup() {
        viewModel = SettingsViewModel(userDataRepository = userDataRepository)
    }

    /**
     * Test that the initial state of the ViewModel is [Loading].
     */
    @Test
    fun stateIsInitiallyLoading(): TestResult = runTest {
        assertEquals(expected = Loading, actual = viewModel.settingsUiState.value)
    }

    /**
     * Test that the state of the ViewModel is [Success] after user data is loaded.
     * It also verifies that the loaded user data matches the expected values.
     *
     * We launch a coroutine in the [TestScope.backgroundScope] using it [CoroutineScope.launch]
     * in which we call the [StateFlow.collect] method of the [SettingsViewModel.settingsUiState]
     * property of our [SettingsViewModel] property [viewModel] to collect the flow but ignore all
     * emitted values. This is done to ensure that the initial state of the flow is [Loading].
     * Then we call the [UserDataRepository.setThemeBrand] method of our [UserDataRepository]
     * property [userDataRepository] with its `themeBrand` argument set to [ANDROID]. This sets
     * the theme brand to [ANDROID]. Then we call the [UserDataRepository.setDarkThemeConfig]
     * method of our [UserDataRepository] property [userDataRepository] with its `darkThemeConfig`
     * argument set to [DARK]. This sets the dark theme config to [DARK]. Then we assert that the
     * state of the flow is [Success] and that the `settings` property of the `Success` state
     * matches the expected values. The `settings` property of the `Success` state contains a
     * [UserEditableSettings] object with its `brand` property set to [ANDROID], its `useDynamicColor`
     * property set to `false`, and its `darkThemeConfig` property set to [DARK].
     */
    @Test
    fun stateIsSuccessAfterUserDataLoaded(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.settingsUiState.collect()
        }

        userDataRepository.setThemeBrand(themeBrand = ANDROID)
        userDataRepository.setDarkThemeConfig(darkThemeConfig = DARK)

        assertEquals(
            expected = Success(
                settings = UserEditableSettings(
                    brand = ANDROID,
                    darkThemeConfig = DARK,
                    useDynamicColor = false,
                ),
            ),
            actual = viewModel.settingsUiState.value,
        )
    }
}
