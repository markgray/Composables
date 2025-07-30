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

package com.google.samples.apps.nowinandroid.core.network.di

import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.demo.DemoNiaNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module that provides a [NiaNetworkDataSource] implementation for the demo flavor.
 * This module is installed in the [SingletonComponent], meaning that the provided
 * [NiaNetworkDataSource] will be a singleton and available throughout the application.
 *  - @[Module]: This annotation marks the [FlavoredNetworkModule] object as a Hilt module.
 *  Modules are responsible for providing instances of classes that cannot be constructor
 *  injected (e.g., interfaces, classes from external libraries, or classes that require complex
 *  setup).
 *  - @[InstallIn] ([SingletonComponent]::class): This annotation tells Hilt that the bindings
 *  defined in this module should be available in the [SingletonComponent]. The [SingletonComponent]
 *  is a Hilt component that lives as long as the application itself. This means that any
 *  dependencies provided by this module will be singletons (only one instance will be created and
 *  shared throughout the app).
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredNetworkModule {

    /**
     * Binds [DemoNiaNetworkDataSource] to [NiaNetworkDataSource] for the demo flavor.
     * This function is used by Hilt to inject the correct implementation of [NiaNetworkDataSource]
     * when the app is built with the demo flavor. @[Binds] Annotates abstract methods of a Module
     * that delegate bindings to their [impl] parameter, a drop-in replacement for @[Provides]
     * methods that simply return an injected parameter.
     *
     * @param impl The implementation of [DemoNiaNetworkDataSource] to be used.
     */
    @Binds
    fun binds(impl: DemoNiaNetworkDataSource): NiaNetworkDataSource
}
