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

import com.codelab.android.datastore.UserPreferences
import com.codelab.android.datastore.ui.TasksViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * This repoository provides a [Flow] of a [List] of dummy [Task] objects to the [TasksViewModel]
 * which the view model `combine`'s with the [Flow] of [UserPreferences] created by the
 * [UserPreferencesRepository] to create a sorted and filtered [List] of [Task]'s that are
 * displayed in the UI's `RecyclerView`.
 */
object TasksRepository {

    /**
     * The [SimpleDateFormat] instance we use to parse strings into [Date]'s to initialize the
     * [Task.deadline] field of some of the [Task] instances in our dummy [List] of [Task].
     */
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /**
     * This is the [Flow] of a [List] of dummy [Task] objects that we supply to [TasksViewModel].
     */
    val tasks: Flow<List<Task>> = flowOf(
        listOf(
            Task(
                name = "Open codelab",
                deadline = simpleDateFormat.parse("2020-07-03")!!,
                priority = TaskPriority.LOW,
                completed = true
            ),
            Task(
                name = "Import project",
                deadline = simpleDateFormat.parse("2020-04-03")!!,
                priority = TaskPriority.MEDIUM,
                completed = true
            ),
            Task(
                name = "Check out the code", deadline = simpleDateFormat.parse("2020-05-03")!!,
                priority = TaskPriority.LOW,
                completed = true
            ),
            Task(
                name = "Read about DataStore", deadline = simpleDateFormat.parse("2020-06-03")!!,
                priority = TaskPriority.HIGH,
                completed = true
            ),
            Task(
                name = "Implement each step",
                deadline = Date(),
                priority = TaskPriority.MEDIUM
            ),
            Task(
                name = "Understand how to use DataStore",
                deadline = simpleDateFormat.parse("2020-04-03")!!,
                priority = TaskPriority.HIGH,
                completed = true
            ),
            Task(
                name = "Understand how to migrate to DataStore",
                deadline = Date(),
                priority = TaskPriority.HIGH
            ),
            Task(
                name = "Get Born",
                deadline = simpleDateFormat.parse("1951-12-11")!!,
                priority = TaskPriority.HIGH,
                completed = true
            ),
            Task(
                name = "Patch Roof",
                deadline = Date(),
                priority = TaskPriority.MEDIUM
            ),
            Task(
                name = "Fix Plumbing",
                deadline = Date(),
                priority = TaskPriority.MEDIUM
            ),
            Task(
                name = "Fix Leaky Roof",
                deadline = Date(),
                priority = TaskPriority.MEDIUM
            ),
            Task(
                name = "Cure Cancer",
                deadline = Date(),
                priority = TaskPriority.HIGH
            ),
            Task(
                name = "Cure Alzheimer's",
                deadline = Date(),
                priority = TaskPriority.HIGH
            ),
            Task(
                name = "Get Back on your diet",
                deadline = Date(),
                priority = TaskPriority.MEDIUM
            ),
            Task(
                name = "Increase Exercise",
                deadline = Date(),
                priority = TaskPriority.MEDIUM
            ),
        )
    )
}
