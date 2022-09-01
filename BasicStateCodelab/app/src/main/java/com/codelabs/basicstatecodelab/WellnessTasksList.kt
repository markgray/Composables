package com.codelabs.basicstatecodelab

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * This Composable displays a [List] of [WellnessTask]'s in a [LazyColumn] using a [WellnessTaskItem]
 * for each [WellnessTask]. Our root Composable is a [LazyColumn] to which we pass our [modifier]
 * parameter as its `modifier` argument. The [items] `content` of that [LazyColumn] uses our [List]
 * of [WellnessTask] parameter [list] as its `items` argument, and for the `key` argument it passes
 * the [WellnessTask.id] field of the [WellnessTask] (this value is guaranteed to remain unique and
 * stable even when [WellnessTask]'s before this [WellnessTask] are removed from the [List]). The
 * `itemContent` displayed by a single item is a [WellnessTaskItem] whose arguments are:
 *  - `taskName` - the [WellnessTask.label] of each `task` is displayed in its [Text] Composable
 *  of the [WellnessTaskItem] UI
 *  - `checked` - the [WellnessTask.checked] field of each `task` is used as the `checked` state
 *  of the [Checkbox] in the [WellnessTaskItem] UI
 *  - `onCheckedChange` - a lambda which calls our [onCheckedTask] parameter with the [WellnessTask]
 *  and the changed checked state of the [Checkbox] in the [WellnessTaskItem] UI
 *  - `onClose` - a lambda which calls our [onCloseTask] parameter with the [WellnessTask]
 *
 * @param list the [List] of [WellnessTask]'s we are to display.
 * @param onCheckedTask a lambda that each [WellnessTaskItem] in our [LazyColumn] should call with
 * the [WellnessTask] it holds and the `checked` state of the [Checkbox] in its UI when that state
 * changes. In our case we are passed a lambda that calls the [WellnessViewModel.changeTaskChecked]
 * method of the viewmodel with the [WellnessTask] and the new checked state, and it updates the
 * entry for the [WellnessTask] in its [List] of [WellnessTask] dataset.
 * @param onCloseTask a lambda hat each [WellnessTaskItem] in our [LazyColumn] should call with
 * the [WellnessTask] it holds when the "Close" [IconButton] is clicked. In our case we are passed
 * a lambda that calls the [WellnessViewModel.remove] method of the viewmodel with the [WellnessTask]
 * and that method removes the [WellnessTask] from its [List] of [WellnessTask] dataset.
 * @param modifier a [Modifier] that our caller can use to modify our appearance and behavior. Our
 * caller does not pass a value so the empty, default, or starter [Modifier] that contains no
 * elements that is specified by the default value of the parameter is used.
 */
@Composable
fun WellnessTasksList(
    list: List<WellnessTask>,
    onCheckedTask: (WellnessTask, Boolean) -> Unit,
    onCloseTask: (WellnessTask) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = list,
            /**
             * Use key param to define unique keys representing the items in a mutable list,
             * instead of using the default key (list position). This prevents unnecessary
             * recompositions.
             */
            key = { task -> task.id }
        ) { task: WellnessTask ->
            WellnessTaskItem(
                taskName = task.label,
                checked = task.checked,
                onCheckedChange = { checked -> onCheckedTask(task, checked) },
                onClose = { onCloseTask(task) }
            )
        }
    }
}
