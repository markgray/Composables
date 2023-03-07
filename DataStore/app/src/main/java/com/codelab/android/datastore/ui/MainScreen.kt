package com.codelab.android.datastore.ui

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.codelab.android.datastore.R
import com.codelab.android.datastore.data.Task
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.coroutines.flow.Flow

/**
 * The main Screen of our app
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
            items(taskList.size) {
                TaskComposer(taskList[it])
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
    val showCompletedChecked = fm.showCompleted()
    val prioritySelected = fm.priority()
    val deadlineSelected = fm.deadline()

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
                    fm.showCompletedClicked()
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
                    fm.priorityClicked()
                },
                selected = prioritySelected
            ) {
                Text(text = "Priority")
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                onClick = {
                   fm.deadlineClicked()
                },
                selected = deadlineSelected
            ) {
                Text(text = "Deadline")
            }
        }
    }
}