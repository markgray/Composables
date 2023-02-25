/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelab.android.datastore.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModelProvider
import com.codelab.android.datastore.UserPreferences
import com.codelab.android.datastore.UserPreferences.SortOrder
import com.codelab.android.datastore.ui.TasksActivity
import com.codelab.android.datastore.ui.TasksViewModel
import com.codelab.android.datastore.ui.TasksViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

/**
 * Class that handles saving and retrieving user preferences from the Proto [DataStore].
 *
 * @param userPreferencesStore the Proto [DataStore] of [UserPreferences] we are to use. This is
 * the [DataStore] of [UserPreferences] extension property that is added to [Context] at the top
 * level of the [TasksActivity] which ends up being passed to the [TasksViewModel] constructor by
 * the [ViewModelProvider] using the [TasksViewModelFactory] class.
 */
class UserPreferencesRepository(
    private val userPreferencesStore: DataStore<UserPreferences>
) {

    /**
     * This is the [Flow] of [UserPreferences] used by [TasksViewModel] to filter and sort the [List]
     * of [Task] objects it supplies to the UI. We just access the [DataStore.data] flow of our
     * [DataStore] of [UserPreferences] field and use the [Flow.catch] extension function on it to
     * catch exceptions in the flow completion and call a lambda with any caught exception which
     * checks if the exception is an [IOException] in which case we log the message "Error reading
     * preferences." and emit the default instance of [UserPreferences] returned by the static method
     * [UserPreferences.getDefaultInstance] to the next method in the chain, and if it is not an
     * [IOException] we rethrow it.
     */
    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data
        .catch { exception: Throwable ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    /**
     * Enable / disable sort by deadline. We use the [DataStore.updateData] method of our field
     * [userPreferencesStore] with the lambda argument taking the current value of [UserPreferences]
     * from the [DataStore] in its variable `currentPreferences` from which it fetches the value
     * of the [UserPreferences.getSortOrder] getter (aka kotlin `sortOrder` property) to initialize
     * its [SortOrder] variable `val currentOrder`, then initializes its [SortOrder] variable
     * `val newSortOrder` depending on the value of our [Boolean] parameter [enable]:
     *  - `true` it initializes its [SortOrder] variable `val newSortOrder` to
     *  [SortOrder.BY_DEADLINE_AND_PRIORITY] if `currentOrder` is [SortOrder.BY_PRIORITY] else to
     *  [SortOrder.BY_DEADLINE] if it is not equal to [SortOrder.BY_PRIORITY].
     *  - `false` it initializes its [SortOrder] variable `val newSortOrder` to [SortOrder.BY_PRIORITY]
     *  if `currentOrder` is [SortOrder.BY_DEADLINE_AND_PRIORITY] else to
     *  [SortOrder.NONE] if it is not equal to [SortOrder.BY_DEADLINE_AND_PRIORITY].
     *
     * We then use the [UserPreferences.toBuilder] method of `currentPreferences` to create a builder,
     * call its `setSortOrder` method with `newSortOrder` to have it set the [SortOrder] in the
     * builder to `newSortOrder` and build the new [UserPreferences] which the `updateData` method
     * of [userPreferencesStore] will use to update the data transactionally in an atomic
     * read-modify-write operation.
     *
     * @param enable if `true` we enable the [SortOrder.BY_DEADLINE] preference, if `false` we
     * disable it
     */
    suspend fun enableSortByDeadline(enable: Boolean) {
        // updateData handles data transactionally, ensuring that if the sort is updated at the same
        // time from another thread, we won't have conflicts
        userPreferencesStore.updateData { currentPreferences: UserPreferences ->
            val currentOrder: SortOrder = currentPreferences.sortOrder
            val newSortOrder: SortOrder =
                if (enable) {
                    if (currentOrder == SortOrder.BY_PRIORITY) {
                        SortOrder.BY_DEADLINE_AND_PRIORITY
                    } else {
                        SortOrder.BY_DEADLINE
                    }
                } else {
                    if (currentOrder == SortOrder.BY_DEADLINE_AND_PRIORITY) {
                        SortOrder.BY_PRIORITY
                    } else {
                        SortOrder.NONE
                    }
                }
            currentPreferences.toBuilder().setSortOrder(newSortOrder).build()
        }
    }

    /**
     * Enable / disable sort by priority. We use the [DataStore.updateData] method of our field
     * [userPreferencesStore] with the lambda argument taking the current value of [UserPreferences]
     * from the [DataStore] in its variable `currentPreferences` from which it fetches the value
     * of the [UserPreferences.getSortOrder] getter (aka kotlin `sortOrder` property) to initialize
     * its [SortOrder] variable `val currentOrder`, then initializes its [SortOrder] variable
     * `val newSortOrder` depending on the value of our [Boolean] parameter [enable]:
     *  - `true` it initializes its [SortOrder] variable `val newSortOrder` to
     *  [SortOrder.BY_DEADLINE_AND_PRIORITY] if `currentOrder` is [SortOrder.BY_DEADLINE] else to
     *  [SortOrder.BY_PRIORITY] if it is not equal to [SortOrder.BY_DEADLINE].
     *  - `false` it initializes its [SortOrder] variable `val newSortOrder` to [SortOrder.BY_DEADLINE]
     *  if `currentOrder` is [SortOrder.BY_DEADLINE_AND_PRIORITY] else to
     *  [SortOrder.NONE] if it is not equal to [SortOrder.BY_DEADLINE_AND_PRIORITY].
     *
     * We then use the [UserPreferences.toBuilder] method of `currentPreferences` to create a builder,
     * call its `setSortOrder` method with `newSortOrder` to have it set the [SortOrder] in the
     * builder to `newSortOrder` and build the new [UserPreferences] which the `updateData` method
     * of [userPreferencesStore] will use to update the data transactionally in an atomic
     * read-modify-write operation.
     *
     * @param enable if `true` we enable the [SortOrder.BY_PRIORITY] preference, if `false` we
     * disable it
     */
    suspend fun enableSortByPriority(enable: Boolean) {
        // updateData handles data transactionally, ensuring that if the sort is updated at the same
        // time from another thread, we won't have conflicts
        userPreferencesStore.updateData { currentPreferences: UserPreferences ->
            val currentOrder: SortOrder = currentPreferences.sortOrder
            val newSortOrder: SortOrder =
                if (enable) {
                    if (currentOrder == SortOrder.BY_DEADLINE) {
                        SortOrder.BY_DEADLINE_AND_PRIORITY
                    } else {
                        SortOrder.BY_PRIORITY
                    }
                } else {
                    if (currentOrder == SortOrder.BY_DEADLINE_AND_PRIORITY) {
                        SortOrder.BY_DEADLINE
                    } else {
                        SortOrder.NONE
                    }
                }
            currentPreferences.toBuilder().setSortOrder(newSortOrder).build()
        }
    }

    /**
     * Called to update the value of the preference stored for `show_completed` in our Proto
     * [DataStore] of [UserPreferences]. We use the [DataStore.updateData] method of our field
     * [userPreferencesStore] with the lambda argument taking the current value of [UserPreferences]
     * from the [DataStore] in its variable `preferences`, and then use the [UserPreferences.toBuilder]
     * method of `preferences` to create a builder, call its `setShowCompleted` method with
     * [completed] to have it set `show_completed` in the builder to our [Boolean] parameter
     * [completed] and build the new [UserPreferences] which the `updateData` method of
     * [userPreferencesStore] will use to update the data transactionally in an atomic
     * read-modify-write operation.
     *
     * @param completed the [Boolean] value we are to set the value stored for `show_completed`
     * in our [DataStore] of [UserPreferences].
     */
    suspend fun updateShowCompleted(completed: Boolean) {
        userPreferencesStore.updateData { preferences: UserPreferences ->
            preferences.toBuilder().setShowCompleted(completed).build()
        }
    }

    /**
     * Fetches the app's [UserPreferences] from our proto [DataStore] flow of [UserPreferences]
     * field [userPreferencesStore] using the [Flow.first] extension function to get the first
     * element emitted by the flow and then cancel the flow's collection and return this [Flow]
     * to the caller.
     *
     * @return the [UserPreferences] from the initial contents of our app's Proto [DataStore] of
     * [UserPreferences] file.
     */
    suspend fun fetchInitialPreferences(): UserPreferences = userPreferencesStore.data.first()

    companion object {
        /**
         * TAG used for logging.
         */
        private const val TAG: String = "UserPreferencesRepo"
    }

}
