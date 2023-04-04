package com.example.android.trackmysleepquality.ui.sleepquality

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.ui.theme.TrackMySleepQualityTheme

/**
 * TODO: Add kdoc
 */
@Composable
fun SleepQualityScreen(sleepNightKey: Long = 0L) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "How was your sleep?",
            fontSize = 20.sp
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.size(64.dp).weight(1f),
                painter = painterResource(id = R.drawable.ic_sleep_0),
                contentDescription = null
            )
            Image(
                modifier = Modifier.size(64.dp).weight(1f),
                painter = painterResource(id = R.drawable.ic_sleep_1),
                contentDescription = null
            )
            Image(
                modifier = Modifier.size(64.dp).weight(1f),
                painter = painterResource(id = R.drawable.ic_sleep_2),
                contentDescription = null
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.size(64.dp).weight(1f),
                painter = painterResource(id = R.drawable.ic_sleep_3),
                contentDescription = null
            )
            Image(
                modifier = Modifier.size(64.dp).weight(1f),
                painter = painterResource(id = R.drawable.ic_sleep_4),
                contentDescription = null
            )
            Image(
                modifier = Modifier.size(64.dp).weight(1f),
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
        SleepQualityScreen()
    }
}
