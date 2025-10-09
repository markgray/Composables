/*
 * Copyright 2019 Google LLC
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

package com.google.samples.apps.sunflower.worker

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.ListenableWorker.Result
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import com.google.common.util.concurrent.ListenableFuture
import com.google.samples.apps.sunflower.utilities.PLANT_DATA_FILENAME
import com.google.samples.apps.sunflower.workers.SeedDatabaseWorker
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Unit tests for [RefreshMainDataWorkTest].
 *
 * The @[RunWith] annotation causes JUnit to invoke the [JUnit4] class to run the tests in
 * this class instead of the runner built into JUnit.
 */
@RunWith(JUnit4::class)
class RefreshMainDataWorkTest {
    /**
     * A [WorkManager] that is initialized for testing purposes.
     */
    private lateinit var workManager: WorkManager

    /**
     * The application [Context] for the instrumentation test.
     */
    private lateinit var context: Context

    /**
     * A [Configuration] that is used for initializing the [WorkManager] for the test.
     */
    private lateinit var configuration: Configuration

    /**
     * Overrides the default WorkManager configuration to use a synchronous executor for testing,
     * making it easier to write and debug tests. It also initializes WorkManager for
     * instrumentation tests.
     *
     * The @[Before] annotation annotates methods which must be executed before
     * every @[Test] annotated method.
     */
    @Before
    fun setup() {
        // Configure WorkManager
        configuration = Configuration.Builder()
            // Set log level to Log.DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            // Use a SynchronousExecutor here to make it easier to write tests
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        context = InstrumentationRegistry.getInstrumentation().targetContext
        WorkManagerTestInitHelper.initializeTestWorkManager(context, configuration)
        workManager = WorkManager.getInstance(context)
    }

    /**
     * Tests the `doWork()` method of the `SeedDatabaseWorker`. It creates an instance of the
     * [SeedDatabaseWorker] using [TestListenableWorkerBuilder], providing the necessary input data.
     * It then executes the worker's task synchronously and asserts that the work completes
     * successfully, indicated by a [Result.Success] return value. This confirms that the worker
     * can be instantiated and run without crashing.
     */
    @Test
    fun testRefreshMainDataWork() {
        // Get the ListenableWorker
        val worker = TestListenableWorkerBuilder<SeedDatabaseWorker>(
            context = context,
            inputData = workDataOf(SeedDatabaseWorker.KEY_FILENAME to PLANT_DATA_FILENAME)
        ).build()

        // Start the work synchronously
        val future: ListenableFuture<Result> = worker.startWork()
        val result: Result = future.get()

        assertThat(result, `is`(Result.Success()))
    }
}