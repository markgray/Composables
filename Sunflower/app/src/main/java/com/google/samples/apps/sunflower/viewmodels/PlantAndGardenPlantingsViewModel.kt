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

import com.google.samples.apps.sunflower.data.GardenPlanting
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * A ViewModel like class that is responsible for formatting and providing data to the UI for a
 * specific [PlantAndGardenPlantings].
 *
 * @param plantings The data to be formatted and displayed in the UI.
 */
class PlantAndGardenPlantingsViewModel(plantings: PlantAndGardenPlantings) {
    /**
     * The [Plant] associated with this instance of [PlantAndGardenPlantingsViewModel].
     */
    private val plant: Plant = checkNotNull(plantings.plant)

    /**
     * The [GardenPlanting] associated with this instance of [PlantAndGardenPlantingsViewModel].
     */
    private val gardenPlanting: GardenPlanting = plantings.gardenPlantings[0]

    /**
     * String representing the last time the plant was watered.
     */
    val waterDateString: String = dateFormat.format(gardenPlanting.lastWateringDate.time)

    /**
     * The number of days between each watering.
     */
    val wateringInterval: Int
        get() = plant.wateringInterval

    /**
     * URL for the plant image.
     */
    val imageUrl: String
        get() = plant.imageUrl

    /**
     * The name of the plant.
     */
    val plantName: String
        get() = plant.name

    /**
     * String representing the date the plant was planted.
     */
    val plantDateString: String = dateFormat.format(gardenPlanting.plantDate.time)

    /**
     * The ID of the plant.
     */
    val plantId: String
        get() = plant.plantId

    companion object {
        /**
         * A [SimpleDateFormat] for formatting dates.
         */
        private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    }
}