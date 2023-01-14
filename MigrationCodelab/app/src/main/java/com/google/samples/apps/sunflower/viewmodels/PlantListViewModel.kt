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
import com.google.samples.apps.sunflower.PlantListFragment
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
     * TODO: Add kdoc
     */
    val plants: LiveData<List<Plant>> = getSavedGrowZoneNumber().switchMap {
        if (it == NO_GROW_ZONE) {
            plantRepository.getPlants()
        } else {
            plantRepository.getPlantsWithGrowZoneNumber(it)
        }
    }

    /**
     * TODO: Add kdoc
     */
    fun setGrowZoneNumber(num: Int) {
        savedStateHandle[GROW_ZONE_SAVED_STATE_KEY] = num
    }

    /**
     * TODO: Add kdoc
     */
    fun clearGrowZoneNumber() {
        savedStateHandle[GROW_ZONE_SAVED_STATE_KEY] = NO_GROW_ZONE
    }

    /**
     * TODO: Add kdoc
     */
    fun isFiltered(): Boolean = getSavedGrowZoneNumber().value != NO_GROW_ZONE

    /**
     * TODO: Add kdoc
     */
    private fun getSavedGrowZoneNumber(): MutableLiveData<Int> {
        return savedStateHandle.getLiveData(GROW_ZONE_SAVED_STATE_KEY, NO_GROW_ZONE)
    }

    companion object {
        /**
         * TODO: Add kdoc
         */
        private const val NO_GROW_ZONE = -1

        /**
         * TODO: Add kdoc
         */
        private const val GROW_ZONE_SAVED_STATE_KEY = "GROW_ZONE_SAVED_STATE_KEY"
    }
}
