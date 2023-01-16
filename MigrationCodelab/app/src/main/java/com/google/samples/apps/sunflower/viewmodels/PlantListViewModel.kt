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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.adapters.PlantAdapter
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantRepository

/**
 * The ViewModel for [PlantListFragment].
 *
 * @param plantRepository our app's singleton [PlantRepository].
 * @param savedStateHandle handle to saved state passed down to our [ViewModel]. We use it to save
 * and restore the grow zone number under the key [GROW_ZONE_SAVED_STATE_KEY]. This value will
 * persist after the process is killed by the system and remain available via the same object.
 */
class PlantListViewModel internal constructor(
    plantRepository: PlantRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * Used to retrieve from the [PlantRepository] the [List] of all the [Plant] instances in the
     * "plants" table of the database sorted by its "name" column if the grow zone found in our
     * [SavedStateHandle] field [savedStateHandle] under the key [GROW_ZONE_SAVED_STATE_KEY] is
     * [NO_GROW_ZONE] or else only those whose grow zone matches the grow zone found. To do this we
     * use the [switchMap] extension function on the [Int] returned by our [getSavedGrowZoneNumber]
     * method (which returns the value found in our [SavedStateHandle] field [savedStateHandle]
     * under the key [GROW_ZONE_SAVED_STATE_KEY]) and if the value is [NO_GROW_ZONE] it returns the
     * [LiveData] wrapped [List] of [Plant] that the [PlantRepository.getPlants] method of our
     * `plantRepository` field returns, otherwise it returns the [LiveData] wrapped [List] of [Plant]
     * that the [PlantRepository.getPlantsWithGrowZoneNumber] returns when passed the grow zone value
     * as its `growZoneNumber` argument.
     *
     * An observer is added to this property by the `subscribeUi` method of [PlantListFragment] which
     * calls the [PlantAdapter.submitList] method of the adapter that feeds [Plant] instances to the
     * [RecyclerView] in its UI whenever [plants] changes value.
     *
     * @return a [LiveData] wrapped [List] of [Plant] retrieved from our [PlantRepository] filtered
     * by our current grow zone if it is not [NO_GROW_ZONE].
     */
    val plants: LiveData<List<Plant>> = getSavedGrowZoneNumber().switchMap { growZone: Int ->
        if (growZone == NO_GROW_ZONE) {
            plantRepository.getPlants()
        } else {
            plantRepository.getPlantsWithGrowZoneNumber(growZoneNumber = growZone)
        }
    }

    /**
     * Saves its [Int] parameter [num] (our new grow zone number) in our [SavedStateHandle] field
     * [savedStateHandle] under the key [GROW_ZONE_SAVED_STATE_KEY]. It will be retrieved using the
     * [getSavedGrowZoneNumber] method by our [plants] when it needs to decide whether to return
     * the entire [List] of [Plant] instances stored in the "plants" table of the database, or a
     * [List] of [Plant] instances whose [Plant.growZoneNumber] is equal to [num].
     *
     * @param num the new grow zone number.
     */
    fun setGrowZoneNumber(num: Int) {
        savedStateHandle[GROW_ZONE_SAVED_STATE_KEY] = num
    }

    /**
     * Stores [NO_GROW_ZONE] under the key [GROW_ZONE_SAVED_STATE_KEY] in our [SavedStateHandle] field
     * [savedStateHandle] (clearing the grow zone that is used by [plants] to filter the [List] of
     * [Plant] instances it returns).
     */
    fun clearGrowZoneNumber() {
        savedStateHandle[GROW_ZONE_SAVED_STATE_KEY] = NO_GROW_ZONE
    }

    /**
     * Returns `true` if the value returned by [getSavedGrowZoneNumber] is not [NO_GROW_ZONE] (ie
     * our [plants] property is filtering for a grow zone number).
     *
     * @return `true` if the value returned by [getSavedGrowZoneNumber] is not [NO_GROW_ZONE].
     */
    fun isFiltered(): Boolean = getSavedGrowZoneNumber().value != NO_GROW_ZONE

    /**
     * This method retrieves the grow zone number that we stored in our [SavedStateHandle] field
     * [savedStateHandle] under the key [GROW_ZONE_SAVED_STATE_KEY] wrapped in a [MutableLiveData]
     * defaulting to [NO_GROW_ZONE] if no grow zone number has been saved yet.
     *
     * @return the value stored in our [SavedStateHandle] field [savedStateHandle] under the key
     * [GROW_ZONE_SAVED_STATE_KEY] wrapped in a [MutableLiveData] defaulting to [NO_GROW_ZONE] if
     * none found.
     */
    private fun getSavedGrowZoneNumber(): MutableLiveData<Int> {
        return savedStateHandle.getLiveData(GROW_ZONE_SAVED_STATE_KEY, NO_GROW_ZONE)
    }

    companion object {
        /**
         * The value used if no grow zone is specified, ie. our [plants] property is not fitering for
         * a specific grow zone.
         */
        private const val NO_GROW_ZONE = -1

        /**
         * The key under which we store the grow zone number that we filtering for in our
         * [SavedStateHandle] field [savedStateHandle].
         */
        private const val GROW_ZONE_SAVED_STATE_KEY = "GROW_ZONE_SAVED_STATE_KEY"
    }
}
