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

package com.google.samples.apps.nowinandroid.core.sync.test

import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import com.google.samples.apps.nowinandroid.sync.di.SyncModule
import com.google.samples.apps.nowinandroid.sync.status.StubSyncSubscriber
import com.google.samples.apps.nowinandroid.sync.status.SyncSubscriber
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/**
 * Hilt module that provides test replacements for [SyncModule]. The meaning of the annotations are:
 *  - @[Module]: Marks this class as a Dagger module.
 *  - @[TestInstallIn]: `components` Install in @[SingletonComponent], the Hilt component for
 *  singleton bindings, `replaces` Replace bindings of [SyncModule] with the ones declared in this
 *  module.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SyncModule::class],
)
internal interface TestSyncModule {
    /**
     * Binds [NeverSyncingSyncManager] to [SyncManager] for testing purposes.
     * This ensures that sync operations are never performed during tests,
     * providing a controlled environment.
     *
     * @param syncStatusMonitor The [NeverSyncingSyncManager] instance to be bound.
     */
    @Binds
    fun bindsSyncStatusMonitor(
        syncStatusMonitor: NeverSyncingSyncManager,
    ): SyncManager

    /**
     * Binds [StubSyncSubscriber] to [SyncSubscriber] for testing.
     * This allows tests to use a stub implementation of [SyncSubscriber]
     * which can be controlled for testing purposes.
     *
     * @param syncSubscriber The [StubSyncSubscriber] to be bound.
     * @return The bound [SyncSubscriber].
     */
    @Binds
    fun bindsSyncSubscriber(
        syncSubscriber: StubSyncSubscriber,
    ): SyncSubscriber
}
