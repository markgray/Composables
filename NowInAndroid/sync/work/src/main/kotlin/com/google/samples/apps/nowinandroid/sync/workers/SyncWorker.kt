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
import androidx.hilt.work.HiltWorker
import androidx.tracing.Trace
import androidx.tracing.traceAsync
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.data.Synchronizer
import com.google.samples.apps.nowinandroid.core.data.repository.NewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.SearchContentsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.datastore.ChangeListVersions
import com.google.samples.apps.nowinandroid.core.datastore.NiaPreferencesDataSource
import com.google.samples.apps.nowinandroid.core.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers.IO
import com.google.samples.apps.nowinandroid.sync.initializers.SyncConstraints
import com.google.samples.apps.nowinandroid.sync.initializers.syncForegroundInfo
import com.google.samples.apps.nowinandroid.sync.status.SyncSubscriber
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Syncs the data layer by delegating to the appropriate repository instances with sync functionality.
 * The @[HiltWorker] annotation identifies a [ListenableWorker] constructor for injection, the
 * @[AssistedInject] annotation Annotates the constuctor of a type that will be created via assisted
 * injection, and the @[Assisted] annotation identifies an argument to the constructor that will be
 * injected by assisted injection.
 *
 * @property appContext the application [Context], injected by Hilt with an assist.
 * @property workerParams the [WorkerParameters] for this worker, injected by Hilt with an assist.
 * @property niaPreferences a [NiaPreferencesDataSource] instance, injected by Hilt.
 * @property topicRepository a [TopicsRepository] instance, injected by Hilt.
 * @property newsRepository a [NewsRepository] instance, injected by Hilt.
 * @property searchContentsRepository a [SearchContentsRepository] instance, injected by Hilt.
 * @property ioDispatcher the [CoroutineDispatcher] for running database operations, injected by Hilt.
 * @property analyticsHelper a [AnalyticsHelper] instance, injected by Hilt.
 * @property syncSubscriber a [SyncSubscriber] instance, injected by Hilt.
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val niaPreferences: NiaPreferencesDataSource,
    private val topicRepository: TopicsRepository,
    private val newsRepository: NewsRepository,
    private val searchContentsRepository: SearchContentsRepository,
    @param:Dispatcher(niaDispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
    private val syncSubscriber: SyncSubscriber,
) : CoroutineWorker(appContext = appContext, params = workerParams), Synchronizer {

    /**
     * The [ForegroundInfo] that is needed when the worker is running in the foreground. This
     * information is required when a [ListenableWorker] runs in
     * the context of a foreground service.
     *
     * @return the [ForegroundInfo] that will be used to run in the foreground.
     */
    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    /**
     * This method is the entry point for the worker. It is responsible for syncing the data layer.
     *
     * Wrapped in a call to the [traceAsync] method with its `methodName` argument "Sync", and its
     * `cookie` argument `0` in order to have its `block` suspend lambd argument wrapped in calls
     * to [Trace.beginAsyncSection] and [Trace.endAsyncSection] we do the following:
     *
     *  - call the [AnalyticsHelper.logSyncStarted] method of our [AnalyticsHelper] property
     *  [analyticsHelper] to log the start of the sync operation.
     *  - call the [SyncSubscriber.subscribe] method of our [SyncSubscriber] property [syncSubscriber]
     *  to subscribe to changes in the sync work state.
     *  - initialize our [Boolean] variable `syncedSuccessfully` to the result of launching and
     *  waiting for [async] calls to [TopicsRepository.sync] and [NewsRepository.sync] in parallel
     *  to return `true` if both calls return `true`.
     *  - call the [AnalyticsHelper.logSyncFinished] method of our [AnalyticsHelper] property
     *  [analyticsHelper] to log the end of the sync operation.
     *  - if our [Boolean] variable `syncedSuccessfully` is `true`, we return [Result.success],
     *  and if it is `false`, we return [Result.retry].
     *
     * @return The result of the sync operation.
     * @see CoroutineWorker.doWork
     */
    override suspend fun doWork(): Result = withContext(context = ioDispatcher) {
        traceAsync(methodName = "Sync", cookie = 0) {
            analyticsHelper.logSyncStarted()

            syncSubscriber.subscribe()

            // First sync the repositories in parallel
            val syncedSuccessfully: Boolean = awaitAll(
                async { topicRepository.sync() },
                async { newsRepository.sync() },
            ).all { successful: Boolean -> successful }

            analyticsHelper.logSyncFinished(syncedSuccessfully = syncedSuccessfully)

            if (syncedSuccessfully) {
                searchContentsRepository.populateFtsData()
                Result.success()
            } else {
                Result.retry()
            }
        }
    }

    /**
     * Gets the [ChangeListVersions] from the [NiaPreferencesDataSource].
     *
     * @return The [ChangeListVersions] from the [NiaPreferencesDataSource].
     */
    override suspend fun getChangeListVersions(): ChangeListVersions =
        niaPreferences.getChangeListVersions()

    /**
     * Updates the [ChangeListVersions] in the [NiaPreferencesDataSource] by running the [update]
     * lambda parameter on it.
     *
     * @param update a lambda which will be run on the current [ChangeListVersions] to produce the
     * new [ChangeListVersions].
     */
    override suspend fun updateChangeListVersions(
        update: ChangeListVersions.() -> ChangeListVersions,
    ) = niaPreferences.updateChangeListVersion(update)

    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}
