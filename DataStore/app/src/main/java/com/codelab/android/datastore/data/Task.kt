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

@file:Suppress("UnusedImport") // Used in kdoc

package com.codelab.android.datastore.data

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.FilterChip
import androidx.compose.ui.graphics.Color
import java.util.Date
import com.codelab.android.datastore.ui.TaskComposer
import com.codelab.android.datastore.ui.MainScreen

/**
 * This enum is used to assign a priority to a [Task], one of [HIGH] (highest priority), [MEDIUM]
 * (medium priority), and [LOW] (lowest priority)
 */
enum class TaskPriority {
    /**
     * Highest priority to assign to a [Task]
     */
    HIGH,

    /**
     * Medium priority to assign to a [Task]
     */
    MEDIUM,

    /**
     * Lowest priority to assign to a [Task]
     */
    LOW
}

/**
 * This is the data class that is used to hold the information about each of the tasks in the list
 * of tasks contained in the [TasksRepository.tasks] `flow` of [List] of [Task] objects.
 *
 * @param name The name of the [Task], it is displayed by the [TaskComposer] Composable in a [Text],
 * when that [Task] is displayed in the [LazyColumn] that is the 'content' of the [Scaffold] of the
 * [MainScreen] Composable.
 * @param deadline The "dead line" for completing the [Task], it is displayed by the [TaskComposer]
 * Composable in a [Text], when that [Task] is displayed in the [LazyColumn] that is the 'content'
 * of the [Scaffold] of the [MainScreen] Composable, and it is also used to sort the list of [Task]'s
 * that are displayed in that [LazyColumn] if the "Deadline" [FilterChip] is "On" in the UI.
 * @param priority The [TaskPriority] of the [Task], it's [TaskPriority.name] is displayed by the
 * [TaskComposer] Composable in a [Text] and it is used to set the text color of the [Text]
 * ([Color.Red] for [TaskPriority.HIGH], [Color.Yellow] for [TaskPriority.MEDIUM] or [Color.Green]
 * for [TaskPriority.LOW]) and it used to sort the list of [Task]'s displayed in the [LazyColumn] if
 * the ""Priority"" [FilterChip] is "On" in the UI.
 * @param completed the [Task] has been completed if this [Boolean] is `true`, it is used to disable
 * the display of the [Task] in the UI's [LazyColumn] when `true` unless the "Show completed tasks"
 * [Switch] is "On", and also used to set the color of the background color of the [Text] displaying
 * the [Task.deadline] ([Color.LightGray] if `true` or [Color.Blue] if `false`).
 */
data class Task(
    val name: String,
    val deadline: Date,
    val priority: TaskPriority,
    val completed: Boolean = false
)
