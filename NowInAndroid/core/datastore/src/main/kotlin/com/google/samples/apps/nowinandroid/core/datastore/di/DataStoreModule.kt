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

package com.google.samples.apps.nowinandroid.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.google.samples.apps.nowinandroid.core.datastore.IntToStringIdsMigration
import com.google.samples.apps.nowinandroid.core.datastore.UserPreferences
import com.google.samples.apps.nowinandroid.core.datastore.UserPreferencesSerializer
import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.IO
import com.google.samples.apps.nowinandroid.core.network.di.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

/**
 * Hilt module called [DataStoreModule] that provides a [DataStore] of [UserPreferences] instance
 * for managing user preferences in our application. The meaning of the HILT annotations are:
 *  - [Module]: This annotation from Hilt indicates that this object is a Hilt module. Modules are
 *  used to provide instances of classes that cannot be constructor-injected (e.g., interfaces,
 *  classes from external libraries, or classes that require a builder pattern).
 *  - [InstallIn] ([SingletonComponent]::class): This Hilt annotation specifies that the bindings
 *  defined in this module will be available in the [SingletonComponent]. This means that any
 *  dependency provided by this module will have a singleton scope, and the same instance will be
 *  provided throughout the application's lifecycle.
 *  - object [DataStoreModule]: This declares [DataStoreModule] as a Kotlin object. Using object
 *  makes it a singleton by default, which is a common practice for Hilt modules.
 *  - [Provides]: This Hilt annotation marks the [providesUserPreferencesDataStore] function as a
 *  provider method. Hilt will use this method to create and provide instances of [DataStore] of
 *  [UserPreferences].
 *  - [Singleton]: This annotation, when used with @Provides, ensures that Hilt will only create a
 *  single instance of [DataStore] of [UserPreferences] and reuse that same instance whenever it's
 *  requested as a dependency.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * Provides a singleton instance of [[DataStore]<[UserPreferences]>].
     *
     * This function is responsible for creating and configuring the [DataStore] for user
     * preferences. It uses a custom serializer [UserPreferencesSerializer] to handle the
     * serialization and deserialization of [UserPreferences] objects. The [DataStore] is created
     * with a scope that combines the application's main [CoroutineScope] with an IO dispatcher,
     * ensuring that DataStore operations are performed off the main thread. It also includes a
     * migration path [IntToStringIdsMigration] to handle any necessary data migrations between
     * different versions of the user preferences data structure. The [DataStore] file is named
     * "user_preferences.pb".
     *
     * @param context The application context, used to get the path for the [DataStore] file.
     * Injected by Hilt.
     * @param ioDispatcher A [CoroutineDispatcher] for IO-bound tasks, ensuring [DataStore]
     * operations do not block the main thread. Injected by Hilt.
     * @param scope The application-level [CoroutineScope]. Injected by Hilt.
     * @param userPreferencesSerializer The serializer for [UserPreferences]. Injected by Hilt.
     * @return A singleton instance of [DataStore]<[UserPreferences]>.
     */
    @Provides
    @Singleton
    internal fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(niaDispatcher = IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer,
    ): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope(context = scope.coroutineContext + ioDispatcher),
            migrations = listOf(
                IntToStringIdsMigration,
            ),
        ) {
            context.dataStoreFile(fileName = "user_preferences.pb")
        }
}
