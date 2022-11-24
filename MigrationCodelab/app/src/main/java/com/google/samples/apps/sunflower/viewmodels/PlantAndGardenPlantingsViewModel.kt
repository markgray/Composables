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
 * TODO: Add kdoc
 */
class PlantAndGardenPlantingsViewModel(plantings: PlantAndGardenPlantings) {
    private val plant: Plant = checkNotNull(plantings.plant)
    private val gardenPlanting: GardenPlanting = plantings.gardenPlantings[0]

    /**
     * TODO: Add kdoc
     */
    val waterDateString: String = dateFormat.format(gardenPlanting.lastWateringDate.time)

    /**
     * TODO: Add kdoc
     */
    val wateringInterval: Int
        get() = plant.wateringInterval

    /**
     * TODO: Add kdoc
     */
    val imageUrl: String
        get() = plant.imageUrl

    /**
     * TODO: Add kdoc
     */
    val plantName: String
        get() = plant.name

    /**
     * TODO: Add kdoc
     */
    val plantDateString: String = dateFormat.format(gardenPlanting.plantDate.time)

    /**
     * TODO: Add kdoc
     */
    val plantId: String
        get() = plant.plantId

    companion object {
        private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    }
}
