package com.codelab.android.datastore.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.codelab.android.datastore.data.Task
import com.google.android.material.bottomappbar.BottomAppBar

/**
 * The main Screen of our app
 */
@Composable
fun MainScreen(tasks: List<Task>) {
    Scaffold(
        bottomBar = { OptionsBar() }
    ) { paddingValues: PaddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(tasks) {
                Text(text = "Task")
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