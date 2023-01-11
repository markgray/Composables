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

import androidx.recyclerview.widget.RecyclerView
import com.google.samples.apps.sunflower.adapters.GardenPlantingAdapter
import com.google.samples.apps.sunflower.data.GardenPlanting
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import com.google.samples.apps.sunflower.GardenFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * This is used to construct a "pseudo"  `ViewModel` that is used for the "viewmodel" binding
 * variable of views that are inflated from the file `layout/list_item_garden_planting.xml` (those
 * views are used by [GardenPlantingAdapter] to display each of the [PlantAndGardenPlantings] objects
 * of its dataset in the [RecyclerView] in the file `layout/fragment_garden.xml` that [GardenFragment]
 * uses as its UI). Each of the [PlantAndGardenPlantingsViewModel]'s holds a different
 * [PlantAndGardenPlantings] instance which provides access to the particular [Plant] and
 * [GardenPlanting] that the [RecyclerView] item is to display allowing it to use binding expressions
 * to update its UI from its "viewmodel" variable.
 *
 * @param plantings the [PlantAndGardenPlantings] instance which holds the [Plant] and the
 * [GardenPlanting] that we are to provide access to.
 */
class PlantAndGardenPlantingsViewModel(plantings: PlantAndGardenPlantings) {
    /**
     * The [Plant] object contained in our [PlantAndGardenPlantings] parameter `plantings`.
     */
    private val plant: Plant = checkNotNull(plantings.plant)

    /**
     * The [GardenPlanting] object contained in our [PlantAndGardenPlantings] parameter `plantings`.
     */
    private val gardenPlanting: GardenPlanting = plantings.gardenPlantings[0]

    /**
     * This property "returns" the [Calendar.time] of the [GardenPlanting.lastWateringDate] property
     * of our [PlantAndGardenPlantings] parameter `plantings` formatted into a [String] using our
     * [SimpleDateFormat] field [dateFormat].
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
