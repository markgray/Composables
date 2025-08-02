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

package com.google.samples.apps.nowinandroid.core.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * A custom runner to set up the instrumented application class for tests.
 */
class NiaTestRunner : AndroidJUnitRunner() {
    /**
     * Overrides the default [AndroidJUnitRunner.newApplication] method to use [HiltTestApplication]
     * as the application class for tests.
     *
     * This ensures that Hilt is properly initialized for instrumented tests.
     *
     * @param cl The class loader to use.
     * @param name The name of the application class to instantiate. This is ignored and
     * [HiltTestApplication] is used instead.
     * @param context The context for the new application.
     * @return A new instance of [HiltTestApplication] that has been cast to [Application] (by our
     * super's super's super's super).
     */
    override fun newApplication(cl: ClassLoader, name: String, context: Context): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
