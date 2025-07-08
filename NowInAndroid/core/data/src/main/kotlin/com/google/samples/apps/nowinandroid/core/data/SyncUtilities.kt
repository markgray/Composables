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

package com.google.samples.apps.nowinandroid.core.data

import android.util.Log
import com.google.samples.apps.nowinandroid.core.datastore.ChangeListVersions
import com.google.samples.apps.nowinandroid.core.network.model.NetworkChangeList
import kotlin.coroutines.cancellation.CancellationException

/**
 * Interface marker for a class that manages synchronization between local data and a remote
 * source for a [Syncable].
 */
interface Synchronizer {
    /**
     * Fetches the [ChangeListVersions] from the datastore.
     */
    suspend fun getChangeListVersions(): ChangeListVersions

    /**
     * Atomically updates the [ChangeListVersions] using its [update] lambda parameter.
     *
     * @param update the lambda that takes the current [ChangeListVersions] and returns an updated
     * [ChangeListVersions].
     */
    suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions)

    /**
     * Syntactic sugar to call [Syncable.syncWith] while omitting the synchronizer argument.
     *
     * @return the result of the [Syncable.syncWith] function
     */
    suspend fun Syncable.sync(): Boolean = this@sync.syncWith(this@Synchronizer)
}

/**
 * Interface marker for a class that is synchronized with a remote source. Syncing must not be
 * performed concurrently and it is the [Synchronizer]'s responsibility to ensure this.
 */
interface Syncable {
    /**
     * Synchronizes the local database backing the repository with the network.
     * Returns if the sync was successful or not.
     *
     * @param synchronizer [Synchronizer] that will perform the sync.
     * @return `true` if the sync was successful or `false` if it failed.
     */
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 *
 * @param block The suspend function that needs to be executed
 * @return a [Result] representing the execution result
 */
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(value = block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.i(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception,
    )
    Result.failure(exception = exception)
}

/**
 * Utility function for syncing a repository with the network.
 * [versionReader] Reads the current version of the model that needs to be synced,
 * [changeListFetcher] Fetches the change list for the model,
 * [versionUpdater] Updates the [ChangeListVersions] after a successful sync,
 * [modelDeleter] Deletes models by consuming the ids of the models that have been deleted,
 * [modelUpdater] Updates models by consuming the ids of the models that have changed.
 *
 * Note that the blocks defined above are never run concurrently, and the [Synchronizer]
 * implementation must guarantee this.
 *
 * We start by initializing our [Int] variable `currentVersion` with the value returned by
 * [versionReader] when called with the [ChangeListVersions] returned by the
 * [Synchronizer.getChangeListVersions] method of our [Synchronizer] receiver. Then we initialize
 * our [List] of [NetworkChangeList] variable `changeList` with the value returned by
 * [changeListFetcher] when called with the `currentVersion` variable. If the `changeList` is
 * empty, we return `true` to indicate that the sync was successful, otherwise we continue by using
 * the [Iterable.partition] extension function to split the `changeList` into two [List] of
 * [NetworkChangeList] variables, `deleted` and `updated` based on the value returned by the
 * `predicate` [NetworkChangeList.isDelete] property of each [NetworkChangeList] object (when `true`
 * the [NetworkChangeList] object is added to the `deleted` [List], otherwise it is added to the
 * `updated` [List]). Next we call the [modelDeleter] lambda parameter with the [List] of [String]
 * that the [Iterable.map] creates from [NetworkChangeList.id] properties of each [NetworkChangeList]
 * in `deleted` (deletes models that have been deleted server-side) and call the [modelUpdater]
 * lambda parameter with the [List] of [String] that the [Iterable.map] creates from
 * [NetworkChangeList.id] properties of each [NetworkChangeList] in `updated` (updates models that
 * have changed server-side). We initialize our [Int] variable `latestVersion` with the value
 * of the [NetworkChangeList.changeListVersion] property of the last [NetworkChangeList] object in
 * `changeList` and call the [Synchronizer.updateChangeListVersions] method of our [Synchronizer]
 * receiver with a [ChangeListVersions] lambda parameter that calls our [versionUpdater] lambda
 * parameter with the `latestVersion` variable. Finally we return the value of the [Result.isSuccess]
 * property of the [Result] object that is returned by the [suspendRunCatching] function (which is
 * `true` if the [Result] is not a [Result.Failure] and `false` if it is).
 *
 * @param versionReader the block used to read the current version of the model
 * @param changeListFetcher the block used to fetch the change list for the model
 * @param versionUpdater the block used to update the [ChangeListVersions] after a successful sync
 * @param modelDeleter the block used to delete models by consuming the ids of the models that have
 * been deleted.
 * @param modelUpdater the block used to update models by consuming the ids of the models that have
 * changed.
 * @return `true` if the sync was successful or `false` if it failed.
 */
suspend fun Synchronizer.changeListSync(
    versionReader: (ChangeListVersions) -> Int,
    changeListFetcher: suspend (Int) -> List<NetworkChangeList>,
    versionUpdater: ChangeListVersions.(Int) -> ChangeListVersions,
    modelDeleter: suspend (List<String>) -> Unit,
    modelUpdater: suspend (List<String>) -> Unit,
): Boolean = suspendRunCatching {
    // Fetch the change list since last sync (akin to a git fetch)
    val currentVersion: Int = versionReader(getChangeListVersions())
    val changeList: List<NetworkChangeList> = changeListFetcher(currentVersion)
    if (changeList.isEmpty()) return@suspendRunCatching true

    val (deleted: List<NetworkChangeList>, updated: List<NetworkChangeList>) =
        changeList.partition(predicate = NetworkChangeList::isDelete)

    // Delete models that have been deleted server-side
    modelDeleter(deleted.map(transform = NetworkChangeList::id))

    // Using the change list, pull down and save the changes (akin to a git pull)
    modelUpdater(updated.map(transform = NetworkChangeList::id))

    // Update the last synced version (akin to updating local git HEAD)
    val latestVersion: Int = changeList.last().changeListVersion
    updateChangeListVersions {
        versionUpdater(latestVersion)
    }
}.isSuccess
