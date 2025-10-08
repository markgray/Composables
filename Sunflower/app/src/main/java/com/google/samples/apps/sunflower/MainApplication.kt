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

package com.google.samples.apps.sunflower

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp

/**
 * Overrides the default [Application] and provides dependency injection with Hilt.
 * Also, provides a custom [Configuration] for [WorkManager] to customize logging behavior.
 *
 * The @[HiltAndroidApp] annotation triggers Hilt's code generation, including a base class for your
 * application that can use dependency injection that will be named "Hilt_MainApplication.java".
 * The application container is the parent container for the app, which means that other containers
 * can access the dependencies that it provides. All modules and entry points that should be
 * installed in the component by Dagger need to be transitive compilation dependencies of this
 * application. (i.e. they need to be dependencies of the dependencies of this application.
 */
@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {
    /**
     * Create a custom configuration for WorkManager with a different logging level for debug builds.
     * Our `get` constructs a [Configuration.Builder], the calls its method
     * [Configuration.Builder.setMinimumLoggingLevel] to set its logging level to [Log.DEBUG]
     * for debug builds and [Log.ERROR] for release builds.
     *
     * @return A [Configuration] to be used to initialize [WorkManager]
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
            .build()
}
