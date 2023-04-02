package com.example.android.trackmysleepquality.ui.sleepdetail

import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

/**
 * ViewModel for SleepDetailFragment.
 *
 * @param sleepNightKey The key of the current night we are working on.
 * @param dataSource Handle to the [SleepDatabaseDao] to use to call its Room SQLite methods.
 */
class SleepDetailViewModel(
    private val sleepNightKey: Long = 0L,
    dataSource: SleepDatabaseDao
) : ViewModel()  {
    /**
     * Hold a reference to `SleepDatabase` via its [SleepDatabaseDao].
     */
    val database: SleepDatabaseDao = dataSource
}