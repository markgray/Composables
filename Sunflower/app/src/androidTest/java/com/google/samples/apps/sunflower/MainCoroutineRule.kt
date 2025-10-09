/*
 * Copyright 2021 Google LLC
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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit rule that sets the main coroutine dispatcher for unit tests. This rule is essential
 * for tests that involve coroutines, especially those that interact with the main thread, such as
 * UI-related tests in Android.
 *
 * It replaces the main dispatcher with a [TestDispatcher] before each test runs and resets it
 * after the test completes. This allows for precise control over the execution of coroutines,
 * enabling tests to be deterministic and reliable.
 *
 * @property testDispatcher The [TestDispatcher] to use for the main dispatcher. Defaults to a
 * [StandardTestDispatcher]. You can provide a different dispatcher, like an
 * [UnconfinedTestDispatcher], for specific testing needs.
 */
class MainCoroutineRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    /**
     * Invoked when a test is about to start. First we call our super's implementation, then we set
     * the main dispatcher to our [TestDispatcher] property [testDispatcher].
     *
     * @param description The [Description] of the test being executed. A [Description] describes a
     * test which is to be run or has been run. Descriptions can be atomic (a single test) or
     * compound (containing children tests). Descriptions are used to provide feedback about the
     * tests that are about to run (for example, the tree view visible in many IDEs) or tests that
     * have been run (for example, the failures view). Descriptions are implemented as a single
     * class rather than a Composite because they are entirely informational. They contain no logic
     * aside from counting their tests.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    /**
     * Invoked when a test method finishes (whether passing or failing). First we call our super's
     * implementation, then we reset the main dispatcher to the default dispatcher.
     *
     * @param description The [Description] of the test being executed. A [Description] describes a
     * test which is to be run or has been run. Descriptions can be atomic (a single test) or
     * compound (containing children tests). Descriptions are used to provide feedback about the
     * tests that are about to run (for example, the tree view visible in many IDEs) or tests that
     * have been run (for example, the failures view). Descriptions are implemented as a single
     * class rather than a Composite because they are entirely informational. They contain no logic
     * aside from counting their tests.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

/**
 * Runs a test block with the [TestDispatcher] provided by this rule.
 *
 * This is a convenience wrapper around `runTest` that uses the dispatcher from this
 * [MainCoroutineRule]. It's designed to simplify writing tests that use this rule,
 * ensuring the test body executes within the correct coroutine context.
 *
 * This function is an extension on [MainCoroutineRule] and is intended to be used
 * as a replacement for the now-deprecated `runBlockingTest` extension on [TestCoroutineScope].
 *
 * @param block The suspendable block of code to be executed as the test.
 * @return A [TestResult] that can be returned from a JUnit test function.
 */
fun MainCoroutineRule.runBlockingTest(block: suspend () -> Unit): TestResult =
    runTest(context = this.testDispatcher) {
        block()
    }
