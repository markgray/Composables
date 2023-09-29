package com.example.android.trackmysleepquality.ui.sleepdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.MainActivity
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepNightItem
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerScreen
import com.example.android.trackmysleepquality.ui.theme.TrackMySleepQualityTheme
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * This Composable is run when the user clicks one of the [SleepNightItem] in the [LazyVerticalGrid]
 * of the [SleepTrackerScreen] with the [SleepNight] that the [SleepNightItem] is displaying in our
 * [sleepNight] parameter. Its purpose is just to format and display all the details contained in
 * the [SleepNight] it is called with. It is composed into the UI in the call to `setContent` in the
 * `onCreate` override of [MainActivity] when the [MutableState] wrapped [Boolean] variable
 * `showDetail` is `true`, and that variable is set to `true` by the `onSleepNightClicked` lambda
 * argument of the [SleepTrackerScreen] which is called by the `onClicked` argument of every
 * [SleepNightItem].
 *
 * Wrapped in our [TrackMySleepQualityTheme] custom [MaterialTheme] our root Composable is a [Column]
 * whose `modifier` argument is a [Modifier.fillMaxSize] to make it occupy the entire incoming
 * constraints, with a [Modifier.background] chained to that to set the background color to
 * [Color.White]. The `content` of the [Column] consists of:
 *  - an [Image] displaying the drawable whose resource ID the [selectSleepImageId] method determines
 *  to be the one that is appropriate for the [SleepNight.sleepQuality] field of our [sleepNight]
 *  parameter. Its `modifier` argument is a `ColumnScope` `Modifier.align` that aligns the [Image]
 *  using [Alignment.CenterHorizontally], with a [Modifier.size] chained to that which sets the size
 *  of the [Image] to 64.dp.
 *  - a [Text] displaying the [String] with the resource ID that the [selectSleepQualityStringId]
 *  method determines to be the one that is appropriate for the [SleepNight.sleepQuality] field of
 *  our [sleepNight] parameter. Its `modifier` argument is a `ColumnScope` `Modifier.align` that
 *  aligns the [Text] using [Alignment.CenterHorizontally].
 *  - a [Text] displaying the [String] that the [convertDurationToFormatted] method formats using the
 *  [SleepNight.startTimeMilli] and [SleepNight.endTimeMilli] fields of our [sleepNight] parameter.
 *  It consists of a [String] displaying the approximate duration of sleep, followed by [String]
 *  for the day of the week that the [SleepNight] was constructed on.
 *  - a [Button] labeled "Close" whose `onClick` argument calls our [onCloseClicked] lambda parameter.
 *
 * @param sleepNight the [SleepNight] whose details we are to display.
 * @param onCloseClicked a lambda that our "Close" [Button] should call when the user clicks on the
 * [Button]. The lambda argument passed to us in the `onCreate` override of [MainActivity] sets the
 * its [MutableState] wrapped [Boolean] variable `showDetail` to `false` causing us to no longer be
 * in the composition the next recomposition pass.
 */
@Composable
fun SleepDetailScreen(
    sleepNight: SleepNight,
    onCloseClicked: () -> Unit
) {
    TrackMySleepQualityTheme {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White)
        ) {
            Image(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .size(64.dp),
                painter = painterResource(id = selectSleepImageId(sleepNight)),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally),
                text = stringResource(selectSleepQualityStringId(sleepNight))
            )
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally),
                text = convertDurationToFormatted(
                    sleepNight.startTimeMilli,
                    sleepNight.endTimeMilli
                )
            )
            Button(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally),
                onClick = { onCloseClicked() }
            ) {
                Text(text = "Close")
            }
        }
    }
}

/**
 * This method returns the resource ID of a drawable that is appropriate to represent the value of
 * the [SleepNight.sleepQuality] field of its [sleepNight] parameter.
 *
 * @param sleepNight the [SleepNight] whose [SleepNight.sleepQuality] field we are to use to select
 * an appropriate drawable to represent it.
 * @return one of seven resource IDs depending on the value of the [SleepNight.sleepQuality] field
 * of our [sleepNight] parameter.
 */
fun selectSleepImageId(sleepNight: SleepNight?): Int {
    return when (sleepNight?.sleepQuality) {
        0 -> R.drawable.ic_sleep_0
        1 -> R.drawable.ic_sleep_1
        2 -> R.drawable.ic_sleep_2
        3 -> R.drawable.ic_sleep_3
        4 -> R.drawable.ic_sleep_4
        5 -> R.drawable.ic_sleep_5
        else -> R.drawable.ic_sleep_active
    }

}

/**
 * This method returns the resource ID of a [String] that is appropriate to describe the value of
 * the [SleepNight.sleepQuality] field of its [sleepNight] parameter.
 *
 * @param sleepNight the [SleepNight] whose [SleepNight.sleepQuality] field we are to use to select
 * an appropriate [String] resource ID to describe it.
 * @return one of seven resource IDs depending on the value of the [SleepNight.sleepQuality] field
 * of our [sleepNight] parameter.
 */
fun selectSleepQualityStringId(sleepNight: SleepNight?): Int {
    return when (sleepNight?.sleepQuality) {
        -1 -> R.string.dash_dash
        0 -> R.string.zero_very_bad
        1 -> R.string.one_poor
        2 -> R.string.two_soso
        3 -> R.string.three_ok
        4 -> R.string.four_pretty_good
        5 -> R.string.five_excellent
        else  -> R.string.zero_very_bad
    }
}

/**
 * Number of milliseconds in one minute
 */
@Suppress("PrivatePropertyName") // It is a constant of sorts
private val ONE_MINUTE_MILLIS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES)

/**
 * Number of milliseconds in one hour
 */
@Suppress("PrivatePropertyName") // It is a constant of sorts
private val ONE_HOUR_MILLIS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)

/**
 * Convert a duration to a formatted string for display.
 *
 * Examples:
 *
 *     6 seconds on Wednesday
 *     2 minutes on Monday
 *     40 hours on Thursday
 *
 * We initialize our [Long] variable `val durationMilli` to our parameter [endTimeMilli] minus our
 * parameter [startTimeMilli]. We initialize our [String] variable `val weekdayString` to the result
 * of formatting [startTimeMilli] according to to a [SimpleDateFormat] constructed for the pattern
 * "EEEE" (Day name in week) for the default locale. Then we branch on the value of `durationMilli`:
 *
 *  - less than [ONE_MINUTE_MILLIS] we initialize our variable `val seconds` to the result of
 *  converting `durationMilli` to seconds and set `val returnString` to the string formatted using
 *  the format [R.string.seconds_length] ("%d seconds on %s") for `seconds` and `weekdayString`.
 *
 *  - less than [ONE_HOUR_MILLIS] we initialize our variable `val minutes` to the result of
 *  converting `durationMilli` to minutes and set `val returnString` to the string formatted using
 *  the format [R.string.minutes_length] ("%d minutes on %s") for `minutes` and `weekdayString`.
 *
 *  - For larger values we initialize our variable `val hours` to the result of converting
 *  `durationMilli` to hours and set `val returnString` to the string formatted using the format
 *  [R.string.hours_length] ("%d hours on %s") for `hours` and `weekdayString`.
 *
 * Finally we return `returnString` to the caller. It is used as the `text` displayed in one of the
 * [Text] widgets of [SleepDetailScreen].
 *
 * @param startTimeMilli the start of the interval
 * @param endTimeMilli the end of the interval
 * @return a [String] which displays the duration of sleep in idiomatic way.
 */
@Composable
fun convertDurationToFormatted(startTimeMilli: Long, endTimeMilli: Long): String {
    val durationMilli = endTimeMilli - startTimeMilli
    val weekdayString = SimpleDateFormat("EEEE", Locale.getDefault()).format(startTimeMilli)
    val returnString: String = when {
        durationMilli < ONE_MINUTE_MILLIS -> {
            val seconds = TimeUnit.SECONDS.convert(durationMilli, TimeUnit.MILLISECONDS)
            stringResource(R.string.seconds_length, seconds, weekdayString)
        }
        durationMilli < ONE_HOUR_MILLIS -> {
            val minutes = TimeUnit.MINUTES.convert(durationMilli, TimeUnit.MILLISECONDS)
            stringResource(R.string.minutes_length, minutes, weekdayString)
        }
        else -> {
            val hours = TimeUnit.HOURS.convert(durationMilli, TimeUnit.MILLISECONDS)
            stringResource(R.string.hours_length, hours, weekdayString)
        }
    }
    return returnString
}

/**
 * Creates and returns a "fake" [SleepNight] for use in our Preview.
 *
 * @return a "fake" [SleepNight].
 */
fun fakeSleepNight(): SleepNight {
    val sleepNight = SleepNight()
    sleepNight.nightId = 0
    sleepNight.endTimeMilli = sleepNight.startTimeMilli + 36_500_000L
    sleepNight.sleepQuality = (0..5).random()
    return sleepNight
}

/**
 * Preview of our [SleepDetailScreen].
 */
@Preview
@Composable
fun SleepDetailScreenPreview() {
    SleepDetailScreen(
        sleepNight = fakeSleepNight(),
        onCloseClicked = {}
    )
}