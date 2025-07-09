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

package com.google.samples.apps.nowinandroid.core.data.test

import com.google.samples.apps.nowinandroid.core.data.di.DataModule
import com.google.samples.apps.nowinandroid.core.data.repository.NewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.RecentSearchRepository
import com.google.samples.apps.nowinandroid.core.data.repository.SearchContentsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.test.repository.FakeNewsRepository
import com.google.samples.apps.nowinandroid.core.data.test.repository.FakeRecentSearchRepository
import com.google.samples.apps.nowinandroid.core.data.test.repository.FakeSearchContentsRepository
import com.google.samples.apps.nowinandroid.core.data.test.repository.FakeTopicsRepository
import com.google.samples.apps.nowinandroid.core.data.test.repository.FakeUserDataRepository
import com.google.samples.apps.nowinandroid.core.data.util.NetworkMonitor
import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/**
 * DI module that provides bindings for fake data repositories.
 * This module is used in tests to replace the production [DataModule] with
 * fake implementations.
 *
 * It is annotated with [TestInstallIn] to have it replace one or more modules in tests.
 * The [TestInstallIn.components] argument is used to specify the component to use for
 * testing. The [SingletonComponent] is used here to ensure that the same
 * instance of the fake data repository is used throughout the tests.
 * The [TestInstallIn.replaces] argument is used to specify the module to replace, in
 * this case [DataModule].
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class],
)
internal interface TestDataModule {
    /**
     * Binds a [FakeTopicsRepository] to the [TopicsRepository] interface.
     * This allows injecting a fake implementation of the repository for testing purposes.
     * TODO: Continue here.
     */
    @Binds
    fun bindsTopicRepository(
        fakeTopicsRepository: FakeTopicsRepository,
    ): TopicsRepository

    @Binds
    fun bindsNewsResourceRepository(
        fakeNewsRepository: FakeNewsRepository,
    ): NewsRepository

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: FakeUserDataRepository,
    ): UserDataRepository

    @Binds
    fun bindsRecentSearchRepository(
        recentSearchRepository: FakeRecentSearchRepository,
    ): RecentSearchRepository

    @Binds
    fun bindsSearchContentsRepository(
        searchContentsRepository: FakeSearchContentsRepository,
    ): SearchContentsRepository

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: AlwaysOnlineNetworkMonitor,
    ): NetworkMonitor

    @Binds
    fun binds(impl: DefaultZoneIdTimeZoneMonitor): TimeZoneMonitor
}
