package com.codelab.android.datastore.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.codelab.android.datastore.data.Task
import com.codelab.android.datastore.data.TaskPriority
import com.codelab.android.datastore.ui.theme.DataStoreTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * This Composable displays the information about a single [Task]. It is a Compose version of the
 * layout file `task_view_item.xml` from the View based codelab.
 *
 * @param task the [Task] whose information we are to display.
 */
@Composable
fun TaskComposer(task: Task) {
    Column (
        modifier = Modifier.fillMaxWidth()
        ) {
        Text(
            text = task.name,
            fontSize = 20.sp
        )
        when (task.priority) {
            TaskPriority.HIGH -> {
                Text(
                    text = "Priority ${task.priority.name}",
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }
            TaskPriority.MEDIUM -> {
                Text(
                    text = "Priority ${task.priority.name}",
                    fontSize = 16.sp,
                    color = Color.Yellow
                )
            }
            TaskPriority.LOW -> {
                Text(
                    text = "Priority ${task.priority.name}",
                    fontSize = 16.sp,
                    color = Color.Green
                )
            }
        }
        Text(
            text = dateFormat.format(task.deadline),
            fontSize = 16.sp,
            color = if (task.completed) {
                Color.LightGray
            } else {
                Color.Blue
            }
        )
    }
}

/**
 * Format date as: Apr 6, 2020
 */
private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

/**
 *
 */
@Preview
@Composable
fun PreviewTaskComposer() {
    DataStoreTheme {
        TaskComposer(Task(
            name = "Feel Better",
            deadline = Date(),
            priority = TaskPriority.HIGH,
            completed = false
        ))
    }
}