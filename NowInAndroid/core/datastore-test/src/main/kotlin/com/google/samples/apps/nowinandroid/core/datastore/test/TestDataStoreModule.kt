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

package com.google.samples.apps.nowinandroid.core.datastore.test

import androidx.datastore.core.DataStore
import com.google.samples.apps.nowinandroid.core.datastore.UserPreferences
import com.google.samples.apps.nowinandroid.core.datastore.UserPreferencesSerializer
import com.google.samples.apps.nowinandroid.core.datastore.di.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * DataStore module that provides a [DataStore] of [UserPreferences] for tests.
 *
 * The meaning of the Hilt annotations are:
 *  - @[Module]: This annotation marks [TestDataStoreModule] as a Hilt module. Hilt modules are
 *  containers for providing dependencies.
 *  - @[TestInstallIn]: This annotation tells Hilt which container to install this module
 *  - @[TestInstallIn.components]: This indicates that the bindings provided by this module will be
 *  installed in the [SingletonComponent]. This means that any dependency provided by this module
 *  will have a singleton scope within the test application
 *  - @[TestInstallIn.replaces]: This is a crucial part for testing. It means that
 *  [TestDataStoreModule] will replace the bindings defined in another Hilt module named
 *  [DataStoreModule] when running tests. This allows you to swap out the real implementation
 *  of your data storage (likely using files on disk) with a test-friendly in-memory version.
 *  - @[Provides]: This annotation marks the [providesUserPreferencesDataStore] function as a
 *  provider method. Hilt will use this method to create instances of [DataStore]<[UserPreferences]>.
 *  - @[Singleton]: This annotation, when applied to a @Provides method, indicates that Hilt should
 *  create only one instance of the provided dependency ([DataStore]<[UserPreferences]>) and reuse
 *  that same instance throughout the application's lifecycle (or, in this test context, the test
 *  application's lifecycle).
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class],
)
internal object TestDataStoreModule {
    /**
     * Since this is a test module, this provides an [InMemoryDataStore] for
     * [UserPreferences] instead of a file based one.
     *
     * @param serializer the [UserPreferencesSerializer] to use for serialization.
     * @return an [InMemoryDataStore] for [UserPreferences].
     */
    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        serializer: UserPreferencesSerializer,
    ): DataStore<UserPreferences> = InMemoryDataStore(initialValue = serializer.defaultValue)
}
