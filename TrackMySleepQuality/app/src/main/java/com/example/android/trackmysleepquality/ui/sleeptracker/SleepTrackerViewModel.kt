package com.example.android.trackmysleepquality.ui.sleeptracker

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

/**
 * ViewModel for `SleepTrackerFragment`.
 *
 * @param dataSource the [SleepDatabaseDao] to use to access the database
 * @param application the [Application] to use to access resources
 */
class SleepTrackerViewModel(
    dataSource: SleepDatabaseDao,
    application: Application
) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via SleepDatabaseDao.
     */
    val database: SleepDatabaseDao = dataSource
}