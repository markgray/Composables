package com.codelab.android.datastore.ui

import androidx.compose.material.FilterChip
import androidx.compose.material.Switch
import com.codelab.android.datastore.UserPreferences
import com.codelab.android.datastore.UserPreferences.SortOrder
import com.codelab.android.datastore.data.Task

/**
 * This is a helper class for accessing and modifying the values of the filter settings stored in
 * the [UserPreferences] of the app. It is used both for setting the state of the widgets in the UI
 * used by the user to toggle the filter settings, and to communicate to the [TasksViewModel] when
 * the user clicks on one of the widgets, thus cutting down the number of parameters needed for this
 * to just a single instance of [FilterManager].
 *
 * @param viewModel The [TasksViewModel] to use to communicate events to the business logic of the app.
 * @param tasksUiModel The [TasksUiModel] which contains the current setting of the [UserPreferences]
 * filter preferences in its [TasksUiModel.showCompleted] and [TasksUiModel.sortOrder] fields. It
 * also contains a reference to the latest [List] of [Task] produced given the current filter
 * settings in its [TasksUiModel.tasks] field, but we ignore that.
 */
class FilterManager(
    private val viewModel: TasksViewModel,
    private var tasksUiModel: TasksUiModel
) {
    /**
     * Called to retrieve the current state of the "Sort by Priority" preference from the [TasksUiModel]
     * we were constructed with. If the [TasksUiModel.sortOrder] field is either [SortOrder.BY_PRIORITY]
     * or [SortOrder.BY_DEADLINE_AND_PRIORITY] we return `true`, otherwise we return `false`. This is
     * used as the initial value of the "remembered" `MutableState` `prioritySelected` variable of the
     * [OptionsBar] that is used as the `selected` argument of the "Priority" [FilterChip].
     */
    fun priority(): Boolean {
        return when (tasksUiModel.sortOrder) {
            SortOrder.BY_PRIORITY -> true
            SortOrder.BY_DEADLINE_AND_PRIORITY -> true
            else -> false
        }
    }

    /**
     * Called to retrieve the current state of the "Sort by Deadline" preference from the [TasksUiModel]
     * we were constructed with. If the [TasksUiModel.sortOrder] field is either [SortOrder.BY_DEADLINE]
     * or [SortOrder.BY_DEADLINE_AND_PRIORITY] we return `true`, otherwise we return `false`. This is
     * used as the initial value of the "remembered" `MutableState` `deadlineSelected` variable of the
     * [OptionsBar] that is used as the `selected` argument of the "Deadline" [FilterChip].
     */
    fun deadline(): Boolean {
        return when (tasksUiModel.sortOrder) {
            SortOrder.BY_DEADLINE -> true
            SortOrder.BY_DEADLINE_AND_PRIORITY -> true
            else -> false
        }
    }

    /**
     * Called to retrieve the current state of the "Show completed tasks" preference from the
     * [TasksUiModel] we were constructed with. We just return the value of the
     * [TasksUiModel.showCompleted] field of our [tasksUiModel] field. This is used as the initial
     * value of the "remembered" `MutableState` `showCompletedChecked` variable of the
     * [OptionsBar] that is used as the `checked` argument of the "Show completed tasks" [Switch].
     */
    fun showCompleted(): Boolean {
        return tasksUiModel.showCompleted
    }

    /**
     * Called to update the value of the "Sort by Priority" preference. It does this by calling the
     * [TasksViewModel.enableSortByPriority] method of our [viewModel] field with its [enable]
     * parameter. It is called in the `onClick` lambda argument of the "Priority" [FilterChip] in
     * the [OptionsBar] when the user clicks the [FilterChip] with the new "toggled" value of the
     * `prioritySelected` "remembered" `MutableState` variable.
     *
     * @param enable the new value for the "Sort by Priority" preference.
     */
    fun priorityClicked(enable: Boolean) {
        viewModel.enableSortByPriority(enable)
    }

    /**
     * TODO: Add kdoc
     */
    fun deadlineClicked(enable: Boolean) {
        viewModel.enableSortByDeadline(enable)
    }

    /**
     * TODO: Add kdoc
     */
    fun showCompletedClicked(enable: Boolean) {
        viewModel.showCompletedTasks(enable)
    }
}