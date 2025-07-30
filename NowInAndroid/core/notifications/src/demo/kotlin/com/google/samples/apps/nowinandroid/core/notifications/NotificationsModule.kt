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

@file:Suppress("unused")

package com.google.samples.apps.nowinandroid.core.notifications

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module that provides a binding for [Notifier].
 *
 * This module is installed in the [SingletonComponent], meaning that the [Notifier]
 * instance provided by this module will be a singleton and will be available
 * throughout the application's lifecycle. The meaning of the annotations are:
 *  - @[Module]: This is a Dagger module that provides dependencies.
 *  - @[InstallIn]: This annotation tells Dagger to install the module in the
 *  [SingletonComponent], which is the top-level Dagger component that
 *  contains all the dependencies provided by the application.
 *  - @[SingletonComponent]: This is a Dagger component that is installed
 *  in the application's singleton scope.
 *  - @[Binds]: This annotation tells Dagger to bind the [NoOpNotifier]
 *  implementation to the [Notifier] interface.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationsModule {
    @Binds
    abstract fun bindNotifier(
        notifier: NoOpNotifier,
    ): Notifier
}
