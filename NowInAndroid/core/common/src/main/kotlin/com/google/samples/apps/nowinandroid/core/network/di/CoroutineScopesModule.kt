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

package com.google.samples.apps.nowinandroid.core.network.di

import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.Default
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier for a [CoroutineScope] that is tied to the application's lifecycle. This scope is
 * suitable for long-running tasks that should not be cancelled when a UI component (like an
 * Activity or Fragment) is destroyed.
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

/**
 * Hilt module that provides [CoroutineScope]s.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineScopesModule {
    /**
     * Provides a [CoroutineScope] that is tied to the application's lifecycle.
     * This scope is suitable for long-running tasks that should not be cancelled
     * when a UI component (like an Activity or Fragment) is destroyed.
     *
     * The scope uses a [SupervisorJob] which means that if one child coroutine fails,
     * it does not cancel the other children or the scope itself.
     *
     * @param dispatcher The [CoroutineDispatcher] to be used by the scope. This is typically
     * a dispatcher that is optimized for CPU-bound tasks (e.g., [Dispatchers.Default]).
     * @return A [CoroutineScope] that is tied to the application's lifecycle.
     */
    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(
        @Dispatcher(Default) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(context = SupervisorJob() + dispatcher)
}
