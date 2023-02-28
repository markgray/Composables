package com.codelab.android.datastore.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.codelab.android.datastore.data.Task
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.coroutines.flow.Flow

/**
 * The main Screen of our app
 */
@Composable
fun MainScreen(tasks: Flow<List<Task>>) {
    val taskList: List<Task> = tasks.collectAsState(initial = listOf()).value
    Scaffold(
        bottomBar = { OptionsBar() }
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
@Composable
fun OptionsBar() {

}