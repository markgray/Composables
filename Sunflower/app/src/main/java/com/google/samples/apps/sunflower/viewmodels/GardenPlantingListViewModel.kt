/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.viewmodels

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import com.google.samples.apps.sunflower.compose.garden.GardenScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * The ViewModel for the [GardenScreen].
 *
 * The @[HiltViewModel] annotation identifies a ViewModel for construction injection.
 * The [ViewModel] annotated with [HiltViewModel] will be available for creation by the
 * `HiltViewModelFactory` and can be retrieved by using the [hiltViewModel] method. The
 * [HiltViewModel] containing a constructor annotated with @[Inject] will have its
 * dependencies defined in the constructor parameters injected by Dagger's Hilt.
 *
 * @param gardenPlantingRepository the singleton [GardenPlantingRepository] injected by Hilt.
 */
@HiltViewModel
class GardenPlantingListViewModel @Inject internal constructor(
    gardenPlantingRepository: GardenPlantingRepository
) : ViewModel() {
    /**
     * A [StateFlow] of all plants that have been added to the garden.
     *
     * This flow is collected by the UI to display the list of planted plants. It is backed by the
     * [GardenPlantingRepository] and converted into a [StateFlow] to make it observable and to
     * survive configuration changes when used with [viewModelScope].
     *
     * The `stateIn` operator transforms the cold [Flow] of [List] of [PlantAndGardenPlantings] from
     * `getPlantedGardens()` into a hot [StateFlow]. This means the flow is shared among all its
     * collectors. The subscription is kept active for 5 seconds (`stopTimeoutMillis = 5000`) after
     * the last collector unsubscribes, which helps prevent re-querying the database on quick
     * configuration changes (like screen rotations). The `initialValue` is an empty list, which
     * is emitted immediately until the first value from the database is ready.
     */
    val plantAndGardenPlantings: StateFlow<List<PlantAndGardenPlantings>> =
        gardenPlantingRepository
            .getPlantedGardens()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = emptyList()
            )
}