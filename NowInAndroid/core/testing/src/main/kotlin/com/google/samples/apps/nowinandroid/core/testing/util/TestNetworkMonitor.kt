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

package com.google.samples.apps.nowinandroid.core.testing.util

import com.google.samples.apps.nowinandroid.core.data.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * A test-only implementation of [NetworkMonitor] that allows setting the network connectivity
 * from tests.
 */
class TestNetworkMonitor : NetworkMonitor {

    /**
     * A mutable state flow that emits the connectivity status.
     * By default, the connectivity status is set to `true` (connected).
     */
    private val connectivityFlow: MutableStateFlow<Boolean> = MutableStateFlow(value = true)

    /**
     * A public read-only property that exposes the [MutableStateFlow] of [Boolean] property
     * [connectivityFlow] as a [Flow] of [Boolean].
     */
    override val isOnline: Flow<Boolean> = connectivityFlow

    /**
     * Sets the connectivity status to the given [isConnected] value.
     * This function is intended for use in tests to simulate different network conditions.
     *
     * @param isConnected `true` if the network should be considered connected, `false` otherwise.
     */
    fun setConnected(isConnected: Boolean) {
        connectivityFlow.value = isConnected
    }
}
