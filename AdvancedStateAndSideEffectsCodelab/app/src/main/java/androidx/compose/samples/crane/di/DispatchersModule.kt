/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.samples.crane.di

import androidx.compose.samples.crane.home.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/**
 * The `@Module` annotation annotates a class that contributes to the object graph, and the `@InstallIn`
 * annotation is required to specify which Hilt Component to install the module in, and in this case
 * the [DispatchersModule] is installed in the generate [SingletonComponent] (the Hilt component for
 * singleton bindings, the injector only instantiates it once).
 */
@Module
@InstallIn(SingletonComponent::class)
class DispatchersModule {

    /**
     * The `@Provides` annotation tells Hilt how to provide types that cannot be constructor injected.
     * The function body of a function that is annotated with `@Provides` will be executed every time
     * Hilt needs to provide an instance of that type that is annotated with the `@DefaultDispatcher`
     * qualifier. The return type of the `@Provides`-annotated function tells Hilt the binding type,
     * the type that the function provides instances of.
     */
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

/**
 * The `@Retention` meta-annotation determines whether an annotation is stored in binary output and
 * visible for reflection, and the [AnnotationRetention.BINARY] argument indicates that it should be
 * stored in binary output, but invisible for reflection. The `@Qualifier` annotation identifies this
 * as a qualifier annotation. A qualifier is an annotation used to identify a binding. When an
 * `@Inject` annotation for a [CoroutineDispatcher] dependency is annotated with [DefaultDispatcher]
 * the [DispatchersModule.provideDefaultDispatcher] method is used by Hilt to construct the
 * [CoroutineDispatcher] instance required. This annotation is used in the [MainViewModel] constructor
 * for its `defaultDispatcher` parameter, and since the constructor is annotated with `@Inject`, Hilt
 * will inject [Dispatchers.Default] for its value when it injects [MainViewModel].
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher
