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
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.apps.sunflower.GardenFragment
import com.google.samples.apps.sunflower.adapters.GardenPlantingAdapter
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings

/**
 * This is the [ViewModel] used by [GardenFragment] to read the [LiveData] wrapped [List] of
 * [PlantAndGardenPlantings] that the [GardenPlantingRepository.getPlantedGardens] method returns
 * (the [GardenPlantingListViewModel.plantAndGardenPlantings] property of the [ViewModel]).
 *
 * @param gardenPlantingRepository the apps singleton instance of [GardenPlantingRepository]
 */
class GardenPlantingListViewModel internal constructor(
    gardenPlantingRepository: GardenPlantingRepository
) : ViewModel() {
    /**
     * [GardenFragment] uses this to access the [LiveData] wrapped [List] of [PlantAndGardenPlantings]
     * that the [GardenPlantingRepository.getPlantedGardens] method returns. It adds an observer in
     * its `subscribeUi` method which submits the [List] whenever its contents change to the
     * [GardenPlantingAdapter] which feeds data to the [RecyclerView] in its UI.
     */
    val plantAndGardenPlantings: LiveData<List<PlantAndGardenPlantings>> =
        gardenPlantingRepository.getPlantedGardens()
}
