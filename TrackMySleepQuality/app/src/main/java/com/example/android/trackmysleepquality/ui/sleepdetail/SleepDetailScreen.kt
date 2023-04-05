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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.ui.theme.TrackMySleepQualityTheme

/**
 * TODO: Add kdoc
 */
@Composable
fun SleepDetailScreen() {
    TrackMySleepQualityTheme {
        Column(
            modifier = Modifier.wrapContentSize()
        ) {
            Image(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .size(64.dp),
                painter = painterResource(id = R.drawable.ic_sleep_5),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally),
                text = "Work in progress"
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
@Preview
@Composable
fun SleepDetailScreenPreview() {
    SleepDetailScreen()
}