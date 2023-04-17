package com.example.android.trackmysleepquality

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.ui.sleepdetail.SleepDetailScreen
import com.example.android.trackmysleepquality.ui.sleepdetail.fakeSleepNight
import com.example.android.trackmysleepquality.ui.sleepquality.SleepQualityScreen
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerScreen
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerViewModel
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerViewModelFactory
import com.example.android.trackmysleepquality.ui.theme.TrackMySleepQualityTheme

/**
 * This is the main Activity of the app.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`.
     * We initialize our [Application] variable `val application` to the application that owns this
     * activity. We initialize our [SleepDatabaseDao] variable `val dataSource` to the
     * [SleepDatabase.sleepDatabaseDao] field of the singleton [SleepDatabase] instance returned by
     * the [SleepDatabase.getInstance] method when passed our [Application] variable `application`.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
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

        var sleepNightList by mutableStateOf(listOf<SleepNight>())

        sleepTrackerViewModel.nights.observe(this) {
            sleepNightList = it
        }
        setContent {
            TrackMySleepQualityTheme {
                var showDetail: Boolean by remember { mutableStateOf(false) }
                var showQuality: Boolean by remember { mutableStateOf(false) }
                var sleepNightClicked: SleepNight = fakeSleepNight()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    SleepTrackerScreen(
                        viewModel = sleepTrackerViewModel,
                        sleepNightList = sleepNightList,
                        onSleepNightClicked = { sleepNight: SleepNight ->
                            Log.i("MainActivity", "SleepNight clicked: $sleepNight")
                            showDetail = true
                            sleepNightClicked = sleepNight
                        },
                        onStartClicked = { sleepTrackerViewModel.initializeTonight() },
                        onStopClicked = { showQuality = true }
                    )
                    if (showDetail) {
                        SleepDetailScreen(
                            sleepNight = sleepNightClicked,
                            onCloseClicked = { showDetail = false }
                        )
                    }
                    if (showQuality) {
                        SleepQualityScreen(
                            onQualityClicked = { quality: Int ->
                                showQuality = false
                                sleepTrackerViewModel.onSetSleepQuality(quality)
                                Log.i("MainActivity", "Quality clicked: $quality")
                            }
                        )
                    }
                }
            }
        }
    }
}
