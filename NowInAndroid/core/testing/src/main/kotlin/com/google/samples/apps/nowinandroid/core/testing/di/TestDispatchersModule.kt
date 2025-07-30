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

package com.google.samples.apps.nowinandroid.core.testing.di

import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.Default
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.IO
import com.google.samples.apps.nowinandroid.core.network.di.DispatchersModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher

/**
 * Hilt module that provides test dispatchers for testing.
 *
 * This module replaces the [DispatchersModule] to inject [TestDispatcher] instances
 * for [CoroutineDispatcher] dependencies, specifically for [IO] and [Default] dispatchers.
 * This allows for precise control over coroutine execution in tests, facilitating
 * predictable and reproducible test results.
 *
 * The [TestDispatcher] provided is a single instance, ensuring that all coroutines
 * launched with either the IO or Default dispatcher in tests will run on the same
 * underlying test dispatcher. This simplifies testing of concurrent operations.
 *
 * The meaning of the annotations are:
 *  - @[Module]: This annotation tells Hilt that this object is a module, which means it provides
 *  dependencies to other parts of your application (or in this case, your test environment).
 *  - @[TestInstallIn] (...): This is a Hilt annotation crucial for testing.
 *      - `components` = [SingletonComponent]::class: This specifies that the bindings defined in
 *      this module should be installed in the SingletonComponent. This means that the provided
 *      TestDispatcher will be available as a singleton throughout your test application.
 *      - `replaces` = [DispatchersModule]::class: This is the key part for testing. It tells Hilt
 *      to replace the bindings defined in your production DispatchersModule with the bindings in
 *      this TestDispatchersModule when running tests. This ensures that your tests use the
 *      controllable TestDispatcher instead of the real Dispatchers.IO and Dispatchers.Default.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatchersModule::class],
)
internal object TestDispatchersModule {
    /**
     * Provides a [CoroutineDispatcher] for I/O operations, using a [TestDispatcher] for testing.
     * This allows tests to control the execution of coroutines launched on this dispatcher.
     *
     * @param testDispatcher The [TestDispatcher] to be used as the I/O dispatcher.
     * @return A [CoroutineDispatcher] that uses the provided [TestDispatcher].
     */
    @Provides
    @Dispatcher(niaDispatcher = IO)
    fun providesIODispatcher(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher

    /**
     * Provides a [CoroutineDispatcher] for default operations, using a [TestDispatcher] for testing.
     * This allows tests to control the execution of coroutines launched on this dispatcher.
     *
     * @param testDispatcher The [TestDispatcher] to be used as the default dispatcher.
     * @return A [CoroutineDispatcher] that uses the provided [TestDispatcher].
     */
    @Provides
    @Dispatcher(niaDispatcher = Default)
    fun providesDefaultDispatcher(
        testDispatcher: TestDispatcher,
    ): CoroutineDispatcher = testDispatcher
}
