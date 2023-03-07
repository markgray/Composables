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
    fun priorityClicked() {
        viewModel.enableSortByPriority(priority())
    }

    /**
     * TODO: Add kdoc
     */
    fun deadlineClicked() {
        viewModel.enableSortByDeadline(deadline())
    }

    /**
     * TODO: Add kdoc
     */
    fun showCompletedClicked() {
        viewModel.showCompletedTasks(showCompleted())
    }
}