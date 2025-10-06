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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.sunflower.BuildConfig
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.PlantRepository
import com.google.samples.apps.sunflower.compose.plantdetail.PlantDetailsScreen
import com.google.samples.apps.sunflower.data.Plant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel used by [PlantDetailsScreen].
 *
 * The @[HiltViewModel] annotation identifies a ViewModel for construction injection.
 * The [ViewModel] annotated with [HiltViewModel] will be available for creation by the
 * `HiltViewModelFactory` and can be retrieved by using the [hiltViewModel] method. The
 * [HiltViewModel] containing a constructor annotated with @[Inject] will have its
 * dependencies defined in the constructor parameters injected by Dagger's Hilt.
 *
 * @param savedStateHandle A handle to the saved state of the ViewModel, used to retrieve
 * the plant ID passed from the previous screen.
 * @param plantRepository The repository for fetching plant data from the database.
 * @param gardenPlantingRepository The repository for managing garden planting data.
 */
@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    plantRepository: PlantRepository,
    private val gardenPlantingRepository: GardenPlantingRepository,
) : ViewModel() {

    /**
     * The ID of the plant being displayed. This is retrieved from the [SavedStateHandle]
     * which is populated by the navigation arguments.
     */
    val plantId: String = savedStateHandle.get<String>(PLANT_ID_SAVED_STATE_KEY)!!

    /**
     * A [StateFlow] that emits `true` if the plant is in the garden, `false` otherwise.
     * This flow is backed by the [GardenPlantingRepository.isPlanted] method and is observed
     * within the `viewModelScope`. The state is shared and kept active for 5 seconds
     * after the last observer unsubscribes, with an initial value of `false`.
     */
    val isPlanted: StateFlow<Boolean> = gardenPlantingRepository.isPlanted(plantId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = false
        )

    /**
     * The plant details for the plant being displayed.
     *
     * This LiveData is populated from the [PlantRepository] based on the [plantId].
     * It is observed by the UI to display the plant's information.
     */
    val plant: LiveData<Plant> = plantRepository.getPlant(plantId = plantId).asLiveData()

    /**
     * The private [MutableLiveData] of [Boolean] that controls the visibility of the
     * "Added to garden" Snackbar. Its value is set to `true` in [addPlantToGarden] and
     * `false` in [dismissSnackbar].
     */
    private val _showSnackbar: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * The public read-only [LiveData] of [Boolean] that controls the visibility of the
     * "Added to garden" Snackbar. Its value is `true` when the plant has been added to
     * the garden and `false` otherwise. It is the publicly exposed version of the [MutableLiveData]
     * of [Boolean] property [_showSnackbar] whose value is updated by [addPlantToGarden] and
     * [dismissSnackbar].
     */
    val showSnackbar: LiveData<Boolean>
        get() = _showSnackbar

    /**
     * Adds the current plant to the garden.
     *
     * This function launches a coroutine in the [viewModelScope] to call the
     * [GardenPlantingRepository.createGardenPlanting] method with the current [plantId].
     * After the plant is added, it sets the value of [_showSnackbar] to `true` to trigger
     * the display of a confirmation Snackbar.
     */
    fun addPlantToGarden() {
        viewModelScope.launch {
            gardenPlantingRepository.createGardenPlanting(plantId = plantId)
            _showSnackbar.value = true
        }
    }

    /**
     * Called when the "Added to garden" snackbar is dismissed.
     *
     * This function sets the value of [_showSnackbar] to `false`,
     * which hides the snackbar from the UI.
     */
    fun dismissSnackbar() {
        _showSnackbar.value = false
    }

    /**
     * Checks if a valid Unsplash API key is available in the build configuration.
     *
     * This function is used to determine whether to show a "View on Unsplash" button
     * in the UI. If the key is not set (i.e., it's "null" as a string), the button
     * will be hidden.
     *
     * @return `true` if the `UNSPLASH_ACCESS_KEY` in `BuildConfig` is not the string "null",
     * `false` otherwise.
     */
    @Suppress("KotlinConstantConditions")
    fun hasValidUnsplashKey(): Boolean = (BuildConfig.UNSPLASH_ACCESS_KEY != "null")

    companion object {
        /**
         * A key for storing the plant ID in the [SavedStateHandle].
         * This key is used to retrieve the plant ID from the navigation arguments.
         */
        private const val PLANT_ID_SAVED_STATE_KEY = "plantId"
    }
}
