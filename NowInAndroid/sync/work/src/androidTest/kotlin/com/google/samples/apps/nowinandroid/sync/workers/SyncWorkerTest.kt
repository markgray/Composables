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

package com.google.samples.apps.nowinandroid.sync.workers

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestDriver
import androidx.work.testing.WorkManagerTestInitHelper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Integration test for [SyncWorker]
 */
@HiltAndroidTest
class SyncWorkerTest {

    /**
     * Test rule for Hilt injection.
     */
    @get:Rule(order = 0)
    val hiltRule: HiltAndroidRule = HiltAndroidRule(this)

    /**
     * The [Context] for the test.
     */
    private val context: Context get() = InstrumentationRegistry.getInstrumentation().context

    /**
     * Sets up the test environment by initializing WorkManager for instrumentation tests.
     * This involves creating a WorkManager configuration with a synchronous executor
     * and debug logging level, and then initializing the test WorkManager instance.
     */
    @Before
    fun setup() {
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(loggingLevel = Log.DEBUG)
            .setExecutor(executor = SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    /**
     * Tests the [SyncWorker] by enqueuing a work request and verifying its state transitions.
     *
     * This test performs the following steps:
     *  1. Creates a [OneTimeWorkRequest] for the [SyncWorker].
     *  2. Enqueues the work request using [WorkManager].
     *  3. Retrieves the [WorkInfo] for the enqueued work request.
     *  4. Asserts that the initial state of the work request is [WorkInfo.State.ENQUEUED].
     *  5. Uses [TestDriver] to simulate that all constraints for the work request have been met.
     *  6. Retrieves the [WorkInfo] again after the constraints are met.
     *  7. Asserts that the state of the work request is now [WorkInfo.State.RUNNING].
     */
    @Test
    fun testSyncWork() {
        // Create request
        val request: OneTimeWorkRequest = SyncWorker.startUpSyncWork()

        val workManager: WorkManager = WorkManager.getInstance(context = context)
        val testDriver: TestDriver = WorkManagerTestInitHelper.getTestDriver(context)!!

        // Enqueue and wait for result.
        workManager.enqueue(request = request).result.get()

        // Get WorkInfo and outputData
        val preRunWorkInfo: WorkInfo? = workManager.getWorkInfoById(id = request.id).get()

        // Assert
        assertEquals(expected = WorkInfo.State.ENQUEUED, actual = preRunWorkInfo?.state)

        // Tells the testing framework that the constraints have been met
        testDriver.setAllConstraintsMet(request.id)

        val postRequirementWorkInfo: WorkInfo? = workManager.getWorkInfoById(id = request.id).get()
        assertEquals(expected = WorkInfo.State.RUNNING, actual = postRequirementWorkInfo?.state)
    }
}
