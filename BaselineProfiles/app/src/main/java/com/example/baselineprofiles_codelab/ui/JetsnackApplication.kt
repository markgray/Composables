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

package com.example.baselineprofiles_codelab.ui

import android.app.Application
import androidx.compose.ui.util.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * Custom [Application] class for the Jetsnack app.
 * This class demonstrates how to include startup tracing in the application's initialization.
 */
class JetsnackApplication : Application() {

    /**
     * Called when the application is starting, before any activity, service, or receiver
     * objects have been created.
     * This method is used to perform application-level initialization, such as calling
     * the superclass's `onCreate` and initializing the custom library with tracing.
     */
    override fun onCreate() {
        super.onCreate()

        initializeLibrary()
    }

    /**
     * Initializes a custom library for the application.
     * This function includes startup tracing using `trace` to mark the initialization process.
     * It simulates a blocking initialization task using `runBlocking` and `delay`.
     */
    private fun initializeLibrary() {
        trace("Custom library init") {
            runBlocking {
                delay(50)
            }
        }
    }
}
