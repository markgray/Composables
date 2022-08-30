package com.codelabs.basicstatecodelab

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * This is the class which holds the data that is displayed in each [WellnessTaskItem] by the
 * [LazyColumn] used in the [WellnessTasksList] Composable. [WellnessViewModel] generates a [List]
 * of these as its dataset, and provides methods that allow the user to modify or remove a particular
 * [WellnessTask].
 *
 * @param id a unique identifier for the [WellnessTask]. It needs to be unique because it is used as
 * the `key` for each of the [WellnessTaskItem]'s used as [items] of the [LazyColumn] of the Composable
 * [WellnessTasksList] (When you specify the key the scroll position will be maintained based on the
 * key, which means if you add/remove items before the current visible item the item with the given
 * key will be kept as the first visible one).
 * @param label the string that the [WellnessTaskItem] displaying this [WellnessTask] will display
 * as its `taskName` argument in the `text` argument of its [Text].
 * @param initialChecked the initial value of our [checked] field, no value is passed by our caller
 * so the default value of `false` is always used.
 */
class WellnessTask(
    val id: Int,
    val label: String,
    initialChecked: Boolean = false
) {
    /**
     * This is the "checked" state of this [WellnessTask]. It is toggled by the [Checkbox] of the
     * [WellnessTaskItem] that displays this [WellnessTask] using the method
     * [WellnessViewModel.changeTaskChecked] of the viewmodel.
     */
    var checked by mutableStateOf(initialChecked)
}

