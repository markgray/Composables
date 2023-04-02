package com.example.android.trackmysleepquality.ui.sleeptracker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

/**
 * This is pretty much boiler plate code for a ViewModel Factory.
 *
 * Provides the SleepDatabaseDao and context to the ViewModel.
 *
 * @param dataSource Handle to the [SleepDatabaseDao] to use to call its Room SQLite methods.
 * @param application the [Application] that owns our activity, used to access resources.
 */
class SleepTrackerViewModelFactory(
    private val dataSource: SleepDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given [Class].
     *
     * @param modelClass a [Class] whose instance is requested
     * @param T          The type parameter for the [ViewModel].
     * @return a newly created [SleepTrackerViewModel] constructed to use [dataSource] as the
     * [SleepDatabaseDao] to access the Room database, and [application] as the context to use
     * to access resources.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepTrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // It is checked by above if statement
            return SleepTrackerViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
