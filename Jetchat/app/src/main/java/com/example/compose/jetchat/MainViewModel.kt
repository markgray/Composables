/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetchat

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.compose.jetchat.components.JetchatDrawer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * The [ViewModel] that is used to communicate between screens.
 */
class MainViewModel : ViewModel() {

    /**
     * The [MutableStateFlow] wrapped [Boolean] field that causes the [DrawerState.open] method of
     * the [DrawerState] that is used by the [JetchatDrawer] used by [NavActivity]. It is private
     * to prevent other classes from modifying it, our method [openDrawer] sets it to `true` and our
     * method [resetOpenDrawerAction] sets it to `false`. Public read-only access is provided by
     * our [StateFlow] wrapped [Boolean] field [drawerShouldBeOpened].
     */
    private val _drawerShouldBeOpened = MutableStateFlow(value = false)

    /**
     * Public read-only access to our [MutableStateFlow] wrapped [Boolean] field [_drawerShouldBeOpened].
     * In the `onCreate` override of [NavActivity] the [StateFlow.collectAsStateWithLifecycle] method
     * is used to initialize its [State] wrapped [Boolean] variable `val drawerOpen` that causes it
     * to call a [LaunchedEffect] when it transitions to `true` that calls the [DrawerState.open]
     * method of the [DrawerState] that is used by the [JetchatDrawer] thereby causing the drawer to
     * open.
     */
    val drawerShouldBeOpened: StateFlow<Boolean> = _drawerShouldBeOpened.asStateFlow()

    /**
     * Sets the value of our [MutableStateFlow] wrapped [Boolean] field [_drawerShouldBeOpened] to
     * `true` which will trigger a [LaunchedEffect] that calls the [DrawerState.open] method of the
     * [DrawerState] that is used by the [JetchatDrawer] composed into the UI by [NavActivity] thereby
     * causing the drawer to open.
     */
    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    /**
     * Sets the value of our [MutableStateFlow] wrapped [Boolean] field [_drawerShouldBeOpened] to
     * `false`.
     */
    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }
}
