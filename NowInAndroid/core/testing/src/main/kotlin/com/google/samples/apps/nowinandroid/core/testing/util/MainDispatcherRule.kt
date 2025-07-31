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

package com.google.samples.apps.nowinandroid.core.testing.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit [TestRule] that sets the Main dispatcher to [testDispatcher]
 * for the duration of the test.
 *
 * @property testDispatcher The [TestDispatcher] to use for the test.
 */
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    /**
     * Overrides the starting method of the [TestWatcher] class.
     * This method is called before each test method is executed.
     * It sets the main dispatcher to the test dispatcher.
     *
     * @param description The description of the test method.
     */
    override fun starting(description: Description): Unit = Dispatchers.setMain(testDispatcher)

    /**
     * Overrides the finished method of the [TestWatcher] class.
     * This method is called after each test method is executed.
     * It resets the main dispatcher.
     *
     * @param description The description of the test method.
     */
    override fun finished(description: Description): Unit = Dispatchers.resetMain()
}
