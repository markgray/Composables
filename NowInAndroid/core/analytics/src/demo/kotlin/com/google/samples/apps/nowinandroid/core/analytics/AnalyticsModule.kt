/*
 * Copyright 2023 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.analytics

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module that provides implementations for [AnalyticsHelper].
 *
 * It is currently configured to provide a [StubAnalyticsHelper] which only logs analytics events.
 * This is suitable for builds where analytics are not required to be sent to a real analytics
 * service.
 *
 * To enable a real analytics implementation, you would:
 *  1. Create a concrete implementation of [AnalyticsHelper].
 *  2. Update the `@Binds` method in this module to provide your concrete implementation.
 *
 * For example:
 * ```kotlin
 * @Binds
 * abstract fun bindsAnalyticsHelper(
 *     analyticsHelperImpl: FirebaseAnalyticsHelper // Or your custom implementation
 * ): AnalyticsHelper
 * ```
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalyticsModule {
    /**
     * Binds the [StubAnalyticsHelper] implementation to the [AnalyticsHelper] interface.
     *
     * This method is used by Hilt to inject the appropriate analytics helper implementation.
     * Currently, it provides a [StubAnalyticsHelper], which means analytics events will only be
     * logged, not sent to any real analytics service.
     *
     * @param analyticsHelperImpl The [StubAnalyticsHelper] instance to be provided.
     * @return An instance of [AnalyticsHelper].
     */
    @Suppress("unused")
    @Binds
    abstract fun bindsAnalyticsHelper(analyticsHelperImpl: StubAnalyticsHelper): AnalyticsHelper
}
