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

package com.google.samples.apps.nowinandroid.core.testing.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

/**
 * Hilt module that provides a [TestDispatcher] to tests. The meaning of the annotations are:
 *  - @[Module]: This annotation tells Hilt that this object is a Hilt module. Modules are used to
 *  provide instances of classes that cannot be constructor-injected (e.g., interfaces, classes from
 *  external libraries, or classes that need to be built with a builder).
 *  - @[InstallIn] ([SingletonComponent]::class): This annotation tells Hilt which component this
 *  module should be installed in. [SingletonComponent] means that the bindings provided by this
 *  module will be available application-wide and will have a singleton scope (only one instance
 *  will be created and shared). For testing, this often means the dispatcher will be available
 *  throughout your test environment.
 *  - @[Provides]: This annotation marks a function within a module that provides an instance
 *  of a type.
 *  - @[Singleton]: This annotation, when used with @[Provides], indicates that Hilt should provide
 *  the same instance every time it's requested within the scope of the component it's installed in
 *  (here, SingletonComponent). So, there will be only one UnconfinedTestDispatcher instance.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object TestDispatcherModule {
    @Provides
    @Singleton
    fun providesTestDispatcher(): TestDispatcher = UnconfinedTestDispatcher()
}
