/*
 * Copyright 2020 Google LLC
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

package com.google.samples.apps.sunflower.di

import com.google.samples.apps.sunflower.api.UnsplashService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger module for providing network-related dependencies. The @[InstallIn] annotation tells Hilt
 * to install this module in the [SingletonComponent], meaning the provided instances will be
 * singletons and live as long as the application. The @[Module] annotation is used to mark this
 * class as a Dagger module, a class that contributes to the object graph.
 */
@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    /**
     * Creates a singleton instance of the [UnsplashService].
     *
     * This function is annotated with @[Singleton] to ensure that Dagger Hilt creates and provides
     * only one instance of the database throughout the application's lifecycle.
     * Dagger Hilt will inject this dependency wherever a [UnsplashService] is required.
     * The @[Provides] annotation tells Hilt that this method is how to create an instance
     * of [UnsplashService].
     *
     * @return A singleton instance of [UnsplashService].
     */
    @Singleton
    @Provides
    fun provideUnsplashService(): UnsplashService {
        return UnsplashService.create()
    }
}
