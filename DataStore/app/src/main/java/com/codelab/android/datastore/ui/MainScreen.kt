package com.codelab.android.datastore.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.codelab.android.datastore.R
import com.codelab.android.datastore.UserPreferences
import com.codelab.android.datastore.data.Task
import com.codelab.android.datastore.data.TasksRepository
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.coroutines.flow.Flow

/**
 * The main Screen of our app. We start by initializing our [List] of [Task] variable `val taskList`
 * using the [Flow.collectAsState] extension method on our [Flow] of [List] of [Task] parameter
 * [tasks]. Then we initialize and remember our [FilterManager] variable `val filterManager` to a
 * [MutableState] wrapped [FilterManager] whose initial value is constructed from our [TasksViewModel]
 * parameter [viewModel] and our [TasksUiModel] parameter [tasksUiModel] (this is keyed on [tasksUiModel]
 * so that it will be recalculated if [tasksUiModel] changes). Then our root Composable is a [Scaffold]
 * whose `bottomBar` argument is an [OptionsBar] whose `fm` argument is our `filterManager` variable.
 * The `content` of the [Scaffold] is a [LazyColumn] whose `modifier` argument is a [Modifier.padding]
 * which adds the [PaddingValues] passed to the `content` lambda to all sides of its `content`. The
 * `content` of the [LazyColumn] is an `items` whose `count` argument is the [List.size] of `taskList`
 * and whose `itemContent` lambda argument calls the [TaskComposer] Composable with its `task` argument
 * the [Task] at the `index` position passed the lambda in the `taskList` [List] of [Task] variable.
 *
 * @param viewModel the [TasksViewModel] to use to communicate events to the business logic.
 * @param tasksUiModel the [TasksUiModel] containing the [List] of [Task] objects created from the
 * [TasksRepository.tasks] field [Flow] of our apps [TasksRepository], as well as the current value
 * of the [UserPreferences.showCompleted_] and [UserPreferences.sortOrder_] preferences.
 * @param tasks a [Flow] of a [List] of [Task] objects that our caller used to `emit` the
 * [TasksUiModel.tasks] field so that we can use the [Flow.collectAsState] extension method to
 * convert it to a [State] to make sure re-composition occurs when the [List] changes (an admittedly
 * circuitous approach to the problem).
 */
@Composable
fun MainScreen(
    viewModel: TasksViewModel,
    tasksUiModel: TasksUiModel,
    tasks: Flow<List<Task>>
) {
    val taskList: List<Task> = tasks.collectAsState(initial = listOf()).value
    val filterManager by remember(tasksUiModel) {
        mutableStateOf(FilterManager(viewModel, tasksUiModel))
    }

    Scaffold(
        bottomBar = { OptionsBar(fm = filterManager) }
    ) { paddingValues: PaddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(count = taskList.size) { index: Int ->
                TaskComposer(task = taskList[index])
            }
        }
    }
}

/**
 * The [BottomAppBar] of the [Scaffold] used by [MainScreen]
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OptionsBar(
    fm: FilterManager
) {
    var showCompletedChecked by remember {
        mutableStateOf(fm.showCompleted())
    }
    Log.i("ShowCompleted", "Initial value of checked is: $showCompletedChecked")
    var prioritySelected by remember {
        mutableStateOf(fm.priority())
    }
    Log.i("ProrityChip", "Initial value of selected is: $prioritySelected")
    var deadlineSelected by remember {
        mutableStateOf(fm.deadline())
    }
    Log.i("DeadlineChip", "Initial value of selected is: $deadlineSelected")

    Column {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_filter_list_24),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Show completed tasks")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = showCompletedChecked,
                onCheckedChange = {
                    showCompletedChecked = !showCompletedChecked
                    fm.showCompletedClicked(showCompletedChecked)
                    Log.i("ShowCompleted", "New value of checked is: $showCompletedChecked")
                }
            )
        }
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baseline_reorder_24),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                onClick = {
                    prioritySelected = !prioritySelected
                    fm.priorityClicked(prioritySelected)
                    Log.i("ProrityChip", "New value of selected is: $prioritySelected")
                },
                selected = prioritySelected
            ) {
                Text(text = "Priority")
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                onClick = {
                    deadlineSelected = !deadlineSelected
                    fm.deadlineClicked(deadlineSelected)
                    Log.i("DeadlineChip", "New value of selected is: $deadlineSelected")
                },
                selected = deadlineSelected
            ) {
                Text(text = "Deadline")
            }
        }
    }
}