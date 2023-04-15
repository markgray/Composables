package com.example.android.trackmysleepquality.ui.sleeptracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
//import com.example.android.trackmysleepquality.ui.sleepdetail.fakeSleepNight
import com.example.android.trackmysleepquality.ui.sleepdetail.selectSleepImageId
import com.example.android.trackmysleepquality.ui.sleepdetail.selectSleepQualityStringId
import com.example.android.trackmysleepquality.ui.sleepquality.SleepQualityScreen
import com.example.android.trackmysleepquality.ui.theme.Purple500

/**
 * This is the main screen of our app. The root Composable is a [Scaffold] whose `topBar` argument is
 * a [StartStopBar] whose `onStartClicked` argument (starts the "clock" for the night's sleep) is a
 * lambda which calls the [SleepTrackerViewModel.onStart] method of our [viewModel] parameter (which
 * constructs a new [SleepNight] and inserts it in our database) followed by a call to our parameter
 * [onStartClicked] (our caller calls the [SleepTrackerViewModel.initializeTonight] method of the
 * [viewModel] in the lambda it passes us), and the `onStopClicked` argument (stops the "clock" for
 * the night's sleep) of the [StartStopBar] is a lambda which calls the [SleepTrackerViewModel.onStop]
 * method of our [viewModel] parameter (sets the [SleepNight.endTimeMilli] field of the nights sleep
 * to the current time and updates the entry in the database) followed by a call to our parameter
 * [onStopClicked] (our caller sets its [MutableState] wrapped [Boolean] variable `showQuality` which
 * causes the [SleepQualityScreen] to be composed over us). The `bottomBar` argument of the [Scaffold]
 * is a [ClearBar] whose `onClearClicked` argument is a lambda which calls the
 * [SleepTrackerViewModel.onClear] method of our [viewModel] parameter (which in turn calls the
 * [SleepDatabaseDao.clear] method of our database to delete all values from the table holding our
 * [SleepNight] data). The `content` of the [Scaffold] is a [LazyVerticalGrid] with the first `item`
 * in it having a `span` argument is a lambda which uses [GridItemSpan] whose `currentLineSpan`
 * argument is `maxLineSpan` to have it take up the entire width of the [LazyVerticalGrid] and the
 * `content` of the `item` is a [Text] displaying the string "Sleep Results" using 30.sp as the
 * `fontSize` and a `modifier` argument of [Modifier.fillMaxWidth] causing it to fill the entire
 * incoming width constraint, with a [Modifier.padding] to add 8.dp padding to all sides of the
 * [Text], followed by a [Modifier.background] to set the background color to [Purple500], and a
 * [Modifier.wrapContentSize] whose `align` argument of [Alignment.Center] aligns its `text` content
 * in its center.
 */
@Composable
fun SleepTrackerScreen(
    viewModel: SleepTrackerViewModel,
    sleepNightList: List<SleepNight>,
    onSleepNightClicked: (SleepNight) -> Unit,
    onStartClicked: () -> Unit,
    onStopClicked: () -> Unit
) {

    Scaffold(
        topBar = {
            StartStopBar(
                onStartClicked = {
                    viewModel.onStart()
                    onStartClicked()
                },
                onStopClicked = {
                    viewModel.onStop()
                    onStopClicked()
                }
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
            items(sleepNightList.size) { index ->
                SleepNightItem(
                    sleepNight = sleepNightList[index],
                    onClicked = onSleepNightClicked
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

/**
 * TODO: Add kdoc
 */
@Composable
fun SleepNightItem(
    sleepNight: SleepNight,
    onClicked: (SleepNight) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .clickable { onClicked(sleepNight) }
    ) {
        Image(
            modifier = Modifier.size(64.dp),
            painter = painterResource(id = selectSleepImageId(sleepNight)),
            contentDescription = null
        )
        Text(
            modifier = Modifier.wrapContentSize(Alignment.Center),
            text = stringResource(id = selectSleepQualityStringId(sleepNight))
        )
    }
}