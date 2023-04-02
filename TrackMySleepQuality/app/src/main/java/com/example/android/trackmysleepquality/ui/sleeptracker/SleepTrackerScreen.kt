package com.example.android.trackmysleepquality.ui.sleeptracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SleepTrackerScreen() {
    Scaffold(
        topBar = { StartStopBar() },
        bottomBar = { ClearBar() }
    ) { paddingValues: PaddingValues ->

        LazyColumn(modifier = Modifier.padding(paddingValues))
        {

        }

    }
}

/**
 * TODO: Add kdoc
 */
@Composable
fun StartStopBar() {
    Row(modifier = Modifier.fillMaxWidth().wrapContentSize(align = Alignment.Center)) {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Start")
        }
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Start")
        }
    }
}

@Composable
fun ClearBar() {
    Box(modifier = Modifier.fillMaxWidth(1f).wrapContentSize(align = Alignment.Center)) {
        Button(
            modifier = Modifier.wrapContentSize(align = Alignment.Center),
            onClick = { /*TODO*/ }
        ) {
            Text(text = "Clear")
        }
    }
}