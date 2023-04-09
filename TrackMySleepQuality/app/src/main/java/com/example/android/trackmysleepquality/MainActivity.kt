package com.example.android.trackmysleepquality

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerScreen
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerViewModel
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerViewModelFactory
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
        val application: Application = requireNotNull(this).application

        // Create an instance of the ViewModel Factory.
        val dataSource: SleepDatabaseDao = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        val sleepTrackerViewModel =
            ViewModelProvider(
                this, viewModelFactory)[SleepTrackerViewModel::class.java]

        setContent {
            TrackMySleepQualityTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SleepTrackerScreen(
                        viewModel = sleepTrackerViewModel,
                        onSleepNightClicked = { sleepNight: SleepNight ->
                            Log.i("MainActivity", "SleepNight clicked: $sleepNight")
                        }
                    )
                }
            }
        }
    }
}
