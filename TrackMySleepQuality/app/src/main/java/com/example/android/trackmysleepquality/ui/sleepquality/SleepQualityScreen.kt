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
 * to that to set the background color to [Color.White]. The `content` of the [Column] consists of
 * two [Row] Composables with both using a `modifier` argument of [Modifier.fillMaxWidth]. The
 * `content` of the first [Row] is:
 *  - an [Image] whose `painter` argument is a [painterResource] for the drawable with resource ID
 *  `R.drawable.ic_sleep_0`, and whose `modifier` argument is a [Modifier.size] that sets its size
 *  to 64.dp, to which is chained a `RowScope` `Modifier.weight` of 1f, followed by a
 *  [Modifier.clickable] which calls our [onQualityClicked] lambda argument with "0". This is our
 *  "Very Bad" sleep quality.
 *  - an [Image] whose `painter` argument is a [painterResource] for the drawable with resource ID
 *  `R.drawable.ic_sleep_1`, and whose `modifier` argument is a [Modifier.size] that sets its size
 *  to 64.dp, to which is chained a `RowScope` `Modifier.weight` of 1f, followed by a
 *  [Modifier.clickable] which calls our [onQualityClicked] lambda argument with "1". This is our
 *  "Poor" sleep quality.
 *  - an [Image] whose `painter` argument is a [painterResource] for the drawable with resource ID
 *  `R.drawable.ic_sleep_2`, and whose `modifier` argument is a [Modifier.size] that sets its size
 *  to 64.dp, to which is chained a `RowScope` `Modifier.weight` of 1f, followed by a
 *  [Modifier.clickable] which calls our [onQualityClicked] lambda argument with "2". This is our
 *  "So-so" sleep quality.
 *
 * The `content of the second [Row] is:
 *  - an [Image] whose `painter` argument is a [painterResource] for the drawable with resource ID
 *  `R.drawable.ic_sleep_3`, and whose `modifier` argument is a [Modifier.size] that sets its size
 *  to 64.dp, to which is chained a `RowScope` `Modifier.weight` of 1f, followed by a
 *  [Modifier.clickable] which calls our [onQualityClicked] lambda argument with "3". This is our
 *  "OK" sleep quality.
 *  - an [Image] whose `painter` argument is a [painterResource] for the drawable with resource ID
 *  `R.drawable.ic_sleep_4`, and whose `modifier` argument is a [Modifier.size] that sets its size
 *  to 64.dp, to which is chained a `RowScope` `Modifier.weight` of 1f, followed by a
 *  [Modifier.clickable] which calls our [onQualityClicked] lambda argument with "4". This is our
 *  "Pretty good" sleep quality.
 *  - an [Image] whose `painter` argument is a [painterResource] for the drawable with resource ID
 *  `R.drawable.ic_sleep_5`, and whose `modifier` argument is a [Modifier.size] that sets its size
 *  to 64.dp, to which is chained a `RowScope` `Modifier.weight` of 1f, followed by a
 *  [Modifier.clickable] which calls our [onQualityClicked] lambda argument with "5". This is our
 *  "Excellent" sleep quality.
 *
 * @param onQualityClicked a lambda which each of the six [Image]s in our UI are configured to call
 * with an integer 0..5 that represents the sleep quality that the [Image] represents.
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
 * This is a preview of our [SleepQualityScreen] Composable.
 */
@Preview
@Composable
fun SleepQualityScreenPreview() {
    TrackMySleepQualityTheme {
        SleepQualityScreen(onQualityClicked = {})
    }
}
