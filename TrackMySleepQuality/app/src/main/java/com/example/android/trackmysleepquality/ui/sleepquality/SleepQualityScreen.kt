package com.example.android.trackmysleepquality.ui.sleepquality

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.trackmysleepquality.MainActivity
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.ui.theme.TrackMySleepQualityTheme
import com.example.android.trackmysleepquality.ui.sleeptracker.StartStopBar
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerScreen

/**
 * This screen allows the user to "rate" the quality of sleep whose duration he just "recorded". It
 * is run when the user clicks the "Stop" [Button] in the [StartStopBar] used as the `topBar` argument
 * of the [Scaffold] in [SleepTrackerScreen]. It is composed into the UI in the call to `setContent`
 * in the `onCreate` override of [MainActivity] when the [MutableState] wrapped [Boolean] variable
 * `showQuality` is `true`, and `showQuality` is set to `true` by the lambda passed as the
 * `onStopClicked` argument of [SleepTrackerScreen], and it is set back to `false` in the lambda
 * that [MainActivity] passes us as our `onQualityClicked` parameter.
 *
 * Our root Composable is a [Column] whose `modifier` argument is a [Modifier.fillMaxWidth] that
 * causes it to occupy the entire incoming width constraint, with a [Modifier.background] chained
 * to that to set the background color to [Color.White].
 */
@Composable
fun SleepQualityScreen(onQualityClicked: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        Text(
            text = "How was your sleep?",
            fontSize = 20.sp
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.size(64.dp).weight(1f).clickable { onQualityClicked(0) },
                painter = painterResource(id = R.drawable.ic_sleep_0),
                contentDescription = null
            )
            Image(
                modifier = Modifier.size(64.dp).weight(1f).clickable { onQualityClicked(1) },
                painter = painterResource(id = R.drawable.ic_sleep_1),
                contentDescription = null
            )
            Image(
                modifier = Modifier.size(64.dp).weight(1f).clickable { onQualityClicked(2) },
                painter = painterResource(id = R.drawable.ic_sleep_2),
                contentDescription = null
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.size(64.dp).weight(1f).clickable { onQualityClicked(3) },
                painter = painterResource(id = R.drawable.ic_sleep_3),
                contentDescription = null
            )
            Image(
                modifier = Modifier.size(64.dp).weight(1f).clickable { onQualityClicked(4) },
                painter = painterResource(id = R.drawable.ic_sleep_4),
                contentDescription = null
            )
            Image(
                modifier = Modifier.size(64.dp).weight(1f).clickable { onQualityClicked(5) },
                painter = painterResource(id = R.drawable.ic_sleep_5),
                contentDescription = null
            )
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Preview
@Composable
fun SleepQualityScreenPreview() {
    TrackMySleepQualityTheme {
        SleepQualityScreen(onQualityClicked = {})
    }
}
