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

package com.google.samples.apps.nowinandroid.core.sync.test

import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Test implementation of [SyncManager] which never syncs.
 */
internal class NeverSyncingSyncManager @Inject constructor() : SyncManager {
    /**
     * [Flow] of [Boolean] representing the current sync status. We return
     * a [Flow] that always emits false, as this [SyncManager] never syncs.
     */
    override val isSyncing: Flow<Boolean> = flowOf(value = false)

    /**
     * Requests that a sync be performed. This function does nothing as this
     * [SyncManager] never syncs.
     */
    override fun requestSync() = Unit
}
