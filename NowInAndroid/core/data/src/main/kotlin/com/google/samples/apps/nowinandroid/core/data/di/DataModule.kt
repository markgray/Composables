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

package com.google.samples.apps.nowinandroid.core.data.di

import com.google.samples.apps.nowinandroid.core.data.repository.DefaultRecentSearchRepository
import com.google.samples.apps.nowinandroid.core.data.repository.DefaultSearchContentsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.NewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.OfflineFirstNewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.OfflineFirstTopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.OfflineFirstUserDataRepository
import com.google.samples.apps.nowinandroid.core.data.repository.RecentSearchRepository
import com.google.samples.apps.nowinandroid.core.data.repository.SearchContentsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.util.ConnectivityManagerNetworkMonitor
import com.google.samples.apps.nowinandroid.core.data.util.NetworkMonitor
import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneBroadcastMonitor
import com.google.samples.apps.nowinandroid.core.data.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module that provides implementations for DI for data module.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    /**
     * Binds [OfflineFirstTopicsRepository] to [TopicsRepository] for use in other modules.
     * This allows other modules to depend on the [TopicsRepository] interface
     * without needing to know about the concrete implementation.
     */
    @Binds
    internal abstract fun bindsTopicRepository(
        topicsRepository: OfflineFirstTopicsRepository,
    ): TopicsRepository

    /**
     * Binds [OfflineFirstNewsRepository] to [NewsRepository] for use in other modules.
     * This allows other modules to depend on the [NewsRepository] interface
     * without needing to know about the concrete implementation.
     */
    @Binds
    internal abstract fun bindsNewsResourceRepository(
        newsRepository: OfflineFirstNewsRepository,
    ): NewsRepository

    /**
     * Binds [OfflineFirstUserDataRepository] to [UserDataRepository] for use in other modules.
     * This allows other modules to depend on the [UserDataRepository] interface
     * without needing to know about the concrete implementation.
     */
    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    /**
     * Binds [DefaultRecentSearchRepository] to [RecentSearchRepository] for use in other modules.
     * This allows other modules to depend on the [RecentSearchRepository] interface
     * without needing to know about the concrete implementation.
     */
    @Binds
    internal abstract fun bindsRecentSearchRepository(
        recentSearchRepository: DefaultRecentSearchRepository,
    ): RecentSearchRepository

    /**
     * Binds [DefaultSearchContentsRepository] to [SearchContentsRepository] for use in other modules.
     * This allows other modules to depend on the [SearchContentsRepository] interface
     * without needing to know about the concrete implementation.
     */
    @Binds
    internal abstract fun bindsSearchContentsRepository(
        searchContentsRepository: DefaultSearchContentsRepository,
    ): SearchContentsRepository

    /**
     * Binds [ConnectivityManagerNetworkMonitor] to [NetworkMonitor] for use in other modules.
     * This allows other modules to depend on the [NetworkMonitor] interface
     * without needing to know about the concrete implementation.
     */
    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    /**
     * Binds [TimeZoneBroadcastMonitor] to [TimeZoneMonitor] for use in other modules.
     * This allows other modules to depend on the [TimeZoneMonitor] interface
     * without needing to know about the concrete implementation.
     */
    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
}
