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
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.sunflower.data.GardenPlanting
import com.google.samples.apps.sunflower.data.GardenPlantingDao
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantDao
import com.google.samples.apps.sunflower.data.PlantRepository
import com.google.samples.apps.sunflower.plantdetail.PlantDetailFragment
import kotlinx.coroutines.launch

/**
 * The ViewModel used in [PlantDetailFragment].
 *
 * @param plantRepository the singleton [PlantRepository] to use to fetch information about each of
 * the available [Plant] objects.
 * @param gardenPlantingRepository the [GardenPlantingRepository] to use to check whether the [Plant]
 * whose [Plant.plantId] matches our [plantId] parameter is planted in our garden, and to create and
 * add the [Plant] whose [Plant.plantId] matches our [plantId] parameter to the garden using our
 * [addPlantToGarden] method.
 * @param plantId the [Plant.plantId] property of the [Plant] that [PlantDetailFragment] is supposed
 * to be displaying.
 */
class PlantDetailViewModel(
    plantRepository: PlantRepository,
    private val gardenPlantingRepository: GardenPlantingRepository,
    private val plantId: String
) : ViewModel() {

    /**
     * Read by a binding expression in the layout file layout/fragment_plant_detail.xml to  find out
     * if the [Plant] whose [Plant.plantId] property matches our [plantId] field is planted in our
     * garden. To do this it returns the value that the [GardenPlantingRepository.isPlanted] method
     * of our [gardenPlantingRepository] field returns when called with [plantId]. It in turn
     * returns the value returned by the [GardenPlantingDao.isPlanted] method for [plantId] when it
     * queries the ROOM database looking for the [Plant].
     */
    val isPlanted: LiveData<Boolean> = gardenPlantingRepository.isPlanted(plantId)

    /**
     * This method is used to observe and/or retrieve the [Plant] whose [Plant.plantId] property
     * matches our [plantId] field from the [PlantRepository] (it in turn calls the [PlantDao.getPlant]
     * method with [plantId] which queries the ROOM database to retrieve the [Plant]).
     */
    val plant: LiveData<Plant> = plantRepository.getPlant(plantId)

    /**
     * This method is called to create a new instance of [GardenPlanting] for the [Plant] whose
     * [Plant.plantId] property is the same as our [plantId] field and add it to the "garden_plantings"
     * table of our ROOM database.
     */
    fun addPlantToGarden() {
        viewModelScope.launch {
            gardenPlantingRepository.createGardenPlanting(plantId)
        }
    }
}
