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

package com.google.samples.apps.nowinandroid.sync.initializers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.google.samples.apps.nowinandroid.sync.workers.SyncWorker

/**
 * Helper class to [initialize] the sync work.
 */
object Sync {

    /**
     * Initializes the background synchronization work for the application, the process that keeps
     * the app's data current.
     *
     * This function schedules a unique periodic work request to sync data using [WorkManager].
     * The sync operation is identified by [SYNC_WORK_NAME] and uses an [ExistingWorkPolicy.KEEP]
     * policy, meaning that if a sync operation is already pending or running, a new one will not be
     * enqueued.
     *
     * It is called from the app module's Application.onCreate() and should be only done once.
     *
     * @param context The application context.
     */
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            // Run sync on app startup and ensure only one sync worker runs at any time
            enqueueUniqueWork(
                uniqueWorkName = SYNC_WORK_NAME,
                existingWorkPolicy = ExistingWorkPolicy.KEEP,
                request = SyncWorker.startUpSyncWork(),
            )
        }
    }
}

/**
 * This is the unique name used to define the periodic sync work request.
 * This name should not be changed otherwise the app may have concurrent sync requests running
 */
internal const val SYNC_WORK_NAME = "SyncWorkName"
