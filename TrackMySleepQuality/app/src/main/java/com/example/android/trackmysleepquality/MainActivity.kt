package com.example.android.trackmysleepquality

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerScreen
import com.example.android.trackmysleepquality.ui.theme.TrackMySleepQualityTheme

/**
 * TODO: Add kdoc
 */
class MainActivity : ComponentActivity() {
    /**
     * TODO: Add kdoc
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrackMySleepQualityTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SleepTrackerScreen()
                }
            }
        }
    }
}

/**
 * TODO: Add kdoc
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TrackMySleepQualityTheme {
        SleepTrackerScreen()
    }
}