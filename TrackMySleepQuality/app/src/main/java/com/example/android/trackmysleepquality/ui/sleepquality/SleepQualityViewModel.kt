package com.example.android.trackmysleepquality.ui.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for SleepQualityFragment.
 *
 * @param sleepNightKey The `nightId` primary key of the [SleepNight] we are currently working on.
 * @param dataSource Handle to the [SleepDatabaseDao] to use to call its Room SQLite methods.
 */
class SleepQualityViewModel(
    private val sleepNightKey: Long = 0L,
    dataSource: SleepDatabaseDao
) : ViewModel() {
    /**
     * Hold a reference to `SleepDatabase` access via its [SleepDatabaseDao].
     */
    val database: SleepDatabaseDao = dataSource

    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel. Because we pass
     * it [viewModelJob], any coroutine started in this scope can be cancelled by calling the
     * `viewModelJob.cancel()` method.
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android. This is a sensible default because most coroutines started by
     * a [ViewModel] update the UI after performing some processing.
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * Variable that tells the fragment whether it should navigate to `SleepTrackerFragment`.
     *
     * This is `private` because we don't want to expose the ability to set [MutableLiveData] to
     * the `Fragment`
     */
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the `SleepTrackerFragment`
     */
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    /**
     * Cancels all coroutines when the ViewModel is cleared, to cleanup any pending work.
     * onCleared() gets called when the ViewModel is destroyed. First we call our super's
     * implementation of `onCleared`, then we call the `cancel` method of [viewModelJob]
     * to cancel all the co-routines we may have started.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Call this immediately after navigating to `SleepTrackerFragment`
     */
    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    /**
     * Called by the android:onClick attributes of each of the icons in our UI with the value of
     * sleep quality that the icon represents as our parameter [quality]. We set the sleep quality
     * and update the database, then navigate back to the `SleepTrackerFragment`. We launch a new
     * coroutine on the [CoroutineScope] of [uiScope] without blocking the current thread, then
     * start a suspending block using the [Dispatchers.IO] coroutine context, suspending until it
     * completes. The suspending block consists of a lambda which fetches the [SleepNight] whose
     * `nightId` PrimaryKey is [sleepNightKey] to our [SleepNight] variable `val tonight`, sets
     * its `sleepQuality` field to our parameter [quality], and then calls the `update` method of
     * [database] to update the value of `tonight` stored in the database. When the database
     * co-routine completes we set the value of our [_navigateToSleepTracker] field to *true*
     * which will cause the `Observer` of that field in our `SleepQualityFragment` to navigate
     * back to the `SleepTrackerFragment`.
     *
     * @param quality the sleep value that the icon in our UI represents.
     */
    fun onSetSleepQuality(quality: Int) {
        uiScope.launch {
            // IO is a thread pool for running operations that access the disk, such as
            // our Room database.
            withContext(Dispatchers.IO) {
                val tonight: SleepNight = database.get(sleepNightKey)
                tonight.sleepQuality = quality
                database.update(tonight)
            }

            // Setting this state variable to true will alert the observer and trigger navigation.
            _navigateToSleepTracker.value = true
        }
    }
}