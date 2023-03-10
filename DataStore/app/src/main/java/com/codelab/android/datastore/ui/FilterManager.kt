package com.codelab.android.datastore.ui

import com.codelab.android.datastore.UserPreferences

/**
 * This is a helper class for
 */
class FilterManager(
    /**
     * TODO: Add kdoc
     */
    private val viewModel: TasksViewModel,
    /**
     * TODO: Add kdoc
     */
    private var tasksUiModel: TasksUiModel
) {
    /**
     * TODO: Add kdoc
     */
    fun priority(): Boolean {
        return when (tasksUiModel.sortOrder) {
            UserPreferences.SortOrder.BY_PRIORITY -> true
            UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY -> true
            else -> false
        }
    }

    /**
     * TODO: Add kdoc
     */
    fun deadline(): Boolean {
        return when (tasksUiModel.sortOrder) {
            UserPreferences.SortOrder.BY_DEADLINE -> true
            UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY -> true
            else -> false
        }
    }

    /**
     * TODO: Add kdoc
     */
    fun showCompleted(): Boolean {
        return tasksUiModel.showCompleted
    }

    /**
     * TODO: Add kdoc
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