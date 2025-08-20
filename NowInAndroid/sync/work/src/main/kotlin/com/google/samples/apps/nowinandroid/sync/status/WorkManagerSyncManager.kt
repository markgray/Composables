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

package com.google.samples.apps.nowinandroid.sync.status

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import com.google.samples.apps.nowinandroid.sync.initializers.SYNC_WORK_NAME
import com.google.samples.apps.nowinandroid.sync.workers.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [SyncManager] backed by [WorkInfo] from [WorkManager]
 *
 * @property context The application context, injected by Hilt.
 */
internal class WorkManagerSyncManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : SyncManager {
    /**
     * A [Flow] that emits `true` if the sync work is currently running, and `false` otherwise.
     *
     * This flow is obtained by observing the [WorkInfo] for the unique work name [SYNC_WORK_NAME]
     * from [WorkManager]. The flow is conflated, meaning that it only emits the latest value.
     */
    override val isSyncing: Flow<Boolean> =
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkFlow(uniqueWorkName = SYNC_WORK_NAME)
            .map(transform = List<WorkInfo>::anyRunning)
            .conflate()

    /**
     * Requests a new sync operation.
     *
     * This function enqueues a new sync worker to perform the synchronization.
     * It uses [ExistingWorkPolicy.KEEP] to ensure that if a sync operation is already pending or running,
     * a new one will not be started.
     */
    override fun requestSync() {
        val workManager = WorkManager.getInstance(context = context)
        // Run sync on app startup and ensure only one sync worker runs at any time
        workManager.enqueueUniqueWork(
            uniqueWorkName = SYNC_WORK_NAME,
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
            request = SyncWorker.startUpSyncWork(),
        )
    }
}

/**
 * Summarizes the [List] of [WorkInfo] into a single boolean indicating whether any of the
 * jobs are running.
 */
private fun List<WorkInfo>.anyRunning() = any { workInfo: WorkInfo ->
    workInfo.state == State.RUNNING
}
