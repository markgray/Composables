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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.samples.apps.sunflower.data.GardenPlantingRepository
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantRepository

/**
 * Factory for creating a [PlantDetailViewModel] with a constructor that takes a [PlantRepository]
 * a [GardenPlantingRepository] and an ID for the current [Plant].
 *
 * @param plantRepository the apps singleton [PlantRepository]
 * @param gardenPlantingRepository the apps singleton [GardenPlantingRepository]
 * @param plantId the [Plant.plantId] of the [Plant] that the user is interested in.
 */
class PlantDetailViewModelFactory(
    private val plantRepository: PlantRepository,
    private val gardenPlantingRepository: GardenPlantingRepository,
    private val plantId: String
) : ViewModelProvider.Factory {

    /**
     * Returns a new instance of [PlantDetailViewModel] constructed to use our [PlantRepository]
     * field [plantRepository], our [GardenPlantingRepository] field [gardenPlantingRepository] and
     * our [plantId] field. First we make sure that our [Class] parameter [modelClass] can
     * be hold a [PlantDetailViewModel] (throwing [IllegalArgumentException] is it cannot). Then we
     * return a new instance of [PlantDetailViewModel] constructed to use the apps singleton
     * [PlantRepository], singleton [GardenPlantingRepository], and the [Plant] whose [Plant.plantId]
     * property is the same as our [plantId] field.
     *
     * @param modelClass the [Class] of the type of [ViewModel] that we are to create.
     * @return a new instance of [PlantDetailViewModel] constructed to use the apps singleton
     * [PlantRepository], singleton [GardenPlantingRepository], and the [Plant] whose [Plant.plantId]
     * property is the same as our [plantId] field.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // It is checked by above if statement
            return PlantDetailViewModel(plantRepository, gardenPlantingRepository, plantId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
