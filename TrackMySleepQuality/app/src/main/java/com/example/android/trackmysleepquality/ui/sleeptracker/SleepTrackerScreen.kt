package com.example.android.trackmysleepquality.ui.sleeptracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.ui.theme.Purple500

/**
 * TODO: Add kdoc
 */
@Composable
fun SleepTrackerScreen(
    viewModel: SleepTrackerViewModel
) {
    Scaffold(
        topBar = {
            StartStopBar(
                onStartClicked = { viewModel.onStart() },
                onStopClicked = { viewModel.onStop() }
            )
        },
        bottomBar = {
            ClearBar(
                onClearClicked = { viewModel.onClear() }
            )
        }
    ) { paddingValues: PaddingValues ->
        LazyVerticalGrid(
            modifier = Modifier
                .padding(paddingValues = paddingValues),
            columns = Fixed(count = 3)
        ) {
            item(
                span = { GridItemSpan(currentLineSpan = maxLineSpan) }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp)
                        .background(color = Purple500)
                        .wrapContentSize(align = Alignment.Center),
                    text = "Sleep Results",
                    fontSize = 30.sp
                )
            }
            item {
                Image(
                    modifier = Modifier.size(64.dp),
                    painter = painterResource(id = R.drawable.ic_sleep_0),
                    contentDescription = null
                )
            }
            item {
                Image(
                    modifier = Modifier.size(64.dp),
                    painter = painterResource(id = R.drawable.ic_sleep_0),
                    contentDescription = null
                )
            }
            item {
                Image(
                    modifier = Modifier.size(64.dp),
                    painter = painterResource(id = R.drawable.ic_sleep_0),
                    contentDescription = null
                )
            }
            item {
                Image(
                    modifier = Modifier.size(64.dp),
                    painter = painterResource(id = R.drawable.ic_sleep_0),
                    contentDescription = null
                )
            }
        }
    }
}


/**
 * TODO: Add kdoc
 */
@Composable
fun StartStopBar(
    modifier: Modifier = Modifier,
    onStartClicked: () -> Unit,
    onStopClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(align = Alignment.Center)
    ) {
        Button(onClick = { onStartClicked() }) {
            Text(text = "Start")
        }
        Spacer(modifier = modifier.width(8.dp))
        Button(onClick = { onStopClicked() }) {
            Text(text = "Stop")
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Composable
fun ClearBar(
    modifier: Modifier = Modifier,
    onClearClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(1f)
            .wrapContentSize(align = Alignment.Center)
    ) {
        Button(
            modifier = modifier.wrapContentSize(align = Alignment.Center),
            onClick = { onClearClicked() }
        ) {
            Text(text = "Clear")
        }
    }
}
