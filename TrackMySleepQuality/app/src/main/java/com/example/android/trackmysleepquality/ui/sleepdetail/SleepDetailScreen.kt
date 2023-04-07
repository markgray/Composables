package com.example.android.trackmysleepquality.ui.sleepdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.ui.theme.TrackMySleepQualityTheme

/**
 * TODO: Add kdoc
 */
@Composable
fun SleepDetailScreen(sleepNight: SleepNight) {
    TrackMySleepQualityTheme {
        Column(
            modifier = Modifier.wrapContentSize()
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
            Button(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Close")
            }
        }
    }
}

/**
 * TODO: Add kdoc
 */
fun selectSleepImageId(item: SleepNight?): Int {
    return when (item?.sleepQuality) {
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
 * TODO: Add kdoc
 */
fun selectSleepQualityStringId(item: SleepNight?): Int {
    return when (item?.sleepQuality) {
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
 * TODO: Add kdoc
 */
fun fakeSleepNight(): SleepNight {
    val sleepNight = SleepNight()
    sleepNight.nightId = 1
    sleepNight.startTimeMilli = sleepNight.startTimeMilli - 3600L
    sleepNight.sleepQuality = 4
    return sleepNight
}

/**
 * TODO: Add kdoc
 */
@Preview
@Composable
fun SleepDetailScreenPreview() {
    SleepDetailScreen(fakeSleepNight())
}