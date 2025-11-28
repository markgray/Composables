@file:Suppress("UnusedImport")

package com.example.android.trackmysleepquality

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.ui.sleepdetail.SleepDetailScreen
import com.example.android.trackmysleepquality.ui.sleepdetail.fakeSleepNight
import com.example.android.trackmysleepquality.ui.sleepquality.SleepQualityScreen
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepNightItem
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerScreen
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerViewModel
import com.example.android.trackmysleepquality.ui.sleeptracker.SleepTrackerViewModelFactory
import com.example.android.trackmysleepquality.ui.theme.TrackMySleepQualityTheme

/**
 * This is the main Activity of the app.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to
     * edge display, then we call our super's implementation of `onCreate`. We initialize our
     * [Application] variable `val application` to the application that owns this activity.
     * We initialize our [SleepDatabaseDao] variable `val dataSource` to the
     * [SleepDatabase.sleepDatabaseDao] field of the singleton [SleepDatabase] instance returned by
     * the [SleepDatabase.getInstance] method when passed our [Application] variable `application`.
     * We initialize our [SleepTrackerViewModelFactory] variable `val viewModelFactory` to a
     * new instance constructed to use `dataSource` and `application` when asked to build a
     * [SleepTrackerViewModel], and then we initialize our [SleepTrackerViewModel] variable
     * `val sleepTrackerViewModel` by calling [ViewModelProvider] with `viewModelFactory` as its
     * `factory` argument and `this` as its [ViewModelStoreOwner]. We initialize our [MutableState]
     * wrapped [List] of [SleepNight] variable `var sleepNightList` by the [mutableStateOf] method.
     * Then we add an observer to the [SleepTrackerViewModel.nights] field of `sleepTrackerViewModel`
     * that sets `sleepNightList` to the field whenever it changes value.
     *
     * We then call the [setContent] method to have it Compose our [TrackMySleepQualityTheme] custom
     * [MaterialTheme] wrapped composable into our activity. In that Composable block we first
     * initialize and remember our [MutableState] wrapped [Boolean] variables `var showDetail`, and
     * `var showQuality` to `false` (when `true` they cause the Composables [SleepDetailScreen] and
     * [SleepQualityScreen] respectively to be composed over our [SleepTrackerScreen] main screen).
     * Then our root Composable is a [Surface] whose `modifier` argument is a
     * [Modifier.safeDrawingPadding] to add padding accommodate the safe drawing insets, chained to
     * a [Modifier.fillMaxSize] to cause it to occupy its entire incoming constraints, and whose
     * `color` argument sets its background color to the [Colors.background] color of
     * [MaterialTheme.colors] (in our case this is the default [Color.White]). The `content` of the
     * [Surface] is our [SleepTrackerScreen] with its `viewModel` argument our [SleepTrackerViewModel]
     * variable `sleepTrackerViewModel`, its `sleepNightList` argument our [List] of [SleepNight]
     * variable `sleepNightList`. Its `onSleepNightClicked` argument is a lambda which sets
     * `showDetail` to `true` (which will cause the [SleepDetailScreen] to be composed atop the
     * [SleepTrackerScreen]) and set `sleepNightClicked` to the [SleepNight] passed the lambda by
     * the [SleepNightItem] that was clicked in the [LazyVerticalGrid] of [SleepTrackerScreen],
     * [SleepDetailScreen] will display the details of this [SleepNight]). Its `onStartClicked`
     * argument is a lambda that calls the [SleepTrackerViewModel.initializeTonight] method of
     * `sleepTrackerViewModel` to initialize the [SleepTrackerViewModel.tonight] field of
     * `sleepTrackerViewModel` with the newest [SleepNight] entry in the database thus starting the
     * "recording" of tonight's sleep duration. Its `onStopClicked` argument is a lambda which sets
     * our [Boolean] variable `showQuality` to `true` causing the [SleepQualityScreen] to be
     * composed atop the [SleepTrackerScreen] to allow the user to assign a quality rating to the
     * [SleepNight] whose duration has just been recorded.
     *
     * Following the call to [SleepTrackerScreen] are two if statements. The first checks if
     * `showDetail` is `true` and if it is will compose [SleepDetailScreen] into the UI with its
     * `sleepNight` argument our [SleepNight] variable `sleepNightClicked`, and with its
     * `onCloseClicked` argument a lambda which sets `showDetail` back to `false`. The second checks
     * if `showQuality` is `true` and if it is will compose [SleepQualityScreen] into the UI with its
     * `onQualityClicked` argument a lambda which sets `showQuality` back to `false` then calls the
     * [SleepTrackerViewModel.onSetSleepQuality] with the [Int] "quality" rating passed the lambda
     * to have `sleepTrackerViewModel` update the quality of the [SleepNight] which was just recorded.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val application: Application = requireNotNull(this).application

        // Create an instance of the ViewModel Factory.
        val dataSource: SleepDatabaseDao = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        val sleepTrackerViewModel =
            ViewModelProvider(this, viewModelFactory)[SleepTrackerViewModel::class.java]

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
                    modifier = Modifier.safeDrawingPadding().fillMaxSize(),
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
