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

import java.util.Date

/**
 * TODO: Add kdoc
 */
enum class TaskPriority {
    /**
     * TODO: Add kdoc
     */
    HIGH,

    /**
     * TODO: Add kdoc
     */
    MEDIUM,

    /**
     * TODO: Add kdoc
     */
    LOW
}

/**
 * This is the data class that is used to hold the information about each of the tasks in the list
 * of tasks contained in the [TasksRepository.tasks] `flow` of [List] of [Task] objects.
 *
 * @param name The name of the [Task], it is displayed in the `TaskViewItemBinding.task` `TextView`
 * of each [Task] displayed by the UI's `RecyclerView`
 * @param deadline The "dead line" for completing the [Task], it is displayed in the
 * `TaskViewItemBinding.deadline` `TextView` of each [Task] displayed by the UI's `RecyclerView` and
 * used to sort the list of [Task]'s if the `ActivityTasksBinding.sortDeadline` `Chip` is "On" in
 * the UI.
 * @param priority The [TaskPriority] of the [Task], it is used to set the text color of the
 * `TaskViewItemBinding.priority` `TextView` of each [Task] displayed by the UI's `RecyclerView` and
 * used to sort the list of [Task]'s if the `ActivityTasksBinding.sortPriority` `Chip` is "On" in
 * the UI.
 * @param completed the [Task] has been completed if this [Boolean] is `true`, it is used to disable
 * the display of the [Task] in the UI's `RecyclerView` when `true` unless the
 * `ActivityTasksBinding.showCompleted` `SwitchMaterial` is "On", and also used to set the color of
 * the background color of the entire `itemView` displaying the [Task] to `R.color.greyAlpha` when
 * the completed [Task] is displayed.
 */
data class Task(
    val name: String,
    val deadline: Date,
    val priority: TaskPriority,
    val completed: Boolean = false
)
