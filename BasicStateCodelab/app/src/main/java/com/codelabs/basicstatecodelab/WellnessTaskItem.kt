package com.codelabs.basicstatecodelab

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * This is the Composable that is used to display each of the [WellnessTask] that are displayed in
 * the [LazyColumn] of the Composable [WellnessTasksList]. Our root composable is a [Row] whose
 * `modifier` argument is our [modifier] parameter, and whose `verticalAlignment` argument is
 * [Alignment.CenterVertically] to have it center its children vertically. Its children are:
 *  - a [Text] displaying our [taskName] parameter as its `text`, using as its `modifier` argument
 *  the `Modifier.weight` of [RowScope] with a `weight` of 1f to have it take up the width left over
 *  in the [Row] after measuring the unweighted sibling elements, with a [Modifier.padding] added to
 *  this to have the `start` padding be 16.dp
 *  - a [Checkbox] whose `checked` argument is our [checked] parameter, and whose `onCheckedChange`
 *  argument is our [onCheckedChange] parameter.
 *  - an [IconButton] whose `onClick` argument is our [onClose] parameter, and whose [Icon] `content`
 *  is the system [ImageVector] drawn by [Icons.Filled.Close]
 *
 * @param taskName the [String] to be used as the `text` argument our [Text]. In our case this is
 * the [WellnessTask.label] field of the [WellnessTask] that we are displaying.
 * @param checked the checked or unchecked state of our [Checkbox]. In our case this is the
 * [WellnessTask.checked] field of the [WellnessTask] that we are displaying.
 * @param onCheckedChange this is a lambda that should be called when the checked or unchecked state
 * of our [Checkbox] changes (it is called with the new [Boolean] value). In our case it ends up
 * calling the [WellnessViewModel.changeTaskChecked] method of our viewmodel with the [WellnessTask]
 * and the new value of [checked].
 * @param onClose this is a lambda that should be called when the "Close" [IconButton] is clicked.
 * In our case it ends up calling the [WellnessViewModel.remove] method of our viewmodel with the
 * [WellnessTask] we are displaying.
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and
 * behavior. We are not called with a value so the empty, default, or starter [Modifier] that
 * contains no elements that is specified by the default value of the parameter is used.
 */
@Composable
fun WellnessTaskItem(
    taskName: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(start = 16.dp),
            text = taskName
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        IconButton(onClick = onClose) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
        }
    }
}
