package com.codelab.android.datastore.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelab.android.datastore.data.Task
import com.codelab.android.datastore.data.TaskPriority
import com.codelab.android.datastore.ui.theme.DataStoreTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * This Composable displays the information about a single [Task]. It is a Compose version of the
 * layout file `task_view_item.xml` from the View based codelab. Our root Composable is a [Card]
 * whose `border` argument is a [BorderStroke] whose `width` is 1.dp and whose `color` is
 * [Color.LightGray] (draws a border around the [Card]). The `content` of the [Card] is a [Column]
 * whose `modifier` argument is a [Modifier.fillMaxWidth] which causes the [Column] to occupy the
 * entire incoming horizontal constraint. The `content` of the [Column] is:
 *  - a [Text] displaying the [Task.name] of our [task] parameter as its `text` using a `fontSize`
 *  of 20.sp
 *  - Then a `when` statement branches on the [Task.priority] field of [task]:
 *  - [TaskPriority.HIGH] composes a [Text] whose `text` is the string "Priority HIGH", whose
 *  `fontSize` is 16.sp, and whose `color` is [Color.Red].
 *  - [TaskPriority.MEDIUM] composes a [Text] whose `text` is the string "Priority MEDIUM", whose
 *  `fontSize` is 16.sp, and whose `color` is [Color.Yellow].
 *  - [TaskPriority.LOW] composes a [Text] whose `text` is the string "Priority LOW", whose
 *  `fontSize` is 16.sp, and whose `color` is [Color.Green].
 *  - a the bottom of the [Column] is a [Text] whose `text` is formatted by the [SimpleDateFormat]
 *  field [dateFormat] from the [Task.deadline] field of [task] with a `fontSize` of 16.sp, and a
 *  `color` of [Color.LightGray] if the [Task.completed] field of [task] is `true` or [Color.Blue]
 *  if the [Task.completed] field is `false`.
 *
 * @param task the [Task] whose information we are to display.
 */
@Composable
fun TaskComposer(task: Task) {
    Card (
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
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
}

/**
 * Format date as: Apr 6, 2020
 */
private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

/**
 * Preview of our [TaskComposer] Composable for a fake instance of [Task]
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