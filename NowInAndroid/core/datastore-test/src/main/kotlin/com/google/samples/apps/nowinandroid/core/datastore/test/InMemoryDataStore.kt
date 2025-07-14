/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.datastore.test

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

/**
 * A test implementation of [DataStore] that stores data in memory.
 *
 * @param initialValue The initial value to store.
 */
class InMemoryDataStore<T>(initialValue: T) : DataStore<T> {
    /**
     * A [MutableStateFlow] that emits the current value of the [T] data.
     */
    override val data: MutableStateFlow<T> = MutableStateFlow(initialValue)

    /**
     * Updates the data in the [DataStore] by applying the given [transform] function.
     *
     * @param transform The function to apply to the current data.
     * @return The updated data.
     */
    override suspend fun updateData(
        transform: suspend (it: T) -> T,
    ): T = data.updateAndGet { transform(it) }
}
