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

@file:Suppress("unused")

package com.google.samples.apps.nowinandroid.sync.di

import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import com.google.samples.apps.nowinandroid.sync.status.StubSyncSubscriber
import com.google.samples.apps.nowinandroid.sync.status.SyncSubscriber
import com.google.samples.apps.nowinandroid.sync.status.WorkManagerSyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Dagger module that provides implementations for sync scheduling and sync status.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    /**
     * Binds [WorkManagerSyncManager] to [SyncManager] as the [SyncManager] implementation.
     *
     * @param syncStatusMonitor the [WorkManagerSyncManager] to return as the [SyncManager].
     * @return the [SyncManager] to use.
     */
    @Binds
    internal abstract fun bindsSyncStatusMonitor(
        syncStatusMonitor: WorkManagerSyncManager,
    ): SyncManager

    /**
     * Binds [StubSyncSubscriber] to [SyncSubscriber] as the [SyncSubscriber] implementation.
     * This binding is used for the no-op version of the app, where sync functionality is
     * not available.
     *
     * @param syncSubscriber the [StubSyncSubscriber] to return as the [SyncSubscriber].
     * @return the [SyncSubscriber] to use.
     */
    @Binds
    internal abstract fun bindsSyncSubscriber(
        syncSubscriber: StubSyncSubscriber,
    ): SyncSubscriber
}
