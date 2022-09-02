package com.codelabs.basicstatecodelab

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

/**
 * This is the [ViewModel] used by [WellnessScreen] to hold that dataset of [WellnessTask] that is
 * displayed by [WellnessTasksList], and to provide the methods [remove] and [changeTaskChecked] that
 * is used by [WellnessTaskItem] to remove or modify that checked state of the [WellnessTask] that it
 * displays.
 */
class WellnessViewModel : ViewModel() {
    /**
     * This is our dataset of [WellnessTask] instances. [getWellnessTasks] generates a [List] of
     * [WellnessTask], and we apply the [toMutableStateList] extension function to this [List] to
     * change the [List] to a [SnapshotStateList] of [WellnessTask] (An implementation of [MutableList]
     * that can be observed and snapshot) It is private to protect it from modification by other
     * classes, public read-only access is provided by [tasks].
     */
    private val _tasks: SnapshotStateList<WellnessTask> = getWellnessTasks().toMutableStateList()

    /**
     * Public read-only access to our [_tasks] dataset of [WellnessTask] instances.
     */
    val tasks: List<WellnessTask>
        get() = _tasks

    /**
     * Removes its [WellnessTask] parameter [item] from the dataset of [WellnessTask] instances held
     * in [_tasks].
     *
     * @param item the [WellnessTask] to remove from our [_tasks] dataset.
     */
    fun remove(item: WellnessTask) {
        _tasks.remove(item)
    }

    /**
     * Finds the [WellnessTask] in our [tasks] dataset with the same [WellnessTask.id] as our
     * [WellnessTask] parameter [item] and sets its [WellnessTask.checked] property to our [Boolean]
     * parameter [checked].
     *
     * @param item the [WellnessTask] whose [WellnessTask.checked] property we want to change.
     * @param checked the value we want to set the [WellnessTask.checked] property of [item] to.
     */
    fun changeTaskChecked(item: WellnessTask, checked: Boolean): Unit? =
        tasks.find { it.id == item.id }?.let { task: WellnessTask ->
            task.checked = checked
        }

}

/**
 * Generates a [List] of 30 [WellnessTask] objects, each with a stable unique [WellnessTask.id].
 *
 * @return a [List] of [WellnessTask] objects.
 */
private fun getWellnessTasks() = List(30) { i ->
    WellnessTask(id = i, label = "Task # $i")
}
