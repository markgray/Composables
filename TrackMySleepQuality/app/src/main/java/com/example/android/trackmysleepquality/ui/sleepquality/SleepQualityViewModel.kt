package com.example.android.trackmysleepquality.ui.sleepquality

import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

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
}